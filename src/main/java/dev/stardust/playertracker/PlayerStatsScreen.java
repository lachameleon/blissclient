package dev.stardust.playertracker;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.utils.render.color.Color;

import java.util.List;
import java.util.Locale;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PlayerStatsScreen extends WindowScreen {
    private static final Color SURFACE = new Color(11, 15, 22, 226);
    private static final Color SURFACE_2 = new Color(18, 24, 34, 238);
    private static final Color LINE = new Color(255, 255, 255, 28);
    private static final Color TEXT = new Color(245, 248, 255);
    private static final Color MUTED = new Color(158, 170, 188);
    private static final Color PINK = new Color(255, 98, 178);
    private static final Color CYAN = new Color(91, 215, 255);
    private static final Color GREEN = new Color(121, 235, 160);
    private static final Color AMBER = new Color(255, 196, 96);
    private static final Color RED = new Color(255, 112, 128);

    private final PlayerTracker tracker;
    private final PlayerTracker.PlayerStats selected;

    public PlayerStatsScreen(GuiTheme theme, PlayerTracker tracker, PlayerTracker.PlayerStats selected) {
        super(theme, selected == null ? "Player Tracker" : selected.name + " Stats");
        this.tracker = tracker;
        this.selected = selected;
    }

    @Override
    public void initWidgets() {
        if (tracker == null || tracker.count() == 0) {
            add(theme.label("No valid player encounters recorded yet."));
            return;
        }

        if (selected == null) {
            addOverview();
            return;
        }

        addPlayerScreen(selected);
    }

    private void addPlayerScreen(PlayerTracker.PlayerStats stats) {
        boolean visible = tracker.isVisible(stats.uuid);
        List<PlayerTracker.HeatmapCell> heatmap = stats.topHeatmap(36);

        add(new PlayerHeaderWidget(stats, visible)).expandX().minWidth(760);

        WTable cards = add(theme.table()).expandX().widget();
        cards.horizontalSpacing = 12;
        cards.verticalSpacing = 12;
        cards.add(new StatCardWidget("Valid Encounters", "%d".formatted(stats.sightingCount), stats.shortSightingsIgnored + " ignored under 1m", PINK)).expandX().minWidth(236);
        cards.add(new StatCardWidget("Last Seen", formatAgo(stats.lastSeen), visible ? "visible right now" : formatDuration(stats.lastSightingMs) + " last encounter", visible ? GREEN : CYAN)).expandX().minWidth(236);
        cards.add(new StatCardWidget("Aggression", formatPercent(stats.aggressivenessChance()), aggressionDetail(stats), aggressionColor(stats))).expandX().minWidth(236);
        cards.row();
        cards.add(new StatCardWidget("Distance", formatDistance(stats.lastDistance), "avg " + formatDistance(stats.averageDistance()), AMBER)).expandX().minWidth(236);
        cards.add(new StatCardWidget("Movement", formatDistance(stats.totalTravelDistance), formatNumber(stats.averageSpeed()) + "/s avg, " + formatNumber(stats.maxSpeed) + "/s peak", CYAN)).expandX().minWidth(236);
        cards.add(new StatCardWidget("Health", formatHealth(stats), formatPing(stats), healthColor(stats))).expandX().minWidth(236);
        cards.row();

        WSection where = add(theme.section("Where", true)).expandX().widget();
        WTable whereTable = where.add(theme.table()).expandX().widget();
        row(whereTable, "Last location", shortDimension(stats.lastDimension) + " - " + formatPosition(stats.lastX, stats.lastY, stats.lastZ));
        row(whereTable, "Last server", clean(stats.lastServer));
        row(whereTable, "Most common server", formatTopEntry(stats.topServers(1)));
        row(whereTable, "Coordinate range", formatBounds(stats));
        row(whereTable, "Hottest area", heatmapSummary(heatmap));

        WSection servers = add(theme.section("Frequent Servers", true)).expandX().widget();
        addFrequencyTable(servers, "Server", stats.topServers(5), stats.sightingCount);

        WSection risk = add(theme.section("Aggression Risk", true)).expandX().widget();
        WTable riskTable = risk.add(theme.table()).expandX().widget();
        row(riskTable, "Chance per encounter", formatPercent(stats.aggressivenessChance()));
        row(riskTable, "Aggressive encounters", "%d of %d finished encounters".formatted(stats.aggressiveEncounters, stats.completedSightings));
        row(riskTable, "Damage signals", "%d damage event%s, %s observed".formatted(stats.totalDamageEvents(), plural(stats.totalDamageEvents()), formatHealthPoints(stats.totalObservedDamage())));
        row(riskTable, "Worst encounter", formatHealthPoints(stats.maxEncounterDamageTaken));
        row(riskTable, "Closest hostile signal", stats.closestAggressionDistance() <= 0 ? "none" : formatDistance(stats.closestAggressionDistance()));
        row(riskTable, "Last hostile signal", stats.lastAggressiveAt <= 0 ? "none" : formatAgo(stats.lastAggressiveAt));

        WSection behavior = add(theme.section("Behavior", true)).expandX().widget();
        WTable behaviorTable = behavior.add(theme.table()).expandX().widget();
        row(behaviorTable, "Encounters", "%d valid, %d finished, %d ignored under 1m".formatted(stats.sightingCount, stats.completedSightings, stats.shortSightingsIgnored));
        row(behaviorTable, "Typical sighting", "%s average, %s longest".formatted(formatDuration(stats.averageSightingMs()), formatDuration(stats.longestSightingMs)));
        row(behaviorTable, "Movement", "%s travelled, %s/s average, %s/s peak".formatted(formatDistance(stats.totalTravelDistance), formatNumber(stats.averageSpeed()), formatNumber(stats.maxSpeed)));
        row(behaviorTable, "Distance range", "%s closest, %s farthest".formatted(formatDistance(stats.distanceSamples == 0 ? 0 : stats.minDistance), formatDistance(stats.maxDistance)));
        row(behaviorTable, "Patterns", "%s most often, %s on %s".formatted(clean(stats.topGameMode()), clean(stats.topHour()), clean(stats.topDay())));
        row(behaviorTable, "Common dimensions", formatTopEntries(stats.topDimensions(3)));
        row(behaviorTable, "Common gamemodes", formatTopEntries(stats.topGameModes(3)));
        if (!stats.previousNames.isEmpty()) row(behaviorTable, "Also known as", String.join(", ", stats.previousNames));

        WSection heatmapSection = add(theme.section("Coordinate Heatmap", true)).expandX().widget();
        heatmapSection.add(new HeatmapWidget(heatmap)).expandX().minWidth(760);
        addHeatmapTable(heatmapSection, heatmap);
    }

    private void addOverview() {
        add(new OverviewHeaderWidget(tracker)).expandX().minWidth(760);

        WSection players = add(theme.section("Top Tracked Players", true)).expandX().widget();
        WTable table = players.add(theme.table()).expandX().widget();
        table.add(theme.label("Player", true));
        table.add(theme.label("Seen", true));
        table.add(theme.label("Last", true));
        table.add(theme.label("", true));
        table.row();

        for (PlayerTracker.PlayerStats stats : tracker.getTop(10)) {
            table.add(theme.label(stats.name));
            table.add(theme.label("%d".formatted(stats.sightingCount)));
            table.add(theme.label(formatAgo(stats.lastSeen)));
            WButton view = table.add(theme.button("Open")).right().widget();
            view.action = () -> mc.setScreen(new PlayerStatsScreen(theme, tracker, stats));
            table.row();
        }

        WSection heatmap = add(theme.section("Global Heatmap", true)).expandX().widget();
        List<PlayerTracker.HeatmapCell> cells = tracker.getHeatmap(36);
        heatmap.add(new HeatmapWidget(cells)).expandX().minWidth(760);
        addHeatmapTable(heatmap, cells);
    }

    private void addFrequencyTable(WSection section, String label, List<PlayerTracker.CountEntry> entries, int total) {
        WTable table = section.add(theme.table()).expandX().widget();
        table.add(theme.label(label, true));
        table.add(theme.label("Encounters", true));
        table.add(theme.label("Share", true));
        table.row();

        if (entries.isEmpty()) {
            table.add(theme.label("No data yet.")).expandCellX();
            table.row();
            return;
        }

        for (PlayerTracker.CountEntry entry : entries) {
            table.add(theme.label(clean(entry.key()), 430));
            table.add(theme.label(String.valueOf(entry.count())));
            table.add(theme.label(total <= 0 ? "0%" : formatPercent(entry.count() * 100.0 / total)));
            table.row();
        }
    }

    private void addHeatmapTable(WSection section, List<PlayerTracker.HeatmapCell> cells) {
        WTable table = section.add(theme.table()).expandX().widget();
        table.add(theme.label("Hot area", true));
        table.add(theme.label("Dimension", true));
        table.add(theme.label("Samples", true));
        table.row();

        if (cells.isEmpty()) {
            table.add(theme.label("No coordinate samples yet.")).expandCellX();
            table.row();
            return;
        }

        for (PlayerTracker.HeatmapCell cell : cells.stream().limit(5).toList()) {
            table.add(theme.label("%d, %d".formatted(cell.centerX(), cell.centerZ())));
            table.add(theme.label(shortDimension(cell.dimension())));
            table.add(theme.label(String.valueOf(cell.count())));
            table.row();
        }
    }

    private void row(WTable table, String key, String value) {
        table.add(theme.label(key)).padRight(10);
        table.add(theme.label(value, 620)).expandCellX();
        table.row();
    }

    private static String heatmapSummary(List<PlayerTracker.HeatmapCell> cells) {
        if (cells.isEmpty()) return "none yet";
        PlayerTracker.HeatmapCell cell = cells.getFirst();
        return "%s - %d, %d (%d samples)".formatted(shortDimension(cell.dimension()), cell.centerX(), cell.centerZ(), cell.count());
    }

    private static String formatBounds(PlayerTracker.PlayerStats stats) {
        if (stats.coordinateSamples == 0) return "unknown";
        return "X %s to %s, Y %s to %s, Z %s to %s".formatted(
            formatNumber(stats.minX),
            formatNumber(stats.maxX),
            formatNumber(stats.minY),
            formatNumber(stats.maxY),
            formatNumber(stats.minZ),
            formatNumber(stats.maxZ)
        );
    }

    private static String formatTopEntry(List<PlayerTracker.CountEntry> entries) {
        if (entries.isEmpty()) return "unknown";
        PlayerTracker.CountEntry entry = entries.getFirst();
        return "%s (%d)".formatted(clean(entry.key()), entry.count());
    }

    private static String formatTopEntries(List<PlayerTracker.CountEntry> entries) {
        if (entries.isEmpty()) return "unknown";
        return entries.stream()
            .map(entry -> "%s (%d)".formatted(clean(entry.key()), entry.count()))
            .reduce((a, b) -> a + ", " + b)
            .orElse("unknown");
    }

    private static String aggressionDetail(PlayerTracker.PlayerStats stats) {
        if (stats.completedSightings == 0) return "not enough finished encounters";
        return "%d/%d encounters, %d damage signal%s".formatted(
            stats.aggressiveEncounters,
            stats.completedSightings,
            stats.totalDamageEvents(),
            plural(stats.totalDamageEvents())
        );
    }

    private static Color aggressionColor(PlayerTracker.PlayerStats stats) {
        double chance = stats.aggressivenessChance();
        if (chance >= 60) return RED;
        if (chance >= 25) return AMBER;
        return GREEN;
    }

    private static String formatHealth(PlayerTracker.PlayerStats stats) {
        if (stats.healthSamples == 0) return "unknown";
        return "%s / %s".formatted(formatNumber(stats.lastHealth), formatNumber(stats.lastMaxHealth));
    }

    private static String formatPing(PlayerTracker.PlayerStats stats) {
        if (stats.lastPing < 0) return "ping unknown";
        if (stats.pingSamples == 0) return stats.lastPing + "ms ping";
        return "%dms ping, %s avg".formatted(stats.lastPing, formatNumber(stats.averagePing()) + "ms");
    }

    private static Color healthColor(PlayerTracker.PlayerStats stats) {
        if (stats.healthSamples == 0 || stats.lastMaxHealth <= 0) return CYAN;
        double ratio = stats.lastHealth / stats.lastMaxHealth;
        if (ratio <= 0.35) return RED;
        if (ratio <= 0.65) return AMBER;
        return GREEN;
    }

    private static String formatPercent(double value) {
        if (!Double.isFinite(value)) return "0%";
        return String.format(Locale.ROOT, "%.0f%%", Math.max(0, value));
    }

    private static String formatHealthPoints(double value) {
        if (!Double.isFinite(value) || value <= 0) return "0 hp";
        return String.format(Locale.ROOT, "%.1f hp", value);
    }

    private static String shortDimension(String dimension) {
        if (dimension == null || dimension.isBlank()) return "unknown";
        int colon = dimension.indexOf(':');
        return colon >= 0 ? dimension.substring(colon + 1) : dimension;
    }

    private static String clean(String value) {
        return value == null || value.isBlank() ? "unknown" : value;
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

        if (days > 0) return "%dd %dh".formatted(days, hours);
        if (hours > 0) return "%dh %dm".formatted(hours, minutes);
        if (minutes > 0) return "%dm %ds".formatted(minutes, seconds);
        return "%ds".formatted(seconds);
    }

    private static String formatDistance(double distance) {
        if (!Double.isFinite(distance) || distance <= 0) return "0 blocks";
        if (distance >= 1000) return String.format(Locale.ROOT, "%,.0f blocks", distance);
        if (distance >= 100) return String.format(Locale.ROOT, "%.0f blocks", distance);
        return String.format(Locale.ROOT, "%.1f blocks", distance);
    }

    private static String formatPosition(double x, double y, double z) {
        return String.format(Locale.ROOT, "%.0f, %.0f, %.0f", x, y, z);
    }

    private static String formatNumber(double value) {
        if (!Double.isFinite(value)) return "0";
        if (Math.abs(value) >= 100) return String.format(Locale.ROOT, "%.0f", value);
        return String.format(Locale.ROOT, "%.1f", value);
    }

    private static String plural(int count) {
        return count == 1 ? "" : "s";
    }

    private abstract static class PanelWidget extends WWidget {
        protected String fit(String text, double maxWidth, boolean title) {
            if (text == null) return "";
            if (theme.textWidth(text, text.length(), title) <= maxWidth) return text;
            if (maxWidth <= theme.textWidth("...", 3, title)) return "...";

            for (int i = text.length() - 1; i > 0; i--) {
                String trimmed = text.substring(0, i) + "...";
                if (theme.textWidth(trimmed, trimmed.length(), title) <= maxWidth) return trimmed;
            }

            return "...";
        }
    }

    private static class PlayerHeaderWidget extends PanelWidget {
        private final PlayerTracker.PlayerStats stats;
        private final boolean visible;

        private PlayerHeaderWidget(PlayerTracker.PlayerStats stats, boolean visible) {
            this.stats = stats;
            this.visible = visible;
        }

        @Override
        protected void onCalculateSize() {
            width = Math.max(740, minWidth);
            height = 98;
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            Color status = visible ? GREEN : CYAN;
            renderer.quad(x, y, width, height, new Color(16, 20, 30, 246), new Color(28, 18, 34, 246));
            renderer.quad(x, y, 4, height, status);
            renderer.quad(x + 12, y + 14, 52, 52, new Color(status.r, status.g, status.b, 42));
            renderer.quad(x + 22, y + 24, 32, 32, new Color(status.r, status.g, status.b, 180));

            renderer.text(fit(stats.name, width - 210, true), x + 78, y + 15, TEXT, true);
            renderer.text(visible ? "Currently visible" : "Last seen " + formatAgo(stats.lastSeen), x + 78, y + 44, status, false);
            renderer.text(fit(clean(stats.lastServer) + " - " + shortDimension(stats.lastDimension), width - 110, false), x + 78, y + 66, MUTED, false);

            String sightings = "%d encounters".formatted(stats.sightingCount);
            renderer.text(sightings, x + width - theme.textWidth(sightings) - 20, y + 22, MUTED, false);
            String time = formatDuration(stats.totalVisibleMs);
            renderer.text(time, x + width - theme.textWidth(time) - 20, y + 50, TEXT, true);
        }
    }

    private static class OverviewHeaderWidget extends PanelWidget {
        private final PlayerTracker tracker;

        private OverviewHeaderWidget(PlayerTracker tracker) {
            this.tracker = tracker;
        }

        @Override
        protected void onCalculateSize() {
            width = Math.max(740, minWidth);
            height = 88;
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            List<PlayerTracker.HeatmapCell> cells = tracker.getHeatmap(1);
            renderer.quad(x, y, width, height, new Color(16, 20, 30, 246), new Color(18, 32, 38, 246));
            renderer.quad(x, y, 4, height, PINK);
            renderer.text("Player Tracker", x + 20, y + 15, TEXT, true);
            renderer.text("%d players with valid encounters".formatted(tracker.count()), x + 20, y + 44, CYAN, false);
            renderer.text(fit("Top area: " + heatmapSummary(cells), width - 40, false), x + 20, y + 66, MUTED, false);
        }
    }

    private static class StatCardWidget extends PanelWidget {
        private final String label;
        private final String value;
        private final String detail;
        private final Color accent;

        private StatCardWidget(String label, String value, String detail, Color accent) {
            this.label = label;
            this.value = value;
            this.detail = detail;
            this.accent = accent;
        }

        @Override
        protected void onCalculateSize() {
            width = Math.max(220, minWidth);
            height = 86;
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            renderer.quad(x, y, width, height, SURFACE);
            renderer.quad(x, y, width, 1, LINE);
            renderer.quad(x, y + height - 3, width, 3, new Color(accent.r, accent.g, accent.b, 170));
            renderer.quad(x + 14, y + 13, 9, 9, accent);
            renderer.text(fit(label, width - 44, false), x + 30, y + 10, MUTED, false);
            renderer.text(fit(value, width - 28, true), x + 14, y + 36, TEXT, true);
            renderer.text(fit(detail, width - 28, false), x + 14, y + 66, MUTED, false);
        }
    }

    private static class HeatmapWidget extends PanelWidget {
        private final List<PlayerTracker.HeatmapCell> cells;

        private HeatmapWidget(List<PlayerTracker.HeatmapCell> cells) {
            this.cells = cells;
            tooltip = "Each square is a %dx%d block coordinate bin.".formatted(PlayerTracker.HEATMAP_BIN_SIZE, PlayerTracker.HEATMAP_BIN_SIZE);
        }

        @Override
        protected void onCalculateSize() {
            width = Math.max(740, minWidth);
            height = 248;
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            renderer.quad(x, y, width, height, SURFACE_2);
            renderer.quad(x, y, width, 1, LINE);
            renderer.quad(x, y + height - 1, width, 1, LINE);
            renderer.text("Hot coordinate areas", x + 18, y + 14, TEXT, true);

            if (cells.isEmpty()) {
                renderer.text("No coordinate samples yet.", x + 18, y + 52, MUTED, false);
                return;
            }

            int maxCount = Math.max(1, cells.stream().mapToInt(PlayerTracker.HeatmapCell::count).max().orElse(1));
            int minX = cells.stream().mapToInt(PlayerTracker.HeatmapCell::centerX).min().orElse(0);
            int maxX = cells.stream().mapToInt(PlayerTracker.HeatmapCell::centerX).max().orElse(0);
            int minZ = cells.stream().mapToInt(PlayerTracker.HeatmapCell::centerZ).min().orElse(0);
            int maxZ = cells.stream().mapToInt(PlayerTracker.HeatmapCell::centerZ).max().orElse(0);
            double plotX = x + 22;
            double plotY = y + 54;
            double plotW = width - 44;
            double plotH = height - 88;

            renderer.quad(plotX, plotY, plotW, plotH, new Color(7, 11, 18, 210));
            for (int i = 1; i < 4; i++) {
                double gx = plotX + plotW * i / 4.0;
                double gy = plotY + plotH * i / 4.0;
                renderer.quad(gx, plotY, 1, plotH, new Color(255, 255, 255, 16));
                renderer.quad(plotX, gy, plotW, 1, new Color(255, 255, 255, 16));
            }

            for (PlayerTracker.HeatmapCell cell : cells) {
                double normalizedX = maxX == minX ? 0.5 : (cell.centerX() - minX) / (double) (maxX - minX);
                double normalizedZ = maxZ == minZ ? 0.5 : (cell.centerZ() - minZ) / (double) (maxZ - minZ);
                double intensity = cell.count() / (double) maxCount;
                double size = 8 + 20 * intensity;
                double cx = plotX + normalizedX * (plotW - size);
                double cy = plotY + normalizedZ * (plotH - size);
                Color heat = new Color(255, 98, 178, (int) (95 + 150 * intensity));
                renderer.quad(cx, cy, size, size, heat);
                renderer.quad(cx + 2, cy + 2, Math.max(2, size - 4), Math.max(2, size - 4), new Color(255, 196, 96, (int) (70 + 120 * intensity)));
            }

            PlayerTracker.HeatmapCell top = cells.getFirst();
            renderer.text(fit("Peak: " + shortDimension(top.dimension()) + " - " + top.centerX() + ", " + top.centerZ(), width - 36, false), x + 18, y + height - 26, MUTED, false);
        }
    }
}
