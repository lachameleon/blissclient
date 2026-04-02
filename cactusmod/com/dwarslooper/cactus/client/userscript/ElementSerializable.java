package com.dwarslooper.cactus.client.userscript;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public abstract class ElementSerializable<T> {
   private final JsonObject json;

   public ElementSerializable(JsonObject json) {
      this.json = json;
   }

   public JsonObject getJson() {
      return this.json;
   }

   public abstract T build(JsonObject var1) throws Exception;

   @NotNull
   public abstract T fallback();

   public T build() {
      try {
         return this.build(this.json);
      } catch (Exception var2) {
         return this.fallback();
      }
   }
}
