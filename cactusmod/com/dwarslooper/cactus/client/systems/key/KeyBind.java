package com.dwarslooper.cactus.client.systems.key;

import com.dwarslooper.cactus.client.util.CactusConstants;
import net.minecraft.class_3675;

public record KeyBind(int key) {
   public KeyBind(int key) {
      this.key = key;
   }

   public boolean isBound() {
      return this.key != unsetValue();
   }

   public String getDisplay() {
      return this.isBound() ? KeybindManager.getKeyName(this.key) : "None";
   }

   public static KeyBind none() {
      return of(Integer.MAX_VALUE);
   }

   private static int unsetValue() {
      return Integer.MAX_VALUE;
   }

   public static KeyBind of(int key) {
      return new KeyBind(key);
   }

   public static boolean isBound(int key) {
      return false;
   }

   public boolean isPressed() {
      return class_3675.method_15987(CactusConstants.mc.method_22683(), this.key);
   }

   public int key() {
      return this.key;
   }
}
