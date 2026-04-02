package com.dwarslooper.cactus.client.feature.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.class_2487;
import net.minecraft.class_2522;

public class CompoundNbtTagArgumentType implements ArgumentType<class_2487> {
   private static final Collection<String> EXAMPLES = Arrays.asList("{foo:bar}", "{foo:[aa, bb],bar:15}");

   public static CompoundNbtTagArgumentType nbtTag() {
      return new CompoundNbtTagArgumentType();
   }

   public class_2487 parse(StringReader reader) throws CommandSyntaxException {
      reader.skipWhitespace();
      if (!reader.canRead()) {
         throw class_2522.field_56410.createWithContext(reader);
      } else {
         StringBuilder b = new StringBuilder();
         int open = 0;

         while(reader.canRead()) {
            if (reader.peek() == '{') {
               ++open;
            } else if (reader.peek() == '}') {
               --open;
            }

            if (open == 0) {
               break;
            }

            b.append(reader.read());
         }

         reader.expect('}');
         b.append('}');
         return class_2522.method_67315(b.toString());
      }
   }

   public static class_2487 getTag(CommandContext<?> context, String name) {
      return (class_2487)context.getArgument(name, class_2487.class);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
