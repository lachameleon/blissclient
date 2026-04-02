package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.class_2172;
import net.minecraft.class_2561;
import net.minecraft.class_642;

public class CopyIpCommand extends Command {
   public CopyIpCommand() {
      super("copyip");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.executes((context) -> {
         class_642 info = CactusConstants.mc.method_1558();
         if (info != null) {
            CactusConstants.mc.field_1774.method_1455(info.field_3761);
            ChatUtils.info((class_2561)class_2561.method_43471("commands.copyip.copied"));
         } else {
            ChatUtils.error((class_2561)class_2561.method_43471("commands.copyip.noServer"));
         }

         return 1;
      });
   }
}
