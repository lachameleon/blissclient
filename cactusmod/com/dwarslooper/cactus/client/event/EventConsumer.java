package com.dwarslooper.cactus.client.event;

@FunctionalInterface
interface EventConsumer<T> {
   void accept(Object var1, T var2);
}
