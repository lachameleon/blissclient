package dev.stardust.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.stardust.playertracker.PlayerStatsPopup;
import dev.stardust.playertracker.PlayerTracker;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
                PlayerStatsPopup.show("Player Tracker", List.of("No player sightings recorded yet."));
                return SINGLE_SUCCESS;
            }

            List<String> lines = new ArrayList<>();
            lines.add("%d tracked players".formatted(tracker.count()));
            for (PlayerTracker.PlayerStats stats : tracker.getTop(5)) {
                lines.add("%s - %d sightings, %s visible, %s".formatted(
                    stats.name,
                    stats.sightingCount,
                    formatDuration(stats.totalVisibleMs),
                    formatAgo(stats.lastSeen)
                ));
            }

            PlayerStatsPopup.show("Player Tracker", lines);

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
                    PlayerStatsPopup.show("Player Tracker", List.of("Player tracker is not loaded."));
                    return SINGLE_SUCCESS;
                }

                String player = StringArgumentType.getString(context, "player");
                PlayerTracker.PlayerStats stats = tracker.get(player);
                if (stats == null) {
                    PlayerStatsPopup.show("Player Tracker", List.of("No sightings recorded for " + player + ".", "Use .stats to list tracked players."));
                    return SINGLE_SUCCESS;
                }

                showStats(tracker, stats);
                return SINGLE_SUCCESS;
            })
        );
    }

    private void showStats(PlayerTracker tracker, PlayerTracker.PlayerStats stats) {
        boolean visible = tracker.isVisible(stats.uuid);

        List<String> lines = new ArrayList<>();
        lines.add("Status: " + (visible ? "currently visible" : "not currently visible"));
        lines.add("Sightings: %d | Visible: %s | Longest: %s".formatted(
            stats.sightingCount,
            formatDuration(stats.totalVisibleMs),
            formatDuration(stats.longestSightingMs)
        ));
        lines.add("First: %s".formatted(formatTimestamp(stats.firstSeen)));
        lines.add("Last: %s (%s)".formatted(
            formatTimestamp(stats.lastSeen),
            formatAgo(stats.lastSeen)
        ));
        lines.add("Distance: %s close | %s avg | %s far".formatted(
            formatDistance(stats.distanceSamples == 0 ? 0 : stats.minDistance),
            formatDistance(stats.averageDistance()),
            formatDistance(stats.maxDistance)
        ));
        lines.add("Last distance: " + formatDistance(stats.lastDistance));
        lines.add("Health: %s/%s | Range: %s-%s".formatted(
            formatNumber(stats.lastHealth),
            formatNumber(stats.lastMaxHealth),
            stats.healthSamples == 0 ? "n/a" : formatNumber(stats.minHealth),
            stats.healthSamples == 0 ? "n/a" : formatNumber(stats.maxHealth)
        ));
        lines.add("Location: %s @ %s".formatted(stats.lastDimension, formatPosition(stats.lastX, stats.lastY, stats.lastZ)));
        lines.add("Server: %s".formatted(stats.lastServer));
        lines.add("Top server: %s | Top dimension: %s".formatted(stats.topServer(), stats.topDimension()));
        lines.add("Ping: %s | Gamemode: %s".formatted(
            stats.lastPing < 0 ? "unknown" : stats.lastPing + "ms",
            stats.lastGameMode
        ));

        if (!stats.previousNames.isEmpty()) {
            lines.add("Previous names: " + String.join(", ", stats.previousNames));
        }

        PlayerStatsPopup.show("Stats: " + stats.name, lines, visible ? 0x7CF2BD : 0xFF79C6);
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
