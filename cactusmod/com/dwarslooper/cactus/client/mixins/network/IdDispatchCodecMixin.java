package com.dwarslooper.cactus.client.mixins.network;

import io.netty.buffer.ByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_9136;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin({class_9136.class})
public abstract class IdDispatchCodecMixin<B extends ByteBuf, V> {
   @Inject(
      method = {"method_56426(Lio/netty/buffer/ByteBuf;Ljava/lang/Object;)V"},
      at = {@At("TAIL")}
   )
   public void listenEncode(B byteBuf, V object, CallbackInfo ci) {
   }

   @Inject(
      method = {"method_56425(Lio/netty/buffer/ByteBuf;)Ljava/lang/Object;"},
      at = {@At("TAIL")}
   )
   public void listenDecode(B byteBuf, CallbackInfoReturnable<V> cir) {
   }
}
