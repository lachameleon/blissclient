package com.dwarslooper.cactus.client.feature.command.arguments;

import com.dwarslooper.cactus.client.util.CactusConstants;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.minecraft.class_1657;
import net.minecraft.class_2172;
import net.minecraft.class_2561;

public class PlayerArgumentType implements ArgumentType<class_1657> {
   private static Collection<String> EXAMPLES;
   private static final DynamicCommandExceptionType NO_SUCH_PLAYER;

   public static PlayerArgumentType player() {
      return new PlayerArgumentType();
   }

   public static class_1657 getPlayer(CommandContext<?> context) {
      return (class_1657)context.getArgument("player", class_1657.class);
   }

   public class_1657 parse(StringReader reader) throws CommandSyntaxException {
      String argument = reader.readString();
      class_1657 playerEntity = null;
      Iterator var4 = CactusConstants.mc.field_1687.method_18456().iterator();

      while(var4.hasNext()) {
         class_1657 p = (class_1657)var4.next();
         if (p.method_5477().getString().equalsIgnoreCase(argument)) {
            playerEntity = p;
            break;
         }
      }

      if (playerEntity == null) {
         throw NO_SUCH_PLAYER.create(argument);
      } else {
         return playerEntity;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return class_2172.method_9264(CactusConstants.mc.field_1687.method_18456().stream().map((e) -> {
         return e.method_5477().getString();
      }), builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      if (CactusConstants.mc.field_1687 != null) {
         EXAMPLES = (Collection)CactusConstants.mc.field_1687.method_18456().stream().limit(3L).map((e) -> {
            return e.method_5477().getString();
         }).collect(Collectors.toList());
      }

      NO_SUCH_PLAYER = new DynamicCommandExceptionType((o) -> {
         return class_2561.method_43469("command.arg.player", new Object[]{o});
      });
   }
}
