package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.util.FreeLook;
import net.minecraft.class_315;
import net.minecraft.class_5498;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_315.class})
public abstract class OptionsMixin {
   @Shadow
   private class_5498 field_26677;

   @Inject(
      method = {"method_31043"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void interceptPerspective(class_5498 perspective, CallbackInfo ci) {
      FreeLook freeLook = (FreeLook)ModuleManager.get().get(FreeLook.class);
      if (freeLook.isFreeLooking() && perspective == class_5498.field_26664) {
         this.field_26677 = class_5498.field_26665;
         ci.cancel();
      }

   }
}
