/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.player.TitleScreenCredits;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    private static final String DISCORD_INVITE = "https://discord.gg/sjH2y63twU";
    private static final Identifier BLISS_LOGO = MeteorClient.identifier("textures/gui/bliss_logo.png");
    private static final int LOGO_TEXTURE_WIDTH = 420;
    private static final int LOGO_TEXTURE_HEIGHT = 122;
    private static final int DISCORD_ICON_SIZE = 30;
    private static final int WHITE = 0xFFFFF6FB;
    private static final int MUTED = 0xFFC9A9BC;
    private static final int CHAMELEON_GREEN_FILL = 0x2E73FF73;
    private static final int CHAMELEON_GREEN_OUTLINE = 0xFF73FF73;
    private static final int CHAMELEON_GREEN_HIGHLIGHT = 0x8873FF73;
    private static final int CHAMELEON_GREEN_SHADE = 0x5573FF73;

    public TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void onExtractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        renderBlissBackground(graphics);
        renderBlissBrand(graphics);

        super.extractRenderState(graphics, mouseX, mouseY, delta);

        renderChameleonButtonTint(graphics);
        renderDiscordIcon(graphics, mouseX, mouseY);
        renderBlissFooter(graphics);
        if (Config.get().titleScreenCredits.get()) TitleScreenCredits.render(graphics);

        ci.cancel();
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT && isDiscordIcon(event.x(), event.y())) {
            ConfirmLinkScreen.confirmLinkNow(this, DISCORD_INVITE);
            cir.setReturnValue(true);
            return;
        }

        if (Config.get().titleScreenCredits.get() && event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (TitleScreenCredits.onClicked(event.x(), event.y())) cir.setReturnValue(true);
        }
    }

    private void renderBlissBackground(GuiGraphicsExtractor graphics) {
        int w = width;
        int h = height;
        long time = Util.getMillis();

        graphics.fillGradient(0, 0, w, h, 0xFF110713, 0xFF070A12);
        graphics.fillGradient(0, 0, w, h / 2 + 40, 0x441B3540, 0x001B3540);
        graphics.fillGradient(0, h / 2, w, h, 0x00241028, 0xDD100711);

        for (int i = 0; i < 46; i++) {
            int x = Math.floorMod(i * 79 + (int) (time / 46), Math.max(w, 1));
            int y = Math.floorMod(i * 43 + (int) (time / 93), Math.max(h - 56, 1));
            int size = 1 + (i % 3 == 0 ? 1 : 0);
            int color = switch (i % 5) {
                case 0 -> 0x66FF73BE;
                case 1 -> 0x5565D6FF;
                default -> 0x35FFF6FB;
            };
            graphics.fill(x, y, x + size, y + size, color);
        }

        int horizon = Math.max(76, h - 92);
        graphics.fillGradient(0, horizon, w, h, 0x5526102A, 0xEE07070C);

        for (int x = -40; x < w + 60; x += 42) {
            int offset = Math.floorMod(x * 3, 34);
            int top = horizon + 14 + offset;
            graphics.fill(x, top, x + 52, h, 0x6D180A1C);
            graphics.fill(x + 6, top + 9, x + 48, h, 0x88200C24);
            graphics.fill(x + 15, top + 20, x + 45, h, 0x99300F31);
        }

        int lineColor = 0x25FF73BE;
        for (int y = horizon; y < h; y += 18) {
            graphics.horizontalLine(0, w, y, lineColor);
        }
        for (int x = Math.floorMod((int) -(time / 120), 32) - 32; x < w; x += 32) {
            graphics.verticalLine(x, horizon, h, 0x1C65D6FF);
        }
    }

    private void renderBlissBrand(GuiGraphicsExtractor graphics) {
        boolean compact = width < 720;
        int logoW = compact ? Math.min(width - 52, 260) : 360;
        int logoH = logoW * LOGO_TEXTURE_HEIGHT / LOGO_TEXTURE_WIDTH;
        int x = compact ? (width - logoW) / 2 : 42;
        int y = compact ? 24 : Math.max(46, height / 2 - 106);

        graphics.fill(x - 12, y - 10, x + logoW + 14, y + logoH + 16, 0x33110713);
        graphics.outline(x - 12, y - 10, logoW + 26, logoH + 26, 0x55FF73BE);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BLISS_LOGO, x, y, 0, 0, logoW, logoH, LOGO_TEXTURE_WIDTH, LOGO_TEXTURE_HEIGHT);

        int taglineY = y + logoH + 15;
        graphics.text(font, "Pink-tuned for 26.1.2", x + 2, taglineY, MUTED, true);
        graphics.text(font, "Private utility client", x + 2, taglineY + 13, 0x99FFF6FB, true);
    }

    private void renderBlissFooter(GuiGraphicsExtractor graphics) {
        String version = "Minecraft " + SharedConstants.getCurrentVersion().name() + "  /  Bliss Client";
        graphics.text(font, version, 8, height - 18, 0xB8FFF6FB, true);
        graphics.text(font, "hogridersupercell123", 8, height - 31, 0x80FF73BE, true);
    }

    private void renderChameleonButtonTint(GuiGraphicsExtractor graphics) {
        for (GuiEventListener child : children()) {
            if (!(child instanceof AbstractWidget widget)) continue;
            if (!widget.visible || isFooterText(widget)) continue;

            int x = widget.getX();
            int y = widget.getY();
            int w = widget.getWidth();
            int h = widget.getHeight();

            graphics.fill(x, y, x + w, y + h, CHAMELEON_GREEN_FILL);
            graphics.outline(x - 1, y - 1, w + 2, h + 2, CHAMELEON_GREEN_OUTLINE);
            graphics.horizontalLine(x + 2, x + w - 3, y + 2, CHAMELEON_GREEN_HIGHLIGHT);
            graphics.horizontalLine(x + 2, x + w - 3, y + h - 3, CHAMELEON_GREEN_SHADE);
        }
    }

    private void renderDiscordIcon(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int x = discordIconX();
        int y = discordIconY();
        boolean hovered = isDiscordIcon(mouseX, mouseY);
        int bgTop = hovered ? 0xFF6B77FF : 0xEE5865F2;
        int bgBottom = hovered ? 0xFF4F5DE8 : 0xEE3F49C8;

        graphics.fill(x + 2, y + 3, x + DISCORD_ICON_SIZE + 2, y + DISCORD_ICON_SIZE + 3, 0x66000000);
        graphics.fillGradient(x, y, x + DISCORD_ICON_SIZE, y + DISCORD_ICON_SIZE, bgTop, bgBottom);
        graphics.outline(x, y, DISCORD_ICON_SIZE, DISCORD_ICON_SIZE, hovered ? WHITE : 0xCCFFF6FB);

        int glyphX = x + 6;
        int glyphY = y + 8;
        graphics.fill(glyphX + 5, glyphY, glyphX + 10, glyphY + 4, WHITE);
        graphics.fill(glyphX + 14, glyphY, glyphX + 19, glyphY + 4, WHITE);
        graphics.fill(glyphX + 2, glyphY + 4, glyphX + 22, glyphY + 15, WHITE);
        graphics.fill(glyphX + 5, glyphY + 13, glyphX + 19, glyphY + 18, WHITE);
        graphics.fill(glyphX + 7, glyphY + 7, glyphX + 10, glyphY + 10, 0xFF5865F2);
        graphics.fill(glyphX + 15, glyphY + 7, glyphX + 18, glyphY + 10, 0xFF5865F2);
        graphics.fill(glyphX + 10, glyphY + 13, glyphX + 15, glyphY + 14, 0xFF5865F2);

        if (hovered) graphics.setTooltipForNextFrame(Component.literal("Join Discord"), mouseX, mouseY);
    }

    private boolean isDiscordIcon(double mouseX, double mouseY) {
        int x = discordIconX();
        int y = discordIconY();
        return mouseX >= x && mouseX <= x + DISCORD_ICON_SIZE && mouseY >= y && mouseY <= y + DISCORD_ICON_SIZE;
    }

    private int discordIconX() {
        return Math.max(8, width - DISCORD_ICON_SIZE - 12);
    }

    private int discordIconY() {
        return 12;
    }

    private boolean isFooterText(AbstractWidget widget) {
        return widget.getHeight() <= 12;
    }
}
