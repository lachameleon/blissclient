package com.dwarslooper.cactus.client.mixins.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_155;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({class_155.class})
public class SharedConstantsMixin {
   @Mutable
   @Shadow
   public static boolean field_1125;

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   public void mod(CallbackInfo ci) {
      field_1125 = true;
   }
}
