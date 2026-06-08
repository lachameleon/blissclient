package dev.stardust.playertracker;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.utils.render.color.Color;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PlayerStatsScreen extends WindowScreen {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private final PlayerTracker tracker;
    private final PlayerTracker.PlayerStats selected;

    public PlayerStatsScreen(GuiTheme theme, PlayerTracker tracker, PlayerTracker.PlayerStats selected) {
        super(theme, selected == null ? "Player Tracker" : "Stats: " + selected.name);
        this.tracker = tracker;
        this.selected = selected;
    }

    @Override
    public void initWidgets() {
        if (tracker == null || tracker.count() == 0) {
            add(theme.label("No player sightings recorded yet."));
            return;
        }

        WSection summary = add(theme.section("Summary", true)).expandX().widget();
        WTable summaryTable = summary.add(theme.table()).expandX().widget();
        row(summaryTable, "Tracked players", String.valueOf(tracker.count()));
        row(summaryTable, "Global heatmap cells", String.valueOf(tracker.getHeatmap(9999).size()));
        row(summaryTable, "Top heatmap", heatmapSummary(tracker.getHeatmap(1)));

        if (selected != null) addPlayerDetails(selected);

        WSection topPlayers = add(theme.section("Top Players", true)).expandX().widget();
        WTable playerTable = topPlayers.add(theme.table()).expandX().widget();
        playerTable.add(theme.label("Name", true));
        playerTable.add(theme.label("Sightings", true));
        playerTable.add(theme.label("Visible", true));
        playerTable.add(theme.label("Last", true));
        playerTable.add(theme.label("", true));
        playerTable.row();

        for (PlayerTracker.PlayerStats stats : tracker.getTop(12)) {
            playerTable.add(theme.label(stats.name));
            playerTable.add(theme.label(String.valueOf(stats.sightingCount)));
            playerTable.add(theme.label(formatDuration(stats.totalVisibleMs)));
            playerTable.add(theme.label(formatAgo(stats.lastSeen)));
            WButton view = playerTable.add(theme.button("View")).right().widget();
            view.action = () -> mc.setScreen(new PlayerStatsScreen(theme, tracker, stats));
            playerTable.row();
        }

        WSection heatmap = add(theme.section(selected == null ? "Coordinate Heatmap" : "Coordinate Heatmap: " + selected.name, true)).expandX().widget();
        List<PlayerTracker.HeatmapCell> cells = selected == null ? tracker.getHeatmap(30) : selected.topHeatmap(30);
        heatmap.add(new HeatmapWidget(cells)).expandX().minWidth(520);
        WTable heatmapTable = heatmap.add(theme.table()).expandX().widget();
        heatmapTable.add(theme.label("Area", true));
        heatmapTable.add(theme.label("Dimension", true));
        heatmapTable.add(theme.label("Samples", true));
        heatmapTable.row();
        for (PlayerTracker.HeatmapCell cell : cells.stream().limit(8).toList()) {
            heatmapTable.add(theme.label("%d, %d".formatted(cell.centerX(), cell.centerZ())));
            heatmapTable.add(theme.label(shortDimension(cell.dimension())));
            heatmapTable.add(theme.label(String.valueOf(cell.count())));
            heatmapTable.row();
        }
    }

    private void addPlayerDetails(PlayerTracker.PlayerStats stats) {
        WSection details = add(theme.section("Details", true)).expandX().widget();
        WTable table = details.add(theme.table()).expandX().widget();

        row(table, "UUID", stats.uuid.toString());
        row(table, "Status", tracker.isVisible(stats.uuid) ? "currently visible" : "not currently visible");
        row(table, "Sightings", "%d total, %d completed".formatted(stats.sightingCount, stats.completedSightings));
        row(table, "Visible time", "%s total, %s avg, %s longest, %s shortest".formatted(
            formatDuration(stats.totalVisibleMs),
            formatDuration(stats.averageSightingMs()),
            formatDuration(stats.longestSightingMs),
            formatDuration(stats.shortestSightingMs())
        ));
        row(table, "First / last seen", "%s / %s (%s)".formatted(formatTimestamp(stats.firstSeen), formatTimestamp(stats.lastSeen), formatAgo(stats.lastSeen)));
        row(table, "Distance", "closest %s, avg %s, farthest %s, last %s".formatted(
            formatDistance(stats.distanceSamples == 0 ? 0 : stats.minDistance),
            formatDistance(stats.averageDistance()),
            formatDistance(stats.maxDistance),
            formatDistance(stats.lastDistance)
        ));
        row(table, "Closest location", formatPosition(stats.closestX, stats.closestY, stats.closestZ));
        row(table, "Farthest location", formatPosition(stats.farthestX, stats.farthestY, stats.farthestZ));
        row(table, "Coordinate bounds", "X %.1f..%.1f, Y %.1f..%.1f, Z %.1f..%.1f".formatted(Locale.ROOT, stats.minX, stats.maxX, stats.minY, stats.maxY, stats.minZ, stats.maxZ));
        row(table, "Average Y", formatNumber(stats.averageY()));
        row(table, "Movement", "%s traveled, avg %s/s, max %s/s, last %s/s".formatted(
            formatDistance(stats.totalTravelDistance),
            formatNumber(stats.averageSpeed()),
            formatNumber(stats.maxSpeed),
            formatNumber(stats.lastSpeed)
        ));
        row(table, "Health", "last %s/%s, avg %s, range %s-%s".formatted(
            formatNumber(stats.lastHealth),
            formatNumber(stats.lastMaxHealth),
            formatNumber(stats.averageHealth()),
            stats.healthSamples == 0 ? "n/a" : formatNumber(stats.minHealth),
            stats.healthSamples == 0 ? "n/a" : formatNumber(stats.maxHealth)
        ));
        row(table, "Ping", "last %s, avg %s, range %s-%s".formatted(
            stats.lastPing < 0 ? "unknown" : stats.lastPing + "ms",
            stats.pingSamples == 0 ? "unknown" : formatNumber(stats.averagePing()) + "ms",
            stats.minPing < 0 ? "unknown" : stats.minPing + "ms",
            stats.maxPing < 0 ? "unknown" : stats.maxPing + "ms"
        ));
        row(table, "Gamemode", "%s, top %s".formatted(stats.lastGameMode, stats.topGameMode()));
        row(table, "Server", "%s, top %s".formatted(stats.lastServer, stats.topServer()));
        row(table, "Dimension", "%s, top %s".formatted(stats.lastDimension, stats.topDimension()));
        row(table, "Most common time", "%s on %s".formatted(stats.topHour(), stats.topDay()));
        row(table, "Heatmap samples", String.valueOf(stats.heatmapSamples));
        if (!stats.previousNames.isEmpty()) row(table, "Previous names", String.join(", ", stats.previousNames));
    }

    private void row(WTable table, String key, String value) {
        table.add(theme.label(key));
        table.add(theme.label(value, 520));
        table.row();
    }

    private static String heatmapSummary(List<PlayerTracker.HeatmapCell> cells) {
        if (cells.isEmpty()) return "none";
        PlayerTracker.HeatmapCell cell = cells.getFirst();
        return "%s @ %d, %d (%d samples)".formatted(shortDimension(cell.dimension()), cell.centerX(), cell.centerZ(), cell.count());
    }

    private static String shortDimension(String dimension) {
        int colon = dimension.indexOf(':');
        return colon >= 0 ? dimension.substring(colon + 1) : dimension;
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

    private static class HeatmapWidget extends WWidget {
        private final List<PlayerTracker.HeatmapCell> cells;

        private HeatmapWidget(List<PlayerTracker.HeatmapCell> cells) {
            this.cells = cells;
            tooltip = "Each square is a %dx%d block coordinate bin.".formatted(PlayerTracker.HEATMAP_BIN_SIZE, PlayerTracker.HEATMAP_BIN_SIZE);
        }

        @Override
        protected void onCalculateSize() {
            width = Math.max(360, minWidth);
            height = 170;
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            renderer.quad(x, y, width, height, new Color(8, 12, 18, 210));
            renderer.quad(x + 1, y + 1, width - 2, height - 2, new Color(18, 25, 35, 230));

            if (cells.isEmpty()) {
                renderer.text("No coordinate samples yet.", x + 12, y + 12, new Color(170, 180, 192), false);
                return;
            }

            int max = Math.max(1, cells.stream().mapToInt(PlayerTracker.HeatmapCell::count).max().orElse(1));
            int columns = 10;
            double gap = 5;
            double cellSize = Math.min((width - 24 - gap * (columns - 1)) / columns, 22);
            double startX = x + 12;
            double startY = y + 12;

            for (int i = 0; i < Math.min(cells.size(), 30); i++) {
                PlayerTracker.HeatmapCell cell = cells.get(i);
                int col = i % columns;
                int row = i / columns;
                double alpha = 0.2 + 0.8 * (cell.count() / (double) max);
                Color color = new Color(255, 98, 178, (int) (255 * alpha));
                double cx = startX + col * (cellSize + gap);
                double cy = startY + row * (cellSize + gap);
                renderer.quad(cx, cy, cellSize, cellSize, color);
            }

            renderer.text("Top %d coordinate bins".formatted(Math.min(cells.size(), 30)), x + 12, y + height - 24, new Color(246, 251, 255), false);
        }
    }
}
