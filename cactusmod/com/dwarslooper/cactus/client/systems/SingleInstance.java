package com.dwarslooper.cactus.client.systems;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SingleInstance {
   private static final Map<Class<?>, Object> instanceMap = new HashMap();

   @SafeVarargs
   public static <T> T of(Class<?> clazz, Supplier<Object> instanceSupplier, Consumer<T>... tasks) {
      if (instanceMap.containsKey(clazz)) {
         return instanceMap.get(clazz);
      } else {
         T instance = instanceSupplier.get();
         instanceMap.put(clazz, instance);
         Consumer[] var4 = tasks;
         int var5 = tasks.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Consumer<T> task = var4[var6];
            task.accept(instance);
         }

         return instance;
      }
   }

   public static boolean has(Class<?> clazz) {
      return instanceMap.containsKey(clazz);
   }

   public static void remove(Class<?> clazz) {
      instanceMap.remove(clazz);
   }
}
