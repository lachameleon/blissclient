package com.dwarslooper.cactus.client.gui.util;

import com.dwarslooper.cactus.client.feature.module.Category;
import com.dwarslooper.cactus.client.gui.widget.CCategoryButton;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_339;

public abstract class CategorySwitcher {
   private final List<CCategoryButton> categoryButtons = new ArrayList();
   private Category activeCategory;
   private int x;
   private int y;

   public CategorySwitcher(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public CCategoryButton createButton(Category category) {
      int width = CactusConstants.mc.field_1772.method_1727(category.getName()) + (category.getIcon() == null ? 16 : 32);
      CCategoryButton button = new CCategoryButton(this.x, this.y, width, 20, category, this);
      this.categoryButtons.add(button);
      this.x += width;
      return button;
   }

   public void switchToCategory(Category category) {
      Iterator var2 = this.categoryButtons.iterator();

      while(var2.hasNext()) {
         CCategoryButton button = (CCategoryButton)var2.next();
         Category buttonCategory = button.getCategory();
         boolean isActiveCategory = buttonCategory == category;
         button.setEnabled(!isActiveCategory);
         if (isActiveCategory) {
            this.activeCategory = category;
         }
      }

      this.updateScreenBasedOnCategory(category);
   }

   public Category getActiveCategory() {
      return this.activeCategory;
   }

   public List<CCategoryButton> getCategoryButtons() {
      return this.categoryButtons;
   }

   public int getTotalButtonWidth() {
      return this.categoryButtons.stream().mapToInt(class_339::method_25368).sum();
   }

   public abstract void updateScreenBasedOnCategory(Category var1);
}
