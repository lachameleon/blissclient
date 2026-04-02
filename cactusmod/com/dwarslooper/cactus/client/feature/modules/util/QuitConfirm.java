package com.dwarslooper.cactus.client.feature.modules.util;

import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.impl.EnumSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;

public class QuitConfirm extends Module {
   public Setting<QuitConfirm.Mode> mode;

   public QuitConfirm() {
      super("quitConfirm", ModuleManager.CATEGORY_UTILITY);
      this.mode = this.mainGroup.add(new EnumSetting("mode", QuitConfirm.Mode.ClickAgain));
   }

   public static enum Mode {
      Confirm,
      ClickAgain,
      Cooldown;

      // $FF: synthetic method
      private static QuitConfirm.Mode[] $values() {
         return new QuitConfirm.Mode[]{Confirm, ClickAgain, Cooldown};
      }
   }
}
