package com.dwarslooper.cactus.client.mixins.entity;

import net.minecraft.class_10009;
import net.minecraft.class_124;
import net.minecraft.class_2960;
import net.minecraft.class_882;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_882.class})
public class ChickenRendererMixin {
   @Unique
   private static final class_2960 BERTA_TEXTURE = class_2960.method_60655("cactus", "textures/special/berta.png");

   @Inject(
      method = {"method_3892(Lnet/minecraft/class_10009;)Lnet/minecraft/class_2960;"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void tributeTexture(class_10009 renderState, CallbackInfoReturnable<class_2960> cir) {
      if (renderState.field_53337 != null) {
         String string = class_124.method_539(renderState.field_53337.getString());
         if ("Berta".equals(string)) {
            cir.setReturnValue(BERTA_TEXTURE);
         }

      }
   }
}
