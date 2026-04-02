package com.dwarslooper.cactus.client.feature.modules.util;

import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.InteractBlockEvent;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.PositionUtils;

public class Replace extends Module {
   public Replace() {
      super("rePlace", ModuleManager.CATEGORY_UTILITY);
   }

   @EventHandler
   public void interactBlock(InteractBlockEvent event) {
      event.cancel();
      CactusConstants.mc.method_1562().method_45730("setblock %s %s".formatted(new Object[]{PositionUtils.toString(event.getHitResult().method_17777()), event.getPlayer().method_5998(event.getHand()).method_7909()}));
   }
}
