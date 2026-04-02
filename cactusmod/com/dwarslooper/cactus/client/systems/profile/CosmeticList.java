package com.dwarslooper.cactus.client.systems.profile;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CosmeticList implements Iterable<Set<AbstractCosmetic<?>>> {
   private final Set<AbstractCosmetic<?>> available = new HashSet();
   private final Map<Class<?>, Set<AbstractCosmetic<?>>> delegate = new HashMap();

   public CosmeticList(Collection<AbstractCosmetic<?>> cosmetics) {
      cosmetics.stream().filter(Objects::nonNull).forEach((cosmetic) -> {
         this.delegate.compute(cosmetic.getClass(), (clazz, set) -> {
            this.available.add(cosmetic);
            if (set == null) {
               set = new HashSet();
            }

            ((Set)set).add(cosmetic);
            return (Set)set;
         });
      });
   }

   @Nullable
   public <T extends AbstractCosmetic<T>> Set<T> allOf(Class<T> clazz) {
      return (Set)this.delegate.get(clazz);
   }

   @Nullable
   public <T extends AbstractCosmetic<T>> T singleOf(Class<T> clazz) {
      return this.has(clazz) ? (AbstractCosmetic)((Set)this.delegate.get(clazz)).iterator().next() : null;
   }

   public <T extends AbstractCosmetic<T>> boolean has(Class<T> clazz) {
      return this.delegate.containsKey(clazz);
   }

   public boolean has(AbstractCosmetic<?> cosmetic) {
      return this.available.contains(cosmetic);
   }

   public Set<AbstractCosmetic<?>> all() {
      return Collections.unmodifiableSet(this.available);
   }

   public boolean isEmpty() {
      return this.delegate.isEmpty();
   }

   @NotNull
   public Iterator<Set<AbstractCosmetic<?>>> iterator() {
      return this.delegate.values().iterator();
   }
}
