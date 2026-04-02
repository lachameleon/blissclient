package com.dwarslooper.cactus.client.mixins.entity;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.util.FlySpeed;
import com.dwarslooper.cactus.client.systems.config.settings.impl.IntegerSetting;
import net.minecraft.class_1656;
import net.minecraft.class_3532;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1656.class})
public abstract class AbilitiesMixin {
   @Inject(
      method = {"method_7252"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void getFlySpeed(CallbackInfoReturnable<Float> cir) {
      FlySpeed flySpeed = (FlySpeed)ModuleManager.get().get(FlySpeed.class);
      IntegerSetting setting = (IntegerSetting)flySpeed.flySpeed;
      if (flySpeed.active()) {
         cir.setReturnValue((float)class_3532.method_15340(setting.get() + FlySpeed.getModifier(), setting.getMin(), setting.getMax()) / 10.0F);
      }

   }
}
