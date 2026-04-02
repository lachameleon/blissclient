package com.dwarslooper.cactus.client.feature.modules.util;

import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;

public class CrashPreventor extends Module {
   public CrashPreventor() {
      super("crashPreventor", ModuleManager.CATEGORY_UTILITY, (new Module.Options()).set(Module.Flag.HUD_LISTED, false).set(Module.Flag.RUN_IN_MENU, true).set(Module.Flag.EXPERIMENTAL, true));
   }
}
