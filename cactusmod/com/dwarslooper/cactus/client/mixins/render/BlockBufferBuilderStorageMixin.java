package com.dwarslooper.cactus.client.mixins.render;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1921;
import net.minecraft.class_750;
import net.minecraft.class_9799;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({class_750.class})
public abstract class BlockBufferBuilderStorageMixin {
   @Shadow
   @Final
   private Map<class_1921, class_9799> field_3951;

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   public void init(CallbackInfo callbackInfo) {
   }
}
