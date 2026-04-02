package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.feature.command.arguments.LinkCodeArgumentType;
import com.dwarslooper.cactus.client.irc.protocol.packets.link.LinkCodePacket;
import com.dwarslooper.cactus.client.util.Utils;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.class_124;
import net.minecraft.class_2172;
import net.minecraft.class_2561;
import net.minecraft.class_2558.class_10608;

public class LinkCommand extends Command {
   public LinkCommand() {
      super("link");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(((LiteralArgumentBuilder)literal("discord").executes((context) -> {
         ChatUtils.info((class_2561)class_2561.method_43470("To link your Discord account, go to our ").method_10852(class_2561.method_43470("Discord Server").method_27695(new class_124[]{class_124.field_1078, class_124.field_1073}).method_27694((style) -> {
            return style.method_10958(new class_10608(Utils.asURI("https://cactusmod.xyz/go/?discord")));
         })).method_27693(" and run §f/link-account§r."));
         return 1;
      })).then(argument("code", LinkCodeArgumentType.linkCode()).executes((context) -> {
         if (IRCCommand.checkIfConnectedElseError()) {
            CactusClient.getInstance().getIrcClient().sendPacket(new LinkCodePacket(LinkCodePacket.Type.Discord, LinkCodeArgumentType.getCode(context, "code")));
         }

         return 1;
      })));
   }
}
