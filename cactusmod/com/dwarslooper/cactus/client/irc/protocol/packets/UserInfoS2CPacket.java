package com.dwarslooper.cactus.client.irc.protocol.packets;

import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketIn;
import com.dwarslooper.cactus.client.systems.profile.CosmeticList;
import com.dwarslooper.cactus.client.systems.profile.CosmeticParser;
import com.dwarslooper.cactus.client.systems.profile.ProfileHandler;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.UUID;

public abstract class UserInfoS2CPacket implements PacketIn {
   protected final UUID uuid;

   public UserInfoS2CPacket(ByteBuf buf) {
      this.uuid = BufferUtils.readUUID(buf);
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public static class Status extends UserInfoS2CPacket {
      public Status(ByteBuf buf) {
         super(buf);
      }

      public void handle(IRCClient client) throws IOException {
      }
   }

   public static class UpdateCape extends UserInfoS2CPacket {
      private final String capeId;
      private final String capeUrl;
      private final int animationSpeed;

      public UpdateCape(ByteBuf buf) {
         super(buf);
         this.capeId = BufferUtils.readString(buf);
         this.capeUrl = BufferUtils.readString(buf);
         this.animationSpeed = buf.readInt();
      }

      public void handle(IRCClient client) throws IOException {
      }
   }

   public static class Full extends UserInfoS2CPacket {
      private final boolean online;
      private final String rank;
      private final CosmeticList cosmetics;

      public Full(ByteBuf buf) {
         super(buf);
         this.online = buf.readBoolean();
         this.rank = BufferUtils.readString(buf);
         this.cosmetics = new CosmeticList(BufferUtils.readCollection(buf, (b) -> {
            return CosmeticParser.parse(b, true);
         }));
      }

      public void handle(IRCClient client) throws IOException {
         ProfileHandler.fromProfile(this.uuid).setProfileState(this.online, this.rank);
         if (!this.cosmetics.isEmpty() || this.online) {
            ProfileHandler.handleUpdatePacket(this.uuid, this.cosmetics);
         }

      }
   }
}
