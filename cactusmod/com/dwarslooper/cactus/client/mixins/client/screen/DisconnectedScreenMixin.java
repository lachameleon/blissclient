package com.dwarslooper.cactus.client.mixins.client.screen;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.util.AutoReconnect;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.mixinterface.DisconnectedScreenAccess;
import java.util.Objects;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_412;
import net.minecraft.class_4185;
import net.minecraft.class_419;
import net.minecraft.class_437;
import net.minecraft.class_500;
import net.minecraft.class_639;
import net.minecraft.class_642;
import net.minecraft.class_9112;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_419.class})
public abstract class DisconnectedScreenMixin extends class_437 implements DisconnectedScreenAccess {
   @Shadow
   @Final
   private class_437 field_2456;
   @Unique
   private class_4185 reconnectButton;

   protected DisconnectedScreenMixin(class_2561 title) {
      super(title);
   }

   @Inject(
      method = {"method_25426"},
      at = {@At("TAIL")}
   )
   private void addReconnectButton(CallbackInfo ci) {
      AutoReconnect autoReconnect = (AutoReconnect)ModuleManager.get().get(AutoReconnect.class);
      if (autoReconnect != null && autoReconnect.active() && autoReconnect.canReconnect()) {
         autoReconnect.restartCountdownIfPossible();
         if (!(Boolean)autoReconnect.hideButtons.get()) {
            int baseY = Math.min(this.field_22790 / 2 + 64, this.field_22790 - 30);
            long now = System.currentTimeMillis();
            this.reconnectButton = (class_4185)this.method_37063(class_4185.method_46430(class_2561.method_43470((String)Objects.requireNonNull(this.getLabel(autoReconnect, now))), (button) -> {
               this.reconnect(autoReconnect);
            }).method_46434(this.field_22789 / 2 - 100, baseY - 24, 200, 20).method_46431());
         }
      }
   }

   public void cactus$autoReconnectTick() {
      AutoReconnect autoReconnect = (AutoReconnect)ModuleManager.get().get(AutoReconnect.class);
      if (autoReconnect != null && autoReconnect.active() && autoReconnect.canReconnect()) {
         long now = System.currentTimeMillis();
         if (autoReconnect.shouldReconnectNow(now)) {
            this.reconnect(autoReconnect);
         } else if (this.reconnectButton != null) {
            this.reconnectButton.method_25355(class_2561.method_43470((String)Objects.requireNonNull(this.getLabel(autoReconnect, now))));
         }

      }
   }

   @Unique
   private String getLabel(AutoReconnect autoReconnect, long nowMillis) {
      int seconds = Math.max(autoReconnect.getSecondsLeft(nowMillis), 0);
      return "Reconnect (" + seconds + ")";
   }

   @Unique
   private void reconnect(AutoReconnect autoReconnect) {
      class_639 address = autoReconnect.getLastAddress();
      class_642 data = autoReconnect.getLastData();
      if (address != null && data != null) {
         if (CactusConstants.mc.field_1687 != null) {
            CactusConstants.mc.method_73360(class_2561.method_43470("Disconnecting"));
         }

         class_412.method_36877((class_437)(this.field_2456 instanceof class_500 ? this.field_2456 : new class_500((class_437)Objects.requireNonNull(this.field_2456))), (class_310)Objects.requireNonNull(CactusConstants.mc), address, data, false, (class_9112)null);
      }
   }
}
