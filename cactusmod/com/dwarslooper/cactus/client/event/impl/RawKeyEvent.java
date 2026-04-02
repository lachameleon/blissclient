package com.dwarslooper.cactus.client.event.impl;

import org.lwjgl.glfw.GLFW;

public class RawKeyEvent extends BaseKeyEvent {
   public RawKeyEvent(long window, int key, int scancode, int action, int modifiers) {
      super(window, key, scancode, action, modifiers);
   }

   public RawKeyEvent(int key, int action) {
      this(-1L, key, GLFW.glfwGetKeyScancode(key), action, 0);
   }
}
