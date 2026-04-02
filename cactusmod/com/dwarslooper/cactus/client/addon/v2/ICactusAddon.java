package com.dwarslooper.cactus.client.addon.v2;

public interface ICactusAddon {
   void onInitialize(RegistryBus var1);

   default void onLoadComplete() {
   }

   default void onShutdown() {
   }
}
