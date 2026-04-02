package com.dwarslooper.cactus.client.feature.modules.render;

import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.impl.ColorSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;

public class Fullbright extends Module {
   public Setting<ColorSetting.ColorValue> color;

   public Fullbright() {
      super("fullbright", ModuleManager.CATEGORY_RENDERING);
      this.color = this.mainGroup.add(new ColorSetting("color", ColorSetting.ColorValue.white()));
   }
}
