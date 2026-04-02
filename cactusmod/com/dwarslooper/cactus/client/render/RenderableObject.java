package com.dwarslooper.cactus.client.render;

import java.awt.Color;
import net.minecraft.class_4587;
import net.minecraft.class_4597.class_4598;

public abstract class RenderableObject {
   private final Color color;
   private final RenderableObject.RenderMode renderMode;
   private long timeLast = 0L;

   public RenderableObject(Color color, RenderableObject.RenderMode renderMode) {
      this.color = color;
      this.renderMode = renderMode;
   }

   public Color getColor() {
      return this.color;
   }

   public RenderableObject.RenderMode getRenderMode() {
      return this.renderMode;
   }

   public RenderableObject timed(long timeLast) {
      this.timeLast = System.currentTimeMillis() + timeLast;
      return this;
   }

   public long timed() {
      return this.timeLast;
   }

   public void build() {
      RenderHelper.hitBoxes.add(this);
   }

   public abstract void draw(class_4598 var1, class_4587 var2, float var3);

   public static enum RenderMode {
      Instant,
      Permanent;

      // $FF: synthetic method
      private static RenderableObject.RenderMode[] $values() {
         return new RenderableObject.RenderMode[]{Instant, Permanent};
      }
   }

   public static enum BoxMode {
      Lines,
      Faces,
      Both;

      // $FF: synthetic method
      private static RenderableObject.BoxMode[] $values() {
         return new RenderableObject.BoxMode[]{Lines, Faces, Both};
      }
   }
}
