package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.feature.command.arguments.CapeArgumentType;
import com.dwarslooper.cactus.client.gui.screen.impl.CosmeticsListScreen;
import com.dwarslooper.cactus.client.irc.protocol.packets.cape.GetAvailableCapesBiDPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.cape.UpdateCapeBiDPacket;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.class_2172;
import net.minecraft.class_437;

public class CapeCommand extends Command {
   public CapeCommand() {
      super("capes");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.then(literal("set").then(argument("cape", CapeArgumentType.cape()).executes((context) -> {
         IRCCommand.checkIfConnectedElseError();
         String changeCape = CapeArgumentType.getCape(context, "cape");
         CactusClient.getInstance().getIrcClient().sendPacket(new UpdateCapeBiDPacket(changeCape));
         CactusClient.getInstance().getIrcClient().sendPacket(new GetAvailableCapesBiDPacket(false));
         return 1;
      })))).then(literal("list").executes((context) -> {
         IRCCommand.checkIfConnectedElseError();
         CactusClient.getInstance().getIrcClient().sendPacket(new GetAvailableCapesBiDPacket(true));
         return 1;
      }))).executes((context) -> {
         Utils.unsafeDelayed(() -> {
            CactusConstants.mc.method_1507(new CosmeticsListScreen((class_437)null));
         }, 100L);
         return 1;
      });
   }
}
