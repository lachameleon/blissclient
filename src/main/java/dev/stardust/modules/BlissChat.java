package dev.stardust.modules;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.exceptions.AuthenticationException;
import dev.stardust.blisschat.BlissChatClient;
import dev.stardust.playertracker.PlayerTracker;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.User;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Util;

import java.net.URI;
import java.util.UUID;

public class BlissChat extends Module {
    private static final String DEFAULT_BACKEND_URL = "wss://blissclientbackend.hogridersupercell123.workers.dev/chat";
    private static final URI BACKEND_URI = URI.create(DEFAULT_BACKEND_URL);
    private static final int HOT_PINK = 0xFF4FA8;
    private static final int BLUE = 0x65D6FF;

    private BlissChatClient client;

    public BlissChat() {
        super(Categories.Misc, "BlissChat", "Online-mode verified Bliss backend chat. Use .chat <message> to send.");
        runInMainMenu = true;
    }

    @Override
    public void onActivate() {
        if (mc.player != null) connect();
    }

    @Override
    public void onDeactivate() {
        disconnect("Module disabled.");
    }

    @EventHandler
    private void onLeave(GameLeftEvent event) {
        disconnect("Left server.");
    }

    public void connectOnServerJoin() {
        mc.execute(() -> {
            if (mc.player == null || mc.getCurrentServer() == null) return;

            if (!isActive()) enable();
            else connect();
        });
    }

    public void sendChat(String message) {
        if (message == null || message.isBlank()) {
            showError("Usage: .chat <message>");
            return;
        }

        if (!isActive()) enable();
        BlissChatClient current = connect();
        if (current != null) current.sendChat(message.trim());
    }

    public void showStatus() {
        BlissChatClient current = client;
        String status;
        ChatFormatting color;
        if (!isActive()) {
            status = "disabled";
            color = ChatFormatting.RED;
        } else if (current != null && current.isReady()) {
            status = "connected";
            color = ChatFormatting.GREEN;
        } else if (current != null && current.isConnecting()) {
            status = "connecting";
            color = ChatFormatting.YELLOW;
        } else {
            status = "disconnected";
            color = ChatFormatting.RED;
        }

        sendLine(Component.literal("Status: ").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(status).withStyle(color))
            .append(Component.literal(" | Backend: ").withStyle(ChatFormatting.GRAY))
            .append(Component.literal(DEFAULT_BACKEND_URL).withStyle(ChatFormatting.WHITE)));
    }

    public void reconnect() {
        disconnect("Reconnecting.");
        connect();
    }

    public void reportSeenPlayer(PlayerTracker.SightingReport report) {
        if (report == null || mc.player == null || mc.getCurrentServer() == null) return;

        if (!isActive()) enable();
        BlissChatClient current = connect();
        if (current != null) current.sendSeenPlayer(report);
    }

    public void authenticate(BlissChatClient socket, String challenge) {
        mc.execute(() -> {
            if (socket != client) return;

            AuthSnapshot snapshot = createAuthSnapshot();
            if (snapshot == null) {
                socket.close(1008, "online-mode client required");
                return;
            }

            Util.ioPool().execute(() -> {
                try {
                    mc.services().sessionService().joinServer(snapshot.uuid(), snapshot.accessToken(), challenge);
                    if (socket == client && socket.isOpen()) {
                        socket.sendAuth(snapshot.username(), snapshot.uuid().toString(), snapshot.serverAddress());
                    }
                } catch (AuthenticationException e) {
                    showError("Mojang session proof failed. Use a Microsoft/online-mode account.");
                    socket.close(1008, "mojang auth failed");
                } catch (Exception e) {
                    showError("Could not authenticate with Bliss backend: " + message(e));
                    socket.close(1011, "auth error");
                }
            });
        });
    }

    public void onSocketOpened(BlissChatClient socket) {
    }

    public void onAuthenticated(BlissChatClient socket) {
    }

    public void onSocketClosed(BlissChatClient socket, int code, String reason, boolean remote, boolean wasReady) {
        if (socket == client) client = null;
        if (code != 1000 && wasReady) {
            String detail = reason == null || reason.isBlank() ? "code " + code : reason;
            showError("Disconnected from Bliss chat: " + detail);
        }
    }

    public void onSocketError(BlissChatClient socket, Exception ex) {
        if (socket != client) return;
        showError("Bliss chat connection failed: " + message(ex));
    }

    public void displayChatMessage(JsonObject json) {
        String username = string(json, "username", "Unknown");
        String uuid = string(json, "uuid", "unknown");
        String serverAddress = string(json, "serverAddress", "unknown");
        String message = string(json, "message", "");
        if (message.isBlank()) return;

        MutableComponent name = Component.literal(username).setStyle(Style.EMPTY
            .withColor(TextColor.fromRgb(HOT_PINK))
            .withBold(true)
            .withHoverEvent(new HoverEvent.ShowText(Component.literal("Playing on: " + serverAddress + "\nUUID: " + uuid)
                .withStyle(ChatFormatting.GRAY))));

        MutableComponent line = Component.empty()
            .append(name)
            .append(Component.literal(": ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(BLUE))))
            .append(Component.literal(message).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFF6FB))));

        sendRawLine(line);
    }

    public void showError(String message) {
        showStatusLine(message, ChatFormatting.RED);
    }

    private BlissChatClient connect() {
        if (mc.player == null) {
            showError("Join a multiplayer server before using Bliss chat.");
            return null;
        }

        BlissChatClient current = client;
        if (current != null && (current.isOpen() || current.isConnecting())) return current;

        current = new BlissChatClient(this, BACKEND_URI);
        client = current;
        current.connect();
        return current;
    }

    private void disconnect(String reason) {
        BlissChatClient current = client;
        client = null;
        if (current != null) current.close(1000, reason);
    }

    private AuthSnapshot createAuthSnapshot() {
        User user = mc.getUser();
        if (user == null || user.getAccessToken() == null || user.getAccessToken().isBlank() || user.getProfileId() == null) {
            showError("Bliss chat requires a Microsoft/online-mode account.");
            return null;
        }

        ServerData server = mc.getCurrentServer();
        if (server == null) {
            showError("Bliss chat only sends from multiplayer servers.");
            return null;
        }

        String serverAddress = server.isRealm() ? "realms" : server.ip;
        if (serverAddress == null || serverAddress.isBlank()) serverAddress = "unknown";

        return new AuthSnapshot(user.getName(), user.getProfileId(), user.getAccessToken(), serverAddress);
    }

    private void showStatusLine(String message, ChatFormatting color) {
        sendLine(Component.literal(message).withStyle(color));
    }

    private void sendLine(Component component) {
        sendRawLine(component);
    }

    private void sendRawLine(Component component) {
        mc.execute(() -> {
            if (mc.player != null) mc.player.sendSystemMessage(component);
        });
    }

    private static String message(Exception ex) {
        String message = ex.getMessage();
        return message == null || message.isBlank() ? ex.getClass().getSimpleName() : message;
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

    private record AuthSnapshot(String username, UUID uuid, String accessToken, String serverAddress) {
    }
}
