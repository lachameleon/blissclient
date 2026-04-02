package com.dwarslooper.cactus.client.compat.server;

import com.google.gson.JsonObject;
import net.minecraft.class_8710;
import net.minecraft.class_9129;
import net.minecraft.class_9139;
import net.minecraft.class_8710.class_9154;

public record ServerJsonPayload(JsonObject json) implements class_8710 {
   public static final class_9154<ServerJsonPayload> ID;
   public static final class_9139<class_9129, ServerJsonPayload> CODEC;

   public ServerJsonPayload(JsonObject json) {
      this.json = json;
   }

   public class_9154<? extends class_8710> method_56479() {
      return ID;
   }

   public JsonObject json() {
      return this.json;
   }

   static {
      ID = new class_9154(ServerCompat.CHANNEL);
      CODEC = class_9139.method_56434(ServerCompat.JSON_CODEC, ServerJsonPayload::json, ServerJsonPayload::new);
   }
}
