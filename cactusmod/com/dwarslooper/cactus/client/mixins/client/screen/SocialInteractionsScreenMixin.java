package com.dwarslooper.cactus.client.mixins.client.screen;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.feature.content.ContentPackManager;
import com.dwarslooper.cactus.client.gui.screen.impl.ReportServerScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.irc.IRCStatus;
import com.dwarslooper.cactus.client.util.CactusConstants;
import net.minecraft.class_2561;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_5521;
import net.minecraft.class_5522;
import net.minecraft.class_642;
import net.minecraft.class_7919;
import net.minecraft.class_5522.class_5523;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_5522.class})
public abstract class SocialInteractionsScreenMixin extends class_437 {
   @Shadow
   private class_4185 field_26886;
   @Shadow
   class_5521 field_26882;
   @Final
   @Shadow
   private static class_2561 field_26878;

   protected SocialInteractionsScreenMixin(class_2561 title) {
      super(title);
   }

   @Inject(
      method = {"method_25426"},
      at = {@At("HEAD")}
   )
   public void init(CallbackInfo ci) {
      if (ContentPackManager.get().isEnabled(ContentPackManager.get().ofId("server_security"))) {
         class_642 serverReportInfo = CactusConstants.mc.method_1558();
         boolean connected = CactusClient.getInstance().getIrcClient().getStatus() == IRCStatus.CONNECTED;
         CButtonWidget reportServer;
         this.method_37063(reportServer = new CButtonWidget(4, this.field_22790 - 24, 100, 20, class_2561.method_43470("Report Server"), (button) -> {
            CactusConstants.mc.method_1507(new ReportServerScreen(serverReportInfo));
         }));
         if (!connected || serverReportInfo == null) {
            reportServer.field_22763 = false;
            reportServer.method_47400(class_7919.method_47407(class_2561.method_43470(connected ? "You are not on a server" : "You have to be connected to the IRC to report servers.")));
         }

      }
   }

   @Inject(
      method = {"method_31352"},
      at = {@At("TAIL")}
   )
   public void setTab(class_5523 currentTab, CallbackInfo ci) {
   }
}
