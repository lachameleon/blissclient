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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
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

        if (mc.level != null) {
            chatHandler.onTick(mc.level.getGameTime());
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

        sendLine(Component.literal("No Discord plugin detected yet (0 clients).")
            .withStyle(ChatFormatting.RED));
        sendLine(Component.literal("Install the BetterDiscord or Vencord plugin and make sure the port matches.")
            .withStyle(ChatFormatting.GRAY));
        sendTutorial();
        tutorialShown = true;
    }

    public void showStatus() {
        if (mc.player == null) return;

        DiscordWebSocketServer server = DiscordWebSocketServer.getInstance();
        if (!isActive()) {
            sendLine(Component.literal("Discord chat module is disabled.").withStyle(ChatFormatting.RED));
            sendLine(Component.literal("Enable the module to start the WebSocket server.").withStyle(ChatFormatting.GRAY));
            return;
        }

        if (server == null) {
            sendLine(Component.literal("WebSocket server is not initialized.").withStyle(ChatFormatting.RED));
            sendLine(Component.literal("Try /discordchat reconnect or re-enable the module.").withStyle(ChatFormatting.GRAY));
            return;
        }

        if (server.isRunning()) {
            MutableComponent status = Component.literal("Status: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal("Running").withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" | Port: ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(String.valueOf(port.get())).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" | Clients: ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(String.valueOf(server.getConnectionCount())).withStyle(ChatFormatting.WHITE));
            sendLine(status);
            if (server.getConnectionCount() == 0) {
                sendLine(Component.literal("No Discord clients connected. Install the plugin and check the port.")
                    .withStyle(ChatFormatting.YELLOW));
                sendLine(Component.literal("Use /discordchat tutorial for setup steps.")
                    .withStyle(ChatFormatting.GRAY));
            }
        } else {
            sendLine(Component.literal("WebSocket server is stopped.").withStyle(ChatFormatting.RED));
            sendLine(Component.literal("Port may be in use or server failed to start.").withStyle(ChatFormatting.GRAY));
            sendLine(Component.literal("Use /discordchat port <number> or /discordchat reconnect.").withStyle(ChatFormatting.GRAY));
        }
    }

    public void sendTutorial() {
        if (mc.player == null) return;

        sendLine(Component.literal("Discord Chat Integration setup:").withStyle(ChatFormatting.GOLD));
        sendLine(Component.literal("1) Download plugin files from ").withStyle(ChatFormatting.GRAY)
            .append(link("Releases", RELEASES_URL)));

        sendLine(Component.literal("2) BetterDiscord install:").withStyle(ChatFormatting.GRAY));
        sendLine(Component.literal("   - Put MinecraftChat.plugin.js in your BetterDiscord plugins folder and enable it.")
            .withStyle(ChatFormatting.GRAY));
        sendLine(Component.literal("   - Guide: ").withStyle(ChatFormatting.GRAY)
            .append(link("BetterDiscord plugin guide", BETTERDISCORD_GUIDE_URL)));

        sendLine(Component.literal("3) Vencord install:").withStyle(ChatFormatting.GRAY));
        sendLine(Component.literal("   - Requires Node.js, git, and pnpm.").withStyle(ChatFormatting.GRAY));
        sendLine(Component.literal("   - Put minecraftChat.tsx in Vencord/src/userplugins.")
            .withStyle(ChatFormatting.GRAY));
        sendLine(Component.literal("   - Run: pnpm build, then pnpm inject. Enable the plugin in Discord.")
            .withStyle(ChatFormatting.GRAY));
        sendLine(Component.literal("   - Guide: ").withStyle(ChatFormatting.GRAY)
            .append(link("Vencord custom plugin guide", VENCORD_GUIDE_URL)));

        sendLine(Component.literal("4) In Discord: click the chat bar gear icon → Add Client.")
            .withStyle(ChatFormatting.GRAY));
        sendLine(Component.literal("   - Set Name, Port (default 25580), Channel ID, Enabled.")
            .withStyle(ChatFormatting.GRAY));
        sendLine(Component.literal("5) Channel ID: enable Developer Mode, right-click channel → Copy ID.")
            .withStyle(ChatFormatting.GRAY));
        sendLine(Component.literal("6) Enable this module in Minecraft, then use /discordchat status.")
            .withStyle(ChatFormatting.GRAY));

        sendLine(Component.literal("Note: Forward-to-Discord may be considered self-botting. Use a private server.")
            .withStyle(ChatFormatting.YELLOW));
    }

    private MutableComponent link(String label, String url) {
        MutableComponent text = Component.literal(label).withStyle(ChatFormatting.AQUA, ChatFormatting.UNDERLINE);
        return text.setStyle(text.getStyle()
            .withClickEvent(new ClickEvent.OpenUrl(URI.create(url)))
            .withHoverEvent(new HoverEvent.ShowText(Component.literal(url).withStyle(ChatFormatting.GRAY))));
    }

    private void sendLine(Component text) {
        ChatUtils.sendMsg(0, "DiscordChat", ChatFormatting.AQUA, text);
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
                    sendLine(Component.literal("WebSocket server started on port " + desiredPort + ".")
                        .withStyle(ChatFormatting.GREEN));
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

        sendLine(Component.literal("Port " + port + " is already in use.").withStyle(ChatFormatting.RED));
        sendLine(Component.literal("Change it in module settings or use /discordchat port <number>.")
            .withStyle(ChatFormatting.GRAY));
    }

    private void showGenericError(String message) {
        if (mc.player == null) return;

        sendLine(Component.literal("Failed to start WebSocket server.").withStyle(ChatFormatting.RED));
        if (message != null && !message.isBlank()) {
            sendLine(Component.literal("Error: " + message).withStyle(ChatFormatting.GRAY));
        }
    }
}
