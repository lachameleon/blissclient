/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.mixin;

import net.minecraft.SharedConstants;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.player.TitleScreenCredits;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    private static final String DISCORD_INVITE = "https://discord.gg/sjH2y63twU";
    private static final int PINK = 0xFFFF73BE;

    @Shadow @Final private LogoRenderer logoRenderer;

    public TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        addDiscordButton();
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void onExtractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        renderBlissBackground(graphics);

        super.extractRenderState(graphics, mouseX, mouseY, delta);

        logoRenderer.extractRenderState(graphics, width, 1.0f);
        renderBlissFooter(graphics);
        if (Config.get().titleScreenCredits.get()) TitleScreenCredits.render(graphics);

        ci.cancel();
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        if (Config.get().titleScreenCredits.get() && event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (TitleScreenCredits.onClicked(event.x(), event.y())) cir.setReturnValue(true);
        }
    }

    private void addDiscordButton() {
        int y = Math.max(8, Math.min(height - 54, height / 4 + 156));
        addRenderableWidget(Button.builder(Component.literal("Discord"), ConfirmLinkScreen.confirmLink(this, DISCORD_INVITE))
            .bounds(width / 2 - 100, y, 200, 20)
            .build());
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

    private void renderBlissFooter(GuiGraphicsExtractor graphics) {
        String version = "Minecraft " + SharedConstants.getCurrentVersion().name() + "  /  ";
        graphics.text(font, version, 8, height - 18, 0xB8FFF6FB, true);
        graphics.text(font, "Bliss Client", 8 + font.width(version), height - 18, PINK, true);
    }
}
