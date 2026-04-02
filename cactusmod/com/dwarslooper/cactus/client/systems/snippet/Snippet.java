package com.dwarslooper.cactus.client.systems.snippet;

import com.dwarslooper.cactus.client.systems.ISerializable;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.google.gson.JsonObject;

public final class Snippet implements ISerializable<Snippet> {
   private final String name;
   private final String content;
   private boolean favorite;

   public Snippet(String name, String content, boolean favorite) {
      this.name = name;
      this.content = content;
      this.favorite = favorite;
   }

   public JsonObject toJson(TreeSerializerFilter filter) {
      JsonObject object = new JsonObject();
      object.addProperty("name", this.getName());
      object.addProperty("content", this.getContent());
      object.addProperty("favorite", this.favorite);
      return object;
   }

   public Snippet fromJson(JsonObject object) {
      throw new UnsupportedOperationException("Object is created by constructor; cannot modify");
   }

   public static Snippet createFromJson(JsonObject object) {
      return new Snippet(object.get("name").getAsString(), object.get("content").getAsString(), object.get("favorite").getAsBoolean());
   }

   public String getName() {
      return this.name;
   }

   public String getContent() {
      return this.content;
   }

   public boolean isFavorite() {
      return this.favorite;
   }

   public void setFavorite(boolean favorite) {
      this.favorite = favorite;
   }

   public String toString() {
      return "Snippet[name=" + this.name + ", content=" + this.content + "]";
   }
}
