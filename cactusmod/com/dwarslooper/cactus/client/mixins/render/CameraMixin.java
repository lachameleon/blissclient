package com.dwarslooper.cactus.client.mixins.render;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.util.FreeLook;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_4184;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_4184.class})
public abstract class CameraMixin {
   @Unique
   private boolean stateLocked;
   @Shadow
   private float field_18718;
   @Shadow
   private float field_18717;

   @Shadow
   protected abstract void method_19325(float var1, float var2);

   @Shadow
   public abstract class_243 method_71156();

   @Shadow
   public abstract Quaternionf method_23767();

   @Inject(
      method = {"method_19321"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_4184;method_19327(DDD)V"
)}
   )
   public void update(class_1937 area, class_1297 focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
      FreeLook freeLook = (FreeLook)ModuleManager.get().get(FreeLook.class);
      if (freeLook.isFreeLooking()) {
         if (thirdPerson) {
            if (!this.stateLocked) {
               FreeLook.camYaw = this.field_18718;
               FreeLook.camPitch = this.field_18717;
            }

            this.method_19325(FreeLook.camYaw, FreeLook.camPitch);
            this.stateLocked = true;
         } else {
            this.stateLocked = false;
         }
      } else {
         this.stateLocked = false;
      }

   }
}
