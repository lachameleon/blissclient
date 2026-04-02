package com.dwarslooper.cactus.client.mixins.render;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.AmbientTweaks;
import net.minecraft.class_638.class_5271;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_5271.class})
public abstract class ClientWorldPropertiesMixin {
   @Inject(
      method = {"method_217"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void modifyTime(CallbackInfoReturnable<Long> cir) {
      AmbientTweaks ambientTweaks = (AmbientTweaks)ModuleManager.get().get(AmbientTweaks.class);
      if (ambientTweaks.active() && (Boolean)ambientTweaks.changeTime.get()) {
         cir.setReturnValue((long)(Integer)ambientTweaks.time.get());
      }

   }
}
