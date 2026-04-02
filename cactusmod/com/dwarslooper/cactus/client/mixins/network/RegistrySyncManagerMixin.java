package com.dwarslooper.cactus.client.mixins.network;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.systems.worldshare.OasisHostManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.minecraft.class_8610;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({RegistrySyncManager.class})
public abstract class RegistrySyncManagerMixin {
   @Inject(
      method = {"configureClient"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/fabricmc/fabric/impl/registry/sync/RegistrySyncManager;getIncompatibleClientText(Ljava/lang/String;Ljava/util/Map;)Lnet/minecraft/class_2561;"
)},
      cancellable = true
   )
   private static void skipRegistrySyncFail(CallbackInfo ci) {
      CactusClient.getLogger().warn("Skipped invalid registries");
      ci.cancel();
   }

   @Inject(
      method = {"configureClient"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void skipClientConfiguration(class_8610 handler, MinecraftServer server, CallbackInfo ci) {
      if (OasisHostManager.forThisSession) {
         CactusClient.getLogger().warn("Skipped configure client for registries");
         ci.cancel();
      }

   }
}
