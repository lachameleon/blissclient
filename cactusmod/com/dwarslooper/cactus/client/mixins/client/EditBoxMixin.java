package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.util.mixinterface.ITextFieldWidget;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_342;
import net.minecraft.class_5481;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_342.class})
public abstract class EditBoxMixin extends class_339 implements ITextFieldWidget {
   @Shadow
   @Final
   private class_327 field_2105;
   @Shadow
   private boolean field_2095;
   @Unique
   private class_2561 unfocusedPlaceholder;

   public EditBoxMixin(int x, int y, int width, int height, class_2561 message) {
      super(x, y, width, height, message);
   }

   public void cactus$setUnfocusedPlaceholder(class_2561 unfocusedPlaceholder) {
      this.unfocusedPlaceholder = unfocusedPlaceholder;
   }

   public class_2561 cactus$getUnfocusedPlaceholder() {
      return this.unfocusedPlaceholder;
   }

   @Inject(
      method = {"method_48579"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_3532;method_15340(III)I"
)}
   )
   public void injectPlaceholder(class_332 context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
      if (this.placeholderApplies()) {
         context.method_27535(this.field_2105, this.unfocusedPlaceholder, this.field_2095 ? this.method_46426() + 4 : this.method_46426(), this.field_2095 ? this.method_46427() + (this.field_22759 - 8) / 2 : this.method_46427(), -1);
      }

   }

   @WrapOperation(
      method = {"method_48579"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_332;method_51430(Lnet/minecraft/class_327;Lnet/minecraft/class_5481;IIIZ)V"
)}
   )
   public void interceptTextRender(class_332 instance, class_327 textRenderer, class_5481 text, int x, int y, int color, boolean shadow, Operation<Void> original) {
      if (!this.placeholderApplies()) {
         original.call(new Object[]{instance, textRenderer, text, x, y, color, shadow});
      }
   }

   @Unique
   private boolean placeholderApplies() {
      return this.unfocusedPlaceholder != null && !this.method_25370();
   }
}
