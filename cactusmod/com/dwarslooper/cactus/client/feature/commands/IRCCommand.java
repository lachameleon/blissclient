package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.net.InetSocketAddress;
import net.minecraft.class_2172;
import net.minecraft.class_2561;

public class IRCCommand extends Command {
   public static InetSocketAddress customAddress = null;

   public IRCCommand() {
      super("irc");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.then(literal("connect").executes((context) -> {
         CactusClient.getInstance().getIrcClient().connect();
         return 1;
      }))).then(literal("disconnect").executes((context) -> {
         if (checkIfConnectedElseError()) {
            CactusClient.getInstance().getIrcClient().disconnect();
         }

         return 1;
      }))).then(literal("send").then(argument("message", StringArgumentType.greedyString()).executes((context) -> {
         if (checkIfConnectedElseError()) {
            CactusClient.getInstance().getIrcClient().sendGlobalChat(StringArgumentType.getString(context, "message"));
         }

         return 1;
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)literal("address").executes((context) -> {
         customAddress = null;
         ChatUtils.info("Using default address");
         return 1;
      })).then(literal("devenv").executes((context) -> {
         customAddress = new InetSocketAddress("localhost", 1414);
         return 1;
      }))).then(argument("ip", StringArgumentType.string()).executes((context) -> {
         try {
            String[] address = StringArgumentType.getString(context, "ip").split(":");
            customAddress = new InetSocketAddress(address[0], Integer.parseInt(address[1]));
            ChatUtils.info("Using address " + address[0] + " and port " + address[1]);
         } catch (Exception var2) {
            ChatUtils.error("Use a correct IP format! (ip:port)");
         }

         return 1;
      })));
   }

   public static boolean checkIfConnectedElseError() {
      if (!IRCClient.connected()) {
         ChatUtils.errorPrefix("CNet", (class_2561)class_2561.method_43471("irc.not_connected"));
         return false;
      } else {
         return true;
      }
   }
}
