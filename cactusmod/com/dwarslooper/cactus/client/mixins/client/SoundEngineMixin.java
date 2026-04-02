package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.util.CactusConstants;
import net.minecraft.class_1109;
import net.minecraft.class_1140;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_1140.class})
public abstract class SoundEngineMixin {
   @Inject(
      method = {"method_4846"},
      at = {@At(
   value = "INVOKE",
   target = "Lorg/slf4j/Logger;info(Lorg/slf4j/Marker;Ljava/lang/String;)V",
   remap = false
)}
   )
   public void onStore(CallbackInfo ci) {
      if (CactusConstants.APRILFOOLS) {
         class_310.method_1551().method_1483().method_4873(class_1109.method_4758(class_3414.method_47908(class_2960.method_60655("cactus", "banana")), 1.0F));
      }

   }
}
