package com.dwarslooper.cactus.client.feature.modules.redstone;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.InteractBlockEvent;
import com.dwarslooper.cactus.client.event.impl.TestEvent;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.impl.IntegerSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import net.minecraft.class_1268;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2885;
import net.minecraft.class_3965;

public class AutoTop extends Module {
   public Setting<Integer> delayMs;

   public AutoTop() {
      super("auto_top", ModuleManager.CATEGORY_REDSTONE);
      this.delayMs = this.mainGroup.add((new IntegerSetting("msDelay", 20)).min(0).max(2000));
   }

   @EventHandler
   public void onPlace(InteractBlockEvent event) {
      class_3965 hitResult = event.getHitResult();
      if (this.active() && event.getHand() == class_1268.field_5808 && CactusConstants.mc.field_1724.method_6079() != null && !CactusConstants.mc.field_1724.method_6079().method_7960()) {
         Utils.unsafeDelayed(() -> {
            class_243 pos = class_2338.method_49638(hitResult.method_17784().method_43206(hitResult.method_17780(), 0.2D).method_1031(0.0D, 1.0D, 0.0D)).method_46558();
            CactusConstants.mc.method_1562().method_52787(new class_2885(class_1268.field_5810, new class_3965(pos, class_2350.field_11033, class_2338.method_49638(pos), false), 1));
         }, (long)(Integer)this.delayMs.get());
      }

   }

   @EventHandler(
      priority = 2
   )
   public void onTest(TestEvent event) {
      CactusClient.getLogger().info("Test event prio 1 called");
   }

   @EventHandler(
      priority = 1
   )
   public void onAnotherTest(TestEvent event) {
      CactusClient.getLogger().info("Test event prio default called");
      event.cancel();
   }

   @EventHandler
   public void onCancelledTest(TestEvent event) {
      CactusClient.getLogger().info("Bro wtf fucking impossible");
   }
}
