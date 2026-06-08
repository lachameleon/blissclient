package dev.stardust.playertracker;

import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.TextStyle;
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
    private static final long MIN_ENCOUNTER_MS = 60000;
    private static final long MAX_TICK_DELTA_MS = 2500;
    private static final long AUTOSAVE_INTERVAL_MS = 60000;
    private static final long HEATMAP_SAMPLE_INTERVAL_MS = 2000;
    private static final long BACKEND_REPORT_INTERVAL_MS = 30000;
    private static final double AGGRESSION_DISTANCE = 16;
    public static final int HEATMAP_BIN_SIZE = 128;

    private final Map<UUID, PlayerStats> players = new HashMap<>();
    private final Map<String, UUID> names = new HashMap<>();
    private final Map<UUID, ActiveSighting> activeSightings = new HashMap<>();

    private long lastAutosave;
    private float lastSelfHealth = -1;

    public PlayerTracker() {
        super("player-tracker");
    }

    public static PlayerTracker get() {
        return Systems.get(PlayerTracker.class);
    }

    @EventHandler
    private void onGameJoined(GameJoinedEvent event) {
        activeSightings.clear();
        lastSelfHealth = -1;
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
            lastSelfHealth = -1;
            return;
        }

        long now = java.lang.System.currentTimeMillis();
        String server = getServerName();
        String dimension = getDimensionName();
        Set<UUID> visible = new HashSet<>();
        float selfHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        float selfDamage = 0;
        if (lastSelfHealth >= 0 && selfHealth < lastSelfHealth - 0.05f) {
            selfDamage = lastSelfHealth - selfHealth;
        }
        lastSelfHealth = selfHealth;

        for (Player player : mc.level.players()) {
            if (player == mc.player || player.getUUID().equals(mc.player.getUUID())) continue;

            UUID uuid = player.getUUID();
            visible.add(uuid);

            PlayerStats stats = getOrCreate(player, now);
            ActiveSighting active = activeSightings.get(uuid);

            if (active == null) {
                active = new ActiveSighting(now);
                activeSightings.put(uuid, active);
            }

            sample(stats, active, player, server, dimension, now);
        }

        if (selfDamage > 0) markLikelyAggressor(visible, selfDamage, now);
        closeMissingSightings(visible);

        if (now - lastAutosave >= AUTOSAVE_INTERVAL_MS) {
            lastAutosave = now;
            save();
        }
    }

    public PlayerStats get(String name) {
        if (name == null) return null;

        UUID uuid = names.get(normalize(name));
        if (uuid != null) {
            PlayerStats stats = players.get(uuid);
            return stats != null && stats.hasValidEncounters() ? stats : null;
        }

        for (PlayerStats stats : players.values()) {
            if (stats.hasValidEncounters() && stats.name.equalsIgnoreCase(name)) return stats;
        }

        return null;
    }

    public List<String> getKnownNames() {
        return players.values().stream()
            .filter(PlayerStats::hasValidEncounters)
            .map(stats -> stats.name)
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .toList();
    }

    public List<PlayerStats> getTop(int limit) {
        return players.values().stream()
            .filter(PlayerStats::hasValidEncounters)
            .sorted(Comparator.comparingLong((PlayerStats stats) -> stats.totalVisibleMs).reversed())
            .limit(limit)
            .toList();
    }

    public List<HeatmapCell> getHeatmap(int limit) {
        Map<String, Integer> counts = new HashMap<>();
        for (PlayerStats stats : players.values()) {
            if (!stats.hasValidEncounters()) continue;
            for (Map.Entry<String, Integer> entry : stats.coordinateHeatmap.entrySet()) {
                counts.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }

        return heatmapCells(counts, limit);
    }

    public int count() {
        return (int) players.values().stream().filter(PlayerStats::hasValidEncounters).count();
    }

    public boolean isVisible(UUID uuid) {
        return activeSightings.containsKey(uuid);
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        ListTag playersTag = new ListTag();

        for (PlayerStats stats : players.values()) {
            if (!stats.hasValidEncounters()) continue;
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

        active.durationMs += delta;
        active.lastSample = now;

        stats.lastSeen = now;
        stats.lastServer = server;
        stats.lastDimension = dimension;
        stats.lastX = player.getX();
        stats.lastY = player.getY();
        stats.lastZ = player.getZ();
        stats.lastDistance = player.distanceTo(mc.player);
        stats.lastHealth = player.getHealth();
        stats.lastMaxHealth = player.getMaxHealth();

        PlayerInfo info = mc.getConnection() == null ? null : mc.getConnection().getPlayerInfo(player.getUUID());
        if (info != null) {
            stats.lastPing = info.getLatency();
            if (info.getGameMode() != null) stats.lastGameMode = info.getGameMode().getName();
        }

        if (!active.counted) {
            if (active.durationMs < MIN_ENCOUNTER_MS) return;
            startEncounter(stats, active, server, dimension, now);
            delta = active.durationMs;
        }

        stats.totalVisibleMs += delta;
        stats.longestSightingMs = Math.max(stats.longestSightingMs, active.durationMs);
        if (stats.coordinateSamples == 0) {
            stats.firstX = stats.lastX;
            stats.firstY = stats.lastY;
            stats.firstZ = stats.lastZ;
            stats.minX = stats.maxX = stats.lastX;
            stats.minY = stats.maxY = stats.lastY;
            stats.minZ = stats.maxZ = stats.lastZ;
        } else {
            stats.minX = Math.min(stats.minX, stats.lastX);
            stats.maxX = Math.max(stats.maxX, stats.lastX);
            stats.minY = Math.min(stats.minY, stats.lastY);
            stats.maxY = Math.max(stats.maxY, stats.lastY);
            stats.minZ = Math.min(stats.minZ, stats.lastZ);
            stats.maxZ = Math.max(stats.maxZ, stats.lastZ);
        }
        stats.totalY += stats.lastY;
        stats.coordinateSamples++;

        if (active.hasPosition && delta > 0) {
            double moved = distance(active.lastX, active.lastY, active.lastZ, stats.lastX, stats.lastY, stats.lastZ);
            if (moved <= 1024) {
                double speed = moved * 1000.0 / delta;
                stats.lastSpeed = speed;
                stats.maxSpeed = Math.max(stats.maxSpeed, speed);
                stats.totalTravelDistance += moved;
                stats.speedSamples++;
            }
        }
        active.lastX = stats.lastX;
        active.lastY = stats.lastY;
        active.lastZ = stats.lastZ;
        active.hasPosition = true;

        if (stats.lastDistance < stats.minDistance) {
            stats.minDistance = stats.lastDistance;
            stats.closestX = stats.lastX;
            stats.closestY = stats.lastY;
            stats.closestZ = stats.lastZ;
        }
        if (stats.lastDistance > stats.maxDistance) {
            stats.maxDistance = stats.lastDistance;
            stats.farthestX = stats.lastX;
            stats.farthestY = stats.lastY;
            stats.farthestZ = stats.lastZ;
        }
        stats.totalDistance += stats.lastDistance;
        stats.distanceSamples++;

        stats.minHealth = Math.min(stats.minHealth, stats.lastHealth);
        stats.maxHealth = Math.max(stats.maxHealth, stats.lastHealth);
        stats.totalHealth += stats.lastHealth;
        stats.healthSamples++;

        if (info != null) {
            stats.minPing = stats.minPing < 0 ? stats.lastPing : Math.min(stats.minPing, stats.lastPing);
            stats.maxPing = Math.max(stats.maxPing, stats.lastPing);
            stats.totalPing += stats.lastPing;
            stats.pingSamples++;
            if (info.getGameMode() != null) {
                stats.lastGameMode = info.getGameMode().getName();
                increment(stats.gameModeSamples, stats.lastGameMode);
            }
        }

        if (active.lastHeatmapSample == 0 || now - active.lastHeatmapSample >= HEATMAP_SAMPLE_INTERVAL_MS) {
            increment(stats.coordinateHeatmap, heatmapKey(dimension, stats.lastX, stats.lastZ));
            active.lastHeatmapSample = now;
            stats.heatmapSamples++;
        }

        reportSighting(stats, active, server, dimension, now);
    }

    private void reportSighting(PlayerStats stats, ActiveSighting active, String server, String dimension, long now) {
        if (active.lastReport > 0 && now - active.lastReport < BACKEND_REPORT_INTERVAL_MS) return;
        if (mc.getUser() == null || mc.getUser().getName() == null || mc.getUser().getProfileId() == null) return;

        active.lastReport = now;
        StatsBackendClient.report(new SightingReport(
            mc.getUser().getName(),
            mc.getUser().getProfileId().toString(),
            server,
            stats.name,
            stats.uuid.toString(),
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
            stats.averageDistance(),
            stats.lastSpeed,
            heatmapBase(stats.lastX),
            heatmapBase(stats.lastZ)
        ));
    }

    private void startEncounter(PlayerStats stats, ActiveSighting active, String server, String dimension, long now) {
        active.counted = true;
        stats.sightingCount++;
        increment(stats.serverSightings, server);
        increment(stats.dimensionSightings, dimension);
        increment(stats.hourSightings, hourKey(now));
        increment(stats.daySightings, dayKey(now));
    }

    private void markLikelyAggressor(Set<UUID> visible, double damage, long now) {
        UUID closestUuid = null;
        PlayerStats closestStats = null;
        double closestDistance = AGGRESSION_DISTANCE;

        for (UUID uuid : visible) {
            PlayerStats stats = players.get(uuid);
            ActiveSighting active = activeSightings.get(uuid);
            if (stats == null || active == null) continue;
            if (stats.lastDistance <= closestDistance) {
                closestDistance = stats.lastDistance;
                closestUuid = uuid;
                closestStats = stats;
            }
        }

        if (closestUuid == null || closestStats == null) return;

        ActiveSighting active = activeSightings.get(closestUuid);
        active.aggressive = true;
        active.damageEvents++;
        active.damageTaken += damage;
        active.lastAggressiveAt = now;
        active.closestAggressionDistance = Math.min(active.closestAggressionDistance, closestDistance);

        if (active.counted) {
            closestStats.openEncounterDamageEvents++;
            closestStats.openEncounterDamageTaken += damage;
            closestStats.lastAggressiveAt = now;
            closestStats.closestAggressionDistance = Math.min(closestStats.closestAggressionDistance, closestDistance);
        }
    }

    private void closeMissingSightings(Set<UUID> visible) {
        activeSightings.entrySet().removeIf(entry -> {
            if (visible.contains(entry.getKey())) return false;
            finishSighting(players.get(entry.getKey()), entry.getValue());
            return true;
        });
    }

    private void closeAllSightings() {
        for (Map.Entry<UUID, ActiveSighting> entry : activeSightings.entrySet()) {
            finishSighting(players.get(entry.getKey()), entry.getValue());
        }
        activeSightings.clear();
    }

    private void finishSighting(PlayerStats stats, ActiveSighting active) {
        if (stats == null || active == null || active.durationMs <= 0) return;
        if (!active.counted || active.durationMs < MIN_ENCOUNTER_MS) {
            stats.shortSightingsIgnored++;
            return;
        }

        stats.completedSightings++;
        stats.lastSightingMs = active.durationMs;
        stats.shortestSightingMs = Math.min(stats.shortestSightingMs, active.durationMs);
        stats.longestSightingMs = Math.max(stats.longestSightingMs, active.durationMs);
        if (active.aggressive) stats.aggressiveEncounters++;
        stats.damageEvents += active.damageEvents;
        stats.totalDamageTakenNearPlayer += active.damageTaken;
        stats.maxEncounterDamageTaken = Math.max(stats.maxEncounterDamageTaken, active.damageTaken);
        if (active.lastAggressiveAt > 0) stats.lastAggressiveAt = active.lastAggressiveAt;
        stats.closestAggressionDistance = Math.min(stats.closestAggressionDistance, active.closestAggressionDistance);
        stats.openEncounterDamageEvents = 0;
        stats.openEncounterDamageTaken = 0;
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

    private static String hourKey(long timestamp) {
        int hour = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).getHour();
        return "%02d:00".formatted(hour);
    }

    private static String dayKey(long timestamp) {
        DayOfWeek day = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).getDayOfWeek();
        return day.getDisplayName(TextStyle.SHORT, Locale.ROOT);
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

    private static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private static int heatmapBase(double coordinate) {
        return (int) Math.floor(coordinate / HEATMAP_BIN_SIZE) * HEATMAP_BIN_SIZE;
    }

    private static String heatmapKey(String dimension, double x, double z) {
        return "%s|%d|%d".formatted(dimension, heatmapBase(x), heatmapBase(z));
    }

    private static HeatmapCell heatmapCell(String key, int count) {
        String[] parts = key.split("\\|");
        if (parts.length < 3) return new HeatmapCell("unknown", 0, 0, count);
        try {
            return new HeatmapCell(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), count);
        } catch (NumberFormatException ignored) {
            return new HeatmapCell("unknown", 0, 0, count);
        }
    }

    private static List<HeatmapCell> heatmapCells(Map<String, Integer> counts, int limit) {
        return counts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(limit)
            .map(entry -> heatmapCell(entry.getKey(), entry.getValue()))
            .toList();
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

    private static List<CountEntry> topEntries(Map<String, Integer> counts, int limit) {
        return counts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(limit)
            .map(entry -> new CountEntry(entry.getKey(), entry.getValue()))
            .toList();
    }

    private static class ActiveSighting {
        private boolean counted;
        private long lastSample;
        private long durationMs;
        private long lastHeatmapSample;
        private boolean hasPosition;
        private double lastX;
        private double lastY;
        private double lastZ;
        private boolean aggressive;
        private int damageEvents;
        private double damageTaken;
        private double closestAggressionDistance = Double.MAX_VALUE;
        private long lastAggressiveAt;
        private long lastReport;

        private ActiveSighting(long now) {
            this.lastSample = now;
        }
    }

    public record HeatmapCell(String dimension, int x, int z, int count) {
        public int centerX() {
            return x + HEATMAP_BIN_SIZE / 2;
        }

        public int centerZ() {
            return z + HEATMAP_BIN_SIZE / 2;
        }
    }

    public record CountEntry(String key, int count) {
    }

    public record SightingReport(
        String reporterUsername,
        String reporterUuid,
        String reporterServerAddress,
        String seenUsername,
        String seenUuid,
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
        double averageDistance,
        double speed,
        int heatmapX,
        int heatmapZ
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
        public double firstX;
        public double firstY;
        public double firstZ;
        public double lastX;
        public double lastY;
        public double lastZ;
        public double minX;
        public double maxX;
        public double minY;
        public double maxY;
        public double minZ;
        public double maxZ;
        public double closestX;
        public double closestY;
        public double closestZ;
        public double farthestX;
        public double farthestY;
        public double farthestZ;
        public double totalY;
        public long coordinateSamples;
        public double totalTravelDistance;
        public double lastSpeed;
        public double maxSpeed;
        public long speedSamples;
        public double totalHealth;
        public int minPing = -1;
        public int maxPing = -1;
        public long totalPing;
        public long pingSamples;
        public long lastSightingMs;
        public long shortestSightingMs = Long.MAX_VALUE;
        public int completedSightings;
        public int shortSightingsIgnored;
        public int aggressiveEncounters;
        public int damageEvents;
        public int openEncounterDamageEvents;
        public double totalDamageTakenNearPlayer;
        public double openEncounterDamageTaken;
        public double maxEncounterDamageTaken;
        public double closestAggressionDistance = Double.MAX_VALUE;
        public long lastAggressiveAt;
        public long heatmapSamples;
        public Map<String, Integer> serverSightings = new LinkedHashMap<>();
        public Map<String, Integer> dimensionSightings = new LinkedHashMap<>();
        public Map<String, Integer> gameModeSamples = new LinkedHashMap<>();
        public Map<String, Integer> hourSightings = new LinkedHashMap<>();
        public Map<String, Integer> daySightings = new LinkedHashMap<>();
        public Map<String, Integer> coordinateHeatmap = new LinkedHashMap<>();

        private PlayerStats(UUID uuid, String name, long now) {
            this.uuid = uuid;
            this.name = name;
            this.firstSeen = now;
            this.lastSeen = now;
        }

        public double averageDistance() {
            return distanceSamples == 0 ? 0 : totalDistance / distanceSamples;
        }

        public double averageHealth() {
            return healthSamples == 0 ? 0 : totalHealth / healthSamples;
        }

        public double averagePing() {
            return pingSamples == 0 ? 0 : (double) totalPing / pingSamples;
        }

        public double averageY() {
            return coordinateSamples == 0 ? 0 : totalY / coordinateSamples;
        }

        public double averageSpeed() {
            if (totalVisibleMs <= 0) return 0;
            return totalTravelDistance / (totalVisibleMs / 1000.0);
        }

        public boolean hasValidEncounters() {
            return sightingCount > 0;
        }

        public long averageSightingMs() {
            return sightingCount == 0 ? 0 : totalVisibleMs / sightingCount;
        }

        public long shortestSightingMs() {
            return shortestSightingMs == Long.MAX_VALUE ? 0 : shortestSightingMs;
        }

        public List<HeatmapCell> topHeatmap(int limit) {
            return heatmapCells(coordinateHeatmap, limit);
        }

        public double aggressivenessChance() {
            if (completedSightings == 0) return 0;
            return aggressiveEncounters * 100.0 / completedSightings;
        }

        public int totalDamageEvents() {
            return damageEvents + openEncounterDamageEvents;
        }

        public double totalObservedDamage() {
            return totalDamageTakenNearPlayer + openEncounterDamageTaken;
        }

        public double closestAggressionDistance() {
            return closestAggressionDistance == Double.MAX_VALUE ? 0 : closestAggressionDistance;
        }

        public List<CountEntry> topServers(int limit) {
            return topEntries(serverSightings, limit);
        }

        public List<CountEntry> topDimensions(int limit) {
            return topEntries(dimensionSightings, limit);
        }

        public List<CountEntry> topGameModes(int limit) {
            return topEntries(gameModeSamples, limit);
        }

        public String topServer() {
            return topKey(serverSightings);
        }

        public String topDimension() {
            return topKey(dimensionSightings);
        }

        public String topGameMode() {
            return topKey(gameModeSamples);
        }

        public String topHour() {
            return topKey(hourSightings);
        }

        public String topDay() {
            return topKey(daySightings);
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
            tag.putDouble("firstX", firstX);
            tag.putDouble("firstY", firstY);
            tag.putDouble("firstZ", firstZ);
            tag.putDouble("lastX", lastX);
            tag.putDouble("lastY", lastY);
            tag.putDouble("lastZ", lastZ);
            tag.putDouble("minX", minX);
            tag.putDouble("maxX", maxX);
            tag.putDouble("minY", minY);
            tag.putDouble("maxY", maxY);
            tag.putDouble("minZ", minZ);
            tag.putDouble("maxZ", maxZ);
            tag.putDouble("closestX", closestX);
            tag.putDouble("closestY", closestY);
            tag.putDouble("closestZ", closestZ);
            tag.putDouble("farthestX", farthestX);
            tag.putDouble("farthestY", farthestY);
            tag.putDouble("farthestZ", farthestZ);
            tag.putDouble("totalY", totalY);
            tag.putLong("coordinateSamples", coordinateSamples);
            tag.putDouble("totalTravelDistance", totalTravelDistance);
            tag.putDouble("lastSpeed", lastSpeed);
            tag.putDouble("maxSpeed", maxSpeed);
            tag.putLong("speedSamples", speedSamples);
            tag.putDouble("totalHealth", totalHealth);
            tag.putInt("minPing", minPing);
            tag.putInt("maxPing", maxPing);
            tag.putLong("totalPing", totalPing);
            tag.putLong("pingSamples", pingSamples);
            tag.putLong("lastSightingMs", lastSightingMs);
            tag.putLong("shortestSightingMs", shortestSightingMs);
            tag.putInt("completedSightings", completedSightings);
            tag.putInt("shortSightingsIgnored", shortSightingsIgnored);
            tag.putInt("aggressiveEncounters", aggressiveEncounters);
            tag.putInt("damageEvents", damageEvents);
            tag.putDouble("totalDamageTakenNearPlayer", totalDamageTakenNearPlayer);
            tag.putDouble("maxEncounterDamageTaken", maxEncounterDamageTaken);
            tag.putDouble("closestAggressionDistance", closestAggressionDistance);
            tag.putLong("lastAggressiveAt", lastAggressiveAt);
            tag.putLong("heatmapSamples", heatmapSamples);
            tag.put("serverSightings", countsToTag(serverSightings));
            tag.put("dimensionSightings", countsToTag(dimensionSightings));
            tag.put("gameModeSamples", countsToTag(gameModeSamples));
            tag.put("hourSightings", countsToTag(hourSightings));
            tag.put("daySightings", countsToTag(daySightings));
            tag.put("coordinateHeatmap", countsToTag(coordinateHeatmap));

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
            stats.firstX = tag.getDoubleOr("firstX", 0);
            stats.firstY = tag.getDoubleOr("firstY", 0);
            stats.firstZ = tag.getDoubleOr("firstZ", 0);
            stats.lastX = tag.getDoubleOr("lastX", 0);
            stats.lastY = tag.getDoubleOr("lastY", 0);
            stats.lastZ = tag.getDoubleOr("lastZ", 0);
            stats.minX = tag.getDoubleOr("minX", Math.min(0, stats.lastX));
            stats.maxX = tag.getDoubleOr("maxX", Math.max(0, stats.lastX));
            stats.minY = tag.getDoubleOr("minY", Math.min(0, stats.lastY));
            stats.maxY = tag.getDoubleOr("maxY", Math.max(0, stats.lastY));
            stats.minZ = tag.getDoubleOr("minZ", Math.min(0, stats.lastZ));
            stats.maxZ = tag.getDoubleOr("maxZ", Math.max(0, stats.lastZ));
            stats.closestX = tag.getDoubleOr("closestX", stats.lastX);
            stats.closestY = tag.getDoubleOr("closestY", stats.lastY);
            stats.closestZ = tag.getDoubleOr("closestZ", stats.lastZ);
            stats.farthestX = tag.getDoubleOr("farthestX", stats.lastX);
            stats.farthestY = tag.getDoubleOr("farthestY", stats.lastY);
            stats.farthestZ = tag.getDoubleOr("farthestZ", stats.lastZ);
            stats.totalY = tag.getDoubleOr("totalY", 0);
            stats.coordinateSamples = tag.getLongOr("coordinateSamples", 0);
            stats.totalTravelDistance = tag.getDoubleOr("totalTravelDistance", 0);
            stats.lastSpeed = tag.getDoubleOr("lastSpeed", 0);
            stats.maxSpeed = tag.getDoubleOr("maxSpeed", 0);
            stats.speedSamples = tag.getLongOr("speedSamples", 0);
            stats.totalHealth = tag.getDoubleOr("totalHealth", 0);
            stats.minPing = tag.getIntOr("minPing", -1);
            stats.maxPing = tag.getIntOr("maxPing", -1);
            stats.totalPing = tag.getLongOr("totalPing", 0);
            stats.pingSamples = tag.getLongOr("pingSamples", 0);
            stats.lastSightingMs = tag.getLongOr("lastSightingMs", 0);
            stats.shortestSightingMs = tag.getLongOr("shortestSightingMs", Long.MAX_VALUE);
            stats.completedSightings = tag.getIntOr("completedSightings", 0);
            stats.shortSightingsIgnored = tag.getIntOr("shortSightingsIgnored", 0);
            stats.aggressiveEncounters = tag.getIntOr("aggressiveEncounters", 0);
            stats.damageEvents = tag.getIntOr("damageEvents", 0);
            stats.totalDamageTakenNearPlayer = tag.getDoubleOr("totalDamageTakenNearPlayer", 0);
            stats.maxEncounterDamageTaken = tag.getDoubleOr("maxEncounterDamageTaken", 0);
            stats.closestAggressionDistance = tag.getDoubleOr("closestAggressionDistance", Double.MAX_VALUE);
            stats.lastAggressiveAt = tag.getLongOr("lastAggressiveAt", 0);
            stats.heatmapSamples = tag.getLongOr("heatmapSamples", 0);
            stats.serverSightings = countsFromTag(tag, "serverSightings");
            stats.dimensionSightings = countsFromTag(tag, "dimensionSightings");
            stats.gameModeSamples = countsFromTag(tag, "gameModeSamples");
            stats.hourSightings = countsFromTag(tag, "hourSightings");
            stats.daySightings = countsFromTag(tag, "daySightings");
            stats.coordinateHeatmap = countsFromTag(tag, "coordinateHeatmap");

            return stats;
        }
    }
}
