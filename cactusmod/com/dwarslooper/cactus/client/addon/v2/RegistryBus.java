package com.dwarslooper.cactus.client.addon.v2;

import com.dwarslooper.cactus.client.CactusClient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class RegistryBus {
   private final RegistrationContext context = new RegistrationContext();
   private final Set<Class<?>> completed = new HashSet();
   private final Map<Class<?>, ContentRegistry<?>> registries = new HashMap();
   private final Map<Class<?>, List<PendingRegistration>> pending = new LinkedHashMap();
   private final Map<Object, Addon> owners = new HashMap();
   private Addon currentRegistrar;

   public void withRegistrar(Addon registrar, Consumer<RegistryBus> wrapped) {
      this.currentRegistrar = registrar;
      wrapped.accept(this);
      this.currentRegistrar = null;
   }

   public <T> void provideRegistry(Class<T> type, ContentRegistry<T> registry) {
      this.registries.put(type, registry);
   }

   public <T> void provideService(Class<T> serviceClass, T instance) {
      this.context.bind(serviceClass, instance);
   }

   public <T> void register(Class<T> type, Function<RegistrationContext, T> factory) {
      this.ensureNonComplete(type);
      this.add(type, factory);
   }

   public <T> void register(Class<T> type, BiConsumer<List<T>, RegistrationContext> consumer) {
      this.ensureNonComplete(type);
      this.add(type, (ctx) -> {
         List<T> list = new ArrayList();
         consumer.accept(list, ctx);
         return list;
      });
   }

   private void add(Class<?> type, Function<RegistrationContext, ?> factory) {
      ((List)this.pending.computeIfAbsent(type, (k) -> {
         return new ArrayList();
      })).add(new PendingRegistration(this.currentRegistrar, factory));
   }

   public <T> void completeAndTake(Class<T> type, BiConsumer<T, Addon> callback) {
      this.complete(type);
      this.take(type, callback);
   }

   public <T> void take(Class<T> type, BiConsumer<T, Addon> callback) {
      this.request(type).forEach((t) -> {
         callback.accept(t, this.getOwner(t));
      });
   }

   public <T> void completeAndTake(Class<T> type, Consumer<T> callback) {
      this.complete(type);
      this.take(type, callback);
   }

   public <T> void take(Class<T> type, Consumer<T> callback) {
      this.request(type).forEach(callback);
   }

   public <T> List<T> request(Class<T> type) {
      ContentRegistry<?> registry = (ContentRegistry)this.registries.get(type);
      if (registry == null) {
         if (this.pending.containsKey(type)) {
            CactusClient.getInstance().getAddonHandler().getLogger().warn("Requested type '{}' was not found in registries, however this type is pending. Was this type completed correctly?", type.getCanonicalName());
         }

         return Collections.emptyList();
      } else {
         return registry.getAll();
      }
   }

   public <T> void complete(Class<T> type) {
      this.ensureNonComplete(type);
      List<PendingRegistration> registrations = (List)this.pending.get(type);
      if (registrations != null) {
         ContentRegistry<Object> registry = (ContentRegistry)this.registries.computeIfAbsent(type, (k) -> {
            return new SimpleRegistry();
         });
         Iterator var4 = registrations.iterator();

         while(var4.hasNext()) {
            PendingRegistration reg = (PendingRegistration)var4.next();
            Object obj = reg.factory().apply(this.context);
            this.owners.put(obj, reg.owner());
            if (obj instanceof Collection) {
               Collection<?> collection = (Collection)obj;
               Objects.requireNonNull(registry);
               collection.forEach(registry::register);
            } else {
               registry.register(obj);
            }
         }
      }

      this.completed.add(type);
      this.pending.remove(type);
   }

   public Addon getOwner(Object o) {
      return (Addon)this.owners.get(o);
   }

   private void ensureNonComplete(Class<?> type) {
      if (this.completed.contains(type)) {
         throw new IllegalStateException("This type is already completed. An instance of RegistryBus should not be retained, use outside of CactusAddon#onInitialize is unsafe!");
      }
   }
}
