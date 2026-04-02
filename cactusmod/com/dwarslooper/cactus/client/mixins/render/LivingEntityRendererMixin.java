package com.dwarslooper.cactus.client.mixins.render;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.NameTags;
import com.dwarslooper.cactus.client.util.CactusConstants;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_922;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_922.class})
public abstract class LivingEntityRendererMixin<T extends class_1309> {
   @Inject(
      at = {@At("HEAD")},
      method = {"method_4055(Lnet/minecraft/class_1309;D)Z"},
      cancellable = true
   )
   private void viewOwnLabel(T livingEntity, double d, CallbackInfoReturnable<Boolean> cir) {
      NameTags nameTags = (NameTags)ModuleManager.get().get(NameTags.class);
      if (nameTags.active()) {
         boolean showNameTags = !(Boolean)nameTags.hideNameTagsInF1.get() || class_310.method_1498();
         if (livingEntity == CactusConstants.mc.method_1560()) {
            if ((Boolean)nameTags.showOwnName.get()) {
               cir.setReturnValue(showNameTags);
            }

            return;
         }

         if (!showNameTags) {
            cir.setReturnValue(false);
         }
      }

   }

   @Inject(
      method = {"method_38563(Lnet/minecraft/class_1309;)Z"},
      at = {@At("TAIL")},
      cancellable = true
   )
   private void shouldFlip(class_1309 entity, CallbackInfoReturnable<Boolean> cir) {
      if (CactusConstants.APRILFOOLS && entity instanceof class_1657) {
         cir.setReturnValue(true);
      }

   }
}
