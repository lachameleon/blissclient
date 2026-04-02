package com.dwarslooper.cactus.client.systems.waypoints;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.Waypoints;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import net.minecraft.class_243;

public class WaypointsWorldInstance {
   private static final Map<String, WaypointEntry> waypointOverrides = new ConcurrentHashMap();
   private final String name;
   private List<WaypointEntry> waypointEntryList = new ArrayList();

   public static Map<String, WaypointEntry> getWaypointOverrides() {
      return waypointOverrides;
   }

   public WaypointsWorldInstance(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public List<WaypointEntry> getWaypointEntryList() {
      return this.waypointEntryList;
   }

   public void setWaypointEntryList(List<WaypointEntry> waypointEntryList) {
      this.waypointEntryList = waypointEntryList;
   }

   public boolean containsWaypointWithName(String name) {
      return this.waypointEntryList.stream().anyMatch((waypointEntry) -> {
         return waypointEntry.getName().equalsIgnoreCase(name);
      });
   }

   public boolean add(WaypointEntry waypointEntry) {
      return this.getWaypointEntryList().add(waypointEntry);
   }

   public boolean delete(WaypointEntry waypointEntry) {
      return this.getWaypointEntryList().remove(waypointEntry);
   }

   public List<WaypointEntry> getAllAvailable() {
      if (Utils.isInWorld()) {
         Waypoints module = (Waypoints)ModuleManager.get().get(Waypoints.class);
         return Stream.concat(this.getWaypointEntryList().stream(), getWaypointOverrides().values().stream()).filter((waypoint) -> {
            class_243 camPos = CactusConstants.mc.field_1773.method_19418().method_71156();
            double distance = Math.sqrt(waypoint.getPosition().method_10268(camPos.field_1352, camPos.field_1351, camPos.field_1350));
            return distance <= (double)(Integer)module.maxDistance.get() && waypoint.isVisible();
         }).toList();
      } else {
         return Collections.emptyList();
      }
   }
}
