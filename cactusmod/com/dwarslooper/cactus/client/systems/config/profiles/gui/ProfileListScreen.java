package com.dwarslooper.cactus.client.systems.config.profiles.gui;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.screen.impl.ChoiceWarningScreen;
import com.dwarslooper.cactus.client.gui.screen.window.WindowScreen;
import com.dwarslooper.cactus.client.gui.toast.ToastSystem;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.list.CheckboxListWidget;
import com.dwarslooper.cactus.client.systems.config.ConfigHandler;
import com.dwarslooper.cactus.client.systems.config.FileConfiguration;
import com.dwarslooper.cactus.client.systems.config.profiles.ConfigurationProfile;
import com.dwarslooper.cactus.client.systems.config.profiles.ProfileManager;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.WidgetIcons;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.class_11909;
import net.minecraft.class_124;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_4265;
import net.minecraft.class_437;
import net.minecraft.class_5244;
import net.minecraft.class_6379;
import net.minecraft.class_7919;
import net.minecraft.class_4265.class_4266;
import org.jetbrains.annotations.NotNull;

public class ProfileListScreen extends CScreen {
   private ProfileListScreen.Widget listWidget;

   public ProfileListScreen() {
      super("profiles");
   }

   public void method_25426() {
      super.method_25426();
      this.listWidget = new ProfileListScreen.Widget(this.field_22789, this.field_22790 - 80);
      this.method_37063(this.listWidget);
      this.method_37063(class_4185.method_46430(class_2561.method_43470(this.getTranslatableElement("create", new Object[0])), (b) -> {
         CactusConstants.mc.method_1507(new ProfileEditScreen((ProfileManager)CactusClient.getConfig(ProfileManager.class)));
      }).method_46434(this.field_22789 / 2 - 120 - 2, this.field_22790 - 32, 120, 20).method_46431());
      this.method_37063(class_4185.method_46430(class_5244.field_24334, (b) -> {
         this.method_25419();
      }).method_46434(this.field_22789 / 2 + 2, this.field_22790 - 32, 120, 20).method_46431());
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 8, -1);
   }

   public class Widget extends class_4265<ProfileListScreen.Widget.Entry> {
      public Widget(int width, int height) {
         super(CactusConstants.mc, width, height, 40, 22);
         this.method_25321(new ProfileListScreen.Widget.CurrentEntry());
         ((ProfileManager)CactusClient.getConfig(ProfileManager.class)).getProfiles().forEach((profile) -> {
            this.method_25321(new ProfileListScreen.Widget.ProfileEntry(profile));
         });
      }

      public class CurrentEntry extends ProfileListScreen.Widget.Entry {
         private final class_2561 NAME;
         private final class_339 importWidget;
         private final class_339 exportWidget;
         private final ConfigHandler configHandler;

         public CurrentEntry() {
            super();
            this.NAME = class_2561.method_43470(ProfileListScreen.this.getTranslatableElement("current", new Object[0])).method_27692(class_124.field_1073);
            this.configHandler = CactusClient.getInstance().getConfigHandler();
            this.importWidget = new CTextureButtonWidget(0, 0, WidgetIcons.IMPORT.offsetX(), (button) -> {
               CactusConstants.mc.method_1507(new ChoiceWarningScreen(ProfileListScreen.this, class_2561.method_43470(ProfileListScreen.this.getTranslatableElement("importWarning", new Object[0])), (b) -> {
                  if (b) {
                     CactusConstants.mc.method_1507(new ProfileListScreen.ConfigSelectScreen(ProfileListScreen.this, (map) -> {
                        ((ProfileManager)CactusClient.getConfig(ProfileManager.class)).importConfigurations(map.keySet().stream().map(FileConfiguration::getFile).toList());
                     }));
                  }

               }));
            });
            this.importWidget.method_47400(class_7919.method_47407(class_2561.method_43470(ProfileListScreen.this.getTranslatableElement("import", new Object[0]))));
            this.exportWidget = new CTextureButtonWidget(0, 0, WidgetIcons.EXPORT.offsetX(), (button) -> {
               CactusConstants.mc.method_1507(new ProfileListScreen.ConfigSelectScreen(ProfileListScreen.this, (map) -> {
                  ((ProfileManager)CactusClient.getConfig(ProfileManager.class)).exportConfigurations(map.keySet().stream().map(FileConfiguration::getFile).toList());
               }));
            });
            this.exportWidget.method_47400(class_7919.method_47407(class_2561.method_43470(ProfileListScreen.this.getTranslatableElement("export", new Object[0]))));
         }

         public void load() {
            this.configHandler.reload();
         }

         public void save() {
            this.configHandler.save();
         }

         @NotNull
         public List<? extends class_6379> method_37025() {
            return ImmutableList.of(this.loadWidget, this.saveWidget, this.importWidget, this.exportWidget);
         }

         @NotNull
         public List<? extends class_364> method_25396() {
            return ImmutableList.of(this.loadWidget, this.saveWidget, this.importWidget, this.exportWidget);
         }

         public void method_25343(@NotNull class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int x = this.method_46426();
            int y = this.method_46427();
            int entryWidth = this.method_25368();
            int entryHeight = this.method_25364();
            this.loadWidget.method_48229(x + entryWidth - 23 - 66, y - 1);
            this.saveWidget.method_48229(x + entryWidth - 23 - 44, y - 1);
            this.importWidget.method_48229(x + entryWidth - 23 - 22, y - 1);
            this.exportWidget.method_48229(x + entryWidth - 23, y - 1);
            this.loadWidget.method_25394(context, mouseX, mouseY, tickDelta);
            this.saveWidget.method_25394(context, mouseX, mouseY, tickDelta);
            this.importWidget.method_25394(context, mouseX, mouseY, tickDelta);
            this.exportWidget.method_25394(context, mouseX, mouseY, tickDelta);
            class_327 var10001 = CactusConstants.mc.field_1772;
            class_2561 var10002 = this.NAME;
            int var10003 = x + 4;
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            context.method_27535(var10001, var10002, var10003, y + (entryHeight - 9) / 2 + 1, -1);
         }

         public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
            Widget.this.method_25313(this);
            super.method_25402(click, doubled);
            return true;
         }

         private List<File> getFiles() {
            return this.configHandler.getFiles(this.configHandler.getDirectory());
         }
      }

      public class ProfileEntry extends ProfileListScreen.Widget.Entry {
         private final ConfigurationProfile profile;
         private final class_339 editWidget;

         public ProfileEntry(ConfigurationProfile profile) {
            super();
            this.profile = profile;
            this.editWidget = new CTextureButtonWidget(0, 0, WidgetIcons.EDIT.offsetX(), (b) -> {
               CactusConstants.mc.method_1507(new ProfileEditScreen((ProfileManager)CactusClient.getConfig(ProfileManager.class), profile));
            });
         }

         public void load() {
            ((ProfileManager)CactusClient.getConfig(ProfileManager.class)).load(this.profile);
         }

         public void save() {
            ((ProfileManager)CactusClient.getConfig(ProfileManager.class)).save(this.profile);
         }

         @NotNull
         public List<? extends class_6379> method_37025() {
            return ImmutableList.of(this.loadWidget, this.saveWidget, this.editWidget);
         }

         @NotNull
         public List<? extends class_364> method_25396() {
            return ImmutableList.of(this.loadWidget, this.saveWidget, this.editWidget);
         }

         public void method_25343(@NotNull class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int x = this.method_46426();
            int y = this.method_46427();
            int entryWidth = this.method_25368();
            int entryHeight = this.method_25364();
            this.loadWidget.method_48229(x + entryWidth - 23 - 44, y - 1);
            this.saveWidget.method_48229(x + entryWidth - 23 - 22, y - 1);
            this.editWidget.method_48229(x + entryWidth - 23, y - 1);
            this.loadWidget.method_25394(context, mouseX, mouseY, tickDelta);
            this.saveWidget.method_25394(context, mouseX, mouseY, tickDelta);
            this.editWidget.method_25394(context, mouseX, mouseY, tickDelta);
            class_327 var10001 = CactusConstants.mc.field_1772;
            String var10002 = this.profile.getName();
            int var10003 = x + 4;
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            context.method_25303(var10001, var10002, var10003, y + (entryHeight - 9) / 2 + 1, -1);
         }

         public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
            Widget.this.method_25313(this);
            super.method_25402(click, doubled);
            return true;
         }
      }

      public abstract class Entry extends class_4266<ProfileListScreen.Widget.Entry> {
         protected final class_339 loadWidget;
         protected final class_339 saveWidget;

         public Entry() {
            this.loadWidget = new CTextureButtonWidget(0, 0, WidgetIcons.LOAD.offsetX(), (button) -> {
               CactusConstants.mc.method_1507(new ChoiceWarningScreen(ProfileListScreen.this, class_2561.method_43470(ProfileListScreen.this.getTranslatableElement("loadWarning", new Object[0])), (b) -> {
                  if (b) {
                     this.load();
                     ToastSystem.displayMessage(ProfileListScreen.this.getTranslatableElement("loadSuccess", new Object[0]), "", class_1802.field_8204);
                  }

               }));
            });
            this.loadWidget.method_47400(class_7919.method_47407(class_2561.method_43470(ProfileListScreen.this.getTranslatableElement("load", new Object[0]))));
            this.saveWidget = new CTextureButtonWidget(0, 0, WidgetIcons.SAVE.offsetX(), (b) -> {
               this.save();
               ToastSystem.displayMessage(ProfileListScreen.this.getTranslatableElement("saveSuccess", new Object[0]), "", class_1802.field_8204);
            });
            this.saveWidget.method_47400(class_7919.method_47407(class_2561.method_43470(ProfileListScreen.this.getTranslatableElement("save", new Object[0]))));
         }

         public abstract void load();

         public abstract void save();

         @NotNull
         public List<? extends class_364> method_25396() {
            return ImmutableList.of(this.loadWidget, this.saveWidget);
         }

         public void method_25343(@NotNull class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int x = this.method_46426();
            int y = this.method_46427();
            int entryWidth = this.method_25368();
            this.loadWidget.method_48229(x + entryWidth - 23 - 22, y - 1);
            this.saveWidget.method_48229(x + entryWidth - 23, y - 1);
            this.loadWidget.method_25394(context, mouseX, mouseY, tickDelta);
            this.saveWidget.method_25394(context, mouseX, mouseY, tickDelta);
         }

         public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
            Widget.this.method_25313(this);
            super.method_25402(click, doubled);
            return true;
         }
      }
   }

   public static class ConfigSelectScreen extends WindowScreen {
      private final Consumer<Map<FileConfiguration<?>, Boolean>> callback;
      private CheckboxListWidget<FileConfiguration<?>> widget;

      public ConfigSelectScreen(class_437 parent, Consumer<Map<FileConfiguration<?>, Boolean>> callback) {
         super("configSelection", 240, 200);
         this.parent = parent;
         this.callback = callback;
      }

      public void method_25426() {
         super.method_25426();
         this.widget = new CheckboxListWidget(this.boxWidth() - 8, this.boxHeight() - 12 - 22, this.y() + 6);
         this.widget.method_46421(this.x() + 4);
         this.widget.method_44382(2.147483647E9D);
         CactusClient.getInstance().getConfigHandler().getConfigurations().values().forEach((config) -> {
            if (!config.isInternal()) {
               this.widget.add(config, class_2561.method_43470(config.getDisplayName()), config.exportAndImportByDefault());
            }

         });
         this.method_37063(this.widget);
         this.method_37063(new CButtonWidget(this.x() + this.boxWidth() / 2 + 2, this.y() + this.boxHeight() - 24, 100, 20, class_5244.field_24334, (button) -> {
            this.method_25419();
            this.callback.accept(this.widget.getAsMap());
         }));
         this.method_37063(new CButtonWidget(this.x() + this.boxWidth() / 2 - 102, this.y() + this.boxHeight() - 24, 100, 20, class_5244.field_24335, (button) -> {
            this.method_25419();
         }));
      }
   }
}
