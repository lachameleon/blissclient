package com.dwarslooper.cactus.client.mixins.entity;

import com.dwarslooper.cactus.client.util.CactusConstants;
import net.minecraft.class_10010;
import net.minecraft.class_4587;
import net.minecraft.class_887;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_887.class})
public class CreeperRendererMixin {
   @Unique
   private float targetScaleX = 1.0F;
   @Unique
   private float targetScaleY = 1.0F;
   @Unique
   private float targetScaleZ = 1.0F;
   @Unique
   private float scaleTime = 1.0F;

   @Inject(
      method = {"method_3900(Lnet/minecraft/class_10010;Lnet/minecraft/class_4587;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onScale(class_10010 creeperEntityRenderState, class_4587 matrixStack, CallbackInfo ci) {
      if (CactusConstants.APRILFOOLS) {
         if (creeperEntityRenderState.field_53303 > 0.0F && creeperEntityRenderState.field_53303 < 30.0F) {
            float progress = creeperEntityRenderState.field_53303 / 30.0F;
            float scaleInterval = 0.1F;
            if (Math.floor((double)(progress / scaleInterval)) != Math.floor((double)((creeperEntityRenderState.field_53303 - 1.0F) / scaleInterval))) {
               this.targetScaleX = (float)Math.random() * 2.0F;
               this.targetScaleY = (float)Math.random() * 5.0F;
               this.targetScaleZ = (float)Math.random() * 2.0F;
               this.scaleTime = 0.0F;
            }

            this.scaleTime += 0.5F;
            if (this.scaleTime > 1.0F) {
               this.scaleTime = 1.0F;
            }

            float scaleX = this.lerp(1.0F, this.targetScaleX, this.scaleTime);
            float scaleY = this.lerp(1.0F, this.targetScaleY, this.scaleTime);
            float scaleZ = this.lerp(1.0F, this.targetScaleZ, this.scaleTime);
            matrixStack.method_22905(scaleX, scaleY, scaleZ);
         } else {
            matrixStack.method_22905(1.0F, 1.0F, 1.0F);
         }

         ci.cancel();
      }
   }

   @Unique
   private float lerp(float start, float end, float progress) {
      return start + (end - start) * progress;
   }
}
