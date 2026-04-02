package com.dwarslooper.cactus.client.irc.protocol.packets.link;

import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketOut;
import io.netty.buffer.ByteBuf;
import java.io.IOException;

public class LinkCodePacket implements PacketOut {
   private final LinkCodePacket.Type type;
   private final String code;

   public LinkCodePacket(LinkCodePacket.Type type, String code) {
      this.type = type;
      this.code = code;
   }

   public void write(ByteBuf buf) throws IOException {
      BufferUtils.writeString(buf, this.type.name());
      BufferUtils.writeString(buf, this.code);
   }

   public static enum Type {
      Discord;

      // $FF: synthetic method
      private static LinkCodePacket.Type[] $values() {
         return new LinkCodePacket.Type[]{Discord};
      }
   }
}
