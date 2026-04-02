package com.dwarslooper.cactus.client.feature.modules.render;

import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.EnumSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.IntegerSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;

public class AmbientTweaks extends Module {
   public Setting<Boolean> changeTime;
   public Setting<Integer> time;
   public Setting<AmbientTweaks.Weather> weather;

   public AmbientTweaks() {
      super("ambientTweaks", ModuleManager.CATEGORY_RENDERING, (new Module.Options()).set(Module.Flag.HUD_LISTED, false));
      this.changeTime = this.mainGroup.add(new BooleanSetting("changeTime", true));
      this.time = this.mainGroup.add((new IntegerSetting("time", 6000)).min(0).max(24000));
      this.weather = this.mainGroup.add(new EnumSetting("weather", AmbientTweaks.Weather.Default));
   }

   public static enum Weather {
      Default,
      Clear,
      Rain,
      Thunder;

      // $FF: synthetic method
      private static AmbientTweaks.Weather[] $values() {
         return new AmbientTweaks.Weather[]{Default, Clear, Rain, Thunder};
      }
   }
}
