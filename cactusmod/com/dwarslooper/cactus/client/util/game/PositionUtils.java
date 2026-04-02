package com.dwarslooper.cactus.client.util.game;

import com.dwarslooper.cactus.client.CactusClient;
import net.minecraft.class_2338;

public class PositionUtils {
   public static class_2338 fromString(String input) {
      try {
         return new class_2338(Integer.parseInt(input.split(" ")[0]), Integer.parseInt(input.split(" ")[1]), Integer.parseInt(input.split(" ")[2]));
      } catch (Exception var2) {
         CactusClient.getLogger().error("Failed to parse BlockPos from String", var2);
         return null;
      }
   }

   public static String toString(class_2338 blockPos) {
      return "%s %s %s".formatted(new Object[]{blockPos.method_10263(), blockPos.method_10264(), blockPos.method_10260()});
   }

   public static String toStringCentered(class_2338 blockPos) {
      return "%s %s %s".formatted(new Object[]{(double)blockPos.method_10263() + 0.5D, (double)blockPos.method_10264() + 0.5D, (double)blockPos.method_10260() + 0.5D});
   }

   public static String toStringAbsolute(class_2338 blockPos) {
      return "%s.0 %s.0 %s.0".formatted(new Object[]{blockPos.method_10263(), blockPos.method_10264(), blockPos.method_10260()});
   }
}
