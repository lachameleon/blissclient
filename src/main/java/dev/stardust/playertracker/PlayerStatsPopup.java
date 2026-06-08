package dev.stardust.playertracker;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screens.ChatScreen;

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

    public static void init() {
        if (!subscribed) {
            MeteorClient.EVENT_BUS.subscribe(PlayerStatsPopup.class);
            subscribed = true;
        }
    }

    public static void show(String title, List<String> lines) {
        show(title, lines, 0xFFFF79C6);
    }

    public static void show(String title, List<String> lines, int accent) {
        init();

        popup = new Popup(
            title == null || title.isBlank() ? "Player Stats" : title,
            limit(lines),
            accent,
            java.lang.System.currentTimeMillis()
        );

        mc.execute(() -> {
            if (mc.screen instanceof ChatScreen) mc.setScreen(null);
        });
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
        HudRenderer renderer = HudRenderer.INSTANCE;
        int screenWidth = Utils.getWindowWidth();
        int maxWidth = Math.min(640, Math.max(360, screenWidth - 48));
        int contentWidth = maxWidth - 24;
        int lineHeight = 24;
        int height = 40 + popup.lines.size() * lineHeight + 10;
        int x = screenWidth - maxWidth - 24;
        int y = 36;

        renderer.begin(event.graphics);
        renderer.quad(x + 8, y + 10, maxWidth, height, color(0x66000000, opacity));
        renderer.quad(x, y, maxWidth, height, color(BACKGROUND, opacity));
        renderer.quad(x + 2, y + 2, maxWidth - 4, height - 4, color(PANEL, opacity));
        renderer.quad(x, y, maxWidth, 4, color(0xFF000000 | popup.accent, opacity));
        renderer.quad(x, y + 4, 4, height - 4, color(0xFF000000 | popup.accent, opacity * 0.72));
        renderer.quad(x + 12, y + 31, maxWidth - 24, 1, color(LINE, opacity));

        renderer.text(clip(renderer, popup.title, contentWidth), x + 12, y + 10, color(0xFF000000 | popup.accent, opacity), true, 1);

        int textY = y + 42;
        for (int i = 0; i < popup.lines.size(); i++) {
            int textColor = i == 0 ? TEXT : (popup.lines.get(i).startsWith("  ") ? MUTED : TEXT);
            renderer.text(clip(renderer, popup.lines.get(i), contentWidth), x + 12, textY + i * lineHeight, color(textColor, opacity), true, 1);
        }

        renderer.end();
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

    private static String clip(HudRenderer renderer, String text, int width) {
        if (renderer.textWidth(text, true, 1) <= width) return text;

        String clipped = text;
        while (clipped.length() > 1 && renderer.textWidth(clipped + "...", true, 1) > width) {
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

    private static Color color(int argb, double opacity) {
        int alpha = (int) (((argb >>> 24) & 0xFF) * Math.max(0, Math.min(1, opacity)));
        return new Color((argb & 0x00FFFFFF) | (alpha << 24));
    }

    private record Popup(String title, List<String> lines, int accent, long createdAt) {
    }
}
