package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.compat.server.ServerCompat;
import com.dwarslooper.cactus.client.event.impl.ClientTickEvent;
import com.dwarslooper.cactus.client.event.impl.ItemUseEvent;
import com.dwarslooper.cactus.client.event.impl.WorldLeftEvent;
import com.dwarslooper.cactus.client.gui.screen.impl.BackgroundSelectorScreen;
import com.dwarslooper.cactus.client.gui.toast.ToastSystem;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.mojang.datafixers.DataFixer;
import java.nio.file.Path;
import net.minecraft.class_155;
import net.minecraft.class_310;
import net.minecraft.class_32;
import net.minecraft.class_437;
import net.minecraft.class_7196;
import net.minecraft.class_8580;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_310.class})
public abstract class MinecraftMixin {
   @Unique
   private static class_32 modifiedLevelStorage;
   @Unique
   private static boolean hasRenderedOnce = false;

   @Shadow
   public abstract class_32 method_1586();

   @Shadow
   public abstract class_8580 method_52702();

   @Shadow
   public abstract DataFixer method_1543();

   @Inject(
      at = {@At("TAIL")},
      method = {"method_1574"}
   )
   private void onTick(CallbackInfo info) {
      CactusClient.EVENT_BUS.post(ClientTickEvent.INSTANCE);
   }

   @Inject(
      method = {"method_1523"},
      at = {@At("HEAD")}
   )
   public void onRender(boolean tick, CallbackInfo ci) {
      if (!hasRenderedOnce) {
         BackgroundSelectorScreen.Configuration.get().registerTexturesNow();
         hasRenderedOnce = true;
      }

   }

   @Inject(
      method = {"method_76795(Lnet/minecraft/class_437;Z)V"},
      at = {@At("HEAD")}
   )
   private void onDisconnect(class_437 disconnectionScreen, boolean transferring, CallbackInfo ci) {
      if (CactusConstants.mc.field_1687 != null) {
         CactusClient.EVENT_BUS.post(WorldLeftEvent.DISCONNECTED);
      }

      CactusConstants.mc.field_1705.method_34003();
   }

   @ModifyArg(
      method = {"method_24288"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_1041;method_24286(Ljava/lang/String;)V"
)
   )
   private String setTitle(String original) {
      return (Boolean)CactusSettings.get().cactusWindowIconAndTitle.get() ? "Cactus Mod v" + String.valueOf(CactusConstants.META.getVersion()) + " MC " + class_155.method_16673().comp_4025() + (CactusConstants.DEVBUILD ? " (DEV)" : "") : original;
   }

   @Inject(
      method = {"method_1586"},
      at = {@At("TAIL")},
      cancellable = true
   )
   public void getLevelStorage(CallbackInfoReturnable<class_32> cir) {
      String dir = (String)CactusSettings.get().customLevelDirectory.get();
      if (!dir.isEmpty()) {
         Path path = Path.of(dir, new String[0]);
         if (modifiedLevelStorage != null && modifiedLevelStorage.method_19636().equals(path)) {
            cir.setReturnValue(modifiedLevelStorage);
         } else {
            try {
               modifiedLevelStorage = new class_32(path, Path.of(dir, new String[]{"backups"}), this.method_52702(), this.method_1543());
               cir.setReturnValue(modifiedLevelStorage);
            } catch (Exception var5) {
               ToastSystem.displayMessage("Load failed", "Couldn't load world folder");
               CactusSettings.get().customLevelDirectory.set("");
            }

         }
      }
   }

   @Inject(
      method = {"method_41735"},
      at = {@At("TAIL")},
      cancellable = true
   )
   public void createServerLoader(CallbackInfoReturnable<class_7196> cir) {
      cir.setReturnValue(new class_7196(CactusConstants.mc, this.method_1586()));
   }

   @Inject(
      method = {"method_47596"},
      at = {@At("TAIL")},
      cancellable = true
   )
   public void modifyTelemetry(CallbackInfoReturnable<Boolean> cir) {
      if ((Boolean)cir.getReturnValue() && CactusClient.hasFinishedLoading() && (Boolean)CactusSettings.get().disableTelemetry.get()) {
         cir.setReturnValue(false);
      }

   }

   @Inject(
      method = {"method_55505"},
      at = {@At("HEAD")}
   )
   public void disconnected(CallbackInfo ci) {
      ServerCompat.close();
   }

   @Inject(
      method = {"method_1555"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void injectRdi(CallbackInfoReturnable<Boolean> cir) {
      cir.setReturnValue(false);
   }

   @Inject(
      method = {"method_1583"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_636;method_2923()Z"
)},
      cancellable = true
   )
   public void injectInteract(CallbackInfo ci) {
      ItemUseEvent event = (ItemUseEvent)CactusClient.EVENT_BUS.post(new ItemUseEvent(CactusConstants.mc.field_1724, CactusConstants.mc.field_1724.method_31548().method_7391(), CactusConstants.mc.field_1765));
      if (event.isCancelled()) {
         ci.cancel();
      }

   }
}
