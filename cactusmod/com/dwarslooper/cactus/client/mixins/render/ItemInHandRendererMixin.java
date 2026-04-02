package com.dwarslooper.cactus.client.mixins.render;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.ViewTweaks;
import com.dwarslooper.cactus.client.feature.modules.render.Zoom;
import net.minecraft.class_11659;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_4587;
import net.minecraft.class_746;
import net.minecraft.class_759;
import net.minecraft.class_811;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_759.class})
public class ItemInHandRendererMixin {
   @Inject(
      method = {"method_22976(FLnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_746;I)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void checkRenderView(float tickProgress, class_4587 matrices, class_11659 orderedRenderCommandQueue, class_746 player, int light, CallbackInfo ci) {
      Zoom zoom = (Zoom)ModuleManager.get().get(Zoom.class);
      if (zoom.isPressed() && (Boolean)zoom.hideHand.get()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"method_3233(Lnet/minecraft/class_1309;Lnet/minecraft/class_1799;Lnet/minecraft/class_811;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;I)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void offsetShield(class_1309 entity, class_1799 stack, class_811 renderMode, class_4587 matrices, class_11659 orderedRenderCommandQueue, int light, CallbackInfo ci) {
      ViewTweaks viewTweaks = (ViewTweaks)ModuleManager.get().get(ViewTweaks.class);
      if (viewTweaks.active() && stack.method_7909() == class_1802.field_8255 && renderMode.method_29998()) {
         matrices.method_46416(0.0F, (float)(Integer)viewTweaks.shieldOffset.get() / 10.0F, 0.0F);
      }

   }
}
