package com.dwarslooper.cactus.client.gui.toast;

import net.minecraft.class_1799;
import net.minecraft.class_370.class_9037;
import net.minecraft.class_372.class_373;

public class ToastIcon {
   public static final ToastIcon WARNING;
   public static final ToastIcon KEYBOARD;
   public static final ToastIcon MOUSE;
   public static final ToastIcon TREE;
   private final Object object;

   protected ToastIcon(Object object) {
      this.object = object;
   }

   protected final Object getObject() {
      return this.object;
   }

   public static ToastIcon fromItemStack(class_1799 itemStack) {
      return new ToastIcon(itemStack);
   }

   static {
      WARNING = new ToastIcon(class_9037.field_47586);
      KEYBOARD = new ToastIcon(class_373.field_2230);
      MOUSE = new ToastIcon(class_373.field_2237);
      TREE = new ToastIcon(class_373.field_2235);
   }
}
