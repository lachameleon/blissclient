package com.dwarslooper.cactus.client.event.impl.render;

import net.minecraft.class_332;
import net.minecraft.class_9779;

public record RenderHUDEvent(class_332 context, class_9779 tickCounter) {
   public RenderHUDEvent(class_332 context, class_9779 tickCounter) {
      this.context = context;
      this.tickCounter = tickCounter;
   }

   public class_332 context() {
      return this.context;
   }

   public class_9779 tickCounter() {
      return this.tickCounter;
   }
}
