package com.dwarslooper.cactus.client.render.tooltip;

import com.dwarslooper.cactus.client.util.CactusConstants;
import net.minecraft.class_11580;
import net.minecraft.class_1299;
import net.minecraft.class_1531;
import net.minecraft.class_1799;
import net.minecraft.class_5632;
import net.minecraft.class_9334;
import org.joml.Quaternionf;

public class ArmorstandTooltipComponent extends EntityTooltipComponent<class_1531> implements class_5632 {
   private static final Quaternionf ARMOR_STAND_ROTATION = (new Quaternionf()).rotationXYZ(0.1F, (float)Math.toRadians(200.0D), 3.1415927F);
   private static final int scale = 20;
   private final class_1531 armorStand;

   public ArmorstandTooltipComponent(class_1799 armorStand) {
      this.armorStand = new class_1531(CactusConstants.mc.field_1687, 0.0D, 0.0D, 0.0D);
      this.armorStand.method_5875(true);
      class_11580<class_1299<?>> data = (class_11580)armorStand.method_58694(class_9334.field_49609);
      if (data != null) {
         data.method_72531(this.armorStand);
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
}
