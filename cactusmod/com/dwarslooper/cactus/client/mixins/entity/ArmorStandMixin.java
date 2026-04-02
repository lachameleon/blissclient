package com.dwarslooper.cactus.client.mixins.entity;

import com.dwarslooper.cactus.client.gui.screen.impl.ArmorStandEditorScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11580;
import net.minecraft.class_1299;
import net.minecraft.class_1531;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2487;
import net.minecraft.class_310;
import net.minecraft.class_9334;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin({class_1531.class})
public abstract class ArmorStandMixin {
   @Inject(
      method = {"method_31480"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void pickWithEntityData(CallbackInfoReturnable<class_1799> cir) {
      class_310 mc = class_310.method_1551();
      boolean controlDown = GLFW.glfwGetKey(mc.method_22683().method_4490(), 341) == 1 || GLFW.glfwGetKey(mc.method_22683().method_4490(), 345) == 1;
      if (controlDown) {
         class_1531 entity = (class_1531)this;
         class_2487 data = ArmorStandEditorScreen.getArmorStandDataExtended(entity);
         class_1799 stack = new class_1799(class_1802.field_8694);
         stack.method_57379(class_9334.field_49609, class_11580.method_72535(class_1299.field_6131, data));
         cir.setReturnValue(stack);
      }

   }
}
