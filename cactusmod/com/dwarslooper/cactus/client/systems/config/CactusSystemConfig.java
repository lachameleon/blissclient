package com.dwarslooper.cactus.client.systems.config;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.systems.ISerializable;
import com.dwarslooper.cactus.client.systems.config.impl.CactusConfig;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.function.Function;

public class CactusSystemConfig implements ISerializable<CactusSystemConfig> {
   public static boolean skipModuleCheatWarning;
   public static boolean skipHDBWarning;
   public static long lastKnownCrash;

   public static CactusSystemConfig get() {
      return (CactusSystemConfig)((CactusConfig)CactusClient.getConfig(CactusConfig.class)).getSubConfig(CactusSystemConfig.class);
   }

   public JsonObject toJson(TreeSerializerFilter filter) {
      JsonObject object = new JsonObject();
      object.addProperty("skipCheatWarning", skipModuleCheatWarning);
      object.addProperty("skipHDBWarning", skipHDBWarning);
      object.addProperty("lastKnownCrash", lastKnownCrash);
      return object;
   }

   public CactusSystemConfig fromJson(JsonObject object) {
      skipModuleCheatWarning = (Boolean)this.orElse(object.get("skipCheatWarning"), JsonElement::getAsBoolean, false);
      skipHDBWarning = (Boolean)this.orElse(object.get("skipHDBWarning"), JsonElement::getAsBoolean, false);
      lastKnownCrash = (Long)this.orElse(object.get("lastKnownCrash"), JsonElement::getAsLong, -1L);
      return this;
   }

   private <T> T orElse(JsonElement value, Function<JsonElement, T> function, T other) {
      return value == null ? other : function.apply(value);
   }
}
