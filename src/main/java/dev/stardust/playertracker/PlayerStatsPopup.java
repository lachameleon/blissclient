package dev.stardust.playertracker;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PlayerStatsPopup {
    private static final long DURATION_MS = 9000;
    private static final long FADE_MS = 220;
    private static final int MAX_LINES = 11;
    private static final int BACKGROUND = 0xE60A0D14;
    private static final int PANEL = 0xF0121720;
    private static final int LINE = 0x40FFFFFF;
    private static final int TEXT = 0xFFF6FBFF;
    private static final int MUTED = 0xFF9AA8B8;

    private static boolean subscribed;
    private static Popup popup;

    private PlayerStatsPopup() {
    }

    public static void show(String title, List<String> lines) {
        show(title, lines, 0xFFFF79C6);
    }

    public static void show(String title, List<String> lines, int accent) {
        if (!subscribed) {
            MeteorClient.EVENT_BUS.subscribe(PlayerStatsPopup.class);
            subscribed = true;
        }

        popup = new Popup(
            title == null || title.isBlank() ? "Player Stats" : title,
            limit(lines),
            accent,
            java.lang.System.currentTimeMillis()
        );
    }

    @EventHandler
    private static void onRender(Render2DEvent event) {
        if (popup == null || mc.font == null) return;

        long now = java.lang.System.currentTimeMillis();
        long age = now - popup.createdAt;
        if (age >= DURATION_MS) {
            popup = null;
            return;
        }

        double opacity = opacity(age);
        int maxWidth = Math.min(320, Math.max(220, event.screenWidth - 24));
        int contentWidth = maxWidth - 24;
        int lineHeight = 12;
        int height = 40 + popup.lines.size() * lineHeight + 10;
        int x = event.screenWidth - maxWidth - 12;
        int y = 18;

        event.graphics.fill(x + 4, y + 5, x + maxWidth + 4, y + height + 5, color(0x66000000, opacity));
        event.graphics.fill(x, y, x + maxWidth, y + height, color(BACKGROUND, opacity));
        event.graphics.fill(x + 1, y + 1, x + maxWidth - 1, y + height - 1, color(PANEL, opacity));
        event.graphics.fill(x, y, x + maxWidth, y + 2, color(0xFF000000 | popup.accent, opacity));
        event.graphics.fill(x, y + 2, x + 2, y + height, color(0xFF000000 | popup.accent, opacity * 0.72));
        event.graphics.fill(x + 12, y + 29, x + maxWidth - 12, y + 30, color(LINE, opacity));

        event.graphics.text(mc.font, Component.literal(clip(popup.title, contentWidth)), x + 12, y + 10, color(0xFF000000 | popup.accent, opacity), false);

        int textY = y + 38;
        for (int i = 0; i < popup.lines.size(); i++) {
            int textColor = i == 0 ? TEXT : (popup.lines.get(i).startsWith("  ") ? MUTED : TEXT);
            event.graphics.text(mc.font, Component.literal(clip(popup.lines.get(i), contentWidth)), x + 12, textY + i * lineHeight, color(textColor, opacity), false);
        }
    }

    private static List<String> limit(List<String> lines) {
        if (lines == null || lines.isEmpty()) return List.of("No data available.");

        List<String> limited = new ArrayList<>(Math.min(lines.size(), MAX_LINES));
        for (String line : lines) {
            if (limited.size() >= MAX_LINES) break;
            limited.add(line == null ? "" : line);
        }
        return limited;
    }

    private static String clip(String text, int width) {
        if (mc.font.width(text) <= width) return text;

        String clipped = text;
        while (clipped.length() > 1 && mc.font.width(clipped + "...") > width) {
            clipped = clipped.substring(0, clipped.length() - 1);
        }
        return clipped + "...";
    }

    private static double opacity(long age) {
        if (age < FADE_MS) return age / (double) FADE_MS;
        long remaining = DURATION_MS - age;
        if (remaining < FADE_MS) return remaining / (double) FADE_MS;
        return 1;
    }

    private static int color(int argb, double opacity) {
        int alpha = (int) (((argb >>> 24) & 0xFF) * Math.max(0, Math.min(1, opacity)));
        return (argb & 0x00FFFFFF) | (alpha << 24);
    }

    private record Popup(String title, List<String> lines, int accent, long createdAt) {
    }
}
