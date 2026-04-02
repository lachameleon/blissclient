package com.dwarslooper.cactus.client.mixins.render;

import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_4185;
import net.minecraft.class_4264;
import net.minecraft.class_9848;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_4264.class})
public abstract class AbstractButtonMixin extends class_339 {
   @Unique
   private long lastColorShift = -1L;
   @Unique
   private float r;
   @Unique
   private float g;
   @Unique
   private float b;

   public AbstractButtonMixin(int i, int j, int k, int l, class_2561 text) {
      super(i, j, k, l, text);
   }

   @Inject(
      method = {"method_75794"},
      at = {@At("TAIL")}
   )
   public void render(class_332 context, CallbackInfo ci) {
      if (RenderUtils.darkMode && this instanceof class_4185) {
         RenderUtils.applyDarkmode(context, this);
      }

   }

   @WrapOperation(
      method = {"method_75794"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_9848;method_61317(F)I"
)}
   )
   public int disco(float alpha, Operation<Integer> original) {
      if (!CactusConstants.APRILFOOLS) {
         return (Integer)original.call(new Object[]{alpha});
      } else {
         if (System.currentTimeMillis() - this.lastColorShift > 200L || this.lastColorShift == -1L) {
            this.lastColorShift = System.currentTimeMillis();
            this.r = (float)Math.random();
            this.g = (float)Math.random();
            this.b = (float)Math.random();
         }

         return class_9848.method_61318(1.0F, this.r, this.g, this.b);
      }
   }
}
