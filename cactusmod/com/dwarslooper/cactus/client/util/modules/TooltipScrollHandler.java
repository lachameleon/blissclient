package com.dwarslooper.cactus.client.util.modules;

public final class TooltipScrollHandler {
   private static int xOffset = 0;
   private static int yOffset = 0;

   private TooltipScrollHandler() {
   }

   public static void reset() {
      xOffset = 0;
      yOffset = 0;
   }

   public static void scrollVertical(double delta) {
      if (delta != 0.0D) {
         int step = (int)Math.round(delta * 5.0D);
         yOffset -= step;
      }
   }

   public static void scrollHorizontal(double delta) {
      if (delta != 0.0D) {
         int step = (int)Math.round(delta * 5.0D);
         xOffset -= step;
      }
   }

   public static int getXOffset() {
      return xOffset;
   }

   public static int getYOffset() {
      return yOffset;
   }
}
