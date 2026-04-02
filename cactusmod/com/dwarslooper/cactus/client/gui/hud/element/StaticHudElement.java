package com.dwarslooper.cactus.client.gui.hud.element;

import com.dwarslooper.cactus.client.gui.hud.element.impl.HudElement;
import org.joml.Vector2i;

public abstract class StaticHudElement<T extends StaticHudElement<T>> extends HudElement<T> {
   public StaticHudElement(String id, Vector2i size, Vector2i minSize) {
      super(id, size, minSize);
   }

   public StaticHudElement(String id, Vector2i size) {
      super(id, size);
   }

   protected StaticHudElement(String id) {
      super(id);
   }

   public abstract T getInstance();
}
