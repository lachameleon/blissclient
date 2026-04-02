package com.dwarslooper.cactus.client.feature.command.arguments;

import com.dwarslooper.cactus.client.systems.waypoints.WaypointEntry;
import com.dwarslooper.cactus.client.systems.waypoints.WaypointManager;
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
import net.minecraft.class_310;

public class WaypointArgumentType implements ArgumentType<WaypointEntry> {
   private static Collection<String> EXAMPLES;
   private static final DynamicCommandExceptionType NO_SUCH_WAYPOINT;

   public static WaypointArgumentType waypointEntry() {
      return new WaypointArgumentType();
   }

   public static WaypointEntry getWaypointEntry(CommandContext<?> context) {
      return (WaypointEntry)context.getArgument("waypoint", WaypointEntry.class);
   }

   public WaypointEntry parse(StringReader reader) throws CommandSyntaxException {
      String argument = reader.getRemaining();
      reader.setCursor(reader.getTotalLength());
      WaypointEntry waypointEntry = (WaypointEntry)WaypointManager.get().getInstance().getWaypointEntryList().stream().filter((w) -> {
         return w.getName().equalsIgnoreCase(argument);
      }).findFirst().orElse((Object)null);
      if (waypointEntry == null) {
         throw NO_SUCH_WAYPOINT.create(argument);
      } else {
         return waypointEntry;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return class_2172.method_9264(WaypointManager.get().getInstance().getWaypointEntryList().stream().map(WaypointEntry::getName), builder);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      if (class_310.method_1551().method_1562() != null) {
         EXAMPLES = (Collection)WaypointManager.get().getInstance().getWaypointEntryList().stream().limit(3L).map(WaypointEntry::getName).collect(Collectors.toList());
      }

      NO_SUCH_WAYPOINT = new DynamicCommandExceptionType((o) -> {
         return class_2561.method_43469("command.arg.waypoint", new Object[]{o});
      });
   }
}
