package com.dwarslooper.cactus.client.addon.v2;

import java.util.HashMap;
import java.util.Map;

public class RegistrationContext {
   private final Map<Class<?>, Object> services = new HashMap();

   protected <T> void bind(Class<T> type, T instance) {
      this.services.put(type, instance);
   }

   public <T> T require(Class<T> type) {
      T val = type.cast(this.services.get(type));
      if (val == null) {
         throw new IllegalStateException("Missing service '%s'".formatted(new Object[]{type.getCanonicalName()}));
      } else {
         return val;
      }
   }
}
