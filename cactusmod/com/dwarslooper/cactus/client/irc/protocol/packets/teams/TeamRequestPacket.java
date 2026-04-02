package com.dwarslooper.cactus.client.irc.protocol.packets.teams;

import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketOut;
import com.dwarslooper.cactus.client.systems.teams.TeamDataType;
import io.netty.buffer.ByteBuf;
import java.io.IOException;

public class TeamRequestPacket implements PacketOut {
   private final int id;
   private final TeamDataType content;

   public TeamRequestPacket(int teamId, TeamDataType type) {
      this.id = teamId;
      this.content = type;
   }

   public void write(ByteBuf buf) throws IOException {
      buf.writeInt(this.id);
      BufferUtils.writeString(buf, this.content.name());
   }
}
