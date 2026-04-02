package com.dwarslooper.cactus.client.util.game;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.ClientTickEvent;
import com.dwarslooper.cactus.client.event.impl.MouseClickEvent;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.EvictingQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import net.minecraft.class_156;

public class InputTimeTracker {
   public static InputTimeTracker INSTANCE = new InputTimeTracker();
   private final Cache<Integer, Queue<Long>> handlerList;

   public InputTimeTracker() {
      this.handlerList = CacheBuilder.newBuilder().expireAfterAccess(10L, TimeUnit.SECONDS).build();
      CactusClient.EVENT_BUS.subscribe(this);
   }

   public int getInputsThisSecond(int button) {
      ConcurrentMap<Integer, Queue<Long>> map = this.handlerList.asMap();
      return !map.containsKey(button) ? 0 : ((Queue)map.get(button)).size();
   }

   @EventHandler
   public void onMouse(MouseClickEvent event) {
      if (event.getAction() == 1) {
         ((Queue)this.handlerList.asMap().computeIfAbsent(event.getButton(), (button) -> {
            return EvictingQueue.create(100);
         })).add(class_156.method_658());
      }

   }

   @EventHandler
   public void onTick(ClientTickEvent event) {
      this.handlerList.asMap().values().forEach((queue) -> {
         queue.removeIf((l) -> {
            return class_156.method_658() - l > 1000L;
         });
      });
   }
}
