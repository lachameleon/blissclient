package com.dwarslooper.cactus.client.render.cosmetics.models;

import com.dwarslooper.cactus.client.render.cosmetics.CosmeticRenderer;
import net.minecraft.class_10055;
import net.minecraft.class_1058;
import net.minecraft.class_11659;
import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_4608;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.class_591;
import net.minecraft.class_630;
import org.joml.Quaternionf;

public class DragonWingCosmeticRenderer implements CosmeticRenderer {
   private final class_630 root;
   private final class_630 leftWing;
   private final class_630 leftWingTip;
   private final class_630 rightWing;
   private final class_630 rightWingTip;
   private final class_1921 layer;

   public DragonWingCosmeticRenderer(class_2960 texture) {
      class_5609 modelData = new class_5609();
      class_5610 modelPartData = modelData.method_32111();
      class_5610 leftWingData = modelPartData.method_32117("left_wing", class_5606.method_32108().method_32096().method_32104("bone", 0.0F, -4.0F, -4.0F, 56, 8, 8, 112, 88).method_32104("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, -56, 88), class_5603.method_32090(8.0F, 5.0F, 2.0F));
      leftWingData.method_32117("left_wing_tip", class_5606.method_32108().method_32096().method_32104("bone", 0.0F, -2.0F, -2.0F, 56, 4, 4, 112, 136).method_32104("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, -56, 144), class_5603.method_32090(56.0F, 0.0F, 0.0F));
      class_5610 rightWingData = modelPartData.method_32117("right_wing", class_5606.method_32108().method_32104("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8, 112, 88).method_32104("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, -56, 88), class_5603.method_32090(-8.0F, 5.0F, 2.0F));
      rightWingData.method_32117("right_wing_tip", class_5606.method_32108().method_32104("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4, 112, 136).method_32104("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, -56, 144), class_5603.method_32090(-56.0F, 0.0F, 0.0F));
      this.root = modelPartData.method_32112(256, 256);
      this.leftWing = this.root.method_32086("left_wing");
      this.leftWingTip = this.leftWing.method_32086("left_wing_tip");
      this.rightWing = this.root.method_32086("right_wing");
      this.rightWingTip = this.rightWing.method_32086("right_wing_tip");
      this.layer = class_12249.method_75994(texture);
   }

   public void render(class_4587 matrices, class_11659 queue, class_10055 entity, class_591 ctx, float tickDelta, int light) {
      matrices.method_22903();
      float f = (float)(System.currentTimeMillis() % 2000L) / 2000.0F;
      float q = f * 6.2831855F;
      this.leftWing.field_3654 = 0.125F - (float)Math.cos((double)q) * 0.2F;
      this.leftWing.field_3675 = -0.25F;
      this.leftWing.field_3674 = -((float)(Math.sin((double)q) + 0.125D)) * 0.2F - 0.9F;
      this.leftWingTip.field_3674 = (float)(Math.sin((double)(q + 2.0F)) + 0.5D) * 0.75F;
      this.rightWing.field_3654 = this.leftWing.field_3654;
      this.rightWing.field_3675 = -this.leftWing.field_3675;
      this.rightWing.field_3674 = -this.leftWing.field_3674;
      this.rightWingTip.field_3674 = -this.leftWingTip.field_3674;
      matrices.method_22903();
      ctx.field_3391.method_22703(matrices);
      matrices.method_22907((new Quaternionf()).rotateX(-0.87964594F));
      matrices.method_22905(0.17F, 0.17F, 0.17F);
      matrices.method_22904(0.0D, -0.4D, 0.8D);
      queue.method_73491(this.root, matrices, this.layer, light, class_4608.field_21444, (class_1058)null);
      matrices.method_22909();
      matrices.method_22909();
   }
}
