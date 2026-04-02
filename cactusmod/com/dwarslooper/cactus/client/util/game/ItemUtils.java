package com.dwarslooper.cactus.client.util.game;

import com.dwarslooper.cactus.client.util.CactusConstants;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.class_124;
import net.minecraft.class_1304;
import net.minecraft.class_156;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2769;
import net.minecraft.class_2873;
import net.minecraft.class_3489;
import net.minecraft.class_9282;
import net.minecraft.class_9288;
import net.minecraft.class_9296;
import net.minecraft.class_9331;
import net.minecraft.class_9334;

public class ItemUtils {
   public static void giveItem(class_1799 itemStack) {
      giveItem(itemStack, CactusConstants.mc.field_1724.method_31548().method_7376());
   }

   public static void giveItem(class_1799 itemStack, int s) {
      assert CactusConstants.mc.field_1724 != null && CactusConstants.mc.method_1562() != null;

      if (s == -1) {
         ChatUtils.error("No free inventory space!");
      }

      CactusConstants.mc.execute(() -> {
         int slot = s + (s < 9 ? 36 : 0);
         if (slot == 45) {
            ++slot;
         }

         CactusConstants.mc.method_1562().method_52787(new class_2873(slot, itemStack));
         CactusConstants.mc.field_1724.field_7498.method_7611(slot).method_53512(itemStack);
         CactusConstants.mc.field_1724.method_31548().method_7381();
      });
   }

   public static boolean trySelectSlot(int slot) {
      if (class_1661.method_7380(slot) && CactusConstants.mc.field_1724 != null) {
         CactusConstants.mc.field_1724.method_31548().method_61496(slot);
         return true;
      } else {
         return false;
      }
   }

   public static class_1799 headOfName(String name) {
      class_1799 stack = new class_1799(class_1802.field_8575);
      class_9296 profileComponent = class_9296.method_74889(name);
      stack.method_57379(class_9334.field_49617, profileComponent);
      return stack;
   }

   public static class_1799 dyeItem(class_1799 stack, Color color) {
      stack.method_57379(class_9334.field_49644, new class_9282(color.getRGB()));
      return stack;
   }

   public static <T, E extends class_9331<T>> void editItemComponent(class_1799 stack, E type, Consumer<T> editor, Supplier<T> defaultSupplier) {
      T component = stack.method_58694(type);
      if (component == null) {
         component = defaultSupplier.get();
      }

      editor.accept(component);
      stack.method_57379(type, component);
   }

   public static List<class_1799> getPlayerArmor() {
      List<class_1799> armor = new ArrayList();
      class_1304[] var1 = class_1304.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         class_1304 equipmentSlot = var1[var3];
         if (equipmentSlot.method_46643()) {
            armor.add(CactusConstants.mc.field_1724.method_31548().field_56552.method_66659(equipmentSlot));
         }
      }

      return armor;
   }

   public static boolean hasItems(class_1799 itemStack) {
      class_9288 compoundTag = (class_9288)itemStack.method_58695(class_9334.field_49622, class_9288.field_49334);
      return compoundTag.method_59712().findAny().isPresent();
   }

   public static String propertyToFormattedString(class_2769<?> property, Comparable<?> comparable) {
      String string = class_156.method_650(property, comparable);
      String var10000;
      if (Boolean.TRUE.equals(comparable)) {
         var10000 = String.valueOf(class_124.field_1060);
         string = var10000 + string;
      } else if (Boolean.FALSE.equals(comparable)) {
         var10000 = String.valueOf(class_124.field_1061);
         string = var10000 + string;
      } else {
         var10000 = String.valueOf(class_124.field_1065);
         string = var10000 + string;
      }

      var10000 = String.valueOf(class_124.field_1080);
      return var10000 + property.method_11899() + ": " + string;
   }

   public static boolean isArmor(class_1799 itemStack) {
      return itemStack.method_31573(class_3489.field_48297) || itemStack.method_31573(class_3489.field_48296) || itemStack.method_31573(class_3489.field_48295) || itemStack.method_31573(class_3489.field_48294);
   }
}
