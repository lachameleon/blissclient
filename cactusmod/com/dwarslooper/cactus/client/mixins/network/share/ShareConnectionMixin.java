package com.dwarslooper.cactus.client.mixins.network.share;

import io.netty.channel.Channel;
import io.netty.channel.local.LocalAddress;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2535;
import net.minecraft.class_2598;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin({class_2535.class})
public abstract class ShareConnectionMixin {
   @Shadow
   private Channel field_11651;

   @Shadow
   public abstract class_2598 method_36121();

   @Inject(
      method = {"method_10756"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void isLocalInject(CallbackInfoReturnable<Boolean> cir) {
      if ((Boolean)cir.getReturnValue() && this.field_11651.localAddress().equals(new LocalAddress("cactus-relay"))) {
         cir.setReturnValue(false);
      }

   }
}
