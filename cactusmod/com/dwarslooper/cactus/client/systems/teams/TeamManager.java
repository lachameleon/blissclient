package com.dwarslooper.cactus.client.systems.teams;

import com.dwarslooper.cactus.client.systems.config.ConfigHandler;
import com.dwarslooper.cactus.client.systems.config.FileConfiguration;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonObject;
import java.util.function.Supplier;

public class TeamManager extends FileConfiguration<TeamManager> {
   public static Cache<Integer, ClientTeamData> CACHE = CacheBuilder.newBuilder().removalListener((n) -> {
      if (n.getValue() != null) {
      }

   }).build();

   public TeamManager(ConfigHandler handler) {
      super("teams", handler);
   }

   public ClientTeamData getOrCreate(int id, Supplier<ClientTeamData> supplier) {
      if (CACHE.asMap().containsKey(id)) {
         return (ClientTeamData)CACHE.getIfPresent(id);
      } else {
         ClientTeamData data = (ClientTeamData)supplier.get();
         CACHE.put(id, data);
         return data;
      }
   }

   public JsonObject toJson(TreeSerializerFilter filter) {
      JsonObject object = new JsonObject();
      return object;
   }

   public TeamManager fromJson(JsonObject object) {
      return this;
   }
}
