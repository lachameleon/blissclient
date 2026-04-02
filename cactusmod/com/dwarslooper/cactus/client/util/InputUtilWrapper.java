package com.dwarslooper.cactus.client.util;

import net.minecraft.class_156;
import net.minecraft.class_3675;
import net.minecraft.class_156.class_158;

public class InputUtilWrapper {
   public static boolean hasControlDown() {
      if (class_156.method_668().equals(class_158.field_1137)) {
         return class_3675.method_15987(CactusConstants.mc.method_22683(), 343) || class_3675.method_15987(CactusConstants.mc.method_22683(), 347);
      } else {
         return class_3675.method_15987(CactusConstants.mc.method_22683(), 341) || class_3675.method_15987(CactusConstants.mc.method_22683(), 345);
      }
   }

   public static boolean hasShiftDown() {
      return class_3675.method_15987(CactusConstants.mc.method_22683(), 340) || class_3675.method_15987(CactusConstants.mc.method_22683(), 344);
   }

   public static boolean hasAltDown() {
      return class_3675.method_15987(CactusConstants.mc.method_22683(), 342) || class_3675.method_15987(CactusConstants.mc.method_22683(), 346);
   }
}
