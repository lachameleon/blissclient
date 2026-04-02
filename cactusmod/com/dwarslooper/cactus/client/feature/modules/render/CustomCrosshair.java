package com.dwarslooper.cactus.client.feature.modules.render;

import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Graphic2DSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;

public class CustomCrosshair extends Module {
   public Setting<Boolean> useLiteralColors;
   public Setting<Boolean> rgb;
   public Setting<Graphic2DSetting.Graphic2D> crosshair;

   public CustomCrosshair() {
      super("crosshair", ModuleManager.CATEGORY_RENDERING, (new Module.Options()).set(Module.Flag.HUD_LISTED, false));
      this.useLiteralColors = this.mainGroup.add(new BooleanSetting("useLiteralColors", false));
      this.rgb = this.mainGroup.add(new BooleanSetting("rgb", false));
      this.crosshair = this.mainGroup.add(new Graphic2DSetting("graphic", Graphic2DSetting.Graphic2D.createEmpty(17, 17)));
   }
}
