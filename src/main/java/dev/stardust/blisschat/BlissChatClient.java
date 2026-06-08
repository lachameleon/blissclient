package dev.stardust.blisschat;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.stardust.modules.BlissChat;
import dev.stardust.playertracker.PlayerTracker;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BlissChatClient extends WebSocketClient {
    private static final Gson GSON = new Gson();
    private static final int MAX_PENDING_MESSAGES = 20;
    private static final int MAX_PENDING_SIGHTINGS = 100;

    private final BlissChat module;
    private final Queue<String> pendingMessages = new ConcurrentLinkedQueue<>();
    private final Queue<JsonObject> pendingSightings = new ConcurrentLinkedQueue<>();

    private volatile boolean ready;
    private volatile boolean connecting = true;

    public BlissChatClient(BlissChat module, URI serverUri) {
        super(serverUri);
        this.module = module;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        connecting = false;
        module.onSocketOpened(this);
    }

    @Override
    public void onMessage(String message) {
        JsonObject json;
        try {
            JsonElement element = JsonParser.parseString(message);
            if (!element.isJsonObject()) return;
            json = element.getAsJsonObject();
        } catch (Exception e) {
            module.showError("Received invalid backend message.");
            return;
        }

        String type = string(json, "type");
        switch (type) {
            case "challenge" -> {
                String challenge = string(json, "challenge");
                if (challenge == null || challenge.isBlank()) {
                    close(1002, "missing challenge");
                    return;
                }
                module.authenticate(this, challenge);
            }
            case "ready" -> {
                ready = true;
                module.onAuthenticated(this);
                flushPending();
            }
            case "chat" -> module.displayChatMessage(json);
            case "error" -> module.showError(string(json, "message", "Backend rejected the request."));
            default -> {
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        boolean wasReady = ready;
        ready = false;
        connecting = false;
        module.onSocketClosed(this, code, reason, remote, wasReady);
    }

    @Override
    public void onError(Exception ex) {
        connecting = false;
        module.onSocketError(this, ex);
    }

    public void sendAuth(String username, String uuid, String serverAddress) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "auth");
        json.addProperty("username", username);
        json.addProperty("uuid", uuid);
        json.addProperty("serverAddress", serverAddress);
        send(GSON.toJson(json));
    }

    public void sendChat(String message) {
        if (!ready || !isOpen()) {
            enqueue(message);
            return;
        }

        JsonObject json = new JsonObject();
        json.addProperty("type", "chat");
        json.addProperty("message", message);
        sendJson(json);
    }

    public void sendSeenPlayer(PlayerTracker.SightingReport report) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "seen_player");
        json.addProperty("seenUsername", report.username());
        json.addProperty("seenUuid", report.uuid().toString());
        json.addProperty("serverAddress", report.serverAddress());
        json.addProperty("dimension", report.dimension());
        json.addProperty("x", report.x());
        json.addProperty("y", report.y());
        json.addProperty("z", report.z());
        json.addProperty("distance", report.distance());
        json.addProperty("health", report.health());
        json.addProperty("maxHealth", report.maxHealth());
        json.addProperty("ping", report.ping());
        json.addProperty("gameMode", report.gameMode());
        json.addProperty("sightingCount", report.sightingCount());
        json.addProperty("totalVisibleMs", report.totalVisibleMs());
        json.addProperty("visibleMs", report.visibleMs());
        json.addProperty("averageDistance", report.averageDistance());
        json.addProperty("speed", report.speed());
        json.addProperty("heatmapX", report.heatmapX());
        json.addProperty("heatmapZ", report.heatmapZ());
        json.addProperty("timestamp", report.timestamp());

        if (!ready || !isOpen()) {
            enqueueSighting(json);
            return;
        }

        sendJson(json);
    }

    public boolean isReady() {
        return ready && isOpen();
    }

    public boolean isConnecting() {
        return connecting;
    }

    private void enqueue(String message) {
        while (pendingMessages.size() >= MAX_PENDING_MESSAGES) pendingMessages.poll();
        pendingMessages.offer(message);
    }

    private void enqueueSighting(JsonObject sighting) {
        while (pendingSightings.size() >= MAX_PENDING_SIGHTINGS) pendingSightings.poll();
        pendingSightings.offer(sighting);
    }

    private void flushPending() {
        String message;
        while ((message = pendingMessages.poll()) != null) sendChat(message);

        JsonObject sighting;
        while ((sighting = pendingSightings.poll()) != null) sendJson(sighting);
    }

    private void sendJson(JsonObject json) {
        send(GSON.toJson(json));
    }

    private static String string(JsonObject json, String key) {
        return string(json, key, null);
    }

    private static String string(JsonObject json, String key, String fallback) {
        JsonElement element = json.get(key);
        if (element == null || element.isJsonNull()) return fallback;
        try {
            return element.getAsString();
        } catch (Exception ignored) {
            return fallback;
        }
    }
}
