package com.dwarslooper.cactus.client.irc.protocol.packets.cosmetic;

import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketOut;
import io.netty.buffer.ByteBuf;
import java.io.IOException;

public class UseEmoteC2SPacket implements PacketOut {
   private final String emote;

   public UseEmoteC2SPacket(String emote) {
      this.emote = emote;
   }

   public void write(ByteBuf buf) throws IOException {
      BufferUtils.writeString(buf, this.emote);
   }
}
