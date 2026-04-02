package com.dwarslooper.cactus.client.event;

import java.lang.reflect.Method;

public record EventSubscription(Object subscriber, Method method, int priority, EventConsumer<Object> handler) {
   public EventSubscription(Object subscriber, Method method, int priority, EventConsumer<Object> handler) {
      this.subscriber = subscriber;
      this.method = method;
      this.priority = priority;
      this.handler = handler;
   }

   public void invoke(Object event) {
      this.handler.accept(this.subscriber, event);
   }

   public Object subscriber() {
      return this.subscriber;
   }

   public Method method() {
      return this.method;
   }

   public int priority() {
      return this.priority;
   }

   public EventConsumer<Object> handler() {
      return this.handler;
   }
}
