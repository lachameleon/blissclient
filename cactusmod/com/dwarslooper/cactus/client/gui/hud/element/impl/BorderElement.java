package com.dwarslooper.cactus.client.gui.hud.element.impl;

import com.dwarslooper.cactus.client.gui.hud.element.DynamicHudElement;
import net.minecraft.class_332;
import org.joml.Vector2i;

public class BorderElement extends DynamicHudElement<BorderElement> {
   public BorderElement() {
      super("border");
   }

   public BorderElement duplicate() {
      return new BorderElement();
   }

   public void renderContent(class_332 context, int x, int y, int width, int height, int screenWidth, int screenHeight, float delta, boolean inEditor) {
      Vector2i effectiveOrigin = this.getEffectiveOrigin(x, y, screenWidth, screenHeight);
      int x1 = effectiveOrigin.x();
      int y1 = effectiveOrigin.y();
      int x2 = x1 + 120;
      int y2 = y1 + 140;
      int color = -256;
      context.method_51738(x1, x2 - 1, y1, color);
      context.method_51738(x1, x2 - 1, y2 - 1, color);
      context.method_51742(x1, y1, y2 - 1, color);
      context.method_51742(x2 - 1, y1, y2 - 1, color);
   }
}
