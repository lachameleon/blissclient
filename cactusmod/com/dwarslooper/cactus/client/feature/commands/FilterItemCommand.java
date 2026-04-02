package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.dwarslooper.cactus.client.util.game.ItemUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2172;
import net.minecraft.class_2287;
import net.minecraft.class_2371;
import net.minecraft.class_2561;
import net.minecraft.class_7923;
import net.minecraft.class_9288;
import net.minecraft.class_9334;

public class FilterItemCommand extends Command {
   public FilterItemCommand() {
      super("filteritem");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      ((LiteralArgumentBuilder)builder.then(literal("item").then(argument("item", class_2287.method_9776(commandRegistryAccess)).executes((context) -> {
         class_1799 stack = class_2287.method_9777(context, "item").method_9785().method_7854();
         ItemUtils.giveItem(this.buildItem(stack));
         return 1;
      })))).then(literal("hand").executes((context) -> {
         if (CactusConstants.mc.field_1724.method_6047().method_7960()) {
            ChatUtils.error(this.getTranslatableElement("noItem", new Object[0]));
            return 1;
         } else {
            ItemUtils.giveItem(this.buildItem(CactusConstants.mc.field_1724.method_6047()));
            return 1;
         }
      }));
   }

   private class_1799 buildItem(class_1799 stack) {
      class_1799 filterItem = new class_1799(class_1802.field_8239);
      List<class_1799> stacks = class_2371.method_37434(5);
      stacks.add(stack);
      class_1799 FILTER = new class_1799(class_1802.field_8600);
      FILTER.method_57379(class_9334.field_49631, class_2561.method_43470("「\ud83c\udf35filter\ud83c\udf35」"));

      for(int i = 0; i < 4; ++i) {
         stacks.add(FILTER.method_46651(i == 0 ? 18 : 1));
      }

      filterItem.method_57379(class_9334.field_49622, class_9288.method_57493(stacks));
      filterItem.method_57379(class_9334.field_49631, class_2561.method_43470("Filter (%s)".formatted(new Object[]{class_7923.field_41178.method_10221(stack.method_7909()).method_12832()})));
      return filterItem;
   }
}
