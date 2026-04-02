package com.dwarslooper.cactus.client.mixins.render;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.Zoom;
import com.dwarslooper.cactus.client.render.tooltip.element.BannerFlagGuiRenderer;
import com.dwarslooper.cactus.client.util.mixinterface.IWorldRenderingState;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_11239;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_4599;
import net.minecraft.class_757;
import net.minecraft.class_759;
import net.minecraft.class_776;
import net.minecraft.class_9779;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_757.class})
public abstract class GameRendererMixin implements IWorldRenderingState {
   @Shadow
   @Final
   private class_4599 field_20948;
   @Shadow
   @Final
   private class_310 field_4015;
   @Unique
   private boolean isRenderingWorld;

   @Inject(
      method = {"<init>"},
      at = {@At("RETURN")}
   )
   public void onInit(class_310 client, class_759 firstPersonHeldItemRenderer, class_4599 buffers, class_776 blockRenderManager, CallbackInfo ci) {
   }

   @ModifyArg(
      method = {"<init>"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_11228;<init>(Lnet/minecraft/class_11246;Lnet/minecraft/class_4597$class_4598;Lnet/minecraft/class_11659;Lnet/minecraft/class_11684;Ljava/util/List;)V"
)
   )
   private List<class_11239<?>> meteor$addSpecialRenderers(List<class_11239<?>> original) {
      List<class_11239<?>> merged = new ArrayList(original);
      merged.add(new BannerFlagGuiRenderer(this.field_20948.method_23000(), this.field_4015.method_72703()));
      return merged;
   }

   @Inject(
      method = {"method_3188"},
      at = {@At("HEAD")}
   )
   public void captureStart(class_9779 tickCounter, CallbackInfo ci) {
      this.isRenderingWorld = true;
   }

   @Inject(
      method = {"method_3188"},
      at = {@At("RETURN")}
   )
   public void captureEnd(class_9779 tickCounter, CallbackInfo ci) {
      this.isRenderingWorld = false;
   }

   @Inject(
      method = {"method_3196"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void modifyFov(class_4184 camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Float> cir) {
      Zoom zoom = (Zoom)ModuleManager.get().get(Zoom.class);
      if (zoom.isPressed() && zoom.mode.get() == Zoom.Mode.Set) {
         cir.setReturnValue(Math.max(0.0F, (Float)cir.getReturnValue() / zoom.getZoomSet()));
      }

   }

   public boolean cactus$isRenderingWorld() {
      return this.isRenderingWorld;
   }
}
