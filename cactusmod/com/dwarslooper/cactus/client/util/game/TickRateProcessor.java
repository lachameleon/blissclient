package com.dwarslooper.cactus.client.util.game;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.ClientTickEvent;
import com.dwarslooper.cactus.client.event.impl.WorldJoinedEvent;
import com.dwarslooper.cactus.client.gui.hud.SimpleHudManager;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import com.dwarslooper.cactus.client.util.generic.MathUtils;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ForwardingCollection;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class TickRateProcessor {
   public static TickRateProcessor INSTANCE = new TickRateProcessor(4);
   private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
   private final EvictingQueue<Float> times;
   private long lastServerUpdate = -1L;

   public TickRateProcessor(int maxSamples) {
      this.times = EvictingQueue.create(maxSamples);
      CactusClient.EVENT_BUS.subscribe(this);
   }

   public void processServerTime() {
      if (Utils.isInWorld() && this.lastServerUpdate > 0L) {
         float tps = this.getTpsNow();
         this.wrapWrite((queue) -> {
            queue.offer(tps);
         });
      }

      this.lastServerUpdate = System.currentTimeMillis();
   }

   @EventHandler
   public void processClientTick(ClientTickEvent event) {
      if (!CactusConstants.mc.method_1493() && Utils.isInWorld() && this.getDiffNow() > 5000L) {
         this.wrapWrite((queue) -> {
            queue.offer(this.getTpsNow());
         });
      }

   }

   @EventHandler
   public void gameJoin(WorldJoinedEvent event) {
      this.reset();
   }

   public void reset() {
      this.lastServerUpdate = System.currentTimeMillis();
      this.wrapWrite(ForwardingCollection::clear);
   }

   public float getTickRate() {
      this.lock.readLock().lock();

      try {
         if (!this.times.isEmpty()) {
            float sum = (Float)this.times.stream().reduce(0.0F, Float::sum);
            float var2 = sum / (float)this.times.size();
            return var2;
         }
      } finally {
         this.lock.readLock().unlock();
      }

      return 0.0F;
   }

   public float getLastTickTime() {
      return (float)this.getDiffNow() / 1000.0F;
   }

   public long getDiffNow() {
      return this.lastServerUpdate == 0L ? 0L : System.currentTimeMillis() - this.lastServerUpdate;
   }

   public float getTpsNow() {
      return 1000.0F / (float)this.getDiffNow() * 20.0F;
   }

   public String getDisplay() {
      float tickRate = this.getTickRate();
      return (tickRate >= 20.001F ? "§a*" : "") + MathUtils.quality(Math.min(tickRate, 20.0F), 17.0F, 15.0F, 8.0F, MathUtils.QualityMode.DECREMENT, (f) -> {
         return String.format("%.2f", f);
      });
   }

   private void wrapWrite(Consumer<EvictingQueue<Float>> action) {
      this.lock.writeLock().lock();

      try {
         action.accept(this.times);
      } finally {
         this.lock.writeLock().unlock();
      }

   }

   public static void registerHUD(SimpleHudManager system) {
      system.registerElement(() -> {
         if ((Boolean)SimpleHudManager.get().getHud().showTPS.get()) {
            String var10000 = INSTANCE.getDisplay();
            return "TPS §8x " + var10000 + (INSTANCE.getDiffNow() > 4000L ? "\n\\m Last Tick: " + String.format("%.1f", INSTANCE.getLastTickTime()) : "");
         } else {
            return null;
         }
      });
   }
}
