package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.class_2172;

public class WorldCommand extends Command {
   public WorldCommand() {
      super("worldmanager");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(literal("prepare").executes((context) -> {
         assert CactusConstants.mc.method_1562() != null;

         CactusConstants.mc.method_1562().method_45730("gamerule doDaylightCycle false");
         CactusConstants.mc.method_1562().method_45730("gamerule doWeatherCycle false");
         CactusConstants.mc.method_1562().method_45730("gamerule doTraderSpawning false");
         CactusConstants.mc.method_1562().method_45730("weather clear");
         CactusConstants.mc.method_1562().method_45730("time set noon");
         return 1;
      }));
   }
}
