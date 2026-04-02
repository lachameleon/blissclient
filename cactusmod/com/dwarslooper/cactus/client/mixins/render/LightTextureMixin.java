package com.dwarslooper.cactus.client.mixins.render;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.Fullbright;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.mixinterface.IWorldRenderingState;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.class_3695;
import net.minecraft.class_765;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_765.class})
public abstract class LightTextureMixin {
   @Shadow
   @Final
   private GpuTexture field_57927;

   @Inject(
      method = {"method_3313"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3695;method_15396(Ljava/lang/String;)V",
   shift = Shift.AFTER
)},
      cancellable = true
   )
   private void update(float tickProgress, CallbackInfo ci, @Local class_3695 profiler) {
      Fullbright fullbright = (Fullbright)ModuleManager.get().get(Fullbright.class);
      if (fullbright.active() && ((IWorldRenderingState)CactusConstants.mc.field_1773).cactus$isRenderingWorld()) {
         RenderSystem.getDevice().createCommandEncoder().clearColorTexture(this.field_57927, -1);
         profiler.method_15407();
         ci.cancel();
      }

   }
}
