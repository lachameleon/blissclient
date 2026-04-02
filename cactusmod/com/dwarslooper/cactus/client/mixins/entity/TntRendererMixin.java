package com.dwarslooper.cactus.client.mixins.entity;

import com.dwarslooper.cactus.client.feature.modules.render.TNTTimer;
import net.minecraft.class_10075;
import net.minecraft.class_11659;
import net.minecraft.class_12075;
import net.minecraft.class_1541;
import net.minecraft.class_243;
import net.minecraft.class_4587;
import net.minecraft.class_897;
import net.minecraft.class_956;
import net.minecraft.class_5617.class_5618;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_956.class})
public abstract class TntRendererMixin extends class_897<class_1541, class_10075> {
   protected TntRendererMixin(class_5618 ctx) {
      super(ctx);
   }

   @Inject(
      method = {"method_4135(Lnet/minecraft/class_10075;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_897;method_3936(Lnet/minecraft/class_10017;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V"
)}
   )
   private void injectLabel(class_10075 tntEntityRenderState, class_4587 matrixStack, class_11659 queue, class_12075 cameraRenderState, CallbackInfo ci) {
      TNTTimer tntTimer = TNTTimer.getInstance();
      if (tntTimer.active()) {
         tntEntityRenderState.field_53338 = new class_243(0.0D, 1.0D, 0.0D);
         tntEntityRenderState.field_53337 = tntTimer.getTime((double)tntEntityRenderState.field_53596);
      }

   }
}
