package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.addon.gui.CactusAddonsScreen;
import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.ServiceConnectionStateEvent;
import com.dwarslooper.cactus.client.feature.content.impl.ContentPackScreen;
import com.dwarslooper.cactus.client.gui.hud.HudEditorScreen;
import com.dwarslooper.cactus.client.gui.music.ClipArrangementScreen;
import com.dwarslooper.cactus.client.gui.screen.EventHandlingCScreen;
import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.irc.IRCStatus;
import com.dwarslooper.cactus.client.systems.NotificationManager;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.systems.config.profiles.gui.ProfileListScreen;
import com.dwarslooper.cactus.client.systems.hdb.gui.HDBScreen;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import java.util.Objects;
import net.minecraft.class_156;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_5250;
import net.minecraft.class_7077;
import net.minecraft.class_7842;
import net.minecraft.class_7843;
import net.minecraft.class_7845;
import net.minecraft.class_7847;
import net.minecraft.class_7919;
import net.minecraft.class_4185.class_4241;
import net.minecraft.class_7845.class_7939;
import org.jetbrains.annotations.NotNull;

public class CactusMainScreen extends EventHandlingCScreen {
   private static final class_7919 noServiceTooltip = class_7919.method_47407(class_2561.method_43471("irc.not_connected"));
   private class_339 msgWidget;

   public CactusMainScreen(class_437 parent) {
      super("cactus");
      this.parent = parent;
   }

   public void method_25426() {
      super.method_25426();
      boolean serviceAvailable = IRCClient.connected();
      boolean experiments = (Boolean)CactusSettings.get().experiments.get();
      if (CactusConstants.DEVBUILD) {
         String devoptions = "Developer Tools";
         int var10004 = this.field_22790 - 4;
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         var10004 -= 9;
         int var10005 = CactusConstants.mc.field_1772.method_1727(devoptions);
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         this.method_37063(new class_7077(4, var10004, var10005, 9, class_2561.method_43470(devoptions), (button) -> {
            CactusConstants.mc.method_1507(new DevOptionsScreen());
         }, CactusConstants.mc.field_1772));
      }

      boolean compact = (Boolean)CactusSettings.get().compactUIDesign.get();
      int columns = compact ? 4 : 10;
      int occupy = compact ? 1 : 5;
      class_7845 grid = new class_7845();
      grid.method_46458().method_46467().method_46464(2);
      class_7939 adder = grid.method_47610(columns);
      class_7847 padTop = grid.method_46457().method_46471(8).method_46461();
      adder.method_47613(this.createButton(6, this.btn("options"), (b) -> {
         CactusConstants.mc.method_1507(new CactusSettingsScreen(this));
      }), occupy);
      adder.method_47613(this.createButton(8, this.btn("modules"), (b) -> {
         CactusConstants.mc.method_1507(new ModuleListScreen());
      }), occupy);
      adder.method_47613(this.createButton(12, this.btn("content"), (b) -> {
         CactusConstants.mc.method_1507(new ContentPackScreen());
      }), occupy);
      adder.method_47613(this.createButton(7, this.btn("accounts"), (b) -> {
         CactusConstants.mc.method_1507(new AccountListScreen(this));
      }), occupy);
      adder.method_47613(this.createButton(14, this.btn("hud"), (b) -> {
         CactusConstants.mc.method_1507(new HudEditorScreen());
      }), occupy);
      adder.method_47613(this.createButton(13, this.btn("servers"), (b) -> {
         CactusConstants.mc.method_1507(new LocalServerListScreen(this));
      }), occupy);
      adder.method_47613(this.createButton(16, this.btn("macros"), (b) -> {
         CactusConstants.mc.method_1507(new MacroListScreen(this));
      }), occupy);
      adder.method_47613(this.createButton(17, this.btn("screenshots"), (b) -> {
         CactusConstants.mc.method_1507(new ScreenshotListScreen());
      }), occupy);
      if (!compact) {
         adder.method_47614(new class_7842(class_2561.method_43470(this.getTranslatableElement("tab.online", new Object[0])), CactusConstants.mc.field_1772), columns, padTop);
      }

      ((class_339)adder.method_47613(this.createButton(3, this.btn("feedback"), (b) -> {
         CactusConstants.mc.method_1507(new FeedbackScreen(this));
      }), occupy)).field_22763 = serviceAvailable;
      ((class_339)adder.method_47613(this.createButton(18, this.btn("cosmetics"), (b) -> {
         CactusConstants.mc.method_1507(new CosmeticsListScreen(this));
      }), occupy)).field_22763 = serviceAvailable;
      if (!compact) {
         adder.method_47614(new class_7842(class_2561.method_43470(this.getTranslatableElement("tab.other", new Object[0])), CactusConstants.mc.field_1772), columns, padTop);
      }

      boolean compressOther = (Boolean)CactusSettings.get().collapseOther.get();
      int otherColumns = compressOther ? 1 : occupy;
      adder.method_47613(this.msgWidget = this.createButton(1, this.btn("notifications"), compressOther, (b) -> {
         CactusConstants.mc.method_1507(new NotificationsScreen(this));
      }), otherColumns);
      adder.method_47613(this.createButton(2, this.btn("about"), compressOther, (b) -> {
         CactusConstants.mc.method_1507(new AboutScreen(this));
      }), otherColumns);
      adder.method_47613(this.createButton(4, this.btn("addons"), compressOther, (b) -> {
         CactusConstants.mc.method_1507(new CactusAddonsScreen(this));
      }), otherColumns);
      adder.method_47613(this.createButton(9, this.btn("folder"), compressOther, (b) -> {
         class_156.method_668().method_672(CactusConstants.DIRECTORY);
      }), otherColumns);
      if (experiments) {
         adder.method_47613(this.createButton(100, class_2561.method_43470("HDB Loader"), compressOther, (b) -> {
            CactusConstants.mc.method_1507(new HDBScreen());
         }), otherColumns);
         adder.method_47613(this.createButton(100, class_2561.method_43470("NBS Editor"), compressOther, (b) -> {
            CactusConstants.mc.method_1507(new ClipArrangementScreen());
         }), otherColumns);
         adder.method_47613(this.createButton(22, class_2561.method_43470("Profiles"), compressOther, (b) -> {
            CactusConstants.mc.method_1507(new ProfileListScreen());
         }), otherColumns);
      }

      grid.method_48227((w) -> {
         class_339 widget = (class_339)w;
         if (widget instanceof class_4185 && !widget.field_22763) {
            widget.method_47400(noServiceTooltip);
         }

         if (compact) {
            widget.method_25358(20);
         }

         if (widget.method_25368() == 20) {
            widget.method_47400(class_7919.method_47407(widget.method_25369()));
            widget.method_25355(class_2561.method_43473());
         }

      });
      grid.method_48222();
      class_7843.method_46443(grid, 0, 24, this.field_22789, this.field_22790, 0.5F, 0.0F);
      grid.method_48227((w) -> {
         this.method_37063((class_339)w);
      });
   }

   @EventHandler
   public void onPacket(ServiceConnectionStateEvent event) {
      if (event.status() != IRCStatus.CONNECTING) {
         CactusConstants.mc.execute(() -> {
            this.method_41843();
         });
      }

   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_25300(this.field_22793, "Cactus Mod", this.field_22789 / 2, 8, -1);
      if (NotificationManager.hasUnread()) {
         RenderUtils.drawImportantNotificationIcon(context, this.msgWidget.method_46426() + this.msgWidget.method_25368() - 4 - 2, this.msgWidget.method_46427() - 4, 1.0F);
      }

   }

   private class_339 createButton(int iconIndex, class_2561 message, boolean small, class_4241 action) {
      class_339 widget = new CTextureButtonWidget(0, 0, iconIndex * 20, message, action);
      widget.method_25358(small ? 20 : 120);
      return widget;
   }

   private class_339 createButton(int iconIndex, class_2561 message, class_4241 action) {
      return this.createButton(iconIndex, message, false, action);
   }

   private class_5250 btn(String key) {
      return class_2561.method_43470(this.getTranslatableElement("button." + key, new Object[0]));
   }
}
