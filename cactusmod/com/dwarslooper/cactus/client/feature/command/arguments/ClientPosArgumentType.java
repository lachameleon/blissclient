package com.dwarslooper.cactus.client.feature.command.arguments;

import com.dwarslooper.cactus.client.util.CactusConstants;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_2170;
import net.minecraft.class_2172;
import net.minecraft.class_2277;
import net.minecraft.class_2278;
import net.minecraft.class_243;
import net.minecraft.class_2172.class_2173;

public class ClientPosArgumentType implements ArgumentType<class_243> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "~0.5 ~1 ~-5");

   public static ClientPosArgumentType pos() {
      return new ClientPosArgumentType();
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      if (!(context.getSource() instanceof class_2172)) {
         return Suggestions.empty();
      } else {
         String string = builder.getRemaining();
         Collection<class_2173> collection2 = ((class_2172)context.getSource()).method_17771();
         return class_2172.method_9260(string, collection2, builder, class_2170.method_9238(this::parse));
      }
   }

   public static class_243 getPos(CommandContext<?> context, String name) {
      return (class_243)context.getArgument(name, class_243.class);
   }

   public class_243 parse(StringReader reader) throws CommandSyntaxException {
      int i = reader.getCursor();
      class_2278 coordinateArgument = class_2278.method_9739(reader);
      if (reader.canRead() && reader.peek() == ' ') {
         reader.skip();
         class_2278 coordinateArgument2 = class_2278.method_9739(reader);
         if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            class_2278 coordinateArgument3 = class_2278.method_9739(reader);
            double x = coordinateArgument.method_9740(CactusConstants.mc.field_1724.method_23317());
            double y = coordinateArgument2.method_9740(CactusConstants.mc.field_1724.method_23318());
            double z = coordinateArgument3.method_9740(CactusConstants.mc.field_1724.method_23321());
            return new class_243(x, y, z);
         } else {
            reader.setCursor(i);
            throw class_2277.field_10755.createWithContext(reader);
         }
      } else {
         reader.setCursor(i);
         throw class_2277.field_10755.createWithContext(reader);
      }
   }
}
