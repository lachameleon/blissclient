package com.dwarslooper.cactus.client.event.impl;

import com.dwarslooper.cactus.client.event.ICancellable;

public class EventCancellable implements ICancellable {
   private boolean cancelled;

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }
}
