package com.dwarslooper.cactus.client.gui.screen;

import com.dwarslooper.cactus.client.CactusClient;

public class EventHandlingCScreen extends CScreen {
   public EventHandlingCScreen(String key) {
      super(key);
   }

   public void method_49589() {
      CactusClient.EVENT_BUS.subscribe(this);
   }

   public void method_25432() {
      CactusClient.EVENT_BUS.unsubscribe(this);
   }
}
