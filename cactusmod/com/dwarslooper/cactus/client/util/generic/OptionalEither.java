package com.dwarslooper.cactus.client.util.generic;

import org.jetbrains.annotations.Nullable;

public record OptionalEither<T, E>(@Nullable T left, @Nullable E right) {
   public OptionalEither(@Nullable T left, @Nullable E right) {
      this.left = left;
      this.right = right;
   }

   public static <T> OptionalEither<T, ?> left(T value) {
      return new OptionalEither(value, (Object)null);
   }

   public static <T> OptionalEither<T, ?> right(T value) {
      return new OptionalEither((Object)null, value);
   }

   public static OptionalEither<?, ?> empty() {
      return new OptionalEither((Object)null, (Object)null);
   }

   public OptionalEither<?, ?> flip() {
      return new OptionalEither(this.right, this.left);
   }

   @Nullable
   public T left() {
      return this.left;
   }

   @Nullable
   public E right() {
      return this.right;
   }
}
