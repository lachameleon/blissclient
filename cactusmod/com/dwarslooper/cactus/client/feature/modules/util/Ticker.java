package com.dwarslooper.cactus.client.feature.modules.util;

import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.ClientTickEvent;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.util.game.ChatUtils;

public class Ticker extends Module {
   public Ticker() {
      super("tick_tool", ModuleManager.CATEGORY_UTILITY, (new Module.Options()).set(Module.Flag.HUD_LISTED, false).set(Module.Flag.EXPERIMENTAL, true));
   }

   @EventHandler
   public void onTick(ClientTickEvent event) {
      ChatUtils.warning("my chat is being flooded. please help.");
   }
}
