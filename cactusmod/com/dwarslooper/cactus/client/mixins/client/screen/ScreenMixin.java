package com.dwarslooper.cactus.client.mixins.client.screen;

import com.dwarslooper.cactus.client.event.CRunnableClickEvent;
import com.dwarslooper.cactus.client.feature.content.ContentPackDependent;
import com.dwarslooper.cactus.client.feature.content.ContentPackManager;
import com.dwarslooper.cactus.client.gui.background.DynamicCubemapTexture;
import com.dwarslooper.cactus.client.gui.background.LoadedBackground;
import com.dwarslooper.cactus.client.gui.screen.impl.BackgroundSelectorScreen;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.userscript.UserscriptManager;
import com.dwarslooper.cactus.client.userscript.impl.ButtonModifier;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.InputUtilWrapper;
import com.dwarslooper.cactus.client.util.mixinterface.DisconnectedScreenAccess;
import com.dwarslooper.cactus.client.util.mixinterface.IBackgroundAccessImpl;
import com.dwarslooper.cactus.client.util.modules.TooltipScrollHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import net.minecraft.class_1044;
import net.minecraft.class_10799;
import net.minecraft.class_11405;
import net.minecraft.class_11909;
import net.minecraft.class_2558;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_364;
import net.minecraft.class_4068;
import net.minecraft.class_4069;
import net.minecraft.class_437;
import net.minecraft.class_6379;
import net.minecraft.class_751;
import net.minecraft.class_766;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ContentPackDependent("title_screen")
@Mixin({class_437.class})
public abstract class ScreenMixin implements IBackgroundAccessImpl, class_4069 {
   @Shadow
   public int field_22789;
   @Shadow
   public int field_22790;
   @Unique
   private static class_766 PANORAMA_RENDERER;
   @Unique
   private static class_2960 background;
   @Unique
   private Vector2d delta;
   @Unique
   private class_339 dragging;

   @Shadow
   protected abstract <T extends class_364 & class_4068 & class_6379> T method_37063(T var1);

   @Inject(
      method = {"method_71999"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void cactus$handleClickEvent(class_2558 clickEvent, class_310 client, class_437 screenAfterRun, CallbackInfo ci) {
      if (clickEvent instanceof CRunnableClickEvent) {
         CRunnableClickEvent cactusClickEvent = (CRunnableClickEvent)clickEvent;
         cactusClickEvent.call();
         ci.cancel();
      }

   }

   @Inject(
      method = {"method_25423(II)V"},
      at = {@At("TAIL")}
   )
   public void updateUSButtons(CallbackInfo ci) {
      this.cactus$updateBackground();
      UserscriptManager.getScripts().values().forEach((us) -> {
         if (us instanceof ButtonModifier) {
            ButtonModifier bm = (ButtonModifier)us;
            bm.invoke((class_437)this);
         }

      });
   }

   @Inject(
      method = {"method_57728"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void renderModifiedBackground(class_332 context, float delta, CallbackInfo ci) {
      this.cactus$renderBackground(context, delta, 1.0F, ci);
   }

   @Unique
   public void cactus$renderBackground(class_332 context, float delta, float alpha, CallbackInfo ci) {
      LoadedBackground selected = BackgroundSelectorScreen.getSelected();
      if ((selected == null || selected instanceof LoadedBackground.Rotating) && PANORAMA_RENDERER != null) {
         PANORAMA_RENDERER.method_3317(context, this.field_22789, this.field_22790, true);
         ci.cancel();
      } else if (selected instanceof LoadedBackground.Static && selected.getTextureIdentifier() != null) {
         context.method_25290(class_10799.field_56883, selected.getTextureIdentifier(), 0, 0, 0.0F, 0.0F, this.field_22789, this.field_22790, this.field_22789, this.field_22790);
         ci.cancel();
      }

   }

   @Unique
   public void cactus$updateBackground() {
      boolean enabled = ContentPackManager.isEnabled();
      class_2960 identifier = class_2960.method_60655(enabled ? "cactus" : "minecraft", "textures/gui/title/background/panorama");
      IntStream.range(0, 6).forEach((face) -> {
         CactusConstants.mc.method_1531().method_4619(identifier.method_48331("_" + face + ".png"));
      });
      if (BackgroundSelectorScreen.getSelected() != null) {
         identifier = BackgroundSelectorScreen.getSelected().getTextureIdentifier();
      }

      if (!identifier.equals(background)) {
         background = identifier;
         LoadedBackground selected = BackgroundSelectorScreen.getSelected();
         if (!(selected instanceof LoadedBackground.Static)) {
            PANORAMA_RENDERER = new class_766(new class_751(identifier));
            if (selected instanceof LoadedBackground.Rotating) {
               LoadedBackground.Rotating rotating = (LoadedBackground.Rotating)selected;
               CactusConstants.mc.method_1531().method_65876(identifier, new DynamicCubemapTexture(identifier, rotating.getParts()));
            } else {
               class_1044 texture = CactusConstants.mc.method_1531().method_4619(identifier);
               if (!(texture instanceof class_11405)) {
                  PANORAMA_RENDERER.method_71651(CactusConstants.mc.method_1531());
               }
            }
         }
      }

   }

   @Inject(
      method = {"method_47413"},
      at = {@At("TAIL")}
   )
   public void renderComponentInfo(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      if ((Boolean)CactusSettings.get().uiDebug.get()) {
         this.iterateAndRenderChildren(context, (class_437)this, new ArrayList());
      }
   }

   @Inject(
      method = {"method_47413"},
      at = {@At("TAIL")}
   )
   private void cactus$autoReconnect(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      if (this instanceof DisconnectedScreenAccess) {
         DisconnectedScreenAccess access = (DisconnectedScreenAccess)this;
         access.cactus$autoReconnectTick();
      }

   }

   @Unique
   private boolean iterateAndRenderChildren(class_332 context, class_4069 element, Collection<String> nestingPath) {
      boolean b = false;
      Iterator var5 = element.method_25396().iterator();

      while(var5.hasNext()) {
         class_364 child = (class_364)var5.next();
         List<String> nPathCopy = new ArrayList(nestingPath);
         if (child instanceof class_4069) {
            class_4069 pe = (class_4069)child;
            nPathCopy.add(pe.getClass().getSimpleName());
            b = b || this.iterateAndRenderChildren(context, pe, nPathCopy);
         }

         if (!b && child instanceof class_339) {
            class_339 cw = (class_339)child;
            b = this.drawElementInfos(context, cw, nestingPath);
         }
      }

      return b;
   }

   @Unique
   private boolean drawElementInfos(class_332 context, class_339 widget, Collection<String> nestingPath) {
      if (widget.method_49606()) {
         if (InputUtilWrapper.hasShiftDown()) {
            context.method_51738(0, this.field_22789, widget.method_46427(), -65536);
            context.method_51738(0, this.field_22789, widget.method_46427() + widget.method_25364() - 1, -65536);
            context.method_51742(widget.method_46426(), 0, this.field_22790, -16711936);
            context.method_51742(widget.method_46426() + widget.method_25368() - 1, 0, this.field_22790, -16711936);
         }

         Objects.requireNonNull(CactusConstants.mc.field_1772);
         int a = 9 + 2;
         int top = widget.method_46427() - a - 3;
         this.drawElementInfo(context, 0, 0, "%s,%s (%s,%s)".formatted(new Object[]{widget.method_46426(), widget.method_46427(), widget.method_25368(), widget.method_25364()}), -1);
         this.drawElementInfo(context, 0, a, "§d" + widget.getClass().getSimpleName(), -1);
         String pkg = widget.getClass().getPackageName();
         String modInfo;
         if (pkg.startsWith("net.minecraft")) {
            modInfo = "§bMinecraft";
         } else if (pkg.startsWith("com.dwarslooper.cactus")) {
            modInfo = "§aCactus";
         } else {
            modInfo = "§cForeign";
         }

         this.drawElementInfo(context, 0, a * 2, modInfo, -1);
         if (!nestingPath.isEmpty()) {
            this.drawElementInfo(context, 0, a * 3, "§e" + String.join(" -> ", nestingPath), -1);
         }

         return true;
      } else {
         return false;
      }
   }

   @Unique
   private void drawElementInfo(class_332 context, int x, int y, String text, int color) {
      context.method_51448().pushMatrix();
      context.method_51448().translate(0.0F, 0.0F);
      int w = CactusConstants.mc.field_1772.method_1727(text);
      int var10003 = x + w + 2;
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      context.method_25294(x, y, var10003, y + 9 + 2, -1442840576);
      context.method_51433(CactusConstants.mc.field_1772, text, x + 1, y + 1, color, false);
      context.method_51448().popMatrix();
   }

   public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
      if (InputUtilWrapper.hasControlDown() && (Boolean)CactusSettings.get().uiDebug.get()) {
         Iterator var3 = this.method_25396().iterator();

         while(var3.hasNext()) {
            class_364 child = (class_364)var3.next();
            if (child instanceof class_339) {
               class_339 cw = (class_339)child;
               if (click.comp_4798() >= (double)cw.method_46426() && click.comp_4799() >= (double)cw.method_46427() && click.comp_4798() < (double)(cw.method_46426() + cw.method_25368()) && click.comp_4799() < (double)(cw.method_46427() + cw.method_25364())) {
                  this.delta = new Vector2d((double)cw.method_46426(), (double)cw.method_46427());
                  this.dragging = cw;
                  return false;
               }
            }
         }
      }

      return super.method_25402(click, doubled);
   }

   @Inject(
      method = {"method_25419"},
      at = {@At("HEAD")}
   )
   private void cactus$resetTooltipScrollOnClose(CallbackInfo ci) {
      TooltipScrollHandler.reset();
   }

   public boolean method_25406(@NotNull class_11909 click) {
      this.dragging = null;
      return super.method_25406(click);
   }

   public boolean method_25403(@NotNull class_11909 click, double offsetX, double offsetY) {
      if (this.dragging != null) {
         this.delta.add(offsetX, offsetY);
         this.dragging.method_48229((int)this.delta.x, (int)this.delta.y);
      }

      return super.method_25403(click, offsetX, offsetY);
   }

   @Unique
   public class_766 cactus$getCubeMapRenderer() {
      return PANORAMA_RENDERER;
   }
}
