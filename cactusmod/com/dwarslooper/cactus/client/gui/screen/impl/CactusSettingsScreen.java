package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.gui.screen.window.WindowScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.list.CheckboxListWidget;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.systems.config.FileConfiguration;
import com.dwarslooper.cactus.client.systems.config.profiles.gui.ProfileListScreen;
import com.dwarslooper.cactus.client.systems.config.settings.gui.AbstractSettingScreen;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_5244;
import org.jetbrains.annotations.NotNull;

public class CactusSettingsScreen extends AbstractSettingScreen {
   public CactusSettingsScreen(class_437 parent) {
      super("cactus_settings", CactusSettings.get().settings);
      this.parent = parent;
   }

   public void method_25426() {
      super.method_25426();
      this.method_37063(new CButtonWidget(30, 6, 80, 20, class_2561.method_43471("gui.screen.profiles.title"), (button) -> {
         CactusConstants.mc.method_1507(new ProfileListScreen());
      }));
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 8, -1);
   }

   private static class ConfigSelectScreen extends WindowScreen {
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
            this.widget.add(config, class_2561.method_43470(config.getName()), config.exportAndImportByDefault());
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
