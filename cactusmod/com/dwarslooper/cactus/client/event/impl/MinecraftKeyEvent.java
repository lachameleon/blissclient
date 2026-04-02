package com.dwarslooper.cactus.client.event.impl;

public class MinecraftKeyEvent extends BaseKeyEvent {
   public MinecraftKeyEvent(long window, int key, int scancode, int action, int modifiers) {
      super(window, key, scancode, action, modifiers);
   }
}
