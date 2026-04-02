package com.dwarslooper.cactus.client.irc.protocol.packets;

import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketIn;
import com.dwarslooper.cactus.client.irc.protocol.PacketOut;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import jdk.jfr.Experimental;

@Experimental
public class UserSyncBiDPacket implements PacketIn, PacketOut {
   private final List<UUID> profileHashes;

   public UserSyncBiDPacket(List<UUID> profileHashes) {
      this.profileHashes = profileHashes;
   }

   public UserSyncBiDPacket(ByteBuf buf) {
      this.profileHashes = (List)BufferUtils.readCollection(buf, BufferUtils::readUUID);
   }

   public void handle(IRCClient client) throws IOException {
      System.out.println("Returned packet from cnet, values will be printed below \\/");
      System.out.println((new ArrayList(this.profileHashes)).toString());
   }

   public void write(ByteBuf buf) throws IOException {
      BufferUtils.writeCollection(buf, this.profileHashes, BufferUtils::writeUUID);
   }
}
