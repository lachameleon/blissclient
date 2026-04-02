package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.feature.command.arguments.FileArgumentType;
import com.dwarslooper.cactus.client.userscript.ScriptExecutable;
import com.dwarslooper.cactus.client.userscript.Userscript;
import com.dwarslooper.cactus.client.userscript.UserscriptManager;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import net.minecraft.class_2172;

public class UserscriptCommand extends Command {
   public static File dir;

   public UserscriptCommand() {
      super("script");
      if (!dir.exists()) {
         dir.mkdirs();
      }

   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.then(literal("load").then(argument("name", FileArgumentType.file(dir)).executes((context) -> {
         File script = FileArgumentType.getFile(context, "name");

         try {
            UserscriptManager.load(script);
         } catch (FileNotFoundException var3) {
            CactusClient.getLogger().error("Unknown file for userscript", var3);
         }

         return 1;
      })))).then(literal("unload").then(argument("name", FileArgumentType.file(dir)).executes((context) -> {
         File script = FileArgumentType.getFile(context, "name");
         UserscriptManager.unload(script.getName());
         return 1;
      })))).then(literal("run").then(argument("name", FileArgumentType.file(dir)).executes((context) -> {
         File script = FileArgumentType.getFile(context, "name");
         Userscript userscript = (Userscript)UserscriptManager.getScripts().get(script.getName());
         if (userscript != null) {
            if (userscript instanceof ScriptExecutable) {
               userscript.runCovered();
            } else {
               ChatUtils.error("This script can't be ran by command!");
            }
         }

         return 1;
      })));
   }

   static {
      dir = new File(CactusConstants.DIRECTORY, "custom");
   }
}
