package com.dwarslooper.cactus.client.gui.hud;

import com.dwarslooper.cactus.client.systems.config.settings.gui.AbstractSettingScreen;
import net.minecraft.class_332;
import org.jetbrains.annotations.NotNull;

public class HudSettingsScreen extends AbstractSettingScreen {
   public HudSettingsScreen(HudManager hudManager) {
      super("hudSettings", hudManager.getSettings().settings);
   }

   public void method_25426() {
      super.method_25426();
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
   }
}
