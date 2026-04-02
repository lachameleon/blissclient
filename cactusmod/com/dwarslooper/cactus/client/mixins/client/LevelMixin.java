package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.AmbientTweaks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1937;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin({class_1937.class})
public abstract class LevelMixin {
   @Unique
   private final AmbientTweaks ambientTweaks = (AmbientTweaks)ModuleManager.get().get(AmbientTweaks.class);

   @Inject(
      method = {"method_8430"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void modifyRain(float delta, CallbackInfoReturnable<Float> cir) {
      if (this.ambientTweaks.active() && this.ambientTweaks.weather.get() != AmbientTweaks.Weather.Default) {
         cir.setReturnValue(this.ambientTweaks.weather.get() == AmbientTweaks.Weather.Clear ? 0.0F : 1.0F);
      }

   }

   @Inject(
      method = {"method_8478"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void modifyThunder(float delta, CallbackInfoReturnable<Float> cir) {
      if (this.ambientTweaks.active() && this.ambientTweaks.weather.get() == AmbientTweaks.Weather.Thunder) {
         cir.setReturnValue(1.0F);
      }

   }
}
