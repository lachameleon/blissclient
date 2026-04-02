package com.dwarslooper.cactus.client.mixins.network;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.systems.worldshare.OasisHostManager;
import java.net.SocketAddress;
import net.minecraft.class_1130;
import net.minecraft.class_1132;
import net.minecraft.class_11560;
import net.minecraft.class_11871;
import net.minecraft.class_2561;
import net.minecraft.class_29;
import net.minecraft.class_3324;
import net.minecraft.class_7659;
import net.minecraft.class_7780;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1130.class})
public abstract class IntegratedPlayerListMixin extends class_3324 {
   public IntegratedPlayerListMixin(MinecraftServer server, class_7780<class_7659> registryManager, class_29 saveHandler, class_11871 managementListener) {
      super(server, registryManager, saveHandler, managementListener);
   }

   @Shadow
   public abstract class_1132 method_4811();

   @Inject(
      method = {"method_14586"},
      at = {@At("TAIL")},
      cancellable = true
   )
   public void canJoin(SocketAddress address, class_11560 configEntry, CallbackInfoReturnable<class_2561> cir) {
      OasisHostManager handler = (OasisHostManager)CactusClient.getConfig(OasisHostManager.class);
      if (this.method_4811().method_3860() && !this.method_4811().method_19466(configEntry) && handler.isWhitelistEnabled() && !handler.getWhitelist().stream().map(String::toLowerCase).toList().contains(configEntry.comp_4423().toLowerCase())) {
         cir.setReturnValue(class_2561.method_48321("worldHosting.notWhitelisted", "This world is private.\nAsk the host to add you to the white-list."));
      }

   }
}
