package com.dwarslooper.cactus.client.feature.macro;

import com.dwarslooper.cactus.client.systems.params.PlaceholderHandler;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import java.util.function.Consumer;
import java.util.function.Function;

public record Action<T>(T data, Action.Type type) {
   public Action(T data, Action.Type type) {
      this.data = data;
      this.type = type;
   }

   public void run() {
      this.type().run(this.data);
   }

   public void runMapped(Function<T, T> mapper) {
      T mapped = mapper.apply(this.data);
      this.type().run(mapped);
   }

   public T data() {
      return this.data;
   }

   public Action.Type type() {
      return this.type;
   }

   public static enum Type {
      RUN_COMMAND((command) -> {
         if (Utils.isInWorld()) {
            assert CactusConstants.mc.field_1724 != null;

            String string = ((String)command).startsWith("/") ? ((String)command).substring(1) : (String)command;
            CactusConstants.mc.field_1724.field_3944.method_45730(PlaceholderHandler.get().replacePlaceholders(string));
         }

      });

      private final Consumer<Object> consumer;

      private Type(Consumer<Object> consumer) {
         this.consumer = consumer;
      }

      public void run(Object content) {
         this.consumer.accept(content);
      }

      // $FF: synthetic method
      private static Action.Type[] $values() {
         return new Action.Type[]{RUN_COMMAND};
      }
   }
}
