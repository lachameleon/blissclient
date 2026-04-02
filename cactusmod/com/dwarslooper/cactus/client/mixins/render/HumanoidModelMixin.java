package com.dwarslooper.cactus.client.mixins.render;

import com.dwarslooper.cactus.client.render.cosmetics.emotes.EmoteAnimation;
import com.dwarslooper.cactus.client.render.cosmetics.emotes.EmoteState;
import com.dwarslooper.cactus.client.render.cosmetics.emotes.EmoteStateHandler;
import com.dwarslooper.cactus.client.util.mixinterface.IBipedEntityModel;
import com.dwarslooper.cactus.client.util.mixinterface.IEntityRenderState;
import java.util.Optional;
import net.minecraft.class_10034;
import net.minecraft.class_243;
import net.minecraft.class_572;
import net.minecraft.class_630;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_572.class})
public class HumanoidModelMixin implements IBipedEntityModel {
   @Shadow
   @Final
   public class_630 field_3398;
   @Shadow
   @Final
   public class_630 field_3391;
   @Shadow
   @Final
   public class_630 field_3392;
   @Shadow
   @Final
   public class_630 field_3397;
   @Shadow
   @Final
   public class_630 field_3401;
   @Shadow
   @Final
   public class_630 field_27433;
   @Unique
   private boolean applyEmoteTransform;

   @Inject(
      method = {"method_17087(Lnet/minecraft/class_10034;)V"},
      at = {@At("HEAD")}
   )
   public void resetValues(class_10034 bipedEntityRenderState, CallbackInfo ci) {
      EmoteStateHandler.resetScale(this.field_3398, this.field_3391, this.field_27433, this.field_3401, this.field_3397, this.field_3392);
      this.field_3398.field_3657 = this.field_3398.field_3655 = this.field_3398.field_3674 = 0.0F;
      this.field_3391.field_3657 = this.field_3391.field_3655 = this.field_3391.field_3674 = 0.0F;
      this.field_3392.field_3657 = -1.9F;
      this.field_3397.field_3657 = 1.9F;
   }

   @Inject(
      method = {"method_17087(Lnet/minecraft/class_10034;)V"},
      at = {@At("TAIL")}
   )
   public void updateAnglesForEmote(class_10034 renderState, CallbackInfo ci) {
      EmoteStateHandler.updateCleanStates();
      Optional<EmoteState> optionalState = EmoteStateHandler.get(((IEntityRenderState)renderState).cactus$getUUID());
      optionalState.ifPresent((state) -> {
         class_243 iPos = state.initialPlayerPosition();
         if (!state.cancelOnMove() || iPos.field_1352 == renderState.field_53325 && iPos.field_1351 == renderState.field_53326 && iPos.field_1350 == renderState.field_53327) {
            EmoteAnimation e = state.animation();
            if (e != null) {
               e.setCurrentTime(System.currentTimeMillis());
               e.getPose(this.field_3401, "RA");
               e.getPose(this.field_27433, "LA");
               e.getPose(this.field_3392, "RL");
               e.getPose(this.field_3397, "LL");
               e.getPose(this.field_3398, "H");
               e.getPose(this.field_3391, "B");
            }

         } else {
            EmoteStateHandler.getAnimationStates().remove(((IEntityRenderState)renderState).cactus$getUUID());
         }
      });
   }

   public void cactus$setApplyEmote(boolean applyEmote) {
      this.applyEmoteTransform = applyEmote;
   }
}
