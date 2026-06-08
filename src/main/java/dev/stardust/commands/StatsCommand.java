package dev.stardust.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.stardust.playertracker.PlayerStatsScreen;
import dev.stardust.playertracker.PlayerTracker;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

import static net.minecraft.commands.SharedSuggestionProvider.suggest;

public class StatsCommand extends Command {
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

                openGui(stats);
                return SINGLE_SUCCESS;
            })
        );
    }

    private void openGui(PlayerTracker.PlayerStats stats) {
        PlayerTracker tracker = PlayerTracker.get();
        if (tracker == null) {
            error("Player tracker is not loaded.");
            return;
        }

        Utils.screenToOpen = new PlayerStatsScreen(GuiThemes.get(), tracker, stats);
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
}
