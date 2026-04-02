package com.dwarslooper.cactus.client.mixins.client.screen;

import net.minecraft.class_4185;
import net.minecraft.class_8671;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_8671.class})
public abstract class ServerReconfigScreenMixin {
   @Shadow
   private class_4185 field_45510;

   @Inject(
      method = {"method_25426"},
      at = {@At("RETURN")}
   )
   private void alwaysEnableDisconnectButton(CallbackInfo ci) {
      this.field_45510.field_22763 = true;
   }
}
