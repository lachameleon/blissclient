package com.dwarslooper.cactus.client.render.objects;

import com.dwarslooper.cactus.client.render.RenderableObject;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat.class_5596;
import java.awt.Color;
import net.minecraft.class_10799;
import net.minecraft.class_12245;
import net.minecraft.class_12247;
import net.minecraft.class_1921;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4587.class_4665;
import net.minecraft.class_4597.class_4598;

public class BoxObject extends RenderableObject {
   public static RenderPipeline overlayPipeline;
   public static class_1921 overlayLayer;
   private final class_238 box;
   private final RenderableObject.BoxMode mode;

   public BoxObject(Color color, RenderableObject.RenderMode renderMode, class_238 box, RenderableObject.BoxMode mode) {
      super(color, renderMode);
      this.box = box;
      this.mode = mode;
   }

   public void draw(class_4598 immediate, class_4587 matrices, float tickDelta) {
      class_310 mc = class_310.method_1551();
      class_243 camPos = mc.field_1773.method_19418().method_71156();
      matrices.method_22903();
      matrices.method_22904(-camPos.field_1352, -camPos.field_1351, -camPos.field_1350);
      class_4588 consumer = immediate.method_73477(overlayLayer);
      class_4665 entry = matrices.method_23760();
      float r = (float)this.getColor().getRed() / 255.0F;
      float g = (float)this.getColor().getGreen() / 255.0F;
      float b = (float)this.getColor().getBlue() / 255.0F;
      float a = 0.2F;
      consumer.method_56824(entry, (float)this.box.field_1320, (float)this.box.field_1322, (float)this.box.field_1321).method_22915(r, g, b, a);
      consumer.method_56824(entry, (float)this.box.field_1320, (float)this.box.field_1325, (float)this.box.field_1321).method_22915(r, g, b, a);
      consumer.method_56824(entry, (float)this.box.field_1320, (float)this.box.field_1325, (float)this.box.field_1324).method_22915(r, g, b, a);
      consumer.method_56824(entry, (float)this.box.field_1320, (float)this.box.field_1322, (float)this.box.field_1324).method_22915(r, g, b, a);
      consumer.method_56824(entry, (float)this.box.field_1323, (float)this.box.field_1322, (float)this.box.field_1321).method_22915(r, g, b, a);
      consumer.method_56824(entry, (float)this.box.field_1323, (float)this.box.field_1322, (float)this.box.field_1324).method_22915(r, g, b, a);
      consumer.method_56824(entry, (float)this.box.field_1323, (float)this.box.field_1325, (float)this.box.field_1324).method_22915(r, g, b, a);
      consumer.method_56824(entry, (float)this.box.field_1323, (float)this.box.field_1325, (float)this.box.field_1321).method_22915(r, g, b, a);
      consumer.method_56824(entry, (float)this.box.field_1323, (float)this.box.field_1322, (float)this.box.field_1321).method_22915(r, g, b, a);
      consumer.method_56824(entry, (float)this.box.field_1323, (float)this.box.field_1325, (float)this.box.field_1321).method_22915(r, g, b, a);
      consumer.method_56824(entry, (float)this.box.field_1320, (float)this.box.field_1325, (float)this.box.field_1321).method_22915(r, g, b, a);
      consumer.method_56824(entry, (float)this.box.field_1320, (float)this.box.field_1322, (float)this.box.field_1321).method_22915(r, g, b, a);
      consumer.method_56824(entry, (float)this.box.field_1323, (float)this.box.field_1322, (float)this.box.field_1324).method_22915(r, g, b, a);
      consumer.method_56824(entry, (float)this.box.field_1320, (float)this.box.field_1322, (float)this.box.field_1324).method_22915(r, g, b, a);
      consumer.method_56824(entry, (float)this.box.field_1320, (float)this.box.field_1325, (float)this.box.field_1324).method_22915(r, g, b, a);
      consumer.method_56824(entry, (float)this.box.field_1323, (float)this.box.field_1325, (float)this.box.field_1324).method_22915(r, g, b, a);
      consumer.method_56824(entry, (float)this.box.field_1323, (float)this.box.field_1325, (float)this.box.field_1321).method_22915(r, g, b, a);
      consumer.method_56824(entry, (float)this.box.field_1323, (float)this.box.field_1325, (float)this.box.field_1324).method_22915(r, g, b, a);
      consumer.method_56824(entry, (float)this.box.field_1320, (float)this.box.field_1325, (float)this.box.field_1324).method_22915(r, g, b, a);
      consumer.method_56824(entry, (float)this.box.field_1320, (float)this.box.field_1325, (float)this.box.field_1321).method_22915(r, g, b, a);
      consumer.method_56824(entry, (float)this.box.field_1323, (float)this.box.field_1322, (float)this.box.field_1321).method_22915(r, g, b, a);
      consumer.method_56824(entry, (float)this.box.field_1320, (float)this.box.field_1322, (float)this.box.field_1321).method_22915(r, g, b, a);
      consumer.method_56824(entry, (float)this.box.field_1320, (float)this.box.field_1322, (float)this.box.field_1324).method_22915(r, g, b, a);
      consumer.method_56824(entry, (float)this.box.field_1323, (float)this.box.field_1322, (float)this.box.field_1324).method_22915(r, g, b, a);
      immediate.method_22993();
      matrices.method_22909();
   }

   static {
      overlayPipeline = RenderPipeline.builder(new Snippet[]{class_10799.field_56860}).withLocation("cactus/pipeline/block_overlay").withVertexFormat(class_290.field_1576, class_5596.field_27382).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).build();
      overlayLayer = class_1921.method_75940("overlay_filled_box", class_12247.method_75927(overlayPipeline).method_75930(class_12245.field_63976).method_75937().method_75938());
   }
}
