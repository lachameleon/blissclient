package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.systems.ISerializable;
import com.dwarslooper.cactus.client.systems.SingleInstance;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.dwarslooper.cactus.client.systems.config.settings.group.SettingGroup;
import com.dwarslooper.cactus.client.systems.config.settings.gui.AbstractSettingScreen;
import com.dwarslooper.cactus.client.systems.config.settings.gui.SettingContainer;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.google.gson.JsonObject;
import net.minecraft.class_332;
import org.jetbrains.annotations.NotNull;

public class ButtonOptions implements ISerializable<ButtonOptions> {
   public SettingContainer settings = new SettingContainer();
   private final SettingGroup sg;
   public Setting<Boolean> removeRealmsButton;
   public Setting<Boolean> cleanTitleScreen;
   public Setting<Boolean> feedbackToCactus;
   public Setting<Boolean> expandTitleScreen;
   public Setting<Boolean> mainMouseSensitivity;
   public Setting<Boolean> cactusByOptionsButton;

   public ButtonOptions() {
      this.sg = this.settings.getDefault();
      this.removeRealmsButton = this.sg.add(new BooleanSetting("removeRealms", false));
      this.cleanTitleScreen = this.sg.add(new BooleanSetting("removeLangAndAccessibility", false));
      this.feedbackToCactus = this.sg.add(new BooleanSetting("feedbackToCactus", true));
      this.expandTitleScreen = this.sg.add(new BooleanSetting("expandTitleScreenWidget", false));
      this.mainMouseSensitivity = this.sg.add(new BooleanSetting("replaceMouseOptions", false));
      this.cactusByOptionsButton = this.sg.add(new BooleanSetting("cactusByOptions", false));
   }

   public JsonObject toJson(TreeSerializerFilter filter) {
      return this.settings.toJson(filter);
   }

   public ButtonOptions fromJson(JsonObject object) {
      this.settings.fromJson(object);
      return this;
   }

   public static ButtonOptions get() {
      return (ButtonOptions)SingleInstance.of(ButtonOptions.class, ButtonOptions::new);
   }

   public static class ButtonsScreen extends AbstractSettingScreen {
      public ButtonsScreen() {
         super("button_options", ButtonOptions.get().settings);
      }

      public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
         super.method_25394(context, mouseX, mouseY, delta);
         context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 8, -1);
      }
   }
}
