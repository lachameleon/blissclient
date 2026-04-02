package com.dwarslooper.cactus.client.mixins.render;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.RenderTweaks;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.class_758;
import net.minecraft.class_758.class_4596;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_758.class})
public abstract class FogRendererMixin {
   @Shadow
   @Final
   private GpuBuffer field_60097;

   @Inject(
      method = {"method_71109"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void cactus$disableFog(class_4596 fogType, CallbackInfoReturnable<GpuBufferSlice> cir) {
      RenderTweaks module = (RenderTweaks)ModuleManager.get().get(RenderTweaks.class);
      if (module.active() && (Boolean)module.noFog.get()) {
         cir.setReturnValue(this.field_60097.slice(0L, (long)class_758.field_60096));
      }

   }

   @ModifyArgs(
      method = {"method_3211"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_758;method_71110(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V"
)
   )
   private void cactus$disableFogAll(Args args) {
      RenderTweaks module = (RenderTweaks)ModuleManager.get().get(RenderTweaks.class);
      if (module.active() && (Boolean)module.noFog.get()) {
         float far = Float.MAX_VALUE;

         for(int i = 3; i <= 8; ++i) {
            args.set(i, far);
         }

      }
   }
}
