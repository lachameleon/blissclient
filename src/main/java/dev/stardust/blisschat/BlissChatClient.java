package dev.stardust.blisschat;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.stardust.modules.BlissChat;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BlissChatClient extends WebSocketClient {
    private static final Gson GSON = new Gson();
    private static final int MAX_PENDING_MESSAGES = 20;

    private final BlissChat module;
    private final Queue<String> pendingMessages = new ConcurrentLinkedQueue<>();

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
            case "history" -> {
                JsonArray messages = json.getAsJsonArray("messages");
                if (messages != null) module.displayHistory(messages);
            }
            case "chat" -> module.displayChatMessage(json);
            case "presence" -> module.displayPresence(json);
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
        send(GSON.toJson(json));
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

    private void flushPending() {
        String message;
        while ((message = pendingMessages.poll()) != null) sendChat(message);
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
