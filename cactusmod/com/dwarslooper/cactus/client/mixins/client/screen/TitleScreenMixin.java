package com.dwarslooper.cactus.client.mixins.client.screen;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.addon.gui.CactusAddonsScreen;
import com.dwarslooper.cactus.client.addon.v2.AddonHandler;
import com.dwarslooper.cactus.client.event.CRunnableClickEvent;
import com.dwarslooper.cactus.client.feature.content.ContentPackDependent;
import com.dwarslooper.cactus.client.feature.content.ContentPackManager;
import com.dwarslooper.cactus.client.gui.screen.impl.BackgroundSelectorScreen;
import com.dwarslooper.cactus.client.gui.screen.impl.ButtonOptions;
import com.dwarslooper.cactus.client.gui.screen.impl.CactusMainScreen;
import com.dwarslooper.cactus.client.gui.screen.impl.ServiceScopeSelectScreen;
import com.dwarslooper.cactus.client.gui.screen.window.UpdateScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.TestDynamicHScreen;
import com.dwarslooper.cactus.client.render.GradientTextRenderer;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.systems.tutorial.TutorialGuide;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.CactusRemoteMeta;
import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import com.dwarslooper.cactus.client.util.generic.ScreenUtils;
import com.dwarslooper.cactus.client.util.mixinterface.IBackgroundAccessImpl;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_10799;
import net.minecraft.class_1109;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_3417;
import net.minecraft.class_3532;
import net.minecraft.class_364;
import net.minecraft.class_437;
import net.minecraft.class_442;
import net.minecraft.class_5250;
import net.minecraft.class_5819;
import net.minecraft.class_8021;
import org.joml.Vector2d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@ContentPackDependent("title_screen")
@Mixin({class_442.class})
public abstract class TitleScreenMixin extends class_437 {
   @Unique
   private static final class_2960 ICON = class_2960.method_60655("cactus", "textures/cactus.png");
   @Unique
   private boolean featuresEnabled;
   @Unique
   private boolean shouldSnow;
   @Unique
   private boolean removeRealms;
   @Unique
   private boolean autism;
   @Unique
   private static boolean isClicked;
   @Unique
   public CTextureButtonWidget cactusButton;
   @Unique
   private final List<Vector2d> snow = new ArrayList();
   @Unique
   private final class_2561 CHANGE_BACKGROUND_TEXT = class_2561.method_43471("gui.screen.backgroundSelector.info");
   @Unique
   private float renderAlpha = 1.0F;

   @Shadow
   public abstract void method_25394(class_332 var1, int var2, int var3, float var4);

   public TitleScreenMixin(class_2561 title) {
      super(title);
   }

   @Inject(
      method = {"method_25426"},
      at = {@At("TAIL")}
   )
   public void onInit(CallbackInfo ci) {
      this.featuresEnabled = ContentPackManager.get().isEnabled(this.getClass());
      this.shouldSnow = Calendar.getInstance().get(2) == 11 && this.featuresEnabled;
      this.removeRealms = ContentPackManager.get().isEnabled(ContentPackManager.get().ofId("no_realms"));
      this.snow.clear();
      boolean buttonByOptions = (Boolean)ButtonOptions.get().cactusByOptionsButton.get();
      boolean removeLangAndAccessibility = (Boolean)ButtonOptions.get().cleanTitleScreen.get();
      int l = this.field_22790 / 4 + 48;
      this.method_37063(this.cactusButton = new CTextureButtonWidget(this.field_22789 / 2 + 128, l + 72 + 12, 0, class_2561.method_43471("gui.screen.cactus.title"), (button) -> {
         ((class_310)Objects.requireNonNull(this.field_22787)).method_1507(new CactusMainScreen(this));
      }));
      if (buttonByOptions) {
         class_339 optionsWidget = ScreenUtils.getButton(this, "menu.options");
         if (optionsWidget != null) {
            int x = optionsWidget.method_46426();
            optionsWidget.method_46421(x + 24);
            optionsWidget.method_25358(optionsWidget.method_25368() - 24);
            this.cactusButton.method_48229(x, optionsWidget.method_46427());
         }
      }

      if ((Boolean)CactusSettings.get().experiments.get()) {
         this.method_37063(new CButtonWidget(0, this.field_22790 - 30, 60, 20, class_2561.method_43470("Tutorial"), (button) -> {
            TutorialGuide.get().start();
         }));
         this.method_37063(new CButtonWidget(64, this.field_22790 - 30, 60, 20, class_2561.method_43470("Scopes"), (button) -> {
            CactusConstants.mc.method_1507(new ServiceScopeSelectScreen());
         }));
         this.method_37063(new CButtonWidget(124, this.field_22790 - 30, 60, 20, class_2561.method_43470("WIDGET"), (button) -> {
            CactusConstants.mc.method_1507(new TestDynamicHScreen());
         }));
      }

      if (removeLangAndAccessibility) {
         if (ScreenUtils.deleteButton(this, "narrator.button.accessibility") && !buttonByOptions) {
            this.cactusButton.method_46421(this.cactusButton.method_46426() - 24);
         }

         ScreenUtils.deleteButton(this, "narrator.button.language");
      }

      isClicked = false;
   }

   @Inject(
      method = {"method_25402"},
      at = {@At("TAIL")}
   )
   public void onClick(class_11909 click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
      if (click.comp_4798() < 16.0D && click.comp_4799() < 16.0D) {
         Optional<CactusRemoteMeta.UpdateInfo> updateInfo = CactusRemoteMeta.checkForUpdate();
         updateInfo.ifPresent((info) -> {
            CactusConstants.mc.method_1507((new UpdateScreen(info.version(), info.downloadUri())).onSubmit((i) -> {
               if (i == UpdateScreen.UserResponse.DOWNLOAD) {
               }

            }));
         });
      } else if (!(Boolean)cir.getReturnValue() && click.method_74245() == 1) {
         CactusConstants.mc.method_1507(new BackgroundSelectorScreen(this, (b) -> {
            ((IBackgroundAccessImpl)this).cactus$updateBackground();
         }));
         CactusConstants.mc.method_1483().method_4873(class_1109.method_47978(class_3417.field_15015, 1.0F));
      } else {
         isClicked = !isClicked;
      }
   }

   @Inject(
      method = {"method_25394"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_8020;method_48209(Lnet/minecraft/class_332;IF)V"
)}
   )
   public void onRender(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      if (CactusConstants.APRILFOOLS && isClicked) {
         class_5819 rnd = class_5819.method_43047();
         Iterator var7 = List.copyOf(this.method_25396()).iterator();

         while(true) {
            class_8021 widget;
            do {
               do {
                  do {
                     do {
                        class_364 e;
                        do {
                           if (!var7.hasNext()) {
                              return;
                           }

                           e = (class_364)var7.next();
                        } while(!(e instanceof class_8021));

                        widget = (class_8021)e;
                     } while(mouseX <= widget.method_46426());
                  } while(mouseX >= widget.method_46426() + widget.method_25368());
               } while(mouseY <= widget.method_46427());
            } while(mouseY >= widget.method_46427() + widget.method_25364());

            int newX = -1;

            int newY;
            int offY;
            for(newY = -1; newX <= 0 || newX >= this.field_22789 - widget.method_25368() || newY <= 0 || newY >= this.field_22790 - widget.method_25364(); newY = widget.method_46427() + offY) {
               int deg = rnd.method_43048(360);
               int offX = (int)(Math.cos(Math.toRadians((double)deg)) * 50.0D);
               offY = (int)(Math.sin(Math.toRadians((double)deg)) * 50.0D);
               newX = widget.method_46426() + offX;
            }

            widget.method_48229(newX, newY);
         }
      }
   }

   @Inject(
      method = {"method_25394"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_332;method_25303(Lnet/minecraft/class_327;Ljava/lang/String;III)V"
)}
   )
   public void onRenderOverlays(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      int i;
      if (this.autism) {
         List<String> texts = List.of("Die Viecher leuchten", "Also ich kann schon lesen", "Ich bin Nicht Low Aber auch nicht High", "ICH HAB AIDS UND HIV... HIIIIIHIII", "Ich hab grade. Nen Kaffee getrunken");

         for(i = 0; i < texts.size(); ++i) {
            String var10001 = (String)texts.get(i);
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            GradientTextRenderer.renderGradientText(context, var10001, 40, 60 + 9 * i, List.of(new Color(-65536), new Color(-256), new Color(-16711936), new Color(-16711681), new Color(-16776961), new Color(-65281)), System.currentTimeMillis() + (long)(i * 100), 2.0F, GradientTextRenderer.Animation.LINEAR_LOOPBACK_FORWARD, true, true);
         }
      }

      int i = class_3532.method_15386(this.renderAlpha * 255.0F) << 24;
      int var10000 = this.field_22790 - 10;
      Objects.requireNonNull(this.field_22793);
      i = var10000 - 9;
      if (this.removeRealms && FabricLoader.getInstance().isModLoaded("modmenu")) {
         Objects.requireNonNull(this.field_22793);
         i += 9;
      }

      RenderUtils.drawTextAlignedRight(context, this.CHANGE_BACKGROUND_TEXT, this.field_22789 - 2, i, -1 | i, true);
      context.method_51448().pushMatrix();
      context.method_25290(class_10799.field_56883, ICON, 1, 1, 0.0F, 0.0F, 16, 16, 16, 16);
      if (CactusRemoteMeta.isOutdated()) {
         RenderUtils.drawImportantNotificationIcon(context, 14, 1, 0.6F);
         if (mouseX < 16 && mouseY < 16) {
            context.method_51448().pushMatrix();
            context.method_51448().translate(0.0F, 0.0F);
            context.method_51434(this.field_22793, List.of(class_2561.method_43470("This build is out of date!"), class_2561.method_43470("It will probably not be compatible with newer online features."), class_2561.method_43470("§cUpdate as soon as possible!")), Math.max(0, mouseX), Math.max(0, mouseY));
            context.method_51448().popMatrix();
         }
      }

      context.method_51448().popMatrix();
      context.method_51448().pushMatrix();
      float s = 0.6666667F;
      context.method_51448().scale(s, s);
      context.method_25303(this.field_22793, "Cactus Mod " + String.valueOf(CactusConstants.DEVBUILD ? "DEV-" + String.valueOf(CactusConstants.META.getVersion()) : CactusConstants.META.getVersion()), 28, 4, CactusClient.getInstance().getRGB());
      AddonHandler addonHandler = CactusClient.getInstance().getAddonHandler();
      if (!addonHandler.getAddons().isEmpty()) {
         int addonCount = addonHandler.getAddons().size();
         class_327 var12 = this.field_22793;
         class_5250 var10002 = class_2561.method_43470(addonCount + " addon" + (addonCount == 1 ? "" : "s") + " loaded").method_27694((style) -> {
            return style.method_10958(new CRunnableClickEvent(() -> {
               CactusConstants.mc.method_1507(new CactusAddonsScreen(this));
            }));
         });
         Objects.requireNonNull(this.field_22793);
         context.method_27535(var12, var10002, 28, 6 + 9, CactusClient.getInstance().getRGB() | i);
      }

      context.method_51448().popMatrix();
      this.snow.forEach((vec) -> {
         context.method_25294((int)vec.x, (int)vec.y, (int)vec.x + 1, (int)vec.y + 1, -1);
      });
   }

   @Inject(
      method = {"method_25393"},
      at = {@At("HEAD")}
   )
   public void onTick(CallbackInfo ci) {
      if (this.shouldSnow || CactusConstants.APRILFOOLS) {
         if (Math.random() < 0.9999D) {
            Vector2d vec = new Vector2d((double)class_5819.method_43047().method_39332(0, this.field_22789), -4.0D);
            this.snow.add(vec);
         }

         this.snow.forEach((v) -> {
            v.set(v.x, v.y + 1.0D);
         });
         this.snow.removeIf((v) -> {
            return v.y > (double)this.field_22790;
         });
      }

   }

   @Inject(
      method = {"method_2253"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void removeRealmsNotificationsWhenDisabled(CallbackInfoReturnable<Boolean> cir) {
      if (this.removeRealms) {
         cir.setReturnValue(false);
      }

   }

   @Mixin(
      value = {class_442.class},
      priority = 4100
   )
   private static class TitleScreenLastMixin extends class_437 {
      public TitleScreenLastMixin(class_2561 title) {
         super(title);
      }

      @Inject(
         method = {"method_25426"},
         at = {@At("TAIL")}
      )
      public void onTitleScreenLast(CallbackInfo ci) {
         if (ContentPackManager.get().isEnabled(ContentPackManager.get().ofId("no_realms")) || (Boolean)ButtonOptions.get().removeRealmsButton.get()) {
            ScreenUtils.deleteButton(this, "menu.online");
         }

      }
   }
}
