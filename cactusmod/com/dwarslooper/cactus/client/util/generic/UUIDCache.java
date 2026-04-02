package com.dwarslooper.cactus.client.util.generic;

import com.dwarslooper.cactus.client.systems.ias.account.Auth;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_4844;

public class UUIDCache {
   public static final UUID EMPTY_UUID = new UUID(0L, 0L);
   private static final Map<String, CompletableFuture<UUID>> UUID_CACHE = new HashMap();

   public static CompletableFuture<UUID> getFuture(String name) {
      return (CompletableFuture)UUID_CACHE.computeIfAbsent(name, (n) -> {
         return CompletableFuture.supplyAsync(() -> {
            return Auth.resolveUUID(n);
         });
      });
   }

   public static UUID getOrResolve(String name) {
      return (UUID)getFuture(name).getNow(EMPTY_UUID);
   }

   public static UUID getOrResolveBlocking(String name) {
      return (UUID)getFuture(name).join();
   }

   public static boolean hasOfflineUUID(String player) {
      return class_4844.method_43344(player).equals(((CompletableFuture)UUID_CACHE.get(player)).getNow((Object)null));
   }
}
