package com.dwarslooper.cactus.client.event.impl;

public class MouseScrollEvent extends EventCancellable {
   private final long window;
   private final double horizontal;
   private final double vertical;
   private final double amount;

   public MouseScrollEvent(long window, double horizontal, double vertical, double amount) {
      this.window = window;
      this.horizontal = horizontal;
      this.vertical = vertical;
      this.amount = amount;
   }

   public long getWindow() {
      return this.window;
   }

   public double getHorizontal() {
      return this.horizontal;
   }

   public double getVertical() {
      return this.vertical;
   }

   public double getAmount() {
      return this.amount;
   }
}
