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
import net.minecraft.class_2172;
import net.minecraft.class_2561;
import net.minecraft.class_640;

public class PlayerListEntryArgumentType implements ArgumentType<class_640> {
   private static Collection<String> EXAMPLES;
   private static final DynamicCommandExceptionType NO_SUCH_PLAYER;

   public static PlayerListEntryArgumentType playerListEntry() {
      return new PlayerListEntryArgumentType();
   }

   public static class_640 getPlayerListEntry(CommandContext<?> context) {
      return (class_640)context.getArgument("player", class_640.class);
   }

   public class_640 parse(StringReader reader) throws CommandSyntaxException {
      String argument = reader.readString();
      class_640 playerListEntry = null;
      Iterator var4 = CactusConstants.mc.method_1562().method_2880().iterator();

      while(var4.hasNext()) {
         class_640 p = (class_640)var4.next();
         if (p.method_2966().name().equalsIgnoreCase(argument)) {
            playerListEntry = p;
            break;
         }
      }

      if (playerListEntry == null) {
         throw NO_SUCH_PLAYER.create(argument);
      } else {
         return playerListEntry;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return class_2172.method_9264(CactusConstants.mc.method_1562().method_2880().stream().map((playerListEntry) -> {
         return playerListEntry.method_2966().name();
      }), builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      if (CactusConstants.mc.method_1562() != null) {
         EXAMPLES = (Collection)CactusConstants.mc.method_1562().method_2880().stream().limit(3L).map((playerListEntry) -> {
            return playerListEntry.method_2966().name();
         }).collect(Collectors.toList());
      }

      NO_SUCH_PLAYER = new DynamicCommandExceptionType((o) -> {
         return class_2561.method_43469("command.arg.playerList", new Object[]{o});
      });
   }
}
