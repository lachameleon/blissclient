package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.event.impl.MinecraftKeyEvent;
import com.dwarslooper.cactus.client.event.impl.RawKeyEvent;
import net.minecraft.class_11908;
import net.minecraft.class_309;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_309.class})
public abstract class KeyboardHandlerMixin {
   @Inject(
      method = {"method_1466"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onHeadKey(long window, int action, class_11908 input, CallbackInfo ci) {
      RawKeyEvent event = (RawKeyEvent)CactusClient.EVENT_BUS.post(new RawKeyEvent(window, input.comp_4795(), input.comp_4796(), action, input.comp_4797()));
      if (event.isCancelled()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"method_1466"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_304;method_1416(Lnet/minecraft/class_3675$class_306;Z)V",
   ordinal = 1
)}
   )
   public void onProcessKeyRelease(long window, int action, class_11908 input, CallbackInfo ci) {
      CactusClient.EVENT_BUS.post(new MinecraftKeyEvent(window, input.comp_4795(), input.comp_4796(), action, input.comp_4797()));
   }

   @Inject(
      method = {"method_1466"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_304;method_1416(Lnet/minecraft/class_3675$class_306;Z)V",
   ordinal = 3
)}
   )
   public void onProcessKeyPress(long window, int action, class_11908 input, CallbackInfo ci) {
      CactusClient.EVENT_BUS.post(new MinecraftKeyEvent(window, input.comp_4795(), input.comp_4796(), action, input.comp_4797()));
   }
}
