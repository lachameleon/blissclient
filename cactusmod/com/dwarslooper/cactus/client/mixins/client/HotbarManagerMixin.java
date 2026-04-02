package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.mojang.datafixers.DataFixer;
import java.nio.file.Path;
import net.minecraft.class_302;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_302.class})
public abstract class HotbarManagerMixin {
   @Shadow
   protected abstract void method_1411();

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   public void preLoadHotbarStorage(Path directory, DataFixer dataFixer, CallbackInfo ci) {
      CactusClient.getLogger().info("Preloading hotbar storage");
      if (CactusClient.hasFinishedLoading() && (Boolean)CactusSettings.get().preLoadHotbar.get()) {
         this.method_1411();
      }

   }
}
