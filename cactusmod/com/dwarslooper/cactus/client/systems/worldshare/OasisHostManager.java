package com.dwarslooper.cactus.client.systems.worldshare;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.irc.protocol.packets.oasis.RequestSessionStartC2SPacket;
import com.dwarslooper.cactus.client.systems.config.ConfigHandler;
import com.dwarslooper.cactus.client.systems.config.FileConfiguration;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class OasisHostManager extends FileConfiguration<OasisHostManager> {
   public static boolean isInitializing;
   public static boolean forThisSession = false;
   private final List<String> whitelist = new ArrayList();
   private boolean whitelistEnabled = false;
   private OasisClient CLIENT;

   public OasisHostManager(ConfigHandler handler) {
      super("world_hosting", handler);
   }

   public static void createSession() {
      isInitializing = true;
      CactusClient.getInstance().getIrcClient().sendPacket(new RequestSessionStartC2SPacket("foo", (String)null));
   }

   public void handleToken(String token) {
      if (isInitializing) {
         this.CLIENT = new OasisClient(token);
         this.connect();
      }
   }

   public void connect() {
      this.CLIENT.connect();
   }

   public JsonObject toJson(TreeSerializerFilter filter) {
      JsonObject object = new JsonObject();
      object.addProperty("whitelisted", this.whitelistEnabled);
      JsonArray array = new JsonArray();
      List var10000 = this.whitelist;
      Objects.requireNonNull(array);
      var10000.forEach(array::add);
      object.add("whitelist", array);
      return object;
   }

   public OasisHostManager fromJson(JsonObject object) {
      this.whitelist.clear();
      Stream var10000 = object.getAsJsonArray("whitelist").asList().stream().map(JsonElement::getAsString);
      List var10001 = this.whitelist;
      Objects.requireNonNull(var10001);
      var10000.forEach(var10001::add);
      this.whitelistEnabled = object.get("whitelisted").getAsBoolean();
      return this;
   }

   public List<String> getWhitelist() {
      return this.whitelist;
   }

   public OasisClient getClientInstance() {
      return this.CLIENT;
   }

   public boolean hasClientInstance() {
      return this.CLIENT != null;
   }

   public void setWhitelistEnabled(boolean enableWhitelist) {
      this.whitelistEnabled = enableWhitelist;
   }

   public boolean isWhitelistEnabled() {
      return this.whitelistEnabled;
   }

   public void close() {
      if (this.CLIENT != null) {
         this.CLIENT.close();
      }

   }
}
