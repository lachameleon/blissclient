package com.dwarslooper.cactus.client.irc.protocol.packets.teams;

import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketIn;
import com.dwarslooper.cactus.client.systems.teams.ClientTeamData;
import com.dwarslooper.cactus.client.systems.teams.TeamDataType;
import com.dwarslooper.cactus.client.systems.teams.TeamManager;
import io.netty.buffer.ByteBuf;
import java.io.IOException;

public class TeamDataPacket implements PacketIn {
   private final int teamId;
   private TeamDataType dataType;

   public TeamDataPacket(ByteBuf buf) {
      this.teamId = buf.readInt();

      try {
         this.dataType = TeamDataType.valueOf(BufferUtils.readString(buf));
         ClientTeamData teamData = (ClientTeamData)TeamManager.CACHE.getIfPresent(this.teamId);
         if (teamData == null) {
            throw new IllegalStateException("Received typed data for unknown team");
         }

         teamData.parse(this.dataType, buf);
      } catch (Exception var3) {
         IRCClient.LOGGER.error("Error parsing team data", var3);
         buf.discardReadBytes();
         buf.clear();
      }

   }

   public void handle(IRCClient client) throws IOException {
   }

   public int getTeamId() {
      return this.teamId;
   }

   public TeamDataType getDataType() {
      return this.dataType;
   }
}
