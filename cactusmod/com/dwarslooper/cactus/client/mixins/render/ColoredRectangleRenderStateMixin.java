package com.dwarslooper.cactus.client.mixins.render;

import com.dwarslooper.cactus.client.util.mixinterface.IRotatableColoredQuadGuiElementRenderState;
import net.minecraft.class_11242;
import net.minecraft.class_4588;
import org.joml.Matrix3x2fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_11242.class})
public abstract class ColoredRectangleRenderStateMixin implements IRotatableColoredQuadGuiElementRenderState {
   @Unique
   private boolean drawSideways;

   @Shadow
   public abstract Matrix3x2fc comp_4070();

   @Shadow
   public abstract int comp_4071();

   @Shadow
   public abstract int comp_4073();

   @Shadow
   public abstract int comp_4072();

   @Shadow
   public abstract int comp_4074();

   @Shadow
   public abstract int comp_4075();

   @Shadow
   public abstract int comp_4076();

   public void cactus$setSideways(boolean sideways) {
      this.drawSideways = sideways;
   }

   public boolean cactus$isSideways() {
      return this.drawSideways;
   }

   @Inject(
      method = {"method_70917"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void injectRotate(class_4588 vertices, CallbackInfo ci) {
      if (this.drawSideways) {
         vertices.method_70815(this.comp_4070(), (float)this.comp_4071(), (float)this.comp_4072()).method_39415(this.comp_4075());
         vertices.method_70815(this.comp_4070(), (float)this.comp_4071(), (float)this.comp_4074()).method_39415(this.comp_4075());
         vertices.method_70815(this.comp_4070(), (float)this.comp_4073(), (float)this.comp_4074()).method_39415(this.comp_4076());
         vertices.method_70815(this.comp_4070(), (float)this.comp_4073(), (float)this.comp_4072()).method_39415(this.comp_4076());
         ci.cancel();
      }

   }
}
