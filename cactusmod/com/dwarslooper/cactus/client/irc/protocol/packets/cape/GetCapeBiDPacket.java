package com.dwarslooper.cactus.client.irc.protocol.packets.cape;

import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketIn;
import com.dwarslooper.cactus.client.irc.protocol.PacketOut;
import com.dwarslooper.cactus.client.systems.profile.AbstractCosmetic;
import com.dwarslooper.cactus.client.systems.profile.CosmeticList;
import com.dwarslooper.cactus.client.systems.profile.ProfileHandler;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.UUID;

public class GetCapeBiDPacket implements PacketIn, PacketOut {
   private final UUID uuid;
   private String capeId;
   private String capeUrl;
   private String filename;
   private int animationSpeed;

   public GetCapeBiDPacket(ByteBuf buf) {
      this.uuid = UUID.fromString(BufferUtils.readString(buf));
      this.capeId = BufferUtils.readString(buf);
      this.capeUrl = BufferUtils.readString(buf);
      this.filename = BufferUtils.readString(buf);
      this.animationSpeed = buf.readInt();
   }

   public GetCapeBiDPacket(UUID uuid) {
      this.uuid = uuid;
   }

   public void write(ByteBuf buf) {
      BufferUtils.writeString(buf, this.uuid.toString());
   }

   public void handle(IRCClient client) {
      ProfileHandler.handleUpdatePacket(this.uuid, new CosmeticList(List.of(new AbstractCosmetic.Cape(this.capeId, this.capeId, (String)null, this.capeUrl, this.animationSpeed))));
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public String getCapeId() {
      return this.capeId;
   }

   public String getCapeUrl() {
      return this.capeUrl;
   }

   public int getAnimationSpeed() {
      return this.animationSpeed;
   }
}
