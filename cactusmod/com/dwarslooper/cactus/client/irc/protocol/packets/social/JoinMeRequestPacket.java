package com.dwarslooper.cactus.client.irc.protocol.packets.social;

import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketOut;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.UUID;

public class JoinMeRequestPacket implements PacketOut {
   private final UUID receiver;
   private final String serverAddress;

   public JoinMeRequestPacket(UUID receiver, String serverAddress) {
      this.receiver = receiver;
      this.serverAddress = serverAddress;
   }

   public void write(ByteBuf buf) throws IOException {
      BufferUtils.writeUUID(buf, this.receiver);
      BufferUtils.writeString(buf, this.serverAddress);
   }
}
