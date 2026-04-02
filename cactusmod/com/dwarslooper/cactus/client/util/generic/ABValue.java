package com.dwarslooper.cactus.client.util.generic;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ABValue<T> {
   private final T a;
   private final T b;
   private final List<T> valuesAsList;

   public ABValue(T a, T b) {
      this.a = a;
      this.b = b;
      this.valuesAsList = ImmutableList.of(a, b);
   }

   public ABValue<T> apply(Consumer<T> consumer) {
      this.collect().forEach(consumer);
      return this;
   }

   public <E> ABValue<E> map(Function<T, E> mapper) {
      return of(this.collect().stream().map(mapper).toList());
   }

   public Collection<T> collect() {
      return this.valuesAsList;
   }

   public T fromBoolean(boolean value) {
      return value ? this.a : this.b;
   }

   public static <T> ABValue<T> of(List<T> list) {
      if (list.size() != 2) {
         throw new IllegalArgumentException("Collection passed to of() must contain exactly 2 objects");
      } else {
         return new ABValue(list.get(0), list.get(1));
      }
   }

   public static <T> ABValue<T> of(T a, T b) {
      return new ABValue(a, b);
   }

   public T a() {
      return this.a;
   }

   public T b() {
      return this.b;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.a, this.b});
   }

   public String toString() {
      String var10000 = String.valueOf(this.a);
      return "ABValue[a=" + var10000 + ", b=" + String.valueOf(this.b) + "]";
   }
}
