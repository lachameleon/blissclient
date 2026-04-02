package com.dwarslooper.cactus.client.irc.protocol.packets.auth;

import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketOut;
import com.dwarslooper.cactus.client.util.CactusConstants;
import io.netty.buffer.ByteBuf;

public class HandshakeC2SPacket implements PacketOut {
   private final String username;
   private final String version;

   public HandshakeC2SPacket(String username) {
      this.username = username;
      this.version = CactusConstants.VERSION;
   }

   public void write(ByteBuf buf) {
      BufferUtils.writeString(buf, this.username);
      BufferUtils.writeString(buf, this.version);
   }
}
