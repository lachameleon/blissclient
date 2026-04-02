package com.dwarslooper.cactus.client.irc.protocol.packets.teams;

import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketIn;
import com.dwarslooper.cactus.client.systems.teams.ClientTeamData;
import com.dwarslooper.cactus.client.systems.teams.TeamManager;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.class_1792;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

public class TeamsStatePacket implements PacketIn {
   private final List<ClientTeamData> teams;

   public TeamsStatePacket(ByteBuf buf) {
      this.teams = (List)BufferUtils.readCollection(buf, (b) -> {
         int id = b.readInt();
         return new ClientTeamData(id, new ClientTeamData.Info(BufferUtils.readString(b), (class_1792)class_7923.field_41178.method_63535(class_2960.method_60656(BufferUtils.readString(b))), BufferUtils.readString(b), (String)null, (UUID)null, buf.readInt(), -1, true));
      });
   }

   public void handle(IRCClient client) throws IOException {
      Map<Integer, ClientTeamData> existingTeams = TeamManager.CACHE.asMap();
      Set<Integer> receivedIds = (Set)this.teams.stream().map(ClientTeamData::getId).collect(Collectors.toSet());
      existingTeams.keySet().removeIf((id) -> {
         return !receivedIds.contains(id);
      });
      this.teams.forEach((team) -> {
         existingTeams.putIfAbsent(team.getId(), team);
      });
   }

   public List<ClientTeamData> getTeams() {
      return this.teams;
   }
}
