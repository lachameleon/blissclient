package com.dwarslooper.cactus.client.gui.hud;

import com.dwarslooper.cactus.client.gui.hud.element.impl.HudElement;
import java.util.function.Consumer;

public record PresetConfig<T extends HudElement<?>>(Class<T> target, String id, Consumer<T> configurator) {
   public PresetConfig(Class<T> target, String id, Consumer<T> configurator) {
      this.target = target;
      this.id = id;
      this.configurator = configurator;
   }

   public Class<T> target() {
      return this.target;
   }

   public String id() {
      return this.id;
   }

   public Consumer<T> configurator() {
      return this.configurator;
   }
}
