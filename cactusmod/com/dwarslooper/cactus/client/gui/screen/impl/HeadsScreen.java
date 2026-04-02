package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ItemUtils;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_1277;
import net.minecraft.class_1657;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_3917;
import net.minecraft.class_465;
import net.minecraft.class_746;
import org.jetbrains.annotations.NotNull;

public class HeadsScreen extends class_465<HeadsScreen.HeadsScreenHandler> {
   private static final class_1277 INVENTORY = new class_1277(150);

   public HeadsScreen() {
      super(new HeadsScreen.HeadsScreenHandler(), ((class_746)Objects.requireNonNull(CactusConstants.mc.field_1724)).method_31548(), class_2561.method_43470("Haha"));
   }

   protected void method_25426() {
      this.field_2776 = 0;
      this.field_2800 = 0;
   }

   protected void method_2389(@NotNull class_332 context, float delta, int mouseX, int mouseY) {
   }

   protected void method_2385(class_332 context, class_1735 slot, int mouseX, int mouseY) {
      context.method_25294(slot.field_7873, slot.field_7872, slot.field_7873 + 16, slot.field_7872 + 16, -1426063361);
      super.method_2385(context, slot, mouseX, mouseY);
   }

   public static class HeadsScreenHandler extends class_1703 {
      protected HeadsScreenHandler() {
         super((class_3917)null, 0);

         assert CactusConstants.mc.field_1724 != null;

         CactusConstants.mc.field_1724.field_7512 = this;

         int i;
         for(i = 0; i < 15; ++i) {
            for(int j = 0; j < 10; ++j) {
               int index = i * 10 + j;
               this.method_7621(new class_1735(HeadsScreen.INVENTORY, index, 40 + i * 20, 40 + j * 20));
            }
         }

         for(i = 0; i < this.field_7761.size(); ++i) {
            List<class_1799> items = HeadBrowserScreen.getAllItems();
            if (i < items.size()) {
               this.method_7611(i).method_53512((class_1799)items.get(i));
            }
         }

      }

      public void method_7593(int slotIndex, int button, @NotNull class_1713 actionType, @NotNull class_1657 player) {
         if (slotIndex >= 0 && slotIndex < this.field_7761.size()) {
            class_1735 s = this.method_7611(slotIndex);
            if (s.method_7677() != class_1799.field_8037) {
               ItemUtils.giveItem(s.method_7677());
            }

         }
      }

      public boolean method_7615(@NotNull class_1735 slot) {
         return false;
      }

      @NotNull
      public class_1799 method_7601(@NotNull class_1657 player, int slot) {
         return class_1799.field_8037;
      }

      public boolean method_7597(@NotNull class_1657 player) {
         return true;
      }
   }
}
