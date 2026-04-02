package com.dwarslooper.cactus.client.mixins.client.cape;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_972;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(
   value = {class_972.class},
   priority = 800
)
public abstract class CapeRenderMixin {
   @WrapOperation(
      method = {"method_4177(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_10055;FF)V"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_12249;method_75982(Lnet/minecraft/class_2960;)Lnet/minecraft/class_1921;",
   ordinal = 0
)}
   )
   private class_1921 fixTransparency(class_2960 texture, Operation<class_1921> original) {
      return class_12249.method_75994(texture);
   }
}
