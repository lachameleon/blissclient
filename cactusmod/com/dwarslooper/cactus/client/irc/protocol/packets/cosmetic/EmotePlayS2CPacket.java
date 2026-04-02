package com.dwarslooper.cactus.client.irc.protocol.packets.cosmetic;

import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketIn;
import com.dwarslooper.cactus.client.render.cosmetics.emotes.EmoteState;
import com.dwarslooper.cactus.client.render.cosmetics.emotes.EmoteStateHandler;
import com.dwarslooper.cactus.client.systems.profile.AbstractCosmetic;
import com.dwarslooper.cactus.client.systems.profile.CosmeticParser;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.class_1657;

public class EmotePlayS2CPacket implements PacketIn {
   private final UUID playerUuid;
   private final AbstractCosmetic.Emote emote;

   public EmotePlayS2CPacket(ByteBuf buf) {
      this.playerUuid = BufferUtils.readUUID(buf);
      AbstractCosmetic<?> parsed = CosmeticParser.parse(buf, true);
      AbstractCosmetic.Emote var10001;
      if (parsed instanceof AbstractCosmetic.Emote) {
         AbstractCosmetic.Emote e = (AbstractCosmetic.Emote)parsed;
         var10001 = e;
      } else {
         var10001 = null;
      }

      this.emote = var10001;
   }

   public void handle(IRCClient client) throws IOException {
      if (Utils.isInWorld()) {
         class_1657 playerEntity = CactusConstants.mc.field_1687.method_18470(this.playerUuid);
         if (playerEntity != null && this.emote != null) {
            this.emote.getEmote().thenAccept((builtAnimation) -> {
               EmoteStateHandler.getAnimationStates().put(this.playerUuid, new EmoteState(builtAnimation, this.emote.cancelsOnMove(), playerEntity.method_73189()));
            });
         }

      }
   }
}
