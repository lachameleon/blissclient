package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.util.mixinterface.IRealmsPeriodicCheckers;
import net.minecraft.class_4341;
import net.minecraft.class_7578;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_7578.class})
public abstract class RealmsDataFetcherMixin implements IRealmsPeriodicCheckers {
   @Unique
   private class_4341 cactus$client;

   @Inject(
      method = {"<init>"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_7581;method_44629(Ljava/lang/String;Ljava/util/concurrent/Callable;Ljava/time/Duration;Lnet/minecraft/class_7587;)Lnet/minecraft/class_7581$class_7586;",
   ordinal = 0
)}
   )
   public void storeClient(class_4341 client, CallbackInfo ci) {
      this.cactus$client = client;
   }

   public class_4341 cactus$getClient() {
      return this.cactus$client;
   }
}
