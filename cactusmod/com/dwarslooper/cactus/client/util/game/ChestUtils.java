package com.dwarslooper.cactus.client.util.game;

import net.minecraft.class_1937;
import net.minecraft.class_2281;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_2745;

public class ChestUtils {
   public static boolean isDouble(class_1937 world, class_2338 blockPos) {
      class_2680 blockState = world.method_8320(blockPos);
      if (!blockState.method_28498(class_2741.field_12506)) {
         return false;
      } else {
         class_2745 type = (class_2745)blockState.method_11654(class_2281.field_10770);
         return type == class_2745.field_12574 || type == class_2745.field_12571;
      }
   }

   public static class_2338 getOtherChestBlockPos(class_1937 world, class_2338 blockPos) {
      class_2680 blockState = world.method_8320(blockPos);
      class_2350 facing = (class_2350)blockState.method_11654(class_2281.field_10768);
      class_2745 type = (class_2745)blockState.method_11654(class_2281.field_10770);
      class_2350 otherBlockDir;
      if (type == class_2745.field_12574) {
         otherBlockDir = facing.method_10170();
      } else {
         otherBlockDir = facing.method_10160();
      }

      return blockPos.method_10093(otherBlockDir);
   }
}
