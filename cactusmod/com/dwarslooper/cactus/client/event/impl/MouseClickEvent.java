package com.dwarslooper.cactus.client.event.impl;

public class MouseClickEvent extends EventCancellable {
   private final long window;
   private final int button;
   private final int action;
   private final int mods;

   public MouseClickEvent(long window, int button, int action, int mods) {
      this.window = window;
      this.button = button;
      this.action = action;
      this.mods = mods;
   }

   public long getWindow() {
      return this.window;
   }

   public int getButton() {
      return this.button;
   }

   public int getAction() {
      return this.action;
   }

   public int getMods() {
      return this.mods;
   }
}
