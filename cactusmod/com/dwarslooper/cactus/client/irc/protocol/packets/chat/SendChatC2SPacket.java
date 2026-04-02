package com.dwarslooper.cactus.client.irc.protocol.packets.chat;

import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketOut;
import io.netty.buffer.ByteBuf;
import java.io.IOException;

public class SendChatC2SPacket implements PacketOut {
   private final String message;

   public SendChatC2SPacket(String message) {
      this.message = message;
   }

   public void write(ByteBuf buf) throws IOException {
      BufferUtils.writeString(buf, this.message);
   }
}
