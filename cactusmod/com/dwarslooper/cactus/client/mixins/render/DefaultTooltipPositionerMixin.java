package com.dwarslooper.cactus.client.mixins.render;

import net.minecraft.class_8001;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_8001.class})
public abstract class DefaultTooltipPositionerMixin {
   @Inject(
      method = {"method_47945(IILorg/joml/Vector2i;II)V"},
      at = {@At("RETURN")}
   )
   private void fixTopOverflows(int screenWidth, int screenHeight, Vector2i pos, int width, int height, CallbackInfo ci) {
      if (pos.x < 5) {
         pos.x = 5;
      }

      if (pos.y < 5) {
         pos.y = 5;
      }

   }
}
