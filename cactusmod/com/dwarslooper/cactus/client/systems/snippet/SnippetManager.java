package com.dwarslooper.cactus.client.systems.snippet;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.systems.config.ConfigHandler;
import com.dwarslooper.cactus.client.systems.config.FileConfiguration;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

public class SnippetManager extends FileConfiguration<SnippetManager> {
   private final List<Snippet> snippets = new ArrayList();

   public static SnippetManager get() {
      return (SnippetManager)CactusClient.getConfig(SnippetManager.class);
   }

   public SnippetManager(ConfigHandler handler) {
      super("snippets", handler);
   }

   public void add(String name, String snippet) {
      this.snippets.add(new Snippet(name, snippet, false));
   }

   public List<Snippet> getSnippets() {
      return this.snippets;
   }

   public JsonObject toJson(TreeSerializerFilter filter) {
      JsonObject root = new JsonObject();
      JsonArray array = new JsonArray();
      this.snippets.forEach((snippet) -> {
         array.add(snippet.toJson(TreeSerializerFilter.ALL));
      });
      root.add("snippets", array);
      return root;
   }

   public SnippetManager fromJson(JsonObject object) {
      this.snippets.clear();
      JsonArray array = object.getAsJsonArray("snippets");
      array.asList().stream().map(JsonElement::getAsJsonObject).forEachOrdered((jsonObject) -> {
         this.snippets.add(Snippet.createFromJson(jsonObject));
      });
      return this;
   }
}
