package dev.stardust.modules;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.exceptions.AuthenticationException;
import dev.stardust.blisschat.BlissChatClient;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
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
import java.net.URISyntaxException;
import java.util.UUID;

public class BlissChat extends Module {
    private static final int PINK = 0xFF73BE;
    private static final int HOT_PINK = 0xFF4FA8;
    private static final int BLUE = 0x65D6FF;
    private static final int PLUM = 0x2A1028;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> backendUrl = sgGeneral.add(new StringSetting.Builder()
        .name("backend-url")
        .description("Cloudflare Worker WebSocket URL for Bliss chat.")
        .defaultValue("ws://localhost:8787/chat")
        .onChanged(value -> {
            if (isActive()) reconnect();
        })
        .build()
    );

    private final Setting<Boolean> autoConnect = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-connect")
        .description("Connect automatically after joining a multiplayer server.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> showStatusMessages = sgGeneral.add(new BoolSetting.Builder()
        .name("show-status-messages")
        .description("Show Bliss chat connection messages in chat.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> showHistory = sgGeneral.add(new BoolSetting.Builder()
        .name("show-history")
        .description("Show recent backend messages after connecting.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> showPresence = sgGeneral.add(new BoolSetting.Builder()
        .name("show-presence")
        .description("Show join and leave messages from Bliss chat.")
        .defaultValue(true)
        .build()
    );

    private BlissChatClient client;

    public BlissChat() {
        super(Categories.Misc, "BlissChat", "Online-mode verified Bliss backend chat. Use .chat <message> to send.");
        runInMainMenu = true;
    }

    @Override
    public void onActivate() {
        if (autoConnect.get() && mc.player != null) connect();
    }

    @Override
    public void onDeactivate() {
        disconnect("Module disabled.");
    }

    @EventHandler
    private void onJoin(GameJoinedEvent event) {
        if (autoConnect.get() && isActive()) connect();
    }

    @EventHandler
    private void onLeave(GameLeftEvent event) {
        disconnect("Left server.");
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
            .append(Component.literal(backendUrl.get()).withStyle(ChatFormatting.WHITE)));
    }

    public void reconnect() {
        disconnect("Reconnecting.");
        connect();
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
        if (socket != client) return;
        showStatusLine("Connected to backend. Waiting for auth challenge.", ChatFormatting.GRAY);
    }

    public void onAuthenticated(BlissChatClient socket) {
        if (socket != client) return;
        showStatusLine("Online-mode session verified.", ChatFormatting.GREEN);
    }

    public void onSocketClosed(BlissChatClient socket, int code, String reason, boolean remote, boolean wasReady) {
        if (socket == client) client = null;
        if (wasReady || code != 1000) {
            String detail = reason == null || reason.isBlank() ? "code " + code : reason;
            showStatusLine("Disconnected from Bliss chat: " + detail, remote ? ChatFormatting.YELLOW : ChatFormatting.GRAY);
        }
    }

    public void onSocketError(BlissChatClient socket, Exception ex) {
        if (socket != client) return;
        showError("Bliss chat connection failed: " + message(ex));
    }

    public void displayHistory(JsonArray messages) {
        if (!showHistory.get() || messages.isEmpty()) return;

        sendLine(Component.literal("Recent Bliss chat").setStyle(Style.EMPTY
            .withColor(TextColor.fromRgb(BLUE))
            .withBold(true)));

        for (JsonElement element : messages) {
            if (element != null && element.isJsonObject()) displayChatMessage(element.getAsJsonObject());
        }
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

        MutableComponent line = prefix()
            .append(name)
            .append(Component.literal("  ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(PLUM))))
            .append(Component.literal(message).withStyle(ChatFormatting.WHITE));

        sendRawLine(line);
    }

    public void displayPresence(JsonObject json) {
        if (!showPresence.get()) return;

        String action = string(json, "action", "");
        String username = string(json, "username", "Unknown");
        String serverAddress = string(json, "serverAddress", "unknown");
        if (!"join".equals(action) && !"leave".equals(action)) return;

        ChatFormatting color = "join".equals(action) ? ChatFormatting.GREEN : ChatFormatting.RED;
        sendLine(Component.literal(username).withStyle(color)
            .append(Component.literal(" " + ("join".equals(action) ? "joined" : "left") + " Bliss chat")
                .withStyle(ChatFormatting.GRAY)
                .withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(Component.literal("Playing on: " + serverAddress)
                    .withStyle(ChatFormatting.GRAY))))));
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

        URI uri;
        try {
            uri = new URI(backendUrl.get());
        } catch (URISyntaxException e) {
            showError("Invalid backend URL: " + backendUrl.get());
            return null;
        }

        current = new BlissChatClient(this, uri);
        client = current;
        showStatusLine("Connecting to Bliss chat...", ChatFormatting.GRAY);
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
        if (!showStatusMessages.get()) return;
        sendLine(Component.literal(message).withStyle(color));
    }

    private void sendLine(Component component) {
        sendRawLine(prefix().append(component));
    }

    private void sendRawLine(Component component) {
        mc.execute(() -> {
            if (mc.player != null) mc.player.sendSystemMessage(component);
        });
    }

    private MutableComponent prefix() {
        return Component.literal("[")
            .withStyle(ChatFormatting.DARK_GRAY)
            .append(Component.literal("Bliss").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(PINK)).withBold(true)))
            .append(Component.literal("Chat").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(BLUE)).withBold(true)))
            .append(Component.literal("] ").withStyle(ChatFormatting.DARK_GRAY));
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
