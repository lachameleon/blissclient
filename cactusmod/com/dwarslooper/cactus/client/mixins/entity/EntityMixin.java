package com.dwarslooper.cactus.client.mixins.entity;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.util.FreeLook;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin({class_1297.class})
public abstract class EntityMixin {
   @Inject(
      method = {"method_5851"},
      at = {@At("HEAD")}
   )
   public void injectGlowing(CallbackInfoReturnable<Boolean> cir) {
   }

   @Inject(
      method = {"method_5872"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void injectLook(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
      FreeLook freeLook = (FreeLook)ModuleManager.get().get(FreeLook.class);
      if ((class_1297)this instanceof class_746 && freeLook.isFreeLooking()) {
         FreeLook.camPitch += (float)cursorDeltaY * 0.15F;
         FreeLook.camYaw += (float)(cursorDeltaX * 0.15000000596046448D);
         ci.cancel();
      }

   }
}
