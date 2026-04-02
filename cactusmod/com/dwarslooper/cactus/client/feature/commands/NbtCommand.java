package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.class_1799;
import net.minecraft.class_2172;
import net.minecraft.class_2487;
import net.minecraft.class_2509;
import net.minecraft.class_2512;
import net.minecraft.class_2520;
import net.minecraft.class_2561;
import net.minecraft.class_9326;

public class NbtCommand extends Command {
   public NbtCommand() {
      super("nbt");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.executes((context) -> {
         assert CactusConstants.mc.field_1724 != null;

         class_1799 stack = CactusConstants.mc.field_1724.method_6047();
         if (stack != null && !stack.method_7960()) {
            class_2487 nbt = new class_2487();
            class_9326 components = stack.method_57380();
            nbt.method_10566("components", (class_2520)class_9326.field_49589.encodeStart(CactusConstants.mc.field_1724.method_56673().method_57093(class_2509.field_11560), components).getOrThrow());
            if (!components.method_57848()) {
               ChatUtils.info(class_2512.method_32270(nbt));
            } else {
               ChatUtils.error((class_2561)class_2561.method_43471("commands.nbt.empty_item"));
            }
         } else {
            ChatUtils.error((class_2561)class_2561.method_43471("commands.nbt.no_item"));
         }

         return 1;
      });
   }
}
