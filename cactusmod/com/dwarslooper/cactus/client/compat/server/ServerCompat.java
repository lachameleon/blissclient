package com.dwarslooper.cactus.client.compat.server;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.compat.server.specific.ServerSpecific;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.function.Function;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.class_2535;
import net.minecraft.class_2960;
import net.minecraft.class_9139;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerCompat {
   public static final Logger LOGGER = LoggerFactory.getLogger("CactusClient / ServerCompat");
   public static final class_2960 CHANNEL = class_2960.method_60655("cactus", "server_compatibility");
   public static final class_9139<ByteBuf, JsonObject> JSON_CODEC = new class_9139<ByteBuf, JsonObject>() {
      public JsonObject decode(ByteBuf byteBuf) {
         byte[] bytes = new byte[byteBuf.readableBytes()];
         byteBuf.readBytes(bytes);
         return (JsonObject)CactusClient.GSON.fromJson(new InputStreamReader(new ByteArrayInputStream(bytes)), JsonObject.class);
      }

      public void encode(ByteBuf byteBuf, JsonObject object) {
         byte[] bytes = CactusClient.GSON.toJson(object).getBytes();
         byteBuf.writeBytes(bytes);
      }
   };

   public static void joined(String address) {
      class_2535 connection = CactusConstants.mc.method_1562().method_48296();
      if (connection != null && connection.method_10758()) {
         ServerSpecific.joinServer(address);
      } else {
         CactusClient.getLogger().error("Unable to create ServerCompat, no open connection");
      }
   }

   public static void close() {
      ServerSpecific.close();
      ServerCompatRegistry[] var0 = ServerCompatRegistry.values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         ServerCompatRegistry value = var0[var2];
         value.reset();
      }

   }

   public static void register() {
      registerReceiver();
   }

   private static void registerReceiver() {
      PayloadTypeRegistry.playS2C().register(ServerJsonPayload.ID, ServerJsonPayload.CODEC);
      PayloadTypeRegistry.playC2S().register(ServerJsonPayload.ID, ServerJsonPayload.CODEC);
      ClientPlayNetworking.registerGlobalReceiver(ServerJsonPayload.ID, (payload, context) -> {
         try {
            ServerCompatRegistry.receive(payload);
         } catch (Exception var3) {
            CactusClient.getLogger().error("Error processing server compatibility payload", var3);
         }

      });
      CactusClient.getLogger().info("Registered ServerCompat receiver");
   }

   private static <T> T validateOrNull(JsonObject object, String name, Function<JsonElement, T> mapper, Predicate<T> validator) {
      if (object.has(name)) {
         try {
            T value = mapper.apply(object.get(name));
            if (!validator.test(value)) {
               throw new IllegalStateException("Invalid value for " + name);
            }

            return value;
         } catch (Exception var5) {
         }
      }

      return null;
   }
}
