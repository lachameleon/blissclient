package com.dwarslooper.cactus.client.event.impl;

public abstract class BaseKeyEvent extends EventCancellable {
   private final long window;
   private final int key;
   private final int scancode;
   private final int action;
   private final int modifiers;

   public BaseKeyEvent(long window, int key, int scancode, int action, int modifiers) {
      this.window = window;
      this.key = key;
      this.scancode = scancode;
      this.action = action;
      this.modifiers = modifiers;
   }

   public long getWindow() {
      return this.window;
   }

   public int getKey() {
      return this.key;
   }

   public int getScancode() {
      return this.scancode;
   }

   public int getAction() {
      return this.action;
   }

   public int getModifiers() {
      return this.modifiers;
   }

   public String toString() {
      return "BaseKeyEvent{window=" + this.window + ", key=" + this.key + ", scancode=" + this.scancode + ", action=" + this.action + ", modifiers=" + this.modifiers + "}";
   }
}
