package com.dwarslooper.cactus.client.irc.protocol.packets.teams;

import com.dwarslooper.cactus.client.irc.protocol.PacketOut;
import io.netty.buffer.ByteBuf;
import java.io.IOException;

public class TeamMembershipUpdateC2SPacket implements PacketOut {
   private final int teamId;
   private final boolean accept;

   public TeamMembershipUpdateC2SPacket(int teamId, boolean accept) {
      this.teamId = teamId;
      this.accept = accept;
   }

   public void write(ByteBuf buf) throws IOException {
      buf.writeInt(this.teamId);
      buf.writeBoolean(this.accept);
   }
}
