package dev.stardust.discordchat;

import dev.stardust.Stardust;
import net.minecraft.client.MinecraftClient;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class DiscordChatHandler {
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final AtomicBoolean isSendingFromDiscord = new AtomicBoolean(false);
    private final ConcurrentHashMap<String, Boolean> processedMessageIds = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> sentFromDiscord = new ConcurrentHashMap<>();
    private static final long SENT_FROM_DISCORD_WINDOW_MS = 3000;
    private final ConcurrentLinkedQueue<DiscordWebSocketServer.ChatMessage> tickSyncQueue = new ConcurrentLinkedQueue<>();

    private volatile String lastSyncGroup = "none";
    private volatile long lastTargetTick = -1;
    private volatile long lastExecutionTick = -1;
    private volatile long lastReceiveTime = -1;
    private volatile long lastExecutionTime = -1;

    private final ExecutorService messageProcessor = Executors.newFixedThreadPool(2, r -> {
        Thread t = new Thread(r, "Discord-Message-Processor");
        t.setDaemon(true);
        return t;
    });

    private final ExecutorService discordForwardExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "Discord-Forward-Processor");
        t.setDaemon(true);
        return t;
    });

    public void setLastSyncGroup(String syncGroup) {
        if (syncGroup != null && !syncGroup.isEmpty()) {
            this.lastSyncGroup = syncGroup;
        }
    }

    public String getLastSyncGroup() {
        return lastSyncGroup;
    }

    public long[] getLastExecutionInfo() {
        if (lastExecutionTime < 0) return null;
        return new long[]{lastTargetTick, lastExecutionTick, lastReceiveTime, lastExecutionTime};
    }

    public void handleDiscordMessage(DiscordWebSocketServer.ChatMessage message) {
        if (!running.get()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.player.networkHandler == null) return;

        messageProcessor.execute(() -> {
            try {
                if (message.messageId != null && !message.messageId.isEmpty()) {
                    if (processedMessageIds.putIfAbsent(message.messageId, Boolean.TRUE) != null) return;
                    if (processedMessageIds.size() > 2000) processedMessageIds.clear();
                }

                if (message.syncGroup != null && !message.syncGroup.isEmpty()) {
                    lastSyncGroup = message.syncGroup;
                }

                if (message.targetTick >= 0) {
                    lastReceiveTime = System.currentTimeMillis();
                    lastTargetTick = message.targetTick;
                    tickSyncQueue.add(message);
                    return;
                }

                if (message.tickSync) {
                    tickSyncQueue.add(message);
                    return;
                }

                executeMessageImmediately(message);
            } catch (Exception e) {
                Stardust.LOG.error("Error processing Discord message: {}", e.getMessage());
            }
        });
    }

    public void onTick(long currentTick) {
        if (!running.get()) return;
        if (tickSyncQueue.isEmpty()) return;

        java.util.List<DiscordWebSocketServer.ChatMessage> readyMessages = new java.util.ArrayList<>();
        java.util.List<DiscordWebSocketServer.ChatMessage> pendingMessages = new java.util.ArrayList<>();

        DiscordWebSocketServer.ChatMessage message;
        while ((message = tickSyncQueue.poll()) != null) {
            if (message.targetTick < 0 || currentTick >= message.targetTick) {
                readyMessages.add(message);
            } else {
                pendingMessages.add(message);
            }
        }

        tickSyncQueue.addAll(pendingMessages);

        for (DiscordWebSocketServer.ChatMessage readyMsg : readyMessages) {
            if (readyMsg.targetTick >= 0) {
                lastExecutionTick = currentTick;
                lastExecutionTime = System.currentTimeMillis();
            }
            executeMessageImmediately(readyMsg);
        }
    }

    private void executeMessageImmediately(DiscordWebSocketServer.ChatMessage message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.player.networkHandler == null) return;

        if (!isSendingFromDiscord.compareAndSet(false, true)) {
            Stardust.LOG.debug("Concurrent message execution");
        }

        try {
            String playerName = getPlayerName(client);
            if (playerName != null) {
                sentFromDiscord.put("<" + playerName + "> " + message.content, System.currentTimeMillis());
            }
            sentFromDiscord.put(message.content, System.currentTimeMillis());

            if (sentFromDiscord.size() > 100) {
                long cutoff = System.currentTimeMillis() - SENT_FROM_DISCORD_WINDOW_MS;
                sentFromDiscord.entrySet().removeIf(entry -> entry.getValue() < cutoff);
            }

            client.execute(() -> {
                try {
                    if (message.content.startsWith("/")) {
                        client.player.networkHandler.sendChatCommand(message.content.substring(1));
                    } else {
                        client.player.networkHandler.sendChatMessage(message.content);
                    }
                } catch (Exception e) {
                    Stardust.LOG.error("Error sending to chat: {}", e.getMessage());
                } finally {
                    messageProcessor.execute(() -> {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        isSendingFromDiscord.set(false);
                    });
                }
            });
        } catch (Exception e) {
            Stardust.LOG.error("Error executing message: {}", e.getMessage());
            isSendingFromDiscord.set(false);
        }
    }

    public void handleIncomingMinecraftMessage(String playerName, String message) {
        if (!running.get()) return;

        long now = System.currentTimeMillis();

        Long sentTime = sentFromDiscord.get(message);
        if (sentTime != null && (now - sentTime) < SENT_FROM_DISCORD_WINDOW_MS) {
            sentFromDiscord.remove(message);
            return;
        }

        if (!sentFromDiscord.isEmpty()) {
            long cutoff = now - SENT_FROM_DISCORD_WINDOW_MS;
            for (var entry : sentFromDiscord.entrySet()) {
                if (entry.getValue() < cutoff) {
                    sentFromDiscord.remove(entry.getKey());
                } else if (message.contains(entry.getKey()) || entry.getKey().equals(message)) {
                    sentFromDiscord.remove(entry.getKey());
                    return;
                }
            }
        }

        sendToDiscordForLogging(playerName, message);
    }

    private void sendToDiscordForLogging(String playerName, String message) {
        discordForwardExecutor.execute(() -> {
            DiscordWebSocketServer server = DiscordWebSocketServer.getInstance();
            if (server != null && server.isRunning() && server.getConnectionCount() > 0) {
                server.broadcastMinecraftMessage(playerName, message);
            }
        });
    }

    private String getPlayerName(MinecraftClient client) {
        if (client == null || client.player == null) return null;

        try {
            String name = client.player.getName().getString();
            if (name != null && !name.isEmpty() && !name.equals("Player")) return name;
        } catch (Exception ignored) {
        }

        try {
            String name = client.player.getGameProfile().name();
            if (name != null && !name.isEmpty() && !name.equals("Player")) return name;
        } catch (Exception ignored) {
        }

        try {
            if (client.getSession() != null) {
                String name = client.getSession().getUsername();
                if (name != null && !name.isEmpty() && !name.equals("Player")) return name;
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    public void shutdown() {
        running.set(false);
        messageProcessor.shutdown();
        discordForwardExecutor.shutdown();
        try {
            if (!messageProcessor.awaitTermination(2, TimeUnit.SECONDS)) messageProcessor.shutdownNow();
            if (!discordForwardExecutor.awaitTermination(2, TimeUnit.SECONDS)) discordForwardExecutor.shutdownNow();
        } catch (InterruptedException e) {
            messageProcessor.shutdownNow();
            discordForwardExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
