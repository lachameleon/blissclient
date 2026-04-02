package com.dwarslooper.cactus.client.event;

import net.minecraft.class_2558;
import net.minecraft.class_2558.class_2559;

public class CRunnableClickEvent implements class_2558 {
   private final Runnable action;

   public CRunnableClickEvent(Runnable runnable) {
      this.action = runnable;
   }

   public void call() {
      this.action.run();
   }

   public class_2559 method_10845() {
      return null;
   }
}
