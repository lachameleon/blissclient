package com.dwarslooper.cactus.client.event;

public interface ICancellable {
   default void cancel() {
      this.setCancelled(true);
   }

   void setCancelled(boolean var1);

   boolean isCancelled();
}
