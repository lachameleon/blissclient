package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.CustomCrosshair;
import com.dwarslooper.cactus.client.feature.modules.render.HUD;
import com.dwarslooper.cactus.client.feature.modules.render.ItemCount;
import com.dwarslooper.cactus.client.feature.modules.util.AntiAdvertise;
import com.dwarslooper.cactus.client.feature.modules.util.AutoCrafter;
import com.dwarslooper.cactus.client.feature.modules.util.EmoteQuickSelector;
import com.dwarslooper.cactus.client.gui.hud.HudEditorScreen;
import com.dwarslooper.cactus.client.gui.hud.HudManager;
import com.dwarslooper.cactus.client.gui.hud.SimpleHudManager;
import com.dwarslooper.cactus.client.gui.hud.element.impl.ScoreboardElement;
import com.dwarslooper.cactus.client.gui.hud.element.impl.StatusEffectsElement;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Graphic2DSetting;
import com.dwarslooper.cactus.client.systems.waypoints.WaypointManager;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import com.dwarslooper.cactus.client.util.generic.ColorUtils;
import com.dwarslooper.cactus.client.util.generic.TextUtils;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import net.minecraft.class_10799;
import net.minecraft.class_124;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_329;
import net.minecraft.class_332;
import net.minecraft.class_408;
import net.minecraft.class_9779;
import org.joml.Matrix3x2fStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_329.class})
public abstract class IngameHudMixin {
   @Shadow
   @Final
   private static class_2960 field_45304;
   @Unique
   private float scale = 1.0F;

   @Shadow
   public abstract class_327 method_1756();

   @Inject(
      method = {"method_55805"},
      at = {@At("HEAD")}
   )
   private void onRender(class_332 context, class_9779 tickCounter, CallbackInfo ci) {
      HUD hud = SimpleHudManager.get().getHud();
      int scaledWidth = context.method_51421();
      int scaledHeight = context.method_51443();
      WaypointManager.get().getInstance().getAllAvailable().forEach((waypoint) -> {
         waypoint.render(context);
      });
      EmoteQuickSelector emoteQuickSelector = (EmoteQuickSelector)ModuleManager.get().get(EmoteQuickSelector.class);
      if (emoteQuickSelector != null) {
         emoteQuickSelector.renderWheel(context);
      }

      if (!(CactusConstants.mc.field_1755 instanceof class_408) && !(CactusConstants.mc.field_1755 instanceof HudEditorScreen) && class_310.method_1498() && hud.active()) {
         HudManager.getInstance().forEach((element) -> {
            element.render(context, scaledWidth, scaledHeight, tickCounter.method_60637(false));
         });
         boolean bg = (Boolean)SimpleHudManager.get().getHud().darkenBackground.get();
         if (SimpleHudManager.get().getHud().active() && (Boolean)hud.showSimple.get()) {
            String display = "CACTUS";
            List<String> lines = new ArrayList();
            List<Supplier<String>> elementList = SimpleHudManager.get().getHudElements();
            int maxWidth = (int)((double)CactusConstants.mc.field_1772.method_1727(display) * 1.5D + (double)CactusConstants.mc.field_1772.method_1727(CactusConstants.VERSION) + 10.0D + 35.0D);
            Iterator var13 = elementList.iterator();

            label69:
            while(true) {
               String elementString;
               do {
                  if (!var13.hasNext()) {
                     float hudScale = (float)(Integer)SimpleHudManager.get().getHud().scale.get() / 4.0F;
                     context.method_51448().pushMatrix();
                     context.method_51448().scale(hudScale, hudScale);
                     if (bg) {
                        int var10004 = lines.size();
                        Objects.requireNonNull(CactusConstants.mc.field_1772);
                        double var26 = (double)(var10004 * (9 + 2));
                        Objects.requireNonNull(CactusConstants.mc.field_1772);
                        context.method_25294(4, 4, maxWidth, (int)(var26 + 9.0D * 1.5D + 5.0D) + 20, ColorUtils.withAlpha(Color.BLACK, 0.6F).getRGB());
                     }

                     context.method_51448().pushMatrix();
                     this.scale(context.method_51448(), 1.5F);
                     int y = 6;
                     context.method_51448().pushMatrix();
                     float s = 0.625F;
                     context.method_51448().scale(s, s);
                     context.method_25290(class_10799.field_56883, class_2960.method_60655("cactus", "textures/cactus.png"), 9, y + 1, 0.0F, 0.0F, 16, 16, 16, 16);
                     context.method_51448().popMatrix();
                     context.method_51433(CactusConstants.mc.field_1772, display, 19, y, CactusClient.getInstance().getRGB(), !bg);
                     this.normalizeScale(context.method_51448());
                     context.method_51448().popMatrix();
                     context.method_51433(CactusConstants.mc.field_1772, CactusConstants.VERSION, (int)((double)CactusConstants.mc.field_1772.method_1727(display) * 1.5D) + 30 + 8 + 2, (int)((float)y * 1.5F), CactusClient.getInstance().getRGB(), !bg);
                     Objects.requireNonNull(CactusConstants.mc.field_1772);
                     int y = y + (int)(9.0D * 1.5D) + 5;
                     context.method_25294(5, y, (int)((double)CactusConstants.mc.field_1772.method_1727(display) * 1.5D) + 15 + 16, y + 1, Color.DARK_GRAY.getRGB());
                     y += 5;

                     for(Iterator var24 = lines.iterator(); var24.hasNext(); y += 9 + 2) {
                        String line = (String)var24.next();
                        context.method_51433(CactusConstants.mc.field_1772, line, 12, y, CactusClient.getInstance().getRGB(), !bg);
                        Objects.requireNonNull(CactusConstants.mc.field_1772);
                     }

                     context.method_51448().scale(1.0F / hudScale, 1.0F / hudScale);
                     context.method_51448().popMatrix();
                     break label69;
                  }

                  Supplier<String> supplierElement = (Supplier)var13.next();
                  elementString = (String)supplierElement.get();
               } while(elementString == null);

               String[] var16 = elementString.split("\n");
               int var17 = var16.length;

               for(int var18 = 0; var18 < var17; ++var18) {
                  String line = var16[var18];
                  line = line.replace("\\m", "➥");
                  lines.add(line);
                  maxWidth = Math.max(maxWidth, CactusConstants.mc.field_1772.method_1727(line) + 4);
               }
            }
         }

         if (AutoCrafter.currentPath != null && ModuleManager.get().active(AutoCrafter.class)) {
            this.renderPath(AutoCrafter.currentPath, context, this.method_1756(), scaledWidth - 20, new AtomicInteger(20));
         }

      }
   }

   @Inject(
      method = {"method_34004"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void checkTitle(class_2561 title, CallbackInfo ci) {
      AntiAdvertise antiAd = (AntiAdvertise)ModuleManager.get().get(AntiAdvertise.class);
      if (antiAd.active() && (Boolean)antiAd.blockTitles.get() && antiAd.isAd(title)) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"method_34002"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void checkSubTitle(class_2561 subtitle, CallbackInfo ci) {
      AntiAdvertise antiAd = (AntiAdvertise)ModuleManager.get().get(AntiAdvertise.class);
      if (antiAd.active() && (Boolean)antiAd.blockTitles.get() && antiAd.isAd(subtitle)) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"method_55803(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void renderSidebar(class_332 context, class_9779 tickCounter, CallbackInfo ci) {
      if (CactusConstants.mc.field_61504.method_72776() && (Boolean)((HUD)ModuleManager.get().get(HUD.class)).hideScoreboardInF3.get()) {
         ci.cancel();
      } else if (HudManager.getInstance().getElements().contains(ScoreboardElement.INSTANCE)) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"method_1762"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_332;method_51431(Lnet/minecraft/class_327;Lnet/minecraft/class_1799;II)V"
)}
   )
   public void renderItemInHotbar(class_332 context, int x, int y, class_9779 tickCounter, class_1657 player, class_1799 stack, int seed, CallbackInfo ci) {
      ItemCount itemCount = (ItemCount)ModuleManager.get().get(ItemCount.class);
      if (itemCount.active()) {
         if ((Boolean)itemCount.onlySelectedSlot.get() && CactusConstants.mc.field_1724 != null && !CactusConstants.mc.field_1724.method_31548().method_7391().equals(stack)) {
            return;
         }

         int all = itemCount.scanCountsFor(stack);
         RenderUtils.beginPixelAligned(context);
         int extraScale = (Boolean)itemCount.customFontSize.get() ? (Integer)itemCount.fontSize.get() : 1;
         if (extraScale != 1) {
            context.method_51448().scale((float)extraScale);
         }

         String text = (Boolean)itemCount.numberAbbreviation.get() ? TextUtils.numberAbbreviation((long)all) : String.valueOf(all);
         context.method_25303(CactusConstants.mc.field_1772, text, RenderUtils.multiplyUI(x + (Integer)itemCount.offsetX.get()) / extraScale, RenderUtils.multiplyUI(y + (Integer)itemCount.offsetY.get()) / extraScale, -1);
         RenderUtils.endPixelAligned(context);
      }

   }

   @Inject(
      method = {"method_1765"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void renderStatusEffectOverlay(class_332 context, class_9779 tickCounter, CallbackInfo ci) {
      if (HudManager.getInstance().getElements().stream().anyMatch((element) -> {
         return element instanceof StatusEffectsElement;
      })) {
         ci.cancel();
      }

   }

   @ModifyArgs(
      method = {"method_1736"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_332;method_52706(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/class_2960;IIII)V",
   ordinal = 1
)
   )
   public void modifyCrosshair(Args args) {
      if (CactusConstants.APRILFOOLS) {
         args.set(1, (int)((double)(((float)CactusConstants.mc.method_22683().method_4486() - 15.0F) / 2.0F) + Math.random() * 2.0D - 1.0D));
         args.set(2, (int)((double)(((float)CactusConstants.mc.method_22683().method_4502() - 15.0F) / 2.0F) + Math.random() * 2.0D - 1.0D));
      }

   }

   @WrapOperation(
      method = {"method_1736"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_332;method_52706(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/class_2960;IIII)V",
   ordinal = 0
)}
   )
   public void renderCustomCrosshair(class_332 context, RenderPipeline pipeline, class_2960 identifier, int x, int y, int width, int height, Operation<Void> original) {
      CustomCrosshair module = (CustomCrosshair)ModuleManager.get().get(CustomCrosshair.class);
      if (!module.active()) {
         original.call(new Object[]{context, pipeline, identifier, x, y, width, height});
      } else {
         boolean literalColors = module.active() && (Boolean)module.useLiteralColors.get();
         RenderPipeline targetPipeline = literalColors ? class_10799.field_56883 : pipeline;
         int centerX = context.method_51421();
         int centerY = context.method_51443();
         int customSize = 17;
         int customX = (centerX - customSize) / 2;
         int customY = (centerY - customSize) / 2;
         Graphic2DSetting.Graphic2D graphic = (Graphic2DSetting.Graphic2D)module.crosshair.get();
         if (graphic.hasBuiltTexture()) {
            int color = -1;
            if ((Boolean)module.rgb.get()) {
               color = (new Color(ColorUtils.getFadingRgb((Integer)CactusSettings.get().fadingSpeed.get()))).getRGB() | -16777216;
            }

            context.method_25291(targetPipeline, graphic.getId(), customX, customY, 0.0F, 0.0F, customSize, customSize, customSize, customSize, color);
         } else {
            graphic.rebuildTexture();
         }

      }
   }

   @Unique
   private boolean renderPath(AutoCrafter.AutoCrafterPath path, class_332 context, class_327 textRenderer, int xOff, AtomicInteger yOff) {
      int missing = ColorUtils.appendFullAlpha((Integer)Utils.orElse(class_124.field_1061.method_532(), -1));
      int acquiredLess = ColorUtils.appendFullAlpha((Integer)Utils.orElse(class_124.field_1054.method_532(), -1));
      int acquired = ColorUtils.appendFullAlpha((Integer)Utils.orElse(class_124.field_1060.method_532(), -1));
      int ownY = yOff.get();
      context.method_51427(new class_1799(path.getType()), xOff - 7, ownY - 7);
      context.method_51448().scale(0.5F, 0.5F);
      int var10002 = path.getAvailable();
      context.method_25300(textRenderer, var10002 + "/" + path.getCount(), xOff * 2 + 3, ownY * 2 + 20, path.getCount() > path.getAvailable() ? missing : acquired);
      context.method_51448().scale(2.0F, 2.0F);
      if (path.getAvailable() >= path.getCount()) {
         context.method_73198(xOff + 11, ownY, 9, 2, acquired);
         return true;
      } else if (path.getChildren().isEmpty()) {
         context.method_73198(xOff + 11, ownY, 9, 2, missing);
         return false;
      } else {
         boolean drawGreen = true;
         boolean drawYellow = false;

         int tmp;
         for(Iterator var12 = path.getChildren().iterator(); var12.hasNext(); context.method_73198(xOff, ownY + 16, 2, tmp - ownY - 14, drawGreen ? acquired : (drawYellow ? acquiredLess : missing))) {
            AutoCrafter.AutoCrafterPath child = (AutoCrafter.AutoCrafterPath)var12.next();
            tmp = yOff.get() + 25;
            yOff.set(tmp);
            if (this.renderPath(child, context, textRenderer, xOff - 20, yOff)) {
               drawYellow = true;
            } else {
               drawGreen = false;
            }
         }

         context.method_73198(xOff + 11, ownY, 9, 2, !drawGreen && !drawYellow ? missing : acquiredLess);
         return drawGreen;
      }
   }

   @Unique
   private void scale(Matrix3x2fStack matrix, float f) {
      if (this.scale != 1.0F) {
         this.normalizeScale(matrix);
      }

      this.scale = f;
      matrix.scale(f, f);
   }

   @Unique
   private void normalizeScale(Matrix3x2fStack matrix) {
      float unscale = 1.0F / this.scale;
      matrix.scale(unscale, unscale);
      this.scale = 1.0F;
   }

   @Unique
   private int gridToScaled(int origin) {
      float unscale = 1.0F / this.scale;
      return (int)((float)origin * unscale);
   }
}
