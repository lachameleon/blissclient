package com.dwarslooper.cactus.client.mixins.network;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.util.AntiForcePack;
import com.dwarslooper.cactus.client.feature.modules.util.TransferConfirm;
import com.dwarslooper.cactus.client.util.CactusConstants;
import net.minecraft.class_124;
import net.minecraft.class_2535;
import net.minecraft.class_2561;
import net.minecraft.class_2720;
import net.minecraft.class_410;
import net.minecraft.class_437;
import net.minecraft.class_8673;
import net.minecraft.class_9151;
import net.minecraft.class_9851;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_8673.class})
public abstract class ClientCommonPacketListenerImplMixin {
   @Shadow
   @Final
   protected class_2535 field_45589;
   @Unique
   private class_9851 proceedWithTransfer;

   public ClientCommonPacketListenerImplMixin() {
      this.proceedWithTransfer = class_9851.field_52396;
   }

   @Shadow
   public abstract void method_56150(class_9151 var1);

   @Inject(
      method = {"method_52784"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void antiForceResourcePack(class_2720 packet, CallbackInfo ci) {
      AntiForcePack antiForcePack = (AntiForcePack)ModuleManager.get().get(AntiForcePack.class);
      if (antiForcePack.active()) {
         ci.cancel();
         antiForcePack.sendPackInfo(packet);
         antiForcePack.sendPackets(this.field_45589, packet.comp_2158());
      }

   }

   @Inject(
      method = {"method_56150"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onTransfer(class_9151 packet, CallbackInfo ci) {
      if (this.proceedWithTransfer == class_9851.field_52396) {
         TransferConfirm module = (TransferConfirm)ModuleManager.get().get(TransferConfirm.class);
         if (module.active()) {
            if (module.isTrusted(packet.comp_2240())) {
               return;
            }

            ci.cancel();
            CactusConstants.mc.execute(() -> {
               try {
                  class_2561 text = class_2561.method_43469("modules.transferConfirm.confirmText", new Object[]{class_2561.method_43470(packet.comp_2240()).method_27692(class_124.field_1073)});
                  class_437 before = CactusConstants.mc.field_1755;
                  class_410 confirmScreen = new class_410((b) -> {
                     this.proceedWithTransfer = b ? class_9851.field_52394 : class_9851.field_52395;
                     CactusConstants.mc.method_1507(before);
                     this.method_56150(packet);
                  }, class_2561.method_43471("modules.transferConfirm.confirmTitle"), text);
                  CactusConstants.mc.method_1507(confirmScreen);
               } catch (Exception var5) {
                  var5.printStackTrace();
               }

            });
         }
      } else {
         if (this.proceedWithTransfer == class_9851.field_52395) {
            ci.cancel();
         }

         this.proceedWithTransfer = class_9851.field_52396;
      }

   }
}
