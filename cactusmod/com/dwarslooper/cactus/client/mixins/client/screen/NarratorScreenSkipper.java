package com.dwarslooper.cactus.client.mixins.client.screen;

import net.minecraft.class_8032;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_8032.class})
public abstract class NarratorScreenSkipper {
   @Shadow
   public abstract void method_25419();

   @Inject(
      method = {"method_25426"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void init(CallbackInfo ci) {
      ci.cancel();
      this.method_25419();
   }
}
