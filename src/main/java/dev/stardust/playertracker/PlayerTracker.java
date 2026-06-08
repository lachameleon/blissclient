package dev.stardust.playertracker;

import dev.stardust.modules.BlissChat;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PlayerTracker extends System<PlayerTracker> {
    private static final long MAX_TICK_DELTA_MS = 2500;
    private static final long AUTOSAVE_INTERVAL_MS = 60000;
    private static final long BACKEND_REPORT_INTERVAL_MS = 15000;

    private final Map<UUID, PlayerStats> players = new HashMap<>();
    private final Map<String, UUID> names = new HashMap<>();
    private final Map<UUID, ActiveSighting> activeSightings = new HashMap<>();

    private long lastAutosave;

    public PlayerTracker() {
        super("player-tracker");
    }

    public static PlayerTracker get() {
        return Systems.get(PlayerTracker.class);
    }

    @EventHandler
    private void onGameJoined(GameJoinedEvent event) {
        activeSightings.clear();
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        closeAllSightings();
        save();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.level == null || mc.player == null) {
            closeAllSightings();
            return;
        }

        long now = java.lang.System.currentTimeMillis();
        String server = getServerName();
        String dimension = getDimensionName();
        Set<UUID> visible = new HashSet<>();

        for (Player player : mc.level.players()) {
            if (player == mc.player || player.getUUID().equals(mc.player.getUUID())) continue;

            UUID uuid = player.getUUID();
            visible.add(uuid);

            PlayerStats stats = getOrCreate(player, now);
            ActiveSighting active = activeSightings.get(uuid);

            if (active == null) {
                active = new ActiveSighting(now);
                activeSightings.put(uuid, active);

                stats.sightingCount++;
                increment(stats.serverSightings, server);
                increment(stats.dimensionSightings, dimension);
            }

            sample(stats, active, player, server, dimension, now);
        }

        closeMissingSightings(visible);

        if (now - lastAutosave >= AUTOSAVE_INTERVAL_MS) {
            lastAutosave = now;
            save();
        }
    }

    public PlayerStats get(String name) {
        if (name == null) return null;

        UUID uuid = names.get(normalize(name));
        if (uuid != null) return players.get(uuid);

        for (PlayerStats stats : players.values()) {
            if (stats.name.equalsIgnoreCase(name)) return stats;
        }

        return null;
    }

    public List<String> getKnownNames() {
        return players.values().stream()
            .map(stats -> stats.name)
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .toList();
    }

    public List<PlayerStats> getTop(int limit) {
        return players.values().stream()
            .sorted(Comparator.comparingLong((PlayerStats stats) -> stats.totalVisibleMs).reversed())
            .limit(limit)
            .toList();
    }

    public int count() {
        return players.size();
    }

    public boolean isVisible(UUID uuid) {
        return activeSightings.containsKey(uuid);
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        ListTag playersTag = new ListTag();

        for (PlayerStats stats : players.values()) {
            playersTag.add(stats.toTag());
        }

        tag.put("players", playersTag);
        return tag;
    }

    @Override
    public PlayerTracker fromTag(CompoundTag tag) {
        players.clear();
        names.clear();
        activeSightings.clear();

        for (Tag playerTag : tag.getListOrEmpty("players")) {
            if (!(playerTag instanceof CompoundTag compound)) continue;

            PlayerStats stats = PlayerStats.fromTag(compound);
            if (stats == null) continue;

            players.put(stats.uuid, stats);
            index(stats);
        }

        return this;
    }

    private PlayerStats getOrCreate(Player player, long now) {
        UUID uuid = player.getUUID();
        PlayerStats stats = players.get(uuid);

        if (stats == null) {
            stats = new PlayerStats(uuid, player.getName().getString(), now);
            players.put(uuid, stats);
        } else {
            stats.updateName(player.getName().getString());
        }

        index(stats);
        return stats;
    }

    private void sample(PlayerStats stats, ActiveSighting active, Player player, String server, String dimension, long now) {
        long delta = Math.max(0, now - active.lastSample);
        if (delta > MAX_TICK_DELTA_MS) delta = 50;

        stats.totalVisibleMs += delta;
        active.durationMs += delta;
        active.lastSample = now;

        stats.longestSightingMs = Math.max(stats.longestSightingMs, active.durationMs);
        stats.lastSeen = now;
        stats.lastServer = server;
        stats.lastDimension = dimension;
        stats.lastX = player.getX();
        stats.lastY = player.getY();
        stats.lastZ = player.getZ();

        double distance = player.distanceTo(mc.player);
        stats.lastDistance = distance;
        stats.minDistance = Math.min(stats.minDistance, distance);
        stats.maxDistance = Math.max(stats.maxDistance, distance);
        stats.totalDistance += distance;
        stats.distanceSamples++;

        float health = player.getHealth();
        stats.lastHealth = health;
        stats.lastMaxHealth = player.getMaxHealth();
        stats.minHealth = Math.min(stats.minHealth, health);
        stats.maxHealth = Math.max(stats.maxHealth, health);
        stats.healthSamples++;

        PlayerInfo info = mc.getConnection() == null ? null : mc.getConnection().getPlayerInfo(player.getUUID());
        if (info != null) {
            stats.lastPing = info.getLatency();
            if (info.getGameMode() != null) stats.lastGameMode = info.getGameMode().getName();
        }

        reportSighting(stats, active, server, dimension, now);
    }

    private void closeMissingSightings(Set<UUID> visible) {
        activeSightings.entrySet().removeIf(entry -> !visible.contains(entry.getKey()));
    }

    private void closeAllSightings() {
        activeSightings.clear();
    }

    private void index(PlayerStats stats) {
        names.put(normalize(stats.name), stats.uuid);
        for (String name : stats.previousNames) names.put(normalize(name), stats.uuid);
    }

    private static String normalize(String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    private static void increment(Map<String, Integer> map, String key) {
        map.merge(key, 1, Integer::sum);
    }

    private static String getServerName() {
        ServerData server = mc.getCurrentServer();
        if (server == null) return mc.hasSingleplayerServer() ? "singleplayer" : "unknown";
        if (server.isRealm()) return "realms";
        return server.ip == null || server.ip.isBlank() ? "unknown" : server.ip;
    }

    private static String getDimensionName() {
        if (mc.level == null) return "unknown";
        return mc.level.dimension().identifier().toString();
    }

    private static void reportSighting(PlayerStats stats, ActiveSighting active, String server, String dimension, long now) {
        if (mc.getCurrentServer() == null) return;
        if (active.lastReport != 0 && now - active.lastReport < BACKEND_REPORT_INTERVAL_MS) return;

        active.lastReport = now;

        BlissChat blissChat = Modules.get().get(BlissChat.class);
        if (blissChat == null) return;

        blissChat.reportSeenPlayer(new SightingReport(
            stats.name,
            stats.uuid,
            server,
            dimension,
            stats.lastX,
            stats.lastY,
            stats.lastZ,
            stats.lastDistance,
            stats.lastHealth,
            stats.lastMaxHealth,
            stats.lastPing,
            stats.lastGameMode,
            stats.sightingCount,
            stats.totalVisibleMs,
            active.durationMs,
            now
        ));
    }

    private static ListTag namesToTag(List<String> names) {
        ListTag tag = new ListTag();
        for (String name : names) {
            CompoundTag nameTag = new CompoundTag();
            nameTag.putString("name", name);
            tag.add(nameTag);
        }
        return tag;
    }

    private static List<String> namesFromTag(CompoundTag tag) {
        List<String> names = new ArrayList<>();
        for (Tag item : tag.getListOrEmpty("previousNames")) {
            if (item instanceof CompoundTag compound) {
                String name = compound.getStringOr("name", "");
                if (!name.isBlank()) names.add(name);
            }
        }
        return names;
    }

    private static ListTag countsToTag(Map<String, Integer> counts) {
        ListTag tag = new ListTag();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            CompoundTag countTag = new CompoundTag();
            countTag.putString("key", entry.getKey());
            countTag.putInt("value", entry.getValue());
            tag.add(countTag);
        }
        return tag;
    }

    private static Map<String, Integer> countsFromTag(CompoundTag tag, String key) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Tag item : tag.getListOrEmpty(key)) {
            if (item instanceof CompoundTag compound) {
                String name = compound.getStringOr("key", "");
                if (!name.isBlank()) counts.put(name, compound.getIntOr("value", 0));
            }
        }
        return counts;
    }

    private static String topKey(Map<String, Integer> counts) {
        return counts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(entry -> "%s (%d)".formatted(entry.getKey(), entry.getValue()))
            .orElse("none");
    }

    private static class ActiveSighting {
        private long lastSample;
        private long durationMs;
        private long lastReport;

        private ActiveSighting(long now) {
            this.lastSample = now;
        }
    }

    public record SightingReport(
        String username,
        UUID uuid,
        String serverAddress,
        String dimension,
        double x,
        double y,
        double z,
        double distance,
        float health,
        float maxHealth,
        int ping,
        String gameMode,
        int sightingCount,
        long totalVisibleMs,
        long visibleMs,
        long timestamp
    ) {
    }

    public static class PlayerStats {
        public final UUID uuid;
        public String name;
        public final List<String> previousNames = new ArrayList<>();
        public long firstSeen;
        public long lastSeen;
        public int sightingCount;
        public long totalVisibleMs;
        public long longestSightingMs;
        public double minDistance = Double.MAX_VALUE;
        public double maxDistance;
        public double totalDistance;
        public long distanceSamples;
        public double lastDistance;
        public float minHealth = Float.MAX_VALUE;
        public float maxHealth;
        public float lastHealth;
        public float lastMaxHealth;
        public long healthSamples;
        public int lastPing = -1;
        public String lastGameMode = "unknown";
        public String lastServer = "unknown";
        public String lastDimension = "unknown";
        public double lastX;
        public double lastY;
        public double lastZ;
        public Map<String, Integer> serverSightings = new LinkedHashMap<>();
        public Map<String, Integer> dimensionSightings = new LinkedHashMap<>();

        private PlayerStats(UUID uuid, String name, long now) {
            this.uuid = uuid;
            this.name = name;
            this.firstSeen = now;
            this.lastSeen = now;
        }

        public double averageDistance() {
            return distanceSamples == 0 ? 0 : totalDistance / distanceSamples;
        }

        public String topServer() {
            return topKey(serverSightings);
        }

        public String topDimension() {
            return topKey(dimensionSightings);
        }

        public void updateName(String name) {
            if (name == null || name.isBlank() || this.name.equals(name)) return;
            if (!previousNames.contains(this.name)) previousNames.add(this.name);
            this.name = name;
        }

        private CompoundTag toTag() {
            CompoundTag tag = new CompoundTag();

            tag.putString("uuid", uuid.toString());
            tag.putString("name", name);
            tag.put("previousNames", namesToTag(previousNames));
            tag.putLong("firstSeen", firstSeen);
            tag.putLong("lastSeen", lastSeen);
            tag.putInt("sightingCount", sightingCount);
            tag.putLong("totalVisibleMs", totalVisibleMs);
            tag.putLong("longestSightingMs", longestSightingMs);
            tag.putDouble("minDistance", minDistance);
            tag.putDouble("maxDistance", maxDistance);
            tag.putDouble("totalDistance", totalDistance);
            tag.putLong("distanceSamples", distanceSamples);
            tag.putDouble("lastDistance", lastDistance);
            tag.putFloat("minHealth", minHealth);
            tag.putFloat("maxHealth", maxHealth);
            tag.putFloat("lastHealth", lastHealth);
            tag.putFloat("lastMaxHealth", lastMaxHealth);
            tag.putLong("healthSamples", healthSamples);
            tag.putInt("lastPing", lastPing);
            tag.putString("lastGameMode", lastGameMode);
            tag.putString("lastServer", lastServer);
            tag.putString("lastDimension", lastDimension);
            tag.putDouble("lastX", lastX);
            tag.putDouble("lastY", lastY);
            tag.putDouble("lastZ", lastZ);
            tag.put("serverSightings", countsToTag(serverSightings));
            tag.put("dimensionSightings", countsToTag(dimensionSightings));

            return tag;
        }

        private static PlayerStats fromTag(CompoundTag tag) {
            UUID uuid;
            try {
                uuid = UUID.fromString(tag.getStringOr("uuid", ""));
            } catch (IllegalArgumentException ignored) {
                return null;
            }

            String name = tag.getStringOr("name", "");
            if (name.isBlank()) return null;

            PlayerStats stats = new PlayerStats(uuid, name, tag.getLongOr("firstSeen", 0));
            stats.previousNames.addAll(namesFromTag(tag));
            stats.lastSeen = tag.getLongOr("lastSeen", stats.firstSeen);
            stats.sightingCount = tag.getIntOr("sightingCount", 0);
            stats.totalVisibleMs = tag.getLongOr("totalVisibleMs", 0);
            stats.longestSightingMs = tag.getLongOr("longestSightingMs", 0);
            stats.minDistance = tag.getDoubleOr("minDistance", Double.MAX_VALUE);
            stats.maxDistance = tag.getDoubleOr("maxDistance", 0);
            stats.totalDistance = tag.getDoubleOr("totalDistance", 0);
            stats.distanceSamples = tag.getLongOr("distanceSamples", 0);
            stats.lastDistance = tag.getDoubleOr("lastDistance", 0);
            stats.minHealth = tag.getFloatOr("minHealth", Float.MAX_VALUE);
            stats.maxHealth = tag.getFloatOr("maxHealth", 0);
            stats.lastHealth = tag.getFloatOr("lastHealth", 0);
            stats.lastMaxHealth = tag.getFloatOr("lastMaxHealth", 0);
            stats.healthSamples = tag.getLongOr("healthSamples", 0);
            stats.lastPing = tag.getIntOr("lastPing", -1);
            stats.lastGameMode = tag.getStringOr("lastGameMode", "unknown");
            stats.lastServer = tag.getStringOr("lastServer", "unknown");
            stats.lastDimension = tag.getStringOr("lastDimension", "unknown");
            stats.lastX = tag.getDoubleOr("lastX", 0);
            stats.lastY = tag.getDoubleOr("lastY", 0);
            stats.lastZ = tag.getDoubleOr("lastZ", 0);
            stats.serverSightings = countsFromTag(tag, "serverSightings");
            stats.dimensionSightings = countsFromTag(tag, "dimensionSightings");

            return stats;
        }
    }
}
