package com.dwarslooper.cactus.client.feature.command.arguments;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.feature.macro.Macro;
import com.dwarslooper.cactus.client.feature.macro.MacroManager;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.minecraft.class_2172;
import net.minecraft.class_2561;

public class MacroArgumentType implements ArgumentType<Macro> {
   private static Collection<String> EXAMPLES;
   private static final DynamicCommandExceptionType NO_SUCH_MACRO;

   public static MacroArgumentType macroEntry() {
      return new MacroArgumentType();
   }

   public static Macro getMacroEntry(CommandContext<?> context) {
      return (Macro)context.getArgument("macro", Macro.class);
   }

   public Macro parse(StringReader reader) throws CommandSyntaxException {
      String argument = reader.getRemaining();
      reader.setCursor(reader.getTotalLength());
      Macro macro = (Macro)MacroManager.get().getMacros().stream().filter((m) -> {
         return m.getName().equalsIgnoreCase(argument);
      }).findFirst().orElse((Object)null);
      if (macro == null) {
         throw NO_SUCH_MACRO.create(argument);
      } else {
         return macro;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return class_2172.method_9264(MacroManager.get().getMacros().stream().map(Macro::getName), builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      if (CactusClient.hasFinishedLoading()) {
         EXAMPLES = (Collection)MacroManager.get().getMacros().stream().limit(3L).map(Macro::getName).collect(Collectors.toList());
      }

      NO_SUCH_MACRO = new DynamicCommandExceptionType((o) -> {
         return class_2561.method_43469("command.arg.macro", new Object[]{o});
      });
   }
}
