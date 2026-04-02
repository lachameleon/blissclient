package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.class_2172;
import net.minecraft.class_2561;

public class ChatCommand extends Command {
   public static ChatCommand.ChatMode MODE;

   public ChatCommand() {
      super("chat");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.then(literal("default").executes((context) -> {
         MODE = ChatCommand.ChatMode.DEFAULT;
         ChatUtils.infoPrefix("CNet", (class_2561)class_2561.method_43471("commands.chat.changed.default"));
         return 1;
      }))).then(literal("irc").executes((context) -> {
         if (IRCCommand.checkIfConnectedElseError()) {
            MODE = ChatCommand.ChatMode.IRC;
            ChatUtils.infoPrefix("CNet", (class_2561)class_2561.method_43471("commands.chat.changed.irc"));
         }

         return 1;
      }))).executes((context) -> {
         if (MODE == ChatCommand.ChatMode.IRC) {
            MODE = ChatCommand.ChatMode.DEFAULT;
            ChatUtils.infoPrefix("CNet", (class_2561)class_2561.method_43471("commands.chat.changed.default"));
         } else if (IRCCommand.checkIfConnectedElseError()) {
            MODE = ChatCommand.ChatMode.IRC;
            ChatUtils.infoPrefix("CNet", (class_2561)class_2561.method_43471("commands.chat.changed.irc"));
         }

         return 1;
      });
   }

   static {
      MODE = ChatCommand.ChatMode.DEFAULT;
   }

   public static enum ChatMode {
      DEFAULT,
      IRC;

      // $FF: synthetic method
      private static ChatCommand.ChatMode[] $values() {
         return new ChatCommand.ChatMode[]{DEFAULT, IRC};
      }
   }
}
