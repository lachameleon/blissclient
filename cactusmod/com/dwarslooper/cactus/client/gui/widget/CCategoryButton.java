package com.dwarslooper.cactus.client.gui.widget;

import com.dwarslooper.cactus.client.feature.module.Category;
import com.dwarslooper.cactus.client.gui.util.CategorySwitcher;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_332.class_12228;
import org.jetbrains.annotations.NotNull;

public class CCategoryButton extends CButtonWidget {
   private final Category category;

   public CCategoryButton(int x, int y, int width, int height, Category category, CategorySwitcher switcher) {
      String var10005 = category.getIcon() != null ? "    " : "";
      super(x, y, width, height, class_2561.method_30163(var10005 + category.getName()), (button) -> {
         switcher.switchToCategory(category);
      });
      this.category = category;
   }

   public Category getCategory() {
      return this.category;
   }

   public void setEnabled(boolean enabled) {
      this.field_22763 = enabled;
   }

   protected void method_75752(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      this.method_75794(context);
      if (this.category.getIcon() != null) {
         context.method_51448().pushMatrix();
         context.method_51448().scale(0.9F, 0.9F);
         context.method_51427(this.category.getIcon(), (int)((float)(this.method_46426() + 4) / 0.9F), (int)((float)(this.method_46427() + 3) / 0.9F));
         context.method_51448().popMatrix();
      }

      this.method_75793(context.method_75787(this, class_12228.field_63850));
   }
}
