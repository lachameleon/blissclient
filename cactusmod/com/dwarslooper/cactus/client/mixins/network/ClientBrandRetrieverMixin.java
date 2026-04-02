package com.dwarslooper.cactus.client.mixins.network;

import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.util.CactusConstants;
import net.minecraft.class_155;
import net.minecraft.client.ClientBrandRetriever;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClientBrandRetriever.class})
public abstract class ClientBrandRetrieverMixin {
   @Inject(
      method = {"getClientModName"},
      at = {@At("TAIL")},
      cancellable = true,
      remap = false
   )
   private static void getBrandName(CallbackInfoReturnable<String> cir) {
      if (CactusConstants.isDoneInitializing() && (Boolean)CactusSettings.get().customClientBrand.get()) {
         String var10001 = String.valueOf(CactusConstants.META.getVersion());
         cir.setReturnValue("cactus-" + var10001 + ":" + class_155.method_16673().comp_4025());
      }
   }
}
