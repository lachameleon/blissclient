package com.dwarslooper.cactus.client.compat.server;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.util.DiscordPresence;
import com.dwarslooper.cactus.client.gui.toast.internal.GenericToast;
import com.dwarslooper.cactus.client.systems.discord.component.impl.ImageAsset;
import com.dwarslooper.cactus.client.systems.waypoints.WaypointEntry;
import com.dwarslooper.cactus.client.systems.waypoints.WaypointsWorldInstance;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.class_1792;
import net.minecraft.class_2338;
import net.minecraft.class_2960;
import net.minecraft.class_7923;
import net.minecraft.class_9848;
import org.jetbrains.annotations.Nullable;

public enum ServerCompatRegistry {
   DISCORD_RPC((data, cs) -> {
      Map<String, ServerPresenceOverride> overrides = ((DiscordPresence)ModuleManager.get().get(DiscordPresence.class)).getOverrides();
      if (data.isEmpty()) {
         overrides.remove("server:custom->provided");
      } else {
         Predicate<String> displayValidator = validateString(64);
         overrides.put("server:custom->provided", new ServerPresenceOverride((String)validateOrNull(data, "details", JsonElement::getAsString, displayValidator), (String)validateOrNull(data, "state", JsonElement::getAsString, displayValidator), (ImageAsset)validateOrNull(data, "small_image", (s) -> {
            return new ImageAsset(s.getAsString(), (String)validateOrNull(data, "small_image_text", JsonElement::getAsString, displayValidator));
         }, (i) -> {
            return i.key().length() <= 128 || Utils.isValidURL(i.key());
         })));
      }
   }, () -> {
      ((DiscordPresence)ModuleManager.get().get(DiscordPresence.class)).getOverrides().remove("server:custom->provided");
   }),
   TOAST_MESSAGE((data, cs) -> {
      String title = (String)validateOrNull(data, "title", JsonElement::getAsString, validateString(24));
      String message = (String)validateOrNull(data, "message", JsonElement::getAsString, validateString(256));
      Integer duration = (Integer)validateOrNull(data, "duration", JsonElement::getAsInt, (i) -> {
         return i > 0 && i <= 20000;
      });
      class_2960 icon = (class_2960)validateOrNull(data, "icon", (e) -> {
         return class_2960.method_60654(e.getAsString());
      }, (i) -> {
         return true;
      });
      if (title != null) {
         GenericToast toast = new GenericToast(title, message);
         if (duration != null) {
            toast.setDuration((long)duration);
         }

         if (icon != null) {
            toast.setIcon((class_1792)class_7923.field_41178.method_63535(icon));
         }

         CactusConstants.mc.method_1566().method_1999(toast);
      }

   }),
   WAYPOINT((data, cs) -> {
      String id = (String)validateOrNull(data, "id", JsonElement::getAsString, validateString(64));
      if (id != null) {
         if (data.has("remove")) {
            WaypointsWorldInstance.getWaypointOverrides().remove(id);
            return;
         }

         Predicate<Integer> coordinateValidator = (i) -> {
            return i >= -30000000 && i <= 30000000;
         };
         Integer x = (Integer)validateOrNull(data, "x", JsonElement::getAsInt, coordinateValidator);
         Integer y = (Integer)validateOrNull(data, "y", JsonElement::getAsInt, coordinateValidator);
         Integer z = (Integer)validateOrNull(data, "z", JsonElement::getAsInt, coordinateValidator);
         if (x == null || y == null || z == null) {
            ServerCompat.LOGGER.error("Received payload with invalid waypoint data");
            return;
         }

         Predicate<Integer> colorValidator = (i) -> {
            try {
               new Color(i, true);
               return true;
            } catch (Exception var2) {
               return false;
            }
         };
         int color = class_9848.method_61330(255, (Integer)validateOr(data, "color", JsonElement::getAsInt, colorValidator, -1));
         String name = (String)validateOrNull(data, "name", JsonElement::getAsString, validateString(16));
         class_2338 pos = new class_2338(x, y, z);
         WaypointsWorldInstance.getWaypointOverrides().put(id, new WaypointEntry(name, pos, "", color, true, false));
      }

   }, () -> {
      WaypointsWorldInstance.getWaypointOverrides().clear();
   });

   private final ServerCompatRegistry.PayloadReceiver receiver;
   private final Runnable resetCallback;

   private ServerCompatRegistry() {
      this((packet, payload) -> {
         throw new IllegalArgumentException("This type does not support receiving payloads");
      });
   }

   private ServerCompatRegistry(ServerCompatRegistry.PayloadReceiver receiver) {
      this(receiver, () -> {
      });
   }

   private ServerCompatRegistry(ServerCompatRegistry.PayloadReceiver receiver, Runnable resetCallback) {
      this.receiver = receiver;
      this.resetCallback = resetCallback;
   }

   public void send(@Nullable String callbackId, JsonObject payload) {
      JsonObject object = new JsonObject();
      if (callbackId != null) {
         object.addProperty("callback_id", callbackId);
      }

      object.addProperty("identifier", this.name().toLowerCase());
      object.add("payload", payload);
      ClientPlayNetworking.send(new ServerJsonPayload(object));
   }

   public static void receive(ServerJsonPayload packet) {
      String identifier = packet.json().get("identifier").getAsString();
      ServerCompatRegistry handler = valueOf(identifier.toUpperCase());
      handler.receiver.receive(packet.json().getAsJsonObject("payload"), new ServerCompatRegistry.CallbackSender(handler, (String)validateOrNull(packet.json(), "nonce", JsonElement::getAsString, validateString(64))));
   }

   public void reset() {
      this.resetCallback.run();
   }

   private static <T> T validateOr(JsonObject object, String name, Function<JsonElement, T> mapper, Predicate<T> validator, T other) {
      if (object.has(name)) {
         try {
            T value = mapper.apply(object.get(name));
            if (!validator.test(value)) {
               throw new IllegalStateException("Invalid value for " + name);
            }

            return value;
         } catch (Exception var6) {
         }
      }

      return other;
   }

   private static <T> T validateOrNull(JsonObject object, String name, Function<JsonElement, T> mapper, Predicate<T> validator) {
      return validateOr(object, name, mapper, validator, (Object)null);
   }

   public static Predicate<String> validateString(int maxLength) {
      return (s) -> {
         return s != null && !s.isEmpty() && s.length() <= maxLength;
      };
   }

   // $FF: synthetic method
   private static ServerCompatRegistry[] $values() {
      return new ServerCompatRegistry[]{DISCORD_RPC, TOAST_MESSAGE, WAYPOINT};
   }

   private interface PayloadReceiver {
      void receive(JsonObject var1, ServerCompatRegistry.CallbackSender var2);
   }

   private static record CallbackSender(ServerCompatRegistry source, @Nullable String nonce) {
      private CallbackSender(ServerCompatRegistry source, @Nullable String nonce) {
         this.source = source;
         this.nonce = nonce;
      }

      public void answer(JsonObject object) {
         if (this.nonce == null) {
            ServerCompat.LOGGER.error("Cannot send callback to server compatibility payload, no nonce provided (type={})", this.source.name());
         } else {
            this.source.send(this.nonce, object);
         }
      }

      public ServerCompatRegistry source() {
         return this.source;
      }

      @Nullable
      public String nonce() {
         return this.nonce;
      }
   }
}
