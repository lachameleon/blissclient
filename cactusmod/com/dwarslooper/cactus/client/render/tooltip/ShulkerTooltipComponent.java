package com.dwarslooper.cactus.client.render.tooltip;

import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import java.awt.Color;
import java.util.Iterator;
import net.minecraft.class_10799;
import net.minecraft.class_1799;
import net.minecraft.class_2371;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_5632;
import net.minecraft.class_5684;

public class ShulkerTooltipComponent implements class_5684, class_5632 {
   private static final class_2960 CONTAINER_BACKGROUND = class_2960.method_60655("cactus", "container_background");
   private final class_2371<class_1799> itemStacks;
   private final Color color;

   public ShulkerTooltipComponent(class_2371<class_1799> itemStacks, Color color) {
      this.itemStacks = itemStacks;
      this.color = color;
   }

   public int method_32661(class_327 textRenderer) {
      return (int)(14.0D + 18.0D * Math.max(1.0D, Math.ceil((double)this.itemStacks.size() / 9.0D))) + 4;
   }

   public int method_32664(class_327 textRenderer) {
      return 14 + 18 * class_3532.method_15340(this.itemStacks.size(), 1, 9);
   }

   public void method_32666(class_327 textRenderer, int x, int y, int width, int height, class_332 context) {
      context.method_52707(class_10799.field_56883, CONTAINER_BACKGROUND, x, y, this.method_32664(textRenderer), this.method_32661(textRenderer) - 4, this.color.getRGB());
      int row = 0;
      int i = 0;
      Iterator var9 = this.itemStacks.iterator();

      while(var9.hasNext()) {
         class_1799 itemStack = (class_1799)var9.next();
         RenderUtils.drawItemWithMeta(context, itemStack, x + 8 + i * 18, y + 8 + row * 18);
         ++i;
         if (i >= 9) {
            i = 0;
            ++row;
         }
      }

   }
}
