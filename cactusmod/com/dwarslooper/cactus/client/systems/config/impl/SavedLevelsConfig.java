package com.dwarslooper.cactus.client.systems.config.impl;

import com.dwarslooper.cactus.client.systems.ISerializable;
import com.dwarslooper.cactus.client.systems.config.ConfigHandler;
import com.dwarslooper.cactus.client.systems.config.FileConfiguration;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.class_34;
import net.minecraft.class_32.class_7411;

public class SavedLevelsConfig extends FileConfiguration<SavedLevelsConfig> {
   public Map<Path, SavedLevelsConfig.LevelSettings> savedLevels = new HashMap();

   public SavedLevelsConfig(ConfigHandler handler) {
      super("level_settings", handler);
   }

   public void setPinned(class_34 level, boolean pinned) {
      ((SavedLevelsConfig.LevelSettings)this.savedLevels.computeIfAbsent(this.path(level), (path) -> {
         return new SavedLevelsConfig.LevelSettings();
      })).pinned = pinned;
      this.clean();
   }

   public boolean isLevelPinned(class_34 level) {
      SavedLevelsConfig.LevelSettings settings = (SavedLevelsConfig.LevelSettings)this.savedLevels.get(this.path(level));
      return settings != null && settings.pinned;
   }

   public void clean() {
      List<Path> list = CactusConstants.mc.method_1586().method_235().comp_731().stream().map(class_7411::comp_732).toList();
      this.savedLevels.entrySet().removeIf((entry) -> {
         return SavedLevelsConfig.LevelSettings.isDefault((SavedLevelsConfig.LevelSettings)entry.getValue()) || ((Path)entry.getKey()).startsWith(CactusConstants.mc.method_1586().method_19636()) && !list.contains(entry.getKey());
      });
   }

   public Path path(class_34 level) {
      return CactusConstants.mc.method_1586().method_52238(level.method_248());
   }

   public String encode(Path path) {
      return Base64.getEncoder().encodeToString(path.toString().getBytes());
   }

   public Path decode(String path) {
      return Path.of(new String(Base64.getDecoder().decode(path.getBytes())), new String[0]);
   }

   public JsonObject toJson(TreeSerializerFilter filter) {
      JsonObject root = new JsonObject();
      this.savedLevels.forEach((path, levelSettings) -> {
         root.add(this.encode(path), levelSettings.toJson(TreeSerializerFilter.ALL));
      });
      return root;
   }

   public SavedLevelsConfig fromJson(JsonObject object) {
      this.savedLevels.clear();
      Iterator var2 = object.keySet().iterator();

      while(var2.hasNext()) {
         String key = (String)var2.next();
         this.savedLevels.put(this.decode(key), (new SavedLevelsConfig.LevelSettings()).fromJson(object.getAsJsonObject(key)));
      }

      return this;
   }

   public static class LevelSettings implements ISerializable<SavedLevelsConfig.LevelSettings> {
      private boolean pinned;

      public JsonObject toJson(TreeSerializerFilter filter) {
         JsonObject object = new JsonObject();
         object.addProperty("pinned", this.pinned);
         return object;
      }

      public SavedLevelsConfig.LevelSettings fromJson(JsonObject object) {
         this.pinned = object.get("pinned").getAsBoolean();
         return this;
      }

      public static boolean isDefault(SavedLevelsConfig.LevelSettings ls) {
         return !ls.pinned;
      }
   }
}
