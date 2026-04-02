package com.dwarslooper.cactus.client.mixins.render;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.BlockOutlines;
import com.dwarslooper.cactus.client.render.RenderHelper;
import com.dwarslooper.cactus.client.systems.config.settings.impl.ColorSetting;
import com.dwarslooper.cactus.client.util.Utils;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.class_11658;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_761;
import net.minecraft.class_9779;
import net.minecraft.class_9922;
import net.minecraft.class_4597.class_4598;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(
   value = {class_761.class},
   priority = 800
)
public abstract class LevelRendererMixin {
   @Final
   @Shadow
   private class_310 field_4088;

   @Inject(
      method = {"method_22710"},
      at = {@At("HEAD")}
   )
   public void onRenderHead(class_9922 allocator, class_9779 tickCounter, boolean renderBlockOutline, class_4184 camera, Matrix4f positionMatrix, Matrix4f matrix4f, Matrix4f projectionMatrix, GpuBufferSlice fogBuffer, Vector4f fogColor, boolean renderSky, CallbackInfo ci) {
      Utils.lastProjMat.set(projectionMatrix);
      Utils.lastPosMat.set(positionMatrix);
   }

   @Inject(
      method = {"method_62210"},
      at = {@At("HEAD")}
   )
   private void onBlockOutlineRender(class_4598 immediate, class_4587 matrices, boolean renderBlockOutline, class_11658 renderStates, CallbackInfo ci) {
      RenderHelper.draw(immediate, matrices, this.field_4088.method_61966().method_60637(false));
   }

   @Inject(
      method = {"method_62210"},
      at = {@At("TAIL")}
   )
   private void afterBlockOutlineRender(class_4598 immediate, class_4587 matrices, boolean renderBlockOutline, class_11658 renderStates, CallbackInfo ci) {
      BlockOutlines module = (BlockOutlines)ModuleManager.get().get(BlockOutlines.class);
      if (module.active() && (Boolean)module.fill.get()) {
         module.renderFill(matrices, renderStates);
      }

   }

   @ModifyArgs(
      method = {"method_62210"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_761;method_22712(Lnet/minecraft/class_4587;Lnet/minecraft/class_4588;DDDLnet/minecraft/class_12074;IF)V",
   ordinal = 1
)
   )
   public void modifyBlockOutlineCall(Args args) {
      BlockOutlines module = (BlockOutlines)ModuleManager.get().get(BlockOutlines.class);
      if (module.active()) {
         args.set(6, ((ColorSetting.ColorValue)module.lineColor.get()).color());
         args.set(7, (float)(Integer)module.lineWidth.get());
      }

   }
}
