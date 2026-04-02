package com.dwarslooper.cactus.client.irc.protocol.packets.social;

import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketIn;
import io.netty.buffer.ByteBuf;
import java.io.IOException;

public class JoinMeReceivePacket implements PacketIn {
   private final String playerName;
   private final String serverAddress;

   public JoinMeReceivePacket(ByteBuf buf) {
      this.playerName = BufferUtils.readString(buf);
      this.serverAddress = BufferUtils.readString(buf);
   }

   public void handle(IRCClient client) throws IOException {
      client.handle(this);
   }

   public String getPlayerName() {
      return this.playerName;
   }

   public String getServerAddress() {
      return this.serverAddress;
   }
}
