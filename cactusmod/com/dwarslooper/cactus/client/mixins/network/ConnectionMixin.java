package com.dwarslooper.cactus.client.mixins.network;

import com.dwarslooper.cactus.client.feature.commands.PacketLoggerCommand;
import com.dwarslooper.cactus.client.util.game.TickRateProcessor;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.class_2535;
import net.minecraft.class_2547;
import net.minecraft.class_2596;
import net.minecraft.class_2761;
import net.minecraft.class_9127;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_2535.class})
public abstract class ConnectionMixin {
   @Inject(
      method = {"method_10759"},
      at = {@At("HEAD")}
   )
   private static void onHandlePacket(class_2596<?> packet, class_2547 listener, CallbackInfo info) {
      if (packet instanceof class_2761) {
         TickRateProcessor.INSTANCE.processServerTime();
      }

   }

   @Inject(
      method = {"method_10764"},
      at = {@At("HEAD")}
   )
   private void logSentPacket(class_2596<?> packet, ChannelFutureListener channelFutureListener, boolean flush, CallbackInfo ci) {
      PacketLoggerCommand.add(packet);
   }

   @Inject(
      method = {"method_10759"},
      at = {@At("HEAD")}
   )
   private static void logReceivedPacket(class_2596<?> packet, class_2547 listener, CallbackInfo ci) {
      PacketLoggerCommand.add(packet);
   }

   @Inject(
      method = {"method_56332"},
      at = {@At(
   value = "INVOKE",
   target = "Ljava/lang/IllegalStateException;<init>(Ljava/lang/String;)V",
   shift = Shift.BEFORE
)},
      cancellable = true
   )
   public void suppressExceptions(class_9127<?> state, class_2547 listener, CallbackInfo ci) {
      ci.cancel();
   }
}
