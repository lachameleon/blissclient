package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.feature.command.CommandManager;
import com.dwarslooper.cactus.client.feature.command.arguments.CommandArgumentType;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import net.minecraft.class_124;
import net.minecraft.class_2172;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_5250;
import net.minecraft.class_2568.class_10613;

public class HelpCommand extends Command {
   public HelpCommand() {
      super("help");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      List<Command> commandList = CommandManager.get().getCommands();
      ((LiteralArgumentBuilder)builder.then(argument("command", CommandArgumentType.command()).executes((context) -> {
         Command command = CommandArgumentType.getCommand(context, "command");
         ChatUtils.info("§e" + command.getName());
         ChatUtils.info(command.getDescription());
         return 1;
      }))).executes((context) -> {
         class_5250 sb = class_2561.method_43470(this.getTranslatableElement("header", new Object[]{commandList.size()})).method_27693("\n");
         commandList.forEach((command) -> {
            if (commandList.indexOf(command) > 0) {
               sb.method_27693("§8, ");
            }

            sb.method_10852(class_2561.method_43470("§e" + command.getName()).method_27694((style) -> {
               class_2583 var10000 = style.method_10977(class_124.field_1054);
               String var10003 = (String)CactusSettings.get().commandPrefix.get();
               return var10000.method_10949(new class_10613(class_2561.method_43470("§e§l§n" + var10003 + command.getName() + "\n§r§a" + command.getDescription())));
            }));
         });
         ChatUtils.info((class_2561)sb);
         return 1;
      });
   }
}
