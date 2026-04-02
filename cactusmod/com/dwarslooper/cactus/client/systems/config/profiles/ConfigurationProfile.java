package com.dwarslooper.cactus.client.systems.config.profiles;

import com.dwarslooper.cactus.client.systems.ISerializable;
import com.dwarslooper.cactus.client.systems.config.ConfigHandler;
import com.dwarslooper.cactus.client.systems.config.FileConfiguration;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigurationProfile implements ISerializable<ConfigurationProfile> {
   private String name;
   private final Set<String> configurations = new HashSet();
   private final List<String> loadOnJoin = new ArrayList();

   public ConfigurationProfile(String name) {
      this.name = name;
   }

   public JsonObject toJson(TreeSerializerFilter filter) {
      JsonObject object = new JsonObject();
      JsonArray savedConfigs = new JsonArray();
      JsonArray loadOnJoin = new JsonArray();
      object.addProperty("name", this.name);
      Set var10000 = this.configurations;
      Objects.requireNonNull(savedConfigs);
      var10000.forEach(savedConfigs::add);
      object.add("configurations", savedConfigs);
      List var5 = this.loadOnJoin;
      Objects.requireNonNull(loadOnJoin);
      var5.forEach(loadOnJoin::add);
      object.add("loadOnJoin", loadOnJoin);
      return object;
   }

   public ConfigurationProfile fromJson(JsonObject object) {
      this.configurations.clear();
      this.loadOnJoin.clear();
      Stream var10000 = object.getAsJsonArray("configurations").asList().stream().map(JsonElement::getAsString);
      Set var10001 = this.configurations;
      Objects.requireNonNull(var10001);
      var10000.forEach(var10001::add);
      var10000 = object.getAsJsonArray("loadOnJoin").asList().stream().map(JsonElement::getAsString);
      List var2 = this.loadOnJoin;
      Objects.requireNonNull(var2);
      var10000.forEach(var2::add);
      return this;
   }

   public static ConfigurationProfile createFromJson(JsonObject object) {
      return (new ConfigurationProfile(object.get("name").getAsString())).fromJson(object);
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Set<String> getConfigurations() {
      return this.configurations;
   }

   public Set<FileConfiguration<?>> getConfigurations(ConfigHandler handler) {
      return (Set)handler.getConfigurations().values().stream().filter(this::containsConfiguration).collect(Collectors.toSet());
   }

   public List<File> getFiles(File directory, ConfigHandler handler) {
      return this.getConfigurations(handler).stream().map((config) -> {
         return config.getFile(directory);
      }).toList();
   }

   public boolean containsConfiguration(FileConfiguration<?> config) {
      return this.configurations.contains(config.getName());
   }

   public boolean loadsForServer(String address) {
      String WILDCARD = "*";
      return this.loadOnJoin.stream().anyMatch((s) -> {
         return s.equals(address) || s.startsWith(WILDCARD) && address.endsWith(s.substring(1)) || s.endsWith(WILDCARD) && address.startsWith(s.substring(0, s.length() - 1)) || s.startsWith(WILDCARD) && s.endsWith(WILDCARD) && s.contains(address);
      });
   }

   public List<String> getLoadOnJoin() {
      return this.loadOnJoin;
   }

   public File getSaveDirectory(File directory) {
      File dir = new File(directory, this.name);
      dir.mkdirs();
      return dir;
   }
}
