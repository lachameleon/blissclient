package com.dwarslooper.cactus.client.event.impl;

import com.dwarslooper.cactus.client.util.CactusConstants;

public class ClientTickEvent {
   public static ClientTickEvent INSTANCE = new ClientTickEvent();

   public float getDelta() {
      return CactusConstants.mc.method_61966().method_60637(false);
   }
}
