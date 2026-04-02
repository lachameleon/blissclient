package com.dwarslooper.cactus.client.gui.hud;

import org.joml.Vector2i;

public enum Anchor {
   LEFT_UP(0.0D, 0.0D),
   LEFT_MIDDLE(0.0D, 0.5D),
   LEFT_LOW(0.0D, 1.0D),
   MIDDLE_UP(0.5D, 0.0D),
   MIDDLE_MIDDLE(0.5D, 0.5D),
   MIDDLE_LOW(0.5D, 1.0D),
   RIGHT_UP(1.0D, 0.0D),
   RIGHT_MIDDLE(1.0D, 0.5D),
   RIGHT_LOW(1.0D, 1.0D);

   private final double horizontalFactor;
   private final double verticalFactor;

   private Anchor(double horizontalFactor, double verticalFactor) {
      this.horizontalFactor = horizontalFactor;
      this.verticalFactor = verticalFactor;
   }

   public static Anchor find(int row, int column) {
      double horizontalFactor = (double)column / 2.0D;
      double verticalFactor = (double)row / 2.0D;
      Anchor[] var6 = values();
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Anchor anchor = var6[var8];
         if (anchor.horizontalFactor == horizontalFactor && anchor.verticalFactor == verticalFactor) {
            return anchor;
         }
      }

      return null;
   }

   public double getHorizontalFactor() {
      return this.horizontalFactor;
   }

   public double getVerticalFactor() {
      return this.verticalFactor;
   }

   public Vector2i getAbsolutePosition(int width, int height) {
      int anchorX = (int)(this.getHorizontalFactor() * (double)width);
      int anchorY = (int)(this.getVerticalFactor() * (double)height);
      return new Vector2i(anchorX, anchorY);
   }

   public Vector2i getElementPosition(Vector2i pos, Vector2i size) {
      int anchorX = (int)((double)(pos.x() - 10) + 10.0D * this.getHorizontalFactor() + (double)size.x() * this.getHorizontalFactor());
      int anchorY = (int)((double)(pos.y() - 10) + 10.0D * this.getVerticalFactor() + (double)size.y() * this.getVerticalFactor());
      return new Vector2i(anchorX, anchorY);
   }

   // $FF: synthetic method
   private static Anchor[] $values() {
      return new Anchor[]{LEFT_UP, LEFT_MIDDLE, LEFT_LOW, MIDDLE_UP, MIDDLE_MIDDLE, MIDDLE_LOW, RIGHT_UP, RIGHT_MIDDLE, RIGHT_LOW};
   }
}
