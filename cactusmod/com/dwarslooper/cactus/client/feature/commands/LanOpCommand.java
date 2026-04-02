package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.feature.command.arguments.PlayerListEntryArgumentType;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.class_1132;
import net.minecraft.class_11560;
import net.minecraft.class_2172;
import net.minecraft.class_2561;
import net.minecraft.class_3324;

public class LanOpCommand extends Command {
   private static final SimpleCommandExceptionType ALREADY_OPPED_EXCEPTION = new SimpleCommandExceptionType(class_2561.method_43471("commands.op.failed"));
   private static final SimpleCommandExceptionType ALREADY_DEOPPED_EXCEPTION = new SimpleCommandExceptionType(class_2561.method_43471("commands.deop.failed"));

   public LanOpCommand() {
      super("wps");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.then(literal("op").then(argument("player", PlayerListEntryArgumentType.playerListEntry()).executes((context) -> {
         if (this.checkServerRunning()) {
            class_1132 integratedServer = CactusConstants.mc.method_1576();
            class_3324 playerManager = integratedServer.method_3760();
            GameProfile profile = PlayerListEntryArgumentType.getPlayerListEntry(context).method_2966();
            class_11560 entry = new class_11560(profile);
            if (playerManager.method_14569(entry)) {
               throw ALREADY_OPPED_EXCEPTION.create();
            }

            integratedServer.method_3760().method_14582(entry);
            ChatUtils.info((class_2561)class_2561.method_43469("commands.op.success", new Object[]{profile.name()}));
         }

         return 1;
      })))).then(literal("deop").then(argument("player", PlayerListEntryArgumentType.playerListEntry()).executes((context) -> {
         if (this.checkServerRunning()) {
            class_1132 integratedServer = CactusConstants.mc.method_1576();
            class_3324 playerManager = integratedServer.method_3760();
            GameProfile profile = PlayerListEntryArgumentType.getPlayerListEntry(context).method_2966();
            class_11560 entry = new class_11560(profile);
            if (!playerManager.method_14569(entry)) {
               throw ALREADY_DEOPPED_EXCEPTION.create();
            }

            integratedServer.method_3760().method_14604(entry);
            ChatUtils.info((class_2561)class_2561.method_43469("commands.deop.success", new Object[]{profile.name()}));
         }

         return 1;
      })))).then(literal("cheatsAllowed").then(argument("allowed", BoolArgumentType.bool()).executes((context) -> {
         if (this.checkServerRunning()) {
            boolean allowed = BoolArgumentType.getBool(context, "allowed");
            class_3324 playerManager = CactusConstants.mc.method_1576().method_3760();
            if (allowed == playerManager.method_14579()) {
               ChatUtils.error(this.getTranslatableElement(allowed ? "enableCheats.fail" : "disableCheats.fail", new Object[0]));
            } else {
               playerManager.method_14607(allowed);
               ChatUtils.info(this.getTranslatableElement(allowed ? "enableCheats.success" : "disableCheats.success", new Object[0]));
            }
         }

         return 1;
      })));
   }

   private boolean checkServerRunning() {
      if (CactusConstants.mc.method_1496()) {
         if (!CactusConstants.mc.method_1576().method_3760().method_14579()) {
            return true;
         }

         ChatUtils.error(this.getTranslatableElement("noDeopCommandsEnabled", new Object[0]));
      } else {
         ChatUtils.error(this.getTranslatableElement("notInWorld", new Object[0]));
      }

      return false;
   }
}
