package dev.stardust.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.stardust.discordchat.DiscordChatHandler;
import dev.stardust.discordchat.DiscordWebSocketServer;
import dev.stardust.modules.DiscordChatIntegration;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.command.CommandSource.suggestMatching;

public class DiscordChatCommand extends Command {
    private static final SuggestionProvider<CommandSource> AUTOMATION_SUGGESTIONS = (context, builder) -> {
        DiscordWebSocketServer server = DiscordWebSocketServer.getInstance();
        if (server != null && server.isRunning()) {
            server.requestAutomationsList();
            return suggestMatching(server.getCachedAutomationNames(), builder);
        }
        return builder.buildFuture();
    };

    public DiscordChatCommand() {
        super("discordchat", "Discord Chat Integration controls", "dcchat", "dci");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            showStatus();
            return SINGLE_SUCCESS;
        });

        builder.then(literal("status")
            .executes(context -> {
                showStatus();
                return SINGLE_SUCCESS;
            })
        );

        builder.then(literal("tutorial")
            .executes(context -> {
                DiscordChatIntegration module = getModule();
                if (module == null) {
                    error("DiscordChat module not found.");
                    return SINGLE_SUCCESS;
                }
                module.sendTutorial();
                return SINGLE_SUCCESS;
            })
        );

        builder.then(literal("port")
            .executes(context -> {
                showPort();
                return SINGLE_SUCCESS;
            })
            .then(argument("port", IntegerArgumentType.integer(1024, 65535))
                .executes(context -> {
                    setPort(IntegerArgumentType.getInteger(context, "port"));
                    return SINGLE_SUCCESS;
                })
            )
        );

        builder.then(literal("reconnect")
            .executes(context -> {
                reconnect();
                return SINGLE_SUCCESS;
            })
        );

        builder.then(literal("disconnect")
            .executes(context -> {
                disconnect();
                return SINGLE_SUCCESS;
            })
        );

        builder.then(literal("ticktest")
            .executes(context -> {
                showTickTest();
                return SINGLE_SUCCESS;
            })
        );

        builder.then(literal("run")
            .executes(context -> {
                listAutomations();
                return SINGLE_SUCCESS;
            })
            .then(argument("automation", StringArgumentType.greedyString())
                .suggests(AUTOMATION_SUGGESTIONS)
                .executes(context -> {
                    runAutomation(StringArgumentType.getString(context, "automation"));
                    return SINGLE_SUCCESS;
                })
            )
        );

        builder.then(literal("stop")
            .executes(context -> {
                stopAutomations();
                return SINGLE_SUCCESS;
            })
        );
    }

    private DiscordChatIntegration getModule() {
        return Modules.get().get(DiscordChatIntegration.class);
    }

    private void showStatus() {
        DiscordChatIntegration module = getModule();
        if (module == null) {
            error("DiscordChat module not found.");
            return;
        }
        module.showStatus();
    }

    private void showPort() {
        DiscordChatIntegration module = getModule();
        if (module == null) {
            error("DiscordChat module not found.");
            return;
        }

        sendLine(Text.literal("Current WebSocket port: " + module.getPort()));
    }

    private void setPort(int port) {
        DiscordChatIntegration module = getModule();
        if (module == null) {
            error("DiscordChat module not found.");
            return;
        }

        int current = module.getPort();
        if (current == port) {
            sendLine(Text.literal("Port is already set to " + port + "."));
            return;
        }

        module.setPort(port);
        sendLine(Text.literal("Port changed from " + current + " to " + port + "."));
    }

    private void reconnect() {
        DiscordChatIntegration module = getModule();
        if (module == null) {
            error("DiscordChat module not found.");
            return;
        }

        if (!module.isActive()) {
            module.enable();
            sendLine(Text.literal("Module enabled. Starting server on port " + module.getPort() + "."));
            return;
        }

        sendLine(Text.literal("Restarting WebSocket server..."));
        module.restartServer();
    }

    private void disconnect() {
        DiscordChatIntegration module = getModule();
        if (module == null) {
            error("DiscordChat module not found.");
            return;
        }

        if (!module.isActive()) {
            sendLine(Text.literal("Module is already disabled."));
            return;
        }

        module.disable();
        sendLine(Text.literal("Discord chat bridge stopped. Enable the module to reconnect."));
    }

    private void showTickTest() {
        DiscordChatIntegration module = getModule();
        if (module == null) {
            error("DiscordChat module not found.");
            return;
        }

        if (!module.isActive()) {
            sendLine(Text.literal("Module is disabled. Enable it to test tick sync."));
            return;
        }

        if (mc == null || mc.world == null) {
            sendLine(Text.literal("Not in a world. Join a server to test tick sync."));
            return;
        }

        long serverTick = mc.world.getTime();
        long clientTimeMs = System.currentTimeMillis();

        String playerName = "Unknown";
        if (mc.player != null) {
            try {
                playerName = mc.player.getName().getString();
            } catch (Exception ignored) {
            }
        }

        DiscordChatHandler handler = module.getChatHandler();
        String syncGroup = handler != null ? handler.getLastSyncGroup() : "none";
        long[] execInfo = handler != null ? handler.getLastExecutionInfo() : null;

        StringBuilder message = new StringBuilder();
        message.append("§6=== Tick Test Result ===§r\n");
        message.append(String.format("§7Player: §f%s§r\n", playerName));
        message.append(String.format("§7Server Tick: §f%d§r\n", serverTick));
        message.append(String.format("§7Client Time: §f%d§r ms\n", clientTimeMs));
        message.append(String.format("§7Sync Group: §f%s§r\n", syncGroup != null ? syncGroup : "none"));

        if (execInfo != null) {
            long targetTick = execInfo[0];
            long execTick = execInfo[1];
            long receiveTime = execInfo[2];
            long execTime = execInfo[3];

            message.append("§6--- Last Sync Execution ---§r\n");
            message.append(String.format("§7Target Tick: §f%d§r\n", targetTick));
            message.append(String.format("§7Exec Tick: §f%d§r\n", execTick));
            message.append(String.format("§7Receive Time: §f%d§r ms\n", receiveTime));
            message.append(String.format("§7Exec Time: §f%d§r ms\n", execTime));
            message.append(String.format("§7Waited: §f%d§r ms", execTime - receiveTime));
        }

        sendLine(Text.literal(message.toString()));
    }

    private void runAutomation(String automationName) {
        DiscordWebSocketServer server = DiscordWebSocketServer.getInstance();
        if (server == null || !server.isRunning()) {
            sendLine(Text.literal("WebSocket server is not running. Use /discordchat reconnect."));
            return;
        }

        if (server.getConnectionCount() == 0) {
            sendLine(Text.literal("No Discord clients connected."));
            return;
        }

        sendLine(Text.literal("Running automation: " + automationName + "..."));
        server.runAutomation(automationName);

        new Thread(() -> {
            try {
                Thread.sleep(500);
                String result = server.getAndClearAutomationResult();
                if (result != null && mc != null && mc.player != null) {
                    mc.execute(() -> sendLine(Text.literal(result)));
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }, "Automation-Result-Wait").start();
    }

    private void stopAutomations() {
        DiscordWebSocketServer server = DiscordWebSocketServer.getInstance();
        if (server == null || !server.isRunning()) {
            sendLine(Text.literal("WebSocket server is not running."));
            return;
        }

        if (server.getConnectionCount() == 0) {
            sendLine(Text.literal("No Discord clients connected."));
            return;
        }

        sendLine(Text.literal("Stopping automations..."));
        server.stopAutomations();

        new Thread(() -> {
            try {
                Thread.sleep(300);
                String result = server.getAndClearAutomationResult();
                if (result != null && mc != null && mc.player != null) {
                    mc.execute(() -> sendLine(Text.literal(result)));
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }, "Automation-Stop-Wait").start();
    }

    private void listAutomations() {
        DiscordWebSocketServer server = DiscordWebSocketServer.getInstance();
        if (server == null || !server.isRunning()) {
            sendLine(Text.literal("WebSocket server is not running."));
            return;
        }

        if (server.getConnectionCount() == 0) {
            sendLine(Text.literal("No Discord clients connected."));
            return;
        }

        server.requestAutomationsList();

        new Thread(() -> {
            try {
                Thread.sleep(300);
                java.util.List<String> names = server.getCachedAutomationNames();
                if (mc != null && mc.player != null) {
                    mc.execute(() -> {
                        if (names.isEmpty()) {
                            sendLine(Text.literal("No automations configured in Discord plugin."));
                        } else {
                            StringBuilder sb = new StringBuilder();
                            sb.append("§6=== Available Automations ===§r\n");
                            for (String name : names) {
                                sb.append("§7• §f").append(name).append("§r\n");
                            }
                            sb.append("§7Use §f/discordchat run <name>§7 to run an automation.");
                            sendLine(Text.literal(sb.toString()));
                        }
                    });
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }, "Automations-List-Wait").start();
    }

    private void sendLine(Text text) {
        ChatUtils.sendMsg(0, "DiscordChat", Formatting.AQUA, text);
    }
}
