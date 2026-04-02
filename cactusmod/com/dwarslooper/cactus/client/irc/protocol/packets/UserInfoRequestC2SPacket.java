package com.dwarslooper.cactus.client.irc.protocol.packets;

import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketOut;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.UUID;

public class UserInfoRequestC2SPacket implements PacketOut {
   private final UUID uuid;
   private final boolean remove;

   public UserInfoRequestC2SPacket(UUID uuid, boolean drop) {
      this.uuid = uuid;
      this.remove = drop;
   }

   public void write(ByteBuf buf) throws IOException {
      BufferUtils.writeUUID(buf, this.uuid);
      buf.writeBoolean(this.remove);
   }
}
