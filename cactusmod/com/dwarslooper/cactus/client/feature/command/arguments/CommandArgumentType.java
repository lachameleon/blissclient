package com.dwarslooper.cactus.client.feature.command.arguments;

import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.feature.command.CommandManager;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_2172;
import net.minecraft.class_2561;

public class CommandArgumentType implements ArgumentType<Command> {
   private static final DynamicCommandExceptionType NO_SUCH_COMMAND = new DynamicCommandExceptionType((o) -> {
      return class_2561.method_43469("command.arg.feature.command", new Object[]{o});
   });

   public static CommandArgumentType command() {
      return new CommandArgumentType();
   }

   public static Command getCommand(CommandContext<?> context, String name) {
      return (Command)context.getArgument(name, Command.class);
   }

   public Command parse(StringReader reader) throws CommandSyntaxException {
      String argument = reader.readString();
      Command command = null;
      Iterator var4 = CommandManager.get().getCommands().iterator();

      while(var4.hasNext()) {
         Command c = (Command)var4.next();
         if (c.getName().equalsIgnoreCase(argument)) {
            command = c;
            break;
         }
      }

      if (command == null) {
         throw NO_SUCH_COMMAND.create(argument);
      } else {
         return command;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return class_2172.method_9264(CommandManager.get().getCommands().stream().map(Command::getName), builder);
   }
}
