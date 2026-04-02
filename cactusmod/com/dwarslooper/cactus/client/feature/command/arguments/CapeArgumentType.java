package com.dwarslooper.cactus.client.feature.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_2172;
import net.minecraft.class_2561;

public class CapeArgumentType implements ArgumentType<String> {
   private static final List<String> availableCapes = new ArrayList();
   private static final DynamicCommandExceptionType INVALID_CAPE = new DynamicCommandExceptionType((o) -> {
      return class_2561.method_43469("command.arg.cape", new Object[]{o});
   });

   public static void updateAvailableCapes(List<String> capes) {
      availableCapes.clear();
      availableCapes.addAll(capes);
      availableCapes.add("none");
   }

   public static CapeArgumentType cape() {
      return new CapeArgumentType();
   }

   public static String getCape(CommandContext<?> context, String name) {
      return (String)context.getArgument(name, String.class);
   }

   public String parse(StringReader reader) throws CommandSyntaxException {
      String argument = reader.readString();
      if (argument.length() >= 2 && argument.length() <= 17) {
         return argument;
      } else {
         throw INVALID_CAPE.create(argument);
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return class_2172.method_9265(this.getCapes(), builder);
   }

   private Collection<String> getCapes() {
      return availableCapes;
   }
}
