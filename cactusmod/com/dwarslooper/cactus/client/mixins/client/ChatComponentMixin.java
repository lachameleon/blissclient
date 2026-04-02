package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.event.impl.MessageReceiveEvent;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.class_2561;
import net.minecraft.class_338;
import net.minecraft.class_7469;
import net.minecraft.class_7591;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {class_338.class},
   priority = 800
)
public abstract class ChatComponentMixin {
   @Inject(
      method = {"method_44811(Lnet/minecraft/class_2561;Lnet/minecraft/class_7469;Lnet/minecraft/class_7591;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void messageAdded(class_2561 message, class_7469 signature, class_7591 indicator, CallbackInfo ci) {
      MessageReceiveEvent event = (MessageReceiveEvent)CactusClient.EVENT_BUS.post(new MessageReceiveEvent(message, signature, indicator));
      if (event.isCancelled()) {
         ci.cancel();
      }

   }

   @Mixin(
      value = {class_338.class},
      priority = 800
   )
   public static class ChatComponentExtendHistoryMixin {
      @ModifyExpressionValue(
         method = {"method_1815", "method_1803", "method_58744(Lnet/minecraft/class_303;)V"},
         at = {@At(
   value = "CONSTANT",
   args = {"intValue=100"}
)}
      )
      public int changeMaxHistory(int original) {
         return (Integer)CactusSettings.get().chatHistoryLength.get();
      }
   }
}
