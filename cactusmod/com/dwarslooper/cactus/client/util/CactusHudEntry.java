package com.dwarslooper.cactus.client.util;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.irc.IRCStatus;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import net.minecraft.class_11630;
import net.minecraft.class_11632;
import net.minecraft.class_1937;
import net.minecraft.class_2818;
import net.minecraft.class_2960;
import org.jetbrains.annotations.Nullable;

public class CactusHudEntry implements class_11632 {
   public static final class_2960 ID = class_2960.method_60655("cactus", "cactus");

   public void method_72751(class_11630 lines, @Nullable class_1937 world, @Nullable class_2818 clientChunk, @Nullable class_2818 chunk) {
      IRCClient ircClient = CactusClient.getInstance().getIrcClient();
      String prefix = "§2[§aCactus§2] §r";
      String var10002 = CactusConstants.VERSION;
      lines.method_72743(ID, "§2[§aCactus§2] §rCactus " + var10002 + (CactusConstants.DEVBUILD ? " (Devbuild)" : "") + ((Boolean)CactusSettings.get().experiments.get() ? " (§cexperiments§r)" : ""));
      class_2960 var10001 = ID;
      var10002 = String.valueOf(ircClient.getStatus());
      lines.method_72743(var10001, "§2[§aCactus§2] §rService: " + var10002 + (ircClient.getStatus() == IRCStatus.CONNECTED ? " -> " + ircClient.getAddress().getAddress().getHostAddress() : ""));
   }

   public boolean method_72753(boolean reducedDebugInfo) {
      return true;
   }
}
