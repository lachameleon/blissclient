package com.dwarslooper.cactus.client.render.tooltip;

import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import net.minecraft.class_1309;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5684;
import org.joml.Quaternionf;
import org.joml.Vector2i;

public abstract class EntityTooltipComponent<T extends class_1309> implements class_5684 {
   private static Quaternionf QUATERNIONF = (new Quaternionf()).rotationXYZ(0.0F, 0.0F, 3.1415927F);
   private static Vector2i PADDING = new Vector2i(20, 10);

   public abstract T getEntity();

   public abstract int getScale();

   public abstract Quaternionf getRotation();

   public Vector2i getPadding() {
      return PADDING;
   }

   public int method_32664(class_327 textRenderer) {
      return (int)(this.getEntity().method_17681() * (float)this.getScale()) + this.getPadding().x();
   }

   public int method_32661(class_327 textRenderer) {
      return (int)(this.getEntity().method_17682() * (float)this.getScale()) + this.getPadding().y();
   }

   public void method_32666(class_327 textRenderer, int x, int y, int width, int height, class_332 context) {
      this.renderEntity(context, x, y, this.getRotation());
   }

   public void renderEntity(class_332 context, int x, int y, Quaternionf quaternionf) {
      RenderUtils.drawEntityAligned(context, x, y, this.method_32664((class_327)null), this.method_32661((class_327)null), this.getScale(), this.getEntity(), quaternionf, (Quaternionf)null);
   }
}
