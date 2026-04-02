package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.dwarslooper.cactus.client.util.game.ItemUtils;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_11580;
import net.minecraft.class_1263;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2172;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2343;
import net.minecraft.class_2487;
import net.minecraft.class_2561;
import net.minecraft.class_2586;
import net.minecraft.class_2591;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3962;
import net.minecraft.class_7923;
import net.minecraft.class_9275;
import net.minecraft.class_9288;
import net.minecraft.class_9334;
import org.jetbrains.annotations.Nullable;

public class SignalStrengthBlockCommand extends Command {
   public SignalStrengthBlockCommand() {
      super("ssb");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      assert CactusConstants.mc.field_1724 != null;

      ((LiteralArgumentBuilder)builder.requires((commandSource) -> {
         return CactusConstants.mc.field_1724.method_68878();
      })).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)argument("signal", IntegerArgumentType.integer(0, 1779)).executes((context) -> {
         this.giveItem(context, false);
         return 1;
      })).then(literal("HAND").executes((context) -> {
         this.giveItem(context, true);
         return 1;
      }))).then(literal("AUTO").executes((context) -> {
         this.giveItem(context, false);
         return 1;
      })));
   }

   private void giveItem(CommandContext<class_2172> context, boolean useAndReplaceHand) {
      class_1747 item = null;
      if (useAndReplaceHand) {
         class_1792 var5 = CactusConstants.mc.field_1724.method_6047().method_7909();
         if (!(var5 instanceof class_1747)) {
            ChatUtils.error(this.getTranslatableElement("noBlock", new Object[0]));
            return;
         }

         class_1747 blockItem = (class_1747)var5;
         item = blockItem;
      }

      class_1799 stack = this.buildItem(item, IntegerArgumentType.getInteger(context, "signal"));
      if (stack != null) {
         try {
            if (useAndReplaceHand) {
               assert class_310.method_1551().field_1724 != null;

               ItemUtils.giveItem(stack, class_310.method_1551().field_1724.method_31548().method_67532());
            } else {
               ItemUtils.giveItem(stack);
            }
         } catch (Exception var6) {
            CactusClient.getLogger().error("Can't find player to give item to", var6);
         }

      }
   }

   private class_1799 buildItem(@Nullable class_1747 blockItem, int signalStrength) {
      class_1792 item = signalStrength > 15 ? class_1802.field_8876 : class_1802.field_8600;
      if (blockItem == null) {
         if (signalStrength <= 8 && signalStrength != 7) {
            blockItem = (class_1747)class_1802.field_17530;
         } else if (signalStrength > 897) {
            blockItem = (class_1747)class_1802.field_8866;
         } else {
            blockItem = (class_1747)class_1802.field_16307;
         }
      }

      class_1799 stack = new class_1799(blockItem);
      class_2487 tag;
      if (blockItem == class_1802.field_17530) {
         if (signalStrength == 7 || signalStrength > 8 || signalStrength < 0) {
            ChatUtils.error(this.getTranslatableElement("invalidComposter", new Object[0]));
         }

         tag = new class_2487();
         tag.method_10569("level", signalStrength);
         stack.method_57379(class_9334.field_49623, class_9275.field_49284.method_57420(class_3962.field_17565, signalStrength));
      } else if (blockItem == class_1802.field_8866) {
         if (signalStrength < 0) {
            ChatUtils.error(this.getTranslatableElement("invalidCommandblock", new Object[0]));
         }

         tag = new class_2487();
         tag.method_10569("SuccessCount", signalStrength);
         tag.method_10567("auto", (byte)1);
         tag.method_10567("powered", (byte)1);
         tag.method_10582("id", "minecraft:command_block");
         stack.method_57379(class_9334.field_49611, class_11580.method_72535(class_2591.field_11904, tag));
      } else {
         class_2248 var7 = blockItem.method_7711();
         if (!(var7 instanceof class_2343)) {
            ChatUtils.error(this.getTranslatableElement("noInventory", new Object[0]));
            return null;
         }

         class_2343 blockEntityProvider = (class_2343)var7;
         class_2586 var8 = blockEntityProvider.method_10123(class_2338.field_10980, blockItem.method_7711().method_9564());
         if (!(var8 instanceof class_1263)) {
            ChatUtils.error(this.getTranslatableElement("noProvider", new Object[0]));
            return null;
         }

         class_1263 inventoryProvider = (class_1263)var8;
         int slots = inventoryProvider.method_5439();
         int stackSize = signalStrength > 15 ? 64 : item.method_7882();
         int itemsNeeded = Math.max(0, signalStrength == 1 ? 1 : (int)Math.ceil((double)(slots * (signalStrength - 1)) / 14.0D * (double)item.method_7882()));
         String itemId = class_7923.field_41178.method_10221(item).toString();
         float f = (float)itemsNeeded / (float)item.method_7882();
         int calculatedSignal = class_3532.method_15375(f / (float)slots * 14.0F) + (itemsNeeded > 0 ? 1 : 0);
         if (calculatedSignal != signalStrength) {
            ChatUtils.error(this.getTranslatableElement("notAchievable", new Object[0]));
         }

         List<class_1799> stacks = new ArrayList();

         for(int count = itemsNeeded; count > 0; count -= stackSize) {
            stacks.add(new class_1799(item, Math.min(stackSize, count)));
         }

         stack.method_57379(class_9334.field_49622, class_9288.method_57493(stacks));
      }

      stack.method_57379(class_9334.field_49631, class_2561.method_43470("§c" + signalStrength));
      return stack;
   }
}
