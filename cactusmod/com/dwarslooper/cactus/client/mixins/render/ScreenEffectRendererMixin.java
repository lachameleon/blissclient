package com.dwarslooper.cactus.client.mixins.render;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.ViewTweaks;
import net.minecraft.class_1058;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4603;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_4603.class})
public class ScreenEffectRendererMixin {
   @Inject(
      method = {"method_23070"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_4587;method_46416(FFF)V"
)}
   )
   private static void translate(class_4587 matrices, class_4597 vertexConsumers, class_1058 sprite, CallbackInfo ci) {
      ViewTweaks viewTweaks = (ViewTweaks)ModuleManager.get().get(ViewTweaks.class);
      if (viewTweaks.active()) {
         matrices.method_46416(0.0F, (float)(Integer)viewTweaks.fireOffset.get() / 10.0F, 0.0F);
      }

   }
}
