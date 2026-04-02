package com.dwarslooper.cactus.client.irc.protocol.packets.auth;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketIn;
import com.dwarslooper.cactus.client.irc.protocol.packets.cosmetic.GetAvailableCosmeticsC2SPacket;
import com.dwarslooper.cactus.client.systems.profile.ProfileHandler;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;

public class LoginResponseS2CPacket implements PacketIn {
   private final boolean success;
   private String message;

   public LoginResponseS2CPacket(ByteBuf buf) {
      this.success = buf.readBoolean();
      if (!this.success) {
         this.message = BufferUtils.readString(buf);
      }

   }

   public void handle(IRCClient client) throws IOException {
      if (!this.success) {
         ChatUtils.errorPrefix("CNet", this.message);
         IRCClient.connectionDenied = true;
         IRCClient.notification(true, "Service", "§c" + this.message);
      } else {
         CactusClient.getLogger().info("CNet connected to: {}", CactusClient.getInstance().getIrcClient().getAddress().getAddress().getHostAddress());
         ChatUtils.infoPrefix("CNet", "§aService connected");
         CactusClient.getInstance().getIrcClient().sendPacket(new GetAvailableCosmeticsC2SPacket());
         ProfileHandler.invalidateProfiles();
      }

   }
}
