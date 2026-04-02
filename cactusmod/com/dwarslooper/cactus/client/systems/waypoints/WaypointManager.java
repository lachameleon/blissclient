package com.dwarslooper.cactus.client.systems.waypoints;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.systems.config.ConfigHandler;
import com.dwarslooper.cactus.client.systems.config.FileConfiguration;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.dwarslooper.cactus.client.util.Utils;
import com.dwarslooper.cactus.client.util.game.PositionUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Iterator;

public class WaypointManager extends FileConfiguration<WaypointManager> {
   private final HashMap<String, WaypointsWorldInstance> waypointInstancesList = new HashMap();

   public static WaypointManager get() {
      return (WaypointManager)CactusClient.getConfig(WaypointManager.class);
   }

   public WaypointManager(ConfigHandler handler) {
      super("waypoints", handler);
   }

   public HashMap<String, WaypointsWorldInstance> getWaypointInstancesList() {
      return this.waypointInstancesList;
   }

   public WaypointsWorldInstance getInstance(String name) {
      if (this.waypointInstancesList.containsKey(name)) {
         return (WaypointsWorldInstance)this.waypointInstancesList.get(name);
      } else {
         this.waypointInstancesList.put(name, new WaypointsWorldInstance(name));
         return this.getInstance(name);
      }
   }

   public WaypointsWorldInstance getInstance() {
      return this.getInstance(Utils.serializeWorldName(Utils.getWorldIdentification()));
   }

   public boolean addIfAbsent(WaypointEntry waypointEntry) {
      WaypointsWorldInstance instance = this.getInstance();
      if (instance.containsWaypointWithName(waypointEntry.getName())) {
         return false;
      } else {
         instance.add(waypointEntry);
         return true;
      }
   }

   public JsonObject toJson(TreeSerializerFilter filter) {
      JsonObject object = new JsonObject();
      this.getWaypointInstancesList().forEach((worldName, waypointsWorldInstance) -> {
         JsonArray array = new JsonArray();
         Iterator var4 = waypointsWorldInstance.getWaypointEntryList().iterator();

         while(var4.hasNext()) {
            WaypointEntry waypointEntry = (WaypointEntry)var4.next();
            JsonObject waypointData = new JsonObject();
            waypointData.addProperty("name", waypointEntry.getName());
            waypointData.addProperty("position", PositionUtils.toString(waypointEntry.getPosition()));
            waypointData.addProperty("dimension", waypointEntry.getDimension());
            waypointData.addProperty("color", waypointEntry.getColor());
            waypointData.addProperty("visible", waypointEntry.isVisible());
            waypointData.addProperty("isDeath", waypointEntry.isDeath());
            array.add(waypointData);
         }

         if (!array.isEmpty()) {
            object.add(Utils.serializeWorldName(worldName), array);
         }

      });
      return object;
   }

   public WaypointManager fromJson(JsonObject object) {
      Iterator var2 = object.keySet().iterator();

      while(var2.hasNext()) {
         String key = (String)var2.next();
         WaypointsWorldInstance instance = new WaypointsWorldInstance(key);
         JsonArray waypoints = object.getAsJsonArray(key);
         Iterator var6 = waypoints.iterator();

         while(var6.hasNext()) {
            JsonElement waypoint = (JsonElement)var6.next();
            if (waypoint instanceof JsonObject) {
               JsonObject jsonObject = (JsonObject)waypoint;
               instance.add(new WaypointEntry(jsonObject.get("name").getAsString(), PositionUtils.fromString(jsonObject.get("position").getAsString()), jsonObject.get("dimension").getAsString(), jsonObject.get("color").getAsInt(), jsonObject.get("visible").getAsBoolean(), jsonObject.get("isDeath").getAsBoolean()));
            }
         }

         this.getWaypointInstancesList().put(key, instance);
      }

      return this;
   }
}
