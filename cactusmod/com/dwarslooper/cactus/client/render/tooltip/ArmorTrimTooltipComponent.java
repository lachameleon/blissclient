package com.dwarslooper.cactus.client.render.tooltip;

import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.class_1531;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5632;
import net.minecraft.class_6880;
import net.minecraft.class_7924;
import net.minecraft.class_8053;
import net.minecraft.class_8055;
import net.minecraft.class_8056;
import net.minecraft.class_9334;
import org.joml.Quaternionf;

public class ArmorTrimTooltipComponent extends EntityTooltipComponent<class_1531> implements class_5632 {
   private static final Quaternionf ARMOR_STAND_ROTATION = (new Quaternionf()).rotationXYZ(0.1F, (float)Math.toRadians(200.0D), 3.1415927F);
   private static final int scale = 20;
   private final class_1531 armorStand;
   private final boolean rotate;

   public ArmorTrimTooltipComponent(class_6880<class_8056> patternReference, boolean rotate) {
      this.armorStand = new class_1531(CactusConstants.mc.field_1687, 0.0D, 0.0D, 0.0D);
      this.armorStand.method_5875(true);
      this.armorStand.method_6907(true);
      this.rotate = rotate;
      if (patternReference != null) {
         class_8053 trim = new class_8053(CactusConstants.mc.field_1687.method_30349().method_30530(class_7924.field_42083).method_46747(class_8055.field_42007), patternReference);
         Set<class_1799> armorItems = (Set)Stream.of(class_1802.field_8743, class_1802.field_8523, class_1802.field_8396, class_1802.field_8660).map(class_1792::method_7854).collect(Collectors.toSet());
         armorItems.forEach((itemStack) -> {
            itemStack.method_57379(class_9334.field_49607, trim);
            this.armorStand.method_5673(this.armorStand.method_32326(itemStack), itemStack);
         });
      }

   }

   public class_1531 getEntity() {
      return this.armorStand;
   }

   public int getScale() {
      return 20;
   }

   public Quaternionf getRotation() {
      return ARMOR_STAND_ROTATION;
   }

   public void method_32666(class_327 textRenderer, int x, int y, int width, int height, class_332 context) {
      if (this.rotate) {
         double deg = (double)System.currentTimeMillis() / 20.0D % 360.0D;
         ARMOR_STAND_ROTATION.rotationXYZ(0.1F, (float)Math.toRadians(deg), 3.1415927F);
      }

      super.method_32666(textRenderer, x, y, width, height, context);
   }
}
