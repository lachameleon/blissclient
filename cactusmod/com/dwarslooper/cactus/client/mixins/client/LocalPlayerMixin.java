package com.dwarslooper.cactus.client.mixins.client;

import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_746.class})
public abstract class LocalPlayerMixin {
   @Inject(
      method = {"method_60887"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_746;method_7346()V"
)},
      cancellable = true
   )
   private void updateNauseaGetCurrentScreenProxy(boolean fromPortalEffect, CallbackInfo ci) {
      ci.cancel();
   }
}
