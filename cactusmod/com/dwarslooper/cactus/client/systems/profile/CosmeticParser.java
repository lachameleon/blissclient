package com.dwarslooper.cactus.client.systems.profile;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public enum CosmeticParser {
   CAPE((id, name, group, buf) -> {
      String textureURL = BufferUtils.readString(buf);
      int animationSpeed = buf.readInt();
      return () -> {
         return new AbstractCosmetic.Cape(id, name, group, textureURL, animationSpeed);
      };
   }),
   MODEL((id, name, group, buf) -> {
      String textureURL = BufferUtils.readString(buf);
      String modelURL = BufferUtils.readString(buf);
      return () -> {
         return new AbstractCosmetic.Model(id, name, group, textureURL, modelURL);
      };
   }),
   WINGS((id, name, group, buf) -> {
      String textureURL = BufferUtils.readString(buf);
      return () -> {
         return new AbstractCosmetic.Wings(id, name, group, textureURL);
      };
   }),
   EMOTE((id, name, group, buf) -> {
      String animationURL = BufferUtils.readString(buf);
      boolean cancelOnMove = buf.readBoolean();
      return () -> {
         return new AbstractCosmetic.Emote(id, name, group, animationURL, cancelOnMove);
      };
   });

   private final CosmeticParser.CosmeticParserFunction parserFunction;
   private static final CompletableFuture<Void> loadFuture = new CompletableFuture();

   private CosmeticParser(CosmeticParser.CosmeticParserFunction parserFunction) {
      this.parserFunction = parserFunction;
   }

   private Supplier<AbstractCosmetic<?>> parse(String id, ByteBuf buf) {
      String group = BufferUtils.readString(buf);
      String name = BufferUtils.readString(buf);
      return this.parserFunction.parse(id, name, group.isEmpty() ? null : group, buf);
   }

   public static AbstractCosmetic<?> parse(ByteBuf buf, boolean full) {
      String id = BufferUtils.readString(buf);
      String type = BufferUtils.readString(buf);
      AbstractCosmetic cosmetic = (AbstractCosmetic)AbstractCosmetic.COSMETICS_CACHE.getIfPresent(id);

      CosmeticParser parser;
      try {
         parser = valueOf(type);
      } catch (IllegalArgumentException var7) {
         CactusClient.getLogger().error("Tried to parse cosmetic of type '{}' which is not known to the client.", type);
         buf.clear();
         return null;
      }

      Supplier<AbstractCosmetic<?>> builder = parser.parse(id, buf);
      if (cosmetic == null) {
         cosmetic = (AbstractCosmetic)builder.get();
         AbstractCosmetic.push(cosmetic);
         if (full) {
            CompletableFuture var10000 = loadFuture;
            Objects.requireNonNull(cosmetic);
            var10000.thenRun(cosmetic::ensureDataLoaded);
         }
      }

      return cosmetic;
   }

   public static void executePendingPostInitTasks() {
      loadFuture.complete((Object)null);
   }

   // $FF: synthetic method
   private static CosmeticParser[] $values() {
      return new CosmeticParser[]{CAPE, MODEL, WINGS, EMOTE};
   }

   @FunctionalInterface
   public interface CosmeticParserFunction {
      Supplier<AbstractCosmetic<?>> parse(String var1, String var2, String var3, ByteBuf var4);
   }
}
