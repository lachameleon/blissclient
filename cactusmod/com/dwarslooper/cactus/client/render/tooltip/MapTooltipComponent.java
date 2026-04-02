package com.dwarslooper.cactus.client.render.tooltip;

import net.minecraft.class_10090;
import net.minecraft.class_1806;
import net.minecraft.class_22;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_330;
import net.minecraft.class_332;
import net.minecraft.class_5632;
import net.minecraft.class_5684;
import net.minecraft.class_9209;
import org.joml.Matrix3x2fStack;

public class MapTooltipComponent implements class_5632, class_5684 {
   private final class_10090 mapRenderState = new class_10090();
   private final class_310 client = class_310.method_1551();
   public class_9209 map;

   public MapTooltipComponent(class_9209 map) {
      this.map = map;
   }

   public int method_32661(class_327 textRenderer) {
      return 130;
   }

   public int method_32664(class_327 textRenderer) {
      return 128;
   }

   public void method_32666(class_327 textRenderer, int x, int y, int width, int height, class_332 context) {
      class_22 state = class_1806.method_7997(this.map, this.client.field_1687);
      if (state != null) {
         class_330 mapRenderer = this.client.method_61965();
         mapRenderer.method_62230(this.map, state, this.mapRenderState);
         Matrix3x2fStack matrices = context.method_51448();
         matrices.pushMatrix();
         matrices.translate((float)x, (float)y);
         matrices.scale(1.0F, 1.0F);
         context.method_70857(this.mapRenderState);
         matrices.popMatrix();
      }
   }
}
