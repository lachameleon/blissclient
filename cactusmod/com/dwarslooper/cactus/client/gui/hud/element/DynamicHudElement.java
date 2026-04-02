package com.dwarslooper.cactus.client.gui.hud.element;

import com.dwarslooper.cactus.client.gui.hud.element.impl.HudElement;
import org.joml.Vector2i;

public abstract class DynamicHudElement<T extends DynamicHudElement<T>> extends HudElement<T> {
   public DynamicHudElement(String id, Vector2i size, Vector2i minSize) {
      super(id, size, minSize);
   }

   public DynamicHudElement(String id, Vector2i size) {
      super(id, size);
   }

   protected DynamicHudElement(String id) {
      super(id);
   }

   public abstract T duplicate();
}
