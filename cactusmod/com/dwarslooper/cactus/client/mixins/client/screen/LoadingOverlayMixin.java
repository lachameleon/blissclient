package com.dwarslooper.cactus.client.mixins.client.screen;

import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.systems.config.settings.impl.ColorSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.function.Function;
import java.util.function.IntSupplier;
import net.minecraft.class_10799;
import net.minecraft.class_156;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_425;
import net.minecraft.class_9848;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_425.class})
public abstract class LoadingOverlayMixin {
   @Unique
   private static final class_2960 CACTUS_SPRITE = class_2960.method_60655("cactus", "textures/cactus.png");
   @Shadow
   private long field_17771;
   @Shadow
   @Final
   private boolean field_18219;
   @Shadow
   private long field_18220;
   @Mutable
   @Shadow
   @Final
   private static IntSupplier field_25041;
   @Shadow
   @Final
   private static int field_32249;
   @Shadow
   @Final
   private static int field_32250;

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   public void create(CallbackInfo ci) {
      field_25041 = () -> {
         if (useVanillaOverlay()) {
            return (Boolean)CactusConstants.mc.field_1690.method_41772().method_41753() ? field_32249 : field_32250;
         } else {
            return getSettingColorOrDefault((settings) -> {
               return settings.loadingOverlayBackgroundColor;
            }, -15460324);
         }
      };
   }

   @Inject(
      method = {"method_25394"},
      at = {@At("TAIL")}
   )
   public void render(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      if (!useVanillaOverlay()) {
         CactusSettings settings = getSettings();
         if (settings != null && (Boolean)settings.loadingOverlayShowSprite.get()) {
            int height = context.method_51443();
            int width = context.method_51421();
            float alpha = this.getOverlayAlpha();
            context.method_51448().pushMatrix();
            int spriteColor = class_9848.method_61318(alpha, 1.0F, 1.0F, 1.0F);
            context.method_25291(class_10799.field_56883, CACTUS_SPRITE, width / 2 - height / 10, height / 12, 0.0F, 0.0F, height / 5, height / 5, height / 5, height / 5, spriteColor);
            context.method_51448().popMatrix();
         }
      }
   }

   @WrapOperation(
      method = {"method_18103"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_9848;method_61324(IIII)I"
)}
   )
   public int getBorderColor(int a, int r, int g, int b, Operation<Integer> original) {
      if (useVanillaOverlay()) {
         return class_9848.method_61324(a, r, g, b);
      } else {
         int configured = getSettingColorOrDefault((settings) -> {
            return settings.loadingOverlayProgressBorderColor;
         }, -13617610);
         return class_9848.method_61330(multiplyAlpha(a, configured), configured & 16777215);
      }
   }

   @ModifyArg(
      method = {"method_18103"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_332;method_25294(IIIII)V",
   ordinal = 0
),
      index = 4
   )
   public int getBarColor(int color) {
      if (useVanillaOverlay()) {
         return color;
      } else {
         int configured = getSettingColorOrDefault((settings) -> {
            return settings.loadingOverlayProgressFillColor;
         }, -8328396);
         int alpha = multiplyAlpha(color >> 24 & 255, configured);
         return class_9848.method_61330(alpha, configured & 16777215);
      }
   }

   @WrapOperation(
      method = {"method_25394"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_425;method_35732(II)I"
)}
   )
   public int getBackgroundColorWithConfigAlpha(int rgb, int alpha, Operation<Integer> original) {
      return useVanillaOverlay() ? (Integer)original.call(new Object[]{rgb, alpha}) : class_9848.method_61330(multiplyAlpha(alpha, rgb), rgb & 16777215);
   }

   @Unique
   private static CactusSettings getSettings() {
      try {
         return CactusSettings.get();
      } catch (Exception var1) {
         return null;
      }
   }

   @Unique
   private float getOverlayAlpha() {
      long l = class_156.method_658();
      if (this.field_18219 && this.field_18220 == -1L) {
         this.field_18220 = l;
      }

      float f = this.field_17771 > -1L ? (float)(l - this.field_17771) / 1000.0F : -1.0F;
      if (f >= 1.0F) {
         return 1.0F - class_3532.method_15363(f - 1.0F, 0.0F, 1.0F);
      } else {
         return this.field_18219 ? class_3532.method_15363(this.field_18220 > -1L ? (float)(l - this.field_18220) / 500.0F : -1.0F, 0.0F, 1.0F) : 1.0F;
      }
   }

   @Unique
   private static int getSettingColorOrDefault(Function<CactusSettings, Setting<ColorSetting.ColorValue>> mapper, int fallbackArgb) {
      CactusSettings settings = getSettings();
      return settings == null ? fallbackArgb : ((ColorSetting.ColorValue)((Setting)mapper.apply(settings)).get()).color();
   }

   @Unique
   private static boolean useVanillaOverlay() {
      CactusSettings settings = getSettings();
      return settings != null && (Boolean)settings.loadingOverlayUseVanilla.get();
   }

   @Unique
   private static int multiplyAlpha(int baseAlpha, int argbColor) {
      int configuredAlpha = argbColor >>> 24 & 255;
      return Math.round((float)baseAlpha / 255.0F * ((float)configuredAlpha / 255.0F) * 255.0F);
   }
}
