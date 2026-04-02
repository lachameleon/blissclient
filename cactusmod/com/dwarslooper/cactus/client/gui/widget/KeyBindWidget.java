package com.dwarslooper.cactus.client.gui.widget;

import com.dwarslooper.cactus.client.systems.key.KeyBind;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_5250;
import net.minecraft.class_4185.class_4241;
import net.minecraft.class_4185.class_7840;
import org.jetbrains.annotations.NotNull;

public class KeyBindWidget extends CButtonWidget {
   private KeyBind keyBind;
   private Consumer<KeyBind> callback;

   public KeyBindWidget(int x, int y, int width, int height, @NotNull KeyBind value) {
      super(x, y, width, height, (class_2561)class_2561.method_43473(), (button) -> {
      });
      this.keyBind = value;
   }

   public KeyBindWidget(int x, int y, int width, int height, @NotNull KeyBind value, Consumer<KeyBind> callback) {
      this(x, y, width, height, value);
      this.callback = callback;
   }

   @NotNull
   public class_2561 method_25369() {
      class_5250 t = class_2561.method_43469("systems.keybindValue", new Object[]{this.keyBind.getDisplay()});
      return this.method_25370() ? class_2561.method_43470("§e> ").method_10852(t.method_27692(class_124.field_1073)).method_27693(" §e<") : t;
   }

   public boolean onKey(int keyCode) {
      if (this.field_22763 && this.field_22764) {
         this.keyBind = keyCode == 256 ? KeyBind.none() : KeyBind.of(keyCode);
         if (this.callback != null) {
            this.callback.accept(this.keyBind);
         }

         this.method_25365(false);
         return true;
      } else {
         return false;
      }
   }

   public boolean useOrElse(int keyCode, Supplier<Boolean> other) {
      return this.method_25370() ? this.onKey(keyCode) : (Boolean)other.get();
   }

   public KeyBind getKeyBind() {
      return this.keyBind;
   }

   public Consumer<KeyBind> getCallback() {
      return this.callback;
   }

   public static class Builder extends class_7840 {
      public Builder(class_2561 message, class_4241 onPress) {
         super(message, onPress);
      }
   }
}
