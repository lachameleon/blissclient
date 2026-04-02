package com.dwarslooper.cactus.client.feature.modules.render;

import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.EnumSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.IntegerSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import net.minecraft.class_3532;

public class Waypoints extends Module {
   public Setting<Boolean> hideClose;
   public Setting<Integer> maxDistance;
   public Setting<Waypoints.DistanceVisibility> distanceVisibility;

   public Waypoints() {
      super("waypoints", ModuleManager.CATEGORY_RENDERING);
      this.hideClose = this.mainGroup.add(new BooleanSetting("hideClose", false));
      this.maxDistance = this.mainGroup.add((new IntegerSetting("maxDistance", 4000)).min(20).max(100000));
      this.distanceVisibility = this.mainGroup.add(new EnumSetting("distanceVisibility", Waypoints.DistanceVisibility.Look));
   }

   public float getAlphaWithDistance(double distance) {
      return (Boolean)this.hideClose.get() ? class_3532.method_15363(((float)distance - 0.5F) / 4.0F, 0.0F, 1.0F) : 0.0F;
   }

   public static enum DistanceVisibility {
      Always,
      Look,
      Hidden;

      public boolean shouldRender(int centerOffset) {
         return this == Always || this == Look && Math.abs(centerOffset) < 40;
      }

      // $FF: synthetic method
      private static Waypoints.DistanceVisibility[] $values() {
         return new Waypoints.DistanceVisibility[]{Always, Look, Hidden};
      }
   }
}
