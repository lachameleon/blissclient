package com.dwarslooper.cactus.client.feature.modules.util;

import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.WorldJoinedEvent;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.IntegerSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.CactusConstants;
import net.minecraft.class_639;
import net.minecraft.class_642;

public class AutoReconnect extends Module {
   public final Setting<Integer> delaySeconds;
   public final Setting<Boolean> hideButtons;
   private class_639 lastAddress;
   private class_642 lastData;
   private long nextAttemptAt;

   public AutoReconnect() {
      super("auto_reconnect", ModuleManager.CATEGORY_UTILITY, (new Module.Options()).set(Module.Flag.RUN_IN_MENU, true));
      this.delaySeconds = this.mainGroup.add((new IntegerSetting("delay", 2)).min(0).max(60));
      this.hideButtons = this.mainGroup.add(new BooleanSetting("hideButtons", false));
   }

   public void onEnable() {
      this.captureCurrentServer();
      this.resetTimer();
   }

   public void setLastServer(class_639 address, class_642 data) {
      this.lastAddress = address;
      this.lastData = data;
      this.resetTimer();
   }

   @EventHandler
   public void onWorldJoined(WorldJoinedEvent event) {
      this.captureCurrentServer();
      this.resetTimer();
   }

   public class_639 getLastAddress() {
      return this.lastAddress;
   }

   public class_642 getLastData() {
      return this.lastData;
   }

   public void resetTimer() {
      this.nextAttemptAt = System.currentTimeMillis() + (long)(Integer)this.delaySeconds.get() * 1000L;
   }

   private void captureCurrentServer() {
      class_642 data = CactusConstants.mc.method_1558();
      if (data != null) {
         this.setLastServer(class_639.method_2950(data.field_3761), new class_642(data.field_3752, data.field_3761, data.method_55616()));
      }

   }

   public void restartCountdownIfPossible() {
      if (this.canReconnect()) {
         this.resetTimer();
      }

   }

   public boolean canReconnect() {
      return this.lastAddress != null && this.lastData != null;
   }

   public boolean shouldReconnectNow(long nowMillis) {
      return this.canReconnect() && nowMillis >= this.nextAttemptAt;
   }

   public int getSecondsLeft(long nowMillis) {
      if (this.nextAttemptAt == 0L) {
         return 0;
      } else {
         long diff = this.nextAttemptAt - nowMillis;
         return (int)Math.max(0.0D, Math.ceil((double)diff / 1000.0D));
      }
   }
}
