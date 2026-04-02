package com.dwarslooper.cactus.client.irc.protocol.packets.cosmetic;

import com.dwarslooper.cactus.client.feature.command.arguments.CapeArgumentType;
import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketIn;
import com.dwarslooper.cactus.client.systems.profile.AbstractCosmetic;
import com.dwarslooper.cactus.client.systems.profile.CosmeticParser;
import com.dwarslooper.cactus.client.systems.profile.ProfileHandler;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ClientCosmeticListS2CPacket implements PacketIn {
   private static final List<CompletableFuture<ClientCosmeticListS2CPacket>> callbacks = new ArrayList();
   private final List<AbstractCosmetic<?>> cosmetics;

   public static CompletableFuture<ClientCosmeticListS2CPacket> addCallback() {
      CompletableFuture<ClientCosmeticListS2CPacket> future = new CompletableFuture();
      callbacks.add(future);
      return future;
   }

   public ClientCosmeticListS2CPacket(ByteBuf buf) {
      this.cosmetics = (List)BufferUtils.readCollection(buf, (b) -> {
         return CosmeticParser.parse(b, false);
      });
      this.cosmetics.removeIf(Objects::isNull);
   }

   public void handle(IRCClient client) throws IOException {
      ProfileHandler.setClientAvailableCosmetics(this.cosmetics);
      CapeArgumentType.updateAvailableCapes(this.cosmetics.stream().filter((c) -> {
         return c instanceof AbstractCosmetic.Cape;
      }).map(AbstractCosmetic::getId).sorted().toList());
      callbacks.removeIf((future) -> {
         future.complete(this);
         return true;
      });
   }

   public List<AbstractCosmetic<?>> getCosmetics() {
      return this.cosmetics;
   }
}
