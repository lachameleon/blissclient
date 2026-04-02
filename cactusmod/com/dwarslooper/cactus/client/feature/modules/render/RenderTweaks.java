package com.dwarslooper.cactus.client.feature.modules.render;

import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;

public class RenderTweaks extends Module {
   public Setting<Boolean> noFog;

   public RenderTweaks() {
      super("renderTweaks", ModuleManager.CATEGORY_RENDERING, (new Module.Options()).set(Module.Flag.HUD_LISTED, false).set(Module.Flag.EXPERIMENTAL, true));
      this.noFog = this.mainGroup.add(new BooleanSetting("noFog", true));
   }
}
