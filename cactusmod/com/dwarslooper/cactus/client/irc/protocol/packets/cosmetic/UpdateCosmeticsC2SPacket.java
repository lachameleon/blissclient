package com.dwarslooper.cactus.client.irc.protocol.packets.cosmetic;

import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketOut;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.Collection;

public class UpdateCosmeticsC2SPacket implements PacketOut {
   private final Collection<String> ids;

   public UpdateCosmeticsC2SPacket(Collection<String> ids) {
      this.ids = ids;
   }

   public void write(ByteBuf buf) throws IOException {
      BufferUtils.writeCollection(buf, this.ids, BufferUtils::writeString);
   }
}
