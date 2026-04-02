package com.dwarslooper.cactus.client.irc.protocol.packets;

import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.irc.protocol.PacketIn;
import com.dwarslooper.cactus.client.irc.protocol.PacketOut;
import io.netty.buffer.ByteBuf;

public class KeepAliveBiDPacket implements PacketOut, PacketIn {
   private final int id;

   public KeepAliveBiDPacket(ByteBuf buf) {
      this.id = buf.readInt();
   }

   public void write(ByteBuf buf) {
      buf.writeInt(this.id);
   }

   public void handle(IRCClient client) {
      client.sendPacket(this);
   }
}
