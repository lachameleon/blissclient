package com.dwarslooper.cactus.client.systems.config;

import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.generic.ScreenUtils;
import net.minecraft.class_339;

public class ConfigHelper {
   public static void addDescriptionIfPresent(Setting<?> setting, class_339 widget) {
      if (!setting.description().isEmpty()) {
         widget.method_47400(ScreenUtils.tooltipLiteral(setting.description()));
      }

   }
}
