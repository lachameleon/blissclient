package com.dwarslooper.cactus.client.mixins.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Main.class})
public abstract class MinecraftMainMixin {
   @WrapOperation(
      method = {"<clinit>"},
      at = {@At(
   value = "INVOKE",
   target = "Ljava/lang/System;setProperty(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
)}
   )
   private static String disableHeadless(String key, String value, Operation<String> original) {
      return (String)original.call(new Object[]{key, "false"});
   }
}
