package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.feature.command.arguments.LitematicArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.io.File;
import net.minecraft.class_2172;

public class SchematicCommand extends Command {
   public SchematicCommand() {
      super("schematics");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(literal("share").then(argument("file", LitematicArgumentType.litematic()).executes((context) -> {
         File f = LitematicArgumentType.getFile(context, "file");
         return 1;
      })));
   }
}
