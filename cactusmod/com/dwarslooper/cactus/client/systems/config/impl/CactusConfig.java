package com.dwarslooper.cactus.client.systems.config.impl;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.gui.screen.impl.BackgroundSelectorScreen;
import com.dwarslooper.cactus.client.systems.ISerializable;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.systems.config.CactusSystemConfig;
import com.dwarslooper.cactus.client.systems.config.ConfigHandler;
import com.dwarslooper.cactus.client.systems.config.FileConfiguration;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.google.gson.JsonObject;

public class CactusConfig extends FileConfiguration<CactusConfig> {
   public static CactusConfig getMain() {
      return (CactusConfig)CactusClient.getConfig(CactusConfig.class);
   }

   public CactusConfig(ConfigHandler handler) {
      super("config", handler);
      this.appendConfig("settings", new CactusSettings());
      this.appendConfig("system", new CactusSystemConfig());
      this.appendConfig("panorama", new BackgroundSelectorScreen.Configuration());
   }

   public <E> void appendConfig(String key, ISerializable<E> serializable) {
      if (this.isLoaded()) {
         CactusClient.getLogger().error("Tried to register SubConfig {} after config was already initialized", key);
      } else {
         super.appendConfig(key, serializable);
      }
   }

   public JsonObject toJson(TreeSerializerFilter filter) {
      return null;
   }

   public CactusConfig fromJson(JsonObject object) {
      return this;
   }
}
