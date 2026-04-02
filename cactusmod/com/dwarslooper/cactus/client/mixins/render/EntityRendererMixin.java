package com.dwarslooper.cactus.client.mixins.render;

import com.dwarslooper.cactus.client.util.mixinterface.IEntityRenderState;
import net.minecraft.class_10017;
import net.minecraft.class_1297;
import net.minecraft.class_897;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_897.class})
public class EntityRendererMixin {
   @Inject(
      method = {"method_62354"},
      at = {@At("HEAD")}
   )
   public void insertUUID(class_1297 entity, class_10017 state, float tickProgress, CallbackInfo ci) {
      ((IEntityRenderState)state).cactus$setUUID(entity.method_5667());
   }
}
