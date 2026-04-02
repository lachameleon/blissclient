package com.dwarslooper.cactus.client.mixins.render;

import com.dwarslooper.cactus.client.feature.content.ContentPackManager;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.BetterTooltips;
import com.dwarslooper.cactus.client.render.GradientTextRenderer;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.modules.TooltipScrollHandler;
import com.llamalad7.mixinextras.sugar.Local;
import java.awt.Color;
import java.util.List;
import net.minecraft.class_1058;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5684;
import net.minecraft.class_8000;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_332.class})
public abstract class GuiGraphicsMixin {
   @Shadow
   @Final
   private Matrix3x2fStack field_44657;
   @Unique
   private static float[] shaderState = new float[4];

   @Inject(
      method = {"method_51435"},
      at = {@At("HEAD")}
   )
   private void cactus$scrollableTooltipsHead(class_327 font, List<class_5684> list, int i, int j, class_8000 clientTooltipPositioner, @Nullable class_2960 identifier, CallbackInfo ci) {
      BetterTooltips tooltips = (BetterTooltips)ModuleManager.get().get(BetterTooltips.class);
      if (tooltips != null && tooltips.active() && (Boolean)tooltips.scrollableTooltips.get()) {
         this.field_44657.pushMatrix();
         this.field_44657.translate((float)TooltipScrollHandler.getXOffset(), (float)TooltipScrollHandler.getYOffset());
      }
   }

   @Inject(
      method = {"method_51435"},
      at = {@At("TAIL")}
   )
   private void cactus$scrollableTooltipsTail(class_327 font, List<class_5684> list, int i, int j, class_8000 clientTooltipPositioner, @Nullable class_2960 identifier, CallbackInfo ci) {
      BetterTooltips tooltips = (BetterTooltips)ModuleManager.get().get(BetterTooltips.class);
      if (tooltips != null && tooltips.active() && (Boolean)tooltips.scrollableTooltips.get()) {
         this.field_44657.popMatrix();
      }
   }

   @Inject(
      method = {"method_27534(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;III)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void injectRenderText(class_327 textRenderer, class_2561 text, int centerX, int y, int color, CallbackInfo ci) {
      if (CactusConstants.APRILFOOLS) {
         GradientTextRenderer.renderGradientText((class_332)this, text.method_27661().getString(), centerX - textRenderer.method_27525(text) / 2, y, List.of(new Color(-65536), new Color(-256), new Color(-16711936), new Color(-16711681), new Color(-16776961), new Color(-65281)), System.currentTimeMillis(), 2.0F, GradientTextRenderer.Animation.LINEAR_LOOPBACK_FORWARD, true, true);
         ci.cancel();
      }
   }

   @ModifyArgs(
      method = {"method_25295(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/class_2960;IIIIFFFFI)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_332;method_70847(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lcom/mojang/blaze3d/textures/GpuTextureView;Lnet/minecraft/class_12137;IIIIFFFFI)V"
)
   )
   public void onDrawTexture(Args args, @Local(argsOnly = true) class_2960 identifier) {
      this.modifyColorArgsIfApplicable(args, identifier, 11);
   }

   @ModifyArgs(
      method = {"method_52712"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_332;method_74707(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lcom/mojang/blaze3d/textures/GpuTextureView;Lnet/minecraft/class_12137;IIIIIIFFFFI)V"
)
   )
   public void onDrawTiledTexture(Args args, @Local(argsOnly = true) class_1058 sprite) {
      this.modifyColorArgsIfApplicable(args, sprite.method_45852(), 13);
   }

   @Unique
   private void modifyColorArgsIfApplicable(Args args, class_2960 identifier, int index) {
      if (ContentPackManager.get().isEnabled(ContentPackManager.get().ofId("dark_mode"))) {
         int color = (Integer)args.get(index);
         if (this.isGUITexture(identifier)) {
            args.set(index, this.getOrModifyGUITextureColor(color));
         }

      }
   }

   @Unique
   private boolean isGUITexture(class_2960 identifier) {
      if (!identifier.method_12836().equals("minecraft")) {
         return false;
      } else {
         String path = identifier.method_12832();
         if (path.equals("textures/atlas/gui.png")) {
            return true;
         } else {
            String prefix = "textures/gui/";
            if (path.startsWith(prefix)) {
               String sub = path.substring(prefix.length());
               return !sub.startsWith("title/") && !sub.startsWith("hanging_signs/");
            } else {
               return false;
            }
         }
      }
   }

   @Unique
   private int getOrModifyGUITextureColor(int color) {
      int alpha = color >> 24 & 255;
      int rgb = color & 16777215;
      if (rgb == 16777215) {
         int darkenFactor = 200;
         int r = Math.max((rgb >> 16 & 255) - darkenFactor, 0);
         int g = Math.max((rgb >> 8 & 255) - darkenFactor, 0);
         int b = Math.max((rgb & 255) - darkenFactor, 0);
         return alpha << 24 | r << 16 | g << 8 | b;
      } else {
         return color;
      }
   }
}
