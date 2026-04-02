package com.dwarslooper.cactus.client.feature.modules.render;

import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.impl.IntegerSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;

public class ViewTweaks extends Module {
   public Setting<Integer> fireOffset;
   public Setting<Integer> shieldOffset;

   public ViewTweaks() {
      super("viewTweaks", ModuleManager.CATEGORY_RENDERING, (new Module.Options()).set(Module.Flag.HUD_LISTED, false));
      this.fireOffset = this.mainGroup.add((new IntegerSetting("fireOffset", -2)).min(-4).max(4));
      this.shieldOffset = this.mainGroup.add((new IntegerSetting("shieldOffset", -2)).min(-4).max(4));
   }
}
