package com.dwarslooper.cactus.client.feature.modules.render;

import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.impl.IntegerSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;

public class PlayerHider extends Module {
   public Setting<Integer> hideDistance;

   public PlayerHider() {
      super("playerHider", ModuleManager.CATEGORY_RENDERING);
      this.hideDistance = this.mainGroup.add((new IntegerSetting("distance", 1)).min(0).max(10));
   }
}
