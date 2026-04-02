package com.dwarslooper.cactus.client.feature.modules.util;

import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.AttackBlockEvent;
import com.dwarslooper.cactus.client.event.impl.InteractBlockEvent;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.CactusConstants;
import net.minecraft.class_2248;
import net.minecraft.class_2302;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;

public class FarmUtils extends Module {
   public Setting<Boolean> preventBreakingNotGrown;
   public Setting<Boolean> rightClickToReplant;
   public Setting<Boolean> onlyReplantSameType;

   public FarmUtils() {
      super("farmUtils", ModuleManager.CATEGORY_UTILITY, (new Module.Options()).set(Module.Flag.SERVER_UNSAFE, true));
      this.preventBreakingNotGrown = this.mainGroup.add(new BooleanSetting("onlyBreakGrown", true));
      this.rightClickToReplant = this.mainGroup.add(new BooleanSetting("instantReplant", false));
      this.onlyReplantSameType = this.mainGroup.add(new BooleanSetting("replantSame", false));
   }

   @EventHandler
   public void attackBlock(AttackBlockEvent event) {
      class_2680 blockState = event.getWorld().method_8320(event.getPos());
      if ((Boolean)this.preventBreakingNotGrown.get()) {
         class_2248 var4 = blockState.method_26204();
         if (var4 instanceof class_2302) {
            class_2302 cropBlock = (class_2302)var4;
            if (!cropBlock.method_9825(blockState)) {
               event.cancel();
            }
         }
      }

   }

   @EventHandler
   public void interactBlock(InteractBlockEvent event) {
      if ((Boolean)this.rightClickToReplant.get()) {
         class_2338 blockPos = event.getHitResult().method_17777();
         class_2680 blockState = event.getPlayer().method_73183().method_8320(blockPos);
         if (CactusConstants.mc.field_1761 != null) {
            class_2248 var5 = blockState.method_26204();
            if (var5 instanceof class_2302) {
               class_2302 cropBlock = (class_2302)var5;
               if (blockState.method_26204().method_36555() == 0.0F && (!(Boolean)this.onlyReplantSameType.get() || cropBlock.method_9574(event.getPlayer().method_73183(), blockPos, blockState, false).method_7909() == event.getPlayer().method_6047().method_7909())) {
                  CactusConstants.mc.field_1761.method_2910(blockPos, class_2350.field_11033);
               }
            }
         }
      }

   }
}
