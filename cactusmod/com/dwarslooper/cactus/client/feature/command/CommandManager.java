package com.dwarslooper.cactus.client.feature.command;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.feature.commands.AutoCraftCommand;
import com.dwarslooper.cactus.client.feature.commands.BindsCommand;
import com.dwarslooper.cactus.client.feature.commands.CapeCommand;
import com.dwarslooper.cactus.client.feature.commands.ChatCommand;
import com.dwarslooper.cactus.client.feature.commands.ContentPackCommand;
import com.dwarslooper.cactus.client.feature.commands.CopyIpCommand;
import com.dwarslooper.cactus.client.feature.commands.DebugCommand;
import com.dwarslooper.cactus.client.feature.commands.FilterItemCommand;
import com.dwarslooper.cactus.client.feature.commands.HelpCommand;
import com.dwarslooper.cactus.client.feature.commands.IRCCommand;
import com.dwarslooper.cactus.client.feature.commands.LanOpCommand;
import com.dwarslooper.cactus.client.feature.commands.LinkCommand;
import com.dwarslooper.cactus.client.feature.commands.MoveBox;
import com.dwarslooper.cactus.client.feature.commands.MsgChatCommand;
import com.dwarslooper.cactus.client.feature.commands.NbtCommand;
import com.dwarslooper.cactus.client.feature.commands.PacketLoggerCommand;
import com.dwarslooper.cactus.client.feature.commands.RGBCommand;
import com.dwarslooper.cactus.client.feature.commands.SchematicCommand;
import com.dwarslooper.cactus.client.feature.commands.SignalStrengthBlockCommand;
import com.dwarslooper.cactus.client.feature.commands.TestCommand;
import com.dwarslooper.cactus.client.feature.commands.ToolCommand;
import com.dwarslooper.cactus.client.feature.commands.UserscriptCommand;
import com.dwarslooper.cactus.client.feature.commands.UuidCommand;
import com.dwarslooper.cactus.client.feature.commands.WaypointCommand;
import com.dwarslooper.cactus.client.feature.commands.WorldCommand;
import com.dwarslooper.cactus.client.systems.ISerializable;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.systems.config.ConfigHandler;
import com.dwarslooper.cactus.client.systems.config.FileConfiguration;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_2172;
import net.minecraft.class_310;

public class CommandManager extends FileConfiguration<CommandManager> {
   private final class_2172 commandSource = class_310.method_1551().method_1562() != null ? class_310.method_1551().method_1562().method_2875() : null;
   private final CommandDispatcher<class_2172> dispatcher = new CommandDispatcher();
   private final List<Command> commands = new ArrayList();

   public static String getCommandPrefix() {
      return ((String)CactusSettings.get().commandPrefix.get()).isEmpty() ? "#" : (String)CactusSettings.get().commandPrefix.get();
   }

   public static String getIRCPrefix() {
      return ((String)CactusSettings.get().ircShortPrefix.get()).isEmpty() ? "" : (String)CactusSettings.get().ircShortPrefix.get();
   }

   public void init() {
      this.registerCommand(new CapeCommand());
      this.registerCommand(new RGBCommand());
      this.registerCommand(new IRCCommand());
      this.registerCommand(new BindsCommand());
      this.registerCommand(new WorldCommand());
      this.registerCommand(new ChatCommand());
      this.registerCommand(new WaypointCommand());
      this.registerCommand(new ContentPackCommand());
      this.registerCommand(new AutoCraftCommand());
      this.registerCommand(new DebugCommand());
      this.registerCommand(new SignalStrengthBlockCommand());
      this.registerCommand(new HelpCommand());
      this.registerCommand(new FilterItemCommand());
      this.registerCommand(new UserscriptCommand());
      this.registerCommand(new PacketLoggerCommand());
      this.registerCommand(new UuidCommand());
      this.registerCommand(new CopyIpCommand());
      this.registerCommand(new LinkCommand());
      this.registerCommand(new NbtCommand());
      this.registerCommand(new LanOpCommand());
      this.registerCommand(new ToolCommand());
      this.registerCommand(new MsgChatCommand());
      if (CactusConstants.DEVBUILD) {
         this.registerCommand(new TestCommand());
         this.registerCommand(new MoveBox());
      }

      if (Utils.isModInstalled("litematica") && Utils.isModInstalled("malilib")) {
         this.registerCommand(new SchematicCommand());
      }

      getMainRegistryBus().completeAndTake(Command.class, this::registerCommand);
   }

   public static CommandManager get() {
      return (CommandManager)CactusClient.getConfig(CommandManager.class);
   }

   public CommandManager(ConfigHandler handler) {
      super("commands", handler);
   }

   public void registerCommand(Command command) {
      LiteralArgumentBuilder<class_2172> builder = LiteralArgumentBuilder.literal(command.getName());
      command.build(builder);
      this.dispatcher.register(builder);
      this.commands.add(command);
   }

   public void execute(String command) throws CommandSyntaxException {
      this.dispatcher.execute(command, this.commandSource);
   }

   public class_2172 getCommandSource() {
      return this.commandSource;
   }

   public CommandDispatcher<class_2172> getDispatcher() {
      return this.dispatcher;
   }

   public List<Command> getCommands() {
      return this.commands;
   }

   public JsonObject toJson(TreeSerializerFilter filter) {
      JsonObject object = new JsonObject();
      Iterator var3 = this.commands.iterator();

      while(var3.hasNext()) {
         Command command = (Command)var3.next();
         if (command instanceof ISerializable) {
            ISerializable<?> serializable = (ISerializable)command;
            JsonObject sElement = serializable.toJson(filter.resolve(command.getName()));
            object.add(command.getName(), sElement);
         }
      }

      return object;
   }

   public CommandManager fromJson(JsonObject object) {
      Iterator var2 = this.commands.iterator();

      while(var2.hasNext()) {
         Command command = (Command)var2.next();
         if (command instanceof ISerializable) {
            ISerializable<?> serializable = (ISerializable)command;
            if (object.has(command.getName())) {
               serializable.fromJson(object.getAsJsonObject(command.getName()));
            }
         }
      }

      return this;
   }
}
