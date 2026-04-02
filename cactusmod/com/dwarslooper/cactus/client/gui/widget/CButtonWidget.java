package com.dwarslooper.cactus.client.gui.widget;

import java.util.function.Supplier;
import net.minecraft.class_11909;
import net.minecraft.class_11910;
import net.minecraft.class_2561;
import net.minecraft.class_4185.class_12231;
import net.minecraft.class_4185.class_4241;

public class CButtonWidget extends class_12231 {
   private class_4241 rightClickAction;
   private Supplier<class_2561> textSupplier;

   public CButtonWidget(int x, int y, int width, int height, class_2561 message, class_4241 onPress) {
      super(x, y, width, height, message, onPress, (button) -> {
         return class_2561.method_43473();
      });
   }

   public CButtonWidget(int x, int y, int width, int height, class_2561 message, class_4241 onPress, boolean active) {
      this(x, y, width, height, message, onPress);
      this.field_22763 = active;
   }

   public CButtonWidget(int x, int y, int width, int height, Supplier<class_2561> messageSupplier, class_4241 onPress) {
      super(x, y, width, height, class_2561.method_43473(), onPress, (button) -> {
         return class_2561.method_43473();
      });
      this.textSupplier = messageSupplier;
   }

   public CButtonWidget(int x, int y, int width, int height, Supplier<class_2561> message, class_4241 onPress, boolean active) {
      this(x, y, width, height, message, onPress);
      this.field_22763 = active;
   }

   public class_2561 method_25369() {
      return this.textSupplier == null ? super.method_25369() : (class_2561)this.textSupplier.get();
   }

   public CButtonWidget withRightClickAction(class_4241 action) {
      this.rightClickAction = action;
      return this;
   }

   protected boolean method_25351(class_11910 input) {
      return input.comp_4801() == 0 || input.comp_4801() == 1 && this.rightClickAction != null;
   }

   public void method_25348(class_11909 click, boolean doubled) {
      if (click.method_74245() == 0) {
         this.method_25306(click);
      } else if (click.method_74245() == 1 && this.rightClickAction != null) {
         this.rightClickAction.onPress(this);
      }

   }
}
