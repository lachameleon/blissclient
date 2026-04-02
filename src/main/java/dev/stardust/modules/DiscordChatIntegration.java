package dev.stardust.modules;

import dev.stardust.discordchat.DiscordChatHandler;
import dev.stardust.discordchat.DiscordWebSocketServer;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.net.BindException;
import java.net.URI;

public class DiscordChatIntegration extends Module {
    private static final String RELEASES_URL = "https://github.com/aurickk/Discord-Chat-Integration/releases";
    private static final String BETTERDISCORD_GUIDE_URL = "https://docs.betterdiscord.app/users/guides/installing-addons";
    private static final String VENCORD_GUIDE_URL = "https://docs.vencord.dev/installing/custom-plugins/";

    private static final int NO_CLIENT_WARNING_DELAY_MS = 8000;

    private static final String DESCRIPTION = """
        Bridges Minecraft chat to Discord using the Discord-Chat-Integration plugin (no bot).

        Setup (Discord plugin required):
        1) Download plugin files: https://github.com/aurickk/Discord-Chat-Integration/releases
        2) BetterDiscord: place MinecraftChat.plugin.js in your BetterDiscord plugins folder, then enable it.
           Guide: https://docs.betterdiscord.app/users/guides/installing-addons
        3) Vencord: requires Node.js, git, and pnpm. Put minecraftChat.tsx in Vencord/src/userplugins,
           run pnpm build + pnpm inject, then enable it.
           Guide: https://docs.vencord.dev/installing/custom-plugins/
        4) In Discord: click the chat bar gear icon, Add Client, set Name/Port (default 25580)/Channel ID, enable it.
        5) Enable this module in Minecraft.

        Use /discordchat status or /discordchat tutorial for chat help.
        Note: Forwarding messages to Discord may be considered self-botting; use a private server.
        """;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> port = sgGeneral.add(
        new IntSetting.Builder()
            .name("port")
            .description("WebSocket port used by the Discord plugin.")
            .defaultValue(25580)
            .range(1024, 65535)
            .sliderRange(1024, 65535)
            .onChanged(value -> {
                if (isActive()) restartServer();
            })
            .build()
    );

    private final Setting<Boolean> forwardToDiscord = sgGeneral.add(
        new BoolSetting.Builder()
            .name("forward-to-discord")
            .description("Forward Minecraft chat messages to Discord.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> showStatusOnJoin = sgGeneral.add(
        new BoolSetting.Builder()
            .name("show-status-on-join")
            .description("Show connection status when joining a world or server.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> showTutorialOnEnable = sgGeneral.add(
        new BoolSetting.Builder()
            .name("show-tutorial-on-enable")
            .description("If no Discord plugin connects, show the setup tutorial in chat.")
            .defaultValue(true)
            .build()
    );

    private DiscordChatHandler chatHandler;
    private long enabledAtMs;
    private boolean tutorialShown;
    private int startToken;

    public DiscordChatIntegration() {
        super(Categories.Misc, "DiscordChat", DESCRIPTION);
        runInMainMenu = true;
    }

    @Override
    public void onActivate() {
        chatHandler = new DiscordChatHandler();
        enabledAtMs = System.currentTimeMillis();
        tutorialShown = false;
        startServer();
    }

    @Override
    public void onDeactivate() {
        startToken++;
        stopServer();
        if (chatHandler != null) {
            chatHandler.shutdown();
            chatHandler = null;
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (chatHandler == null) return;

        if (mc.world != null) {
            chatHandler.onTick(mc.world.getTime());
        }

        maybeShowTutorial();
    }

    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event) {
        if (chatHandler == null || !forwardToDiscord.get()) return;

        String content = event.getMessage().getString();
        if (content == null || content.isBlank()) return;

        chatHandler.handleIncomingMinecraftMessage("System", content);
    }

    @EventHandler
    private void onJoin(GameJoinedEvent event) {
        if (showStatusOnJoin.get()) showStatus();
    }

    private void maybeShowTutorial() {
        if (!showTutorialOnEnable.get() || tutorialShown) return;
        if (mc.player == null) return;

        DiscordWebSocketServer server = DiscordWebSocketServer.getInstance();
        if (server == null || !server.isRunning()) return;

        if (server.getConnectionCount() > 0) {
            tutorialShown = true;
            return;
        }

        long elapsed = System.currentTimeMillis() - enabledAtMs;
        if (elapsed < NO_CLIENT_WARNING_DELAY_MS) return;

        sendLine(Text.literal("No Discord plugin detected yet (0 clients).")
            .formatted(Formatting.RED));
        sendLine(Text.literal("Install the BetterDiscord or Vencord plugin and make sure the port matches.")
            .formatted(Formatting.GRAY));
        sendTutorial();
        tutorialShown = true;
    }

    public void showStatus() {
        if (mc.player == null) return;

        DiscordWebSocketServer server = DiscordWebSocketServer.getInstance();
        if (!isActive()) {
            sendLine(Text.literal("Discord chat module is disabled.").formatted(Formatting.RED));
            sendLine(Text.literal("Enable the module to start the WebSocket server.").formatted(Formatting.GRAY));
            return;
        }

        if (server == null) {
            sendLine(Text.literal("WebSocket server is not initialized.").formatted(Formatting.RED));
            sendLine(Text.literal("Try /discordchat reconnect or re-enable the module.").formatted(Formatting.GRAY));
            return;
        }

        if (server.isRunning()) {
            MutableText status = Text.literal("Status: ").formatted(Formatting.GRAY)
                .append(Text.literal("Running").formatted(Formatting.GREEN))
                .append(Text.literal(" | Port: ").formatted(Formatting.GRAY))
                .append(Text.literal(String.valueOf(port.get())).formatted(Formatting.WHITE))
                .append(Text.literal(" | Clients: ").formatted(Formatting.GRAY))
                .append(Text.literal(String.valueOf(server.getConnectionCount())).formatted(Formatting.WHITE));
            sendLine(status);
            if (server.getConnectionCount() == 0) {
                sendLine(Text.literal("No Discord clients connected. Install the plugin and check the port.")
                    .formatted(Formatting.YELLOW));
                sendLine(Text.literal("Use /discordchat tutorial for setup steps.")
                    .formatted(Formatting.GRAY));
            }
        } else {
            sendLine(Text.literal("WebSocket server is stopped.").formatted(Formatting.RED));
            sendLine(Text.literal("Port may be in use or server failed to start.").formatted(Formatting.GRAY));
            sendLine(Text.literal("Use /discordchat port <number> or /discordchat reconnect.").formatted(Formatting.GRAY));
        }
    }

    public void sendTutorial() {
        if (mc.player == null) return;

        sendLine(Text.literal("Discord Chat Integration setup:").formatted(Formatting.GOLD));
        sendLine(Text.literal("1) Download plugin files from ").formatted(Formatting.GRAY)
            .append(link("Releases", RELEASES_URL)));

        sendLine(Text.literal("2) BetterDiscord install:").formatted(Formatting.GRAY));
        sendLine(Text.literal("   - Put MinecraftChat.plugin.js in your BetterDiscord plugins folder and enable it.")
            .formatted(Formatting.GRAY));
        sendLine(Text.literal("   - Guide: ").formatted(Formatting.GRAY)
            .append(link("BetterDiscord plugin guide", BETTERDISCORD_GUIDE_URL)));

        sendLine(Text.literal("3) Vencord install:").formatted(Formatting.GRAY));
        sendLine(Text.literal("   - Requires Node.js, git, and pnpm.").formatted(Formatting.GRAY));
        sendLine(Text.literal("   - Put minecraftChat.tsx in Vencord/src/userplugins.")
            .formatted(Formatting.GRAY));
        sendLine(Text.literal("   - Run: pnpm build, then pnpm inject. Enable the plugin in Discord.")
            .formatted(Formatting.GRAY));
        sendLine(Text.literal("   - Guide: ").formatted(Formatting.GRAY)
            .append(link("Vencord custom plugin guide", VENCORD_GUIDE_URL)));

        sendLine(Text.literal("4) In Discord: click the chat bar gear icon → Add Client.")
            .formatted(Formatting.GRAY));
        sendLine(Text.literal("   - Set Name, Port (default 25580), Channel ID, Enabled.")
            .formatted(Formatting.GRAY));
        sendLine(Text.literal("5) Channel ID: enable Developer Mode, right-click channel → Copy ID.")
            .formatted(Formatting.GRAY));
        sendLine(Text.literal("6) Enable this module in Minecraft, then use /discordchat status.")
            .formatted(Formatting.GRAY));

        sendLine(Text.literal("Note: Forward-to-Discord may be considered self-botting. Use a private server.")
            .formatted(Formatting.YELLOW));
    }

    private MutableText link(String label, String url) {
        MutableText text = Text.literal(label).formatted(Formatting.AQUA, Formatting.UNDERLINE);
        return text.setStyle(text.getStyle()
            .withClickEvent(new ClickEvent.OpenUrl(URI.create(url)))
            .withHoverEvent(new HoverEvent.ShowText(Text.literal(url).formatted(Formatting.GRAY))));
    }

    private void sendLine(Text text) {
        ChatUtils.sendMsg(0, "DiscordChat", Formatting.AQUA, text);
    }

    public int getPort() {
        return port.get();
    }

    public void setPort(int newPort) {
        port.set(newPort);
    }

    public DiscordChatHandler getChatHandler() {
        return chatHandler;
    }

    public void restartServer() {
        if (!isActive()) return;
        stopServer();
        startServer();
    }

    private void startServer() {
        int token = ++startToken;
        int desiredPort = port.get();

        DiscordWebSocketServer.createInstance(desiredPort);
        DiscordWebSocketServer server = DiscordWebSocketServer.getInstance();
        if (server == null) return;

        server.setMessageHandler(message -> {
            if (chatHandler != null) chatHandler.handleDiscordMessage(message);
        });
        server.setSyncGroupHandler(syncGroup -> {
            if (chatHandler != null) chatHandler.setLastSyncGroup(syncGroup);
        });

        new Thread(() -> {
            try {
                server.start();
                int attempts = 0;
                while (!server.isRunning() && attempts < 10) {
                    Thread.sleep(50);
                    attempts++;
                }
                if (!server.isRunning()) throw new RuntimeException("Server failed to start");

                if (token == startToken && mc.player != null) {
                    sendLine(Text.literal("WebSocket server started on port " + desiredPort + ".")
                        .formatted(Formatting.GREEN));
                }
            } catch (Exception e) {
                if (token != startToken) return;

                boolean isBindError = e instanceof BindException ||
                    e.getCause() instanceof BindException ||
                    (e.getMessage() != null && (e.getMessage().contains("Address already in use") ||
                        e.getMessage().contains("BindException") ||
                        e.getMessage().contains("already bound")));

                if (isBindError) {
                    showPortError(desiredPort);
                } else {
                    showGenericError(e.getMessage());
                }
            }
        }, "Discord-WebSocket-Server").start();
    }

    private void stopServer() {
        DiscordWebSocketServer server = DiscordWebSocketServer.getInstance();
        if (server != null && server.isRunning()) {
            server.stopServer();
        }
    }

    private void showPortError(int port) {
        if (mc.player == null) return;

        sendLine(Text.literal("Port " + port + " is already in use.").formatted(Formatting.RED));
        sendLine(Text.literal("Change it in module settings or use /discordchat port <number>.")
            .formatted(Formatting.GRAY));
    }

    private void showGenericError(String message) {
        if (mc.player == null) return;

        sendLine(Text.literal("Failed to start WebSocket server.").formatted(Formatting.RED));
        if (message != null && !message.isBlank()) {
            sendLine(Text.literal("Error: " + message).formatted(Formatting.GRAY));
        }
    }
}
