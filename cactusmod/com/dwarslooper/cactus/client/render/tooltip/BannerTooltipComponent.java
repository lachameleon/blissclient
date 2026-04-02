package com.dwarslooper.cactus.client.render.tooltip;

import com.dwarslooper.cactus.client.render.tooltip.element.BannerFlagGuiElementRenderState;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.google.common.collect.ImmutableList;
import net.minecraft.class_10377;
import net.minecraft.class_1746;
import net.minecraft.class_1767;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2582;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5602;
import net.minecraft.class_5632;
import net.minecraft.class_5684;
import net.minecraft.class_6862;
import net.minecraft.class_6880;
import net.minecraft.class_7871;
import net.minecraft.class_7924;
import net.minecraft.class_9307;
import net.minecraft.class_9334;
import net.minecraft.class_9307.class_9308;

public class BannerTooltipComponent implements class_5684, class_5632 {
   private final class_1799 stack;
   private final class_10377 bannerField;
   private final class_7871<class_2582> bannerPatternLookup;

   public BannerTooltipComponent(class_1799 stack) {
      this.bannerField = new class_10377(CactusConstants.mc.method_31974().method_32072(class_5602.field_55122));
      this.bannerPatternLookup = CactusConstants.mc.field_1724.method_56673().method_30530(class_7924.field_41252);
      if (stack.method_57826(class_9334.field_56398)) {
         this.stack = this.getBannerFromPattern(stack);
      } else {
         if (!(stack.method_7909() instanceof class_1746)) {
            throw new IllegalArgumentException("Item is not a Banner or Banner Pattern");
         }

         this.stack = stack;
      }

   }

   public class_1799 getBannerFromPattern(class_1799 item) {
      class_6862<class_2582> bannerComponent = (class_6862)item.method_58694(class_9334.field_56398);
      if (!item.method_7960() && bannerComponent != null) {
         class_1799 banner = new class_1799(class_1802.field_8572);
         ImmutableList<class_6880<class_2582>> registryEntries = (ImmutableList)this.bannerPatternLookup.method_46733(bannerComponent).map(ImmutableList::copyOf).orElse(ImmutableList.of());
         class_1767 dyeColor = class_1767.field_7961;
         if (!registryEntries.isEmpty()) {
            banner.method_57379(class_9334.field_49619, new class_9307(registryEntries.stream().map((bpre) -> {
               return new class_9308(bpre, dyeColor);
            }).toList()));
         }

         return banner;
      } else {
         throw new NullPointerException("Banner Pattern is null");
      }
   }

   public int method_32661(class_327 textRenderer) {
      return 48;
   }

   public int method_32664(class_327 textRenderer) {
      return 20;
   }

   public void method_32666(class_327 textRenderer, int x, int y, int width, int height, class_332 context) {
      context.field_59826.method_70922(new BannerFlagGuiElementRenderState(this.bannerField, ((class_1746)this.stack.method_7909()).method_7706(), (class_9307)this.stack.method_58694(class_9334.field_49619), x, y + 4, x + this.method_32664((class_327)null), y + this.method_32661((class_327)null) - 4, context.field_44659.method_70863(), 16.0F));
   }
}
