package com.dwarslooper.cactus.client.userscript;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Userscript {
   private final JsonObject json;
   private String name;
   private String description;
   private List<String> authors;

   public Userscript(File file) throws FileNotFoundException {
      FileReader reader = new FileReader(file);
      this.json = (JsonObject)CactusClient.GSON.fromJson(reader, JsonObject.class);
   }

   public Userscript(JsonObject json) {
      this.json = json;
   }

   public abstract void load();

   public abstract void unload();

   public void run(JsonObject data) throws Exception {
   }

   public void runCovered() {
      try {
         this.run(this.data());
      } catch (Exception var2) {
         ChatUtils.error("Userscript '%s' failed to run.".formatted(new Object[]{this.name}));
         ChatUtils.error(var2.getMessage());
      }

   }

   public void init() {
      try {
         JsonObject info = this.json.getAsJsonObject("info");
         this.name = info.get("name").getAsString();
         this.name = info.get("description").getAsString();
         this.authors = (List)info.get("authors").getAsJsonArray().asList().stream().map(JsonElement::getAsString).collect(Collectors.toList());
      } catch (Exception var2) {
         throw new IllegalArgumentException("A userscript is required to have a name, description and its authors specified as well as a type and a version.");
      }
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }

   public List<String> getAuthors() {
      return this.authors;
   }

   public JsonObject getJson() {
      return this.json;
   }

   public JsonObject data() {
      return this.getJson().getAsJsonObject("data");
   }
}
