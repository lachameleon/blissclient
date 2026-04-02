package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.feature.modules.util.AutoCrafter;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Iterator;
import net.minecraft.class_2172;
import net.minecraft.class_2287;

public class AutoCraftCommand extends Command {
   public AutoCraftCommand() {
      super("autocraft");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.requires((commandSource) -> {
         return !CactusConstants.mc.field_1724.method_7325();
      })).then(literal("reset").executes((context) -> {
         AutoCrafter.currentPath = null;
         return 1;
      }))).then(argument("item", class_2287.method_9776(commandRegistryAccess)).then(argument("amount", IntegerArgumentType.integer(1)).executes((context) -> {
         AutoCrafter.AutoCrafterPath path = AutoCrafter.AutoCrafterPath.generatePath(class_2287.method_9777(context, "item").method_9785(), IntegerArgumentType.getInteger(context, "amount"));
         this.logComp(path, "");
         AutoCrafter.currentPath = path;
         return 1;
      })));
   }

   private void logComp(AutoCrafter.AutoCrafterPath path, String pre) {
      ChatUtils.info(pre + " > " + String.valueOf(path.getType()) + " (" + path.getCount() + ")");
      Iterator var3 = path.getChildren().iterator();

      while(var3.hasNext()) {
         AutoCrafter.AutoCrafterPath sp = (AutoCrafter.AutoCrafterPath)var3.next();
         this.logComp(sp, pre + "  ");
      }

   }
}
