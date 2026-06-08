package dev.stardust.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.stardust.playertracker.PlayerTracker;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static net.minecraft.commands.SharedSuggestionProvider.suggest;

public class StatsCommand extends Command {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    public StatsCommand() {
        super("stats", "Shows detailed local sighting stats for a tracked player.", "seen");
    }

    @Override
    public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
        builder.executes(_ -> {
            PlayerTracker tracker = PlayerTracker.get();
            if (tracker == null || tracker.count() == 0) {
                info("No player sightings recorded yet.");
                return SINGLE_SUCCESS;
            }

            info("--- Player Tracker ((highlight)%d(default)) ---", tracker.count());
            for (PlayerTracker.PlayerStats stats : tracker.getTop(5)) {
                info("(highlight)%s(default) - %d sightings, %s visible, last seen %s",
                    stats.name,
                    stats.sightingCount,
                    formatDuration(stats.totalVisibleMs),
                    formatAgo(stats.lastSeen)
                );
            }

            return SINGLE_SUCCESS;
        });

        builder.then(argument("player", StringArgumentType.word())
            .suggests((_, suggestions) -> {
                PlayerTracker tracker = PlayerTracker.get();
                return tracker == null ? suggestions.buildFuture() : suggest(tracker.getKnownNames(), suggestions);
            })
            .executes(context -> {
                PlayerTracker tracker = PlayerTracker.get();
                if (tracker == null) {
                    error("Player tracker is not loaded.");
                    return SINGLE_SUCCESS;
                }

                String player = StringArgumentType.getString(context, "player");
                PlayerTracker.PlayerStats stats = tracker.get(player);
                if (stats == null) {
                    error("No sightings recorded for %s.", player);
                    return SINGLE_SUCCESS;
                }

                showStats(tracker, stats);
                return SINGLE_SUCCESS;
            })
        );
    }

    private void showStats(PlayerTracker tracker, PlayerTracker.PlayerStats stats) {
        boolean visible = tracker.isVisible(stats.uuid);

        info("--- Stats for (highlight)%s(default) ---", stats.name);
        info("UUID: (highlight)%s", stats.uuid);
        info("Status: %s", visible ? "(highlight)currently visible(default)" : "not currently visible");
        info("Sightings: (highlight)%d(default) | Total visible: (highlight)%s(default) | Longest: (highlight)%s(default)",
            stats.sightingCount,
            formatDuration(stats.totalVisibleMs),
            formatDuration(stats.longestSightingMs)
        );
        info("First seen: (highlight)%s(default) | Last seen: (highlight)%s(default) (%s)",
            formatTimestamp(stats.firstSeen),
            formatTimestamp(stats.lastSeen),
            formatAgo(stats.lastSeen)
        );
        info("Distance: closest (highlight)%s(default), average (highlight)%s(default), farthest (highlight)%s(default), last (highlight)%s(default)",
            formatDistance(stats.distanceSamples == 0 ? 0 : stats.minDistance),
            formatDistance(stats.averageDistance()),
            formatDistance(stats.maxDistance),
            formatDistance(stats.lastDistance)
        );
        info("Health: last (highlight)%s/%s(default), observed range (highlight)%s-%s(default)",
            formatNumber(stats.lastHealth),
            formatNumber(stats.lastMaxHealth),
            stats.healthSamples == 0 ? "n/a" : formatNumber(stats.minHealth),
            stats.healthSamples == 0 ? "n/a" : formatNumber(stats.maxHealth)
        );
        info("Last location: (highlight)%s(default) @ (highlight)%s(default)",
            stats.lastDimension,
            formatPosition(stats.lastX, stats.lastY, stats.lastZ)
        );
        info("Last server: (highlight)%s(default) | Top server: (highlight)%s(default)", stats.lastServer, stats.topServer());
        info("Top dimension: (highlight)%s(default) | Ping: (highlight)%s(default) | Gamemode: (highlight)%s(default)",
            stats.topDimension(),
            stats.lastPing < 0 ? "unknown" : stats.lastPing + "ms",
            stats.lastGameMode
        );

        if (!stats.previousNames.isEmpty()) {
            info("Previous names: (highlight)%s", String.join(", ", stats.previousNames));
        }
    }

    private static String formatTimestamp(long timestamp) {
        if (timestamp <= 0) return "unknown";
        return DATE_FORMAT.format(Instant.ofEpochMilli(timestamp));
    }

    private static String formatAgo(long timestamp) {
        if (timestamp <= 0) return "unknown";
        long delta = Math.max(0, java.lang.System.currentTimeMillis() - timestamp);
        if (delta < 1000) return "just now";
        return formatDuration(delta) + " ago";
    }

    private static String formatDuration(long millis) {
        long seconds = Math.max(0, millis / 1000);
        long days = seconds / 86400;
        seconds %= 86400;
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;
        seconds %= 60;

        if (days > 0) return "%dd %dh %dm".formatted(days, hours, minutes);
        if (hours > 0) return "%dh %dm %ds".formatted(hours, minutes, seconds);
        if (minutes > 0) return "%dm %ds".formatted(minutes, seconds);
        return "%ds".formatted(seconds);
    }

    private static String formatDistance(double distance) {
        return formatNumber(distance) + " blocks";
    }

    private static String formatPosition(double x, double y, double z) {
        return "%.1f, %.1f, %.1f".formatted(Locale.ROOT, x, y, z);
    }

    private static String formatNumber(double value) {
        return "%.1f".formatted(Locale.ROOT, value);
    }
}
