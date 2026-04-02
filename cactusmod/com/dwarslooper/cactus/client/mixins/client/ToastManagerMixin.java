package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.gui.toast.internal.CToast;
import net.minecraft.class_368;
import net.minecraft.class_374;
import net.minecraft.class_374.class_375;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_375.class})
public abstract class ToastManagerMixin {
   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   public void create(class_374 toastManager, class_368 instance, int topIndex, int requiredSpaceCount, CallbackInfo ci) {
      if (instance instanceof CToast) {
         CToast cToast = (CToast)instance;
         cToast.posY = topIndex * 32;
      }

   }
}
