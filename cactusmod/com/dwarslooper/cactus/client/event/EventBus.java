package com.dwarslooper.cactus.client.event;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventBus {
   private static final Logger LOGGER = LoggerFactory.getLogger("CactusClient / Event Handler");
   private final Map<Class<?>, List<EventSubscription>> subscribers = new ConcurrentHashMap();

   public void subscribe(Object subscriber) {
      Method[] var2 = subscriber.getClass().getDeclaredMethods();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Method method = var2[var4];
         if (this.isValid(method)) {
            method.trySetAccessible();
            Class<?> eventType = method.getParameterTypes()[0];
            EventHandler annotation = (EventHandler)method.getAnnotation(EventHandler.class);
            int priority = annotation != null ? annotation.priority() : 0;

            try {
               EventConsumer<Object> lambda = this.createLambda(method);
               this.subscribers.compute(eventType, (k, handlers) -> {
                  if (handlers == null) {
                     handlers = new CopyOnWriteArrayList();
                  }

                  ((List)handlers).add(new EventSubscription(subscriber, method, priority, lambda));
                  ((List)handlers).sort((a, b) -> {
                     return Integer.compare(b.priority(), a.priority());
                  });
                  return (List)handlers;
               });
            } catch (Throwable var10) {
               LOGGER.error("Failed to create lambda for {}#{}", new Object[]{subscriber.getClass().getName(), method.getName(), var10});
            }
         }
      }

   }

   public void unsubscribe(Object subscriber) {
      Iterator var2 = this.subscribers.values().iterator();

      while(var2.hasNext()) {
         List<EventSubscription> handlers = (List)var2.next();
         handlers.removeIf((handler) -> {
            return handler.subscriber() == subscriber;
         });
      }

   }

   public synchronized <T> T post(T event) {
      List<EventSubscription> handlers = (List)this.subscribers.get(event.getClass());
      if (handlers != null) {
         Iterator var3 = handlers.iterator();

         while(var3.hasNext()) {
            EventSubscription handler = (EventSubscription)var3.next();

            try {
               handler.invoke(event);
               if (event instanceof ICancellable) {
                  ICancellable cancellable = (ICancellable)event;
                  if (cancellable.isCancelled()) {
                     break;
                  }
               }
            } catch (Exception var6) {
               LOGGER.warn("{}#{} failed to handle {}", new Object[]{handler.subscriber().getClass().getCanonicalName(), handler.method().getName(), event.getClass().getCanonicalName(), var6});
            }
         }
      }

      return event;
   }

   private boolean isValid(Method method) {
      if (!method.isAnnotationPresent(EventHandler.class)) {
         return false;
      } else if (method.getReturnType() != Void.TYPE) {
         return false;
      } else if (method.getParameterCount() != 1) {
         return false;
      } else {
         return !method.getParameters()[0].getType().isPrimitive();
      }
   }

   private EventConsumer<Object> createLambda(Method method) throws Throwable {
      Lookup lookup = MethodHandles.lookup();
      MethodHandle methodHandle = lookup.unreflect(method);
      CallSite site = LambdaMetafactory.metafactory(lookup, "accept", MethodType.methodType(EventConsumer.class), MethodType.methodType(Void.TYPE, Object.class, Object.class), methodHandle, methodHandle.type());
      return site.getTarget().invokeExact();
   }
}
