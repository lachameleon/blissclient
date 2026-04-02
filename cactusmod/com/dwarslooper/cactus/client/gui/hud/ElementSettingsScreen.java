package com.dwarslooper.cactus.client.gui.hud;

import com.dwarslooper.cactus.client.gui.hud.element.impl.HudElement;
import com.dwarslooper.cactus.client.systems.config.settings.gui.AbstractSettingScreen;
import net.minecraft.class_332;
import org.jetbrains.annotations.NotNull;

public class ElementSettingsScreen extends AbstractSettingScreen {
   private final HudElement<?> element;

   public ElementSettingsScreen(HudElement<?> element) {
      super("hudElementSettings", element.settings);
      this.element = element;
   }

   public void method_25426() {
      super.method_25426();
      this.addCopyPaste();
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_25300(this.field_22793, this.element.getName(), this.field_22789 / 2, 8, -1);
   }
}
