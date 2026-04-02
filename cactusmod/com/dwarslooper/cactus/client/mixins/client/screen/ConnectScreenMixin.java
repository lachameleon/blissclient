package com.dwarslooper.cactus.client.mixins.client.screen;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.util.AutoReconnect;
import net.minecraft.class_310;
import net.minecraft.class_412;
import net.minecraft.class_437;
import net.minecraft.class_639;
import net.minecraft.class_642;
import net.minecraft.class_9112;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_412.class})
public abstract class ConnectScreenMixin {
   @Inject(
      method = {"method_36877(Lnet/minecraft/class_437;Lnet/minecraft/class_310;Lnet/minecraft/class_639;Lnet/minecraft/class_642;ZLnet/minecraft/class_9112;)V"},
      at = {@At("HEAD")}
   )
   private static void cactus$storeLastServer(class_437 parent, class_310 mc, class_639 address, class_642 data, boolean bl, class_9112 transferState, CallbackInfo ci) {
      AutoReconnect autoReconnect = (AutoReconnect)ModuleManager.get().get(AutoReconnect.class);
      if (autoReconnect != null) {
         autoReconnect.setLastServer(address, data);
      }

   }
}
