package com.dwarslooper.cactus.client.mixins.network.share;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.systems.worldshare.OasisHostManager;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ServerChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import java.io.IOException;
import java.net.InetAddress;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3242;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({class_3242.class})
public abstract class ServerConnectionListenerMixin {
   @Unique
   private static final ThreadLocal<Boolean> isInitializing = ThreadLocal.withInitial(() -> {
      return false;
   });

   @Shadow
   public abstract void method_14354(@Nullable InetAddress var1, int var2) throws IOException;

   @Inject(
      method = {"method_14354"},
      at = {@At("HEAD")}
   )
   private void bind(InetAddress address, int port, CallbackInfo ci) {
      if (OasisHostManager.forThisSession) {
         if (!(Boolean)isInitializing.get()) {
            isInitializing.set(true);

            try {
               this.method_14354(address, port);
            } catch (Exception var8) {
               CactusClient.getLogger().error("Failed to bind address for cactus relay", var8);
            } finally {
               isInitializing.set(false);
            }
         } else {
            OasisHostManager.createSession();
         }

      }
   }

   @ModifyArg(
      method = {"method_14354"},
      at = @At(
   value = "INVOKE",
   target = "Lio/netty/bootstrap/ServerBootstrap;channel(Ljava/lang/Class;)Lio/netty/bootstrap/AbstractBootstrap;",
   remap = false
)
   )
   private Class<? extends ServerChannel> redirectChannel(Class<? extends ServerChannel> aClass) {
      CactusClient.getLogger().info("Returning channel");
      return (Boolean)isInitializing.get() && OasisHostManager.forThisSession ? LocalServerChannel.class : aClass;
   }

   @Redirect(
      method = {"method_14354"},
      at = @At(
   value = "INVOKE",
   target = "Lio/netty/bootstrap/ServerBootstrap;localAddress(Ljava/net/InetAddress;I)Lio/netty/bootstrap/AbstractBootstrap;",
   remap = false
)
   )
   private AbstractBootstrap<ServerBootstrap, ServerChannel> redirectAddress(ServerBootstrap instance, InetAddress address, int port) {
      CactusClient.getLogger().info("Relay bound");
      return (Boolean)isInitializing.get() && OasisHostManager.forThisSession ? instance.localAddress(new LocalAddress("cactus-relay")) : instance.localAddress(address, port);
   }

   @Inject(
      method = {"method_14356"},
      at = {@At("HEAD")}
   )
   private void stop(CallbackInfo ci) {
      ((OasisHostManager)CactusClient.getConfig(OasisHostManager.class)).close();
   }
}
