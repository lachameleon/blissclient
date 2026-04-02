package com.dwarslooper.cactus.client.feature.modules.redstone;

import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.AttackBlockEvent;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.PositionUtils;
import net.minecraft.class_1263;
import net.minecraft.class_1269;
import net.minecraft.class_156;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2343;

public class AntiContainerDrop extends Module {
   private long time;

   public AntiContainerDrop() {
      super("no_container_drop", ModuleManager.CATEGORY_REDSTONE);
   }

   @EventHandler
   public void blockBreak(AttackBlockEvent event) {
      if (event.getPlayer().method_68878() && this.active()) {
         class_2248 block = event.getWorld().method_8320(event.getPos()).method_26204();
         if (block instanceof class_2343) {
            class_2343 provider = (class_2343)block;
            if (provider.method_10123(class_2338.field_10980, block.method_9564()) instanceof class_1263) {
               if (class_156.method_658() - this.time < 200L) {
                  event.setResult(class_1269.field_5812);
                  return;
               }

               this.time = class_156.method_658();
               CactusConstants.mc.method_1562().method_45730("setblock " + PositionUtils.toString(event.getPos()) + " minecraft:air");
               event.setResult(class_1269.field_5812);
            }
         }
      }

   }
}
