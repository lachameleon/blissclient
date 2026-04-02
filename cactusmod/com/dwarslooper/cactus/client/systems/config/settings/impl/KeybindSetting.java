package com.dwarslooper.cactus.client.systems.config.settings.impl;

import com.dwarslooper.cactus.client.systems.key.KeyBind;
import com.google.gson.JsonObject;
import java.util.Objects;
import net.minecraft.class_10799;
import net.minecraft.class_11908;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_5250;

public class KeybindSetting extends Setting<KeyBind> {
   public KeybindSetting(String id, KeyBind value) {
      super(id, value);
   }

   public void save(JsonObject object) {
      object.addProperty("key", ((KeyBind)this.get()).key());
   }

   public KeyBind load(JsonObject object) {
      this.set(KeyBind.of(object.get("key").getAsInt()));
      return (KeyBind)this.get();
   }

   public boolean isPressed() {
      return ((KeyBind)this.get()).isPressed();
   }

   public class_339 buildWidget() {
      return new KeybindSetting.Widget();
   }

   public class Widget extends Setting<KeyBind>.Widget {
      private static final class_2960 TEXTURE = class_2960.method_60654("widget/button");

      public Widget() {
         super();
      }

      public void wrappedRender(class_332 context, int mouseX, int mouseY, float delta) {
         context.method_52706(class_10799.field_56883, TEXTURE, this.field_22758 - this.widgetWidth, 0, this.widgetWidth, 20);
         this.drawOverlay(context, this.field_22758 - this.widgetWidth, this.widgetWidth);
         class_327 var10001 = this.textRenderer;
         class_2561 var10002 = this.getDisplayText();
         int var10003 = this.field_22758 - this.widgetWidth + 4;
         int var10004 = this.method_25364();
         Objects.requireNonNull(this.textRenderer);
         context.method_27535(var10001, var10002, var10003, (var10004 - 9) / 2 + 1, -1);
      }

      private class_2561 getDisplayText() {
         class_5250 display = class_2561.method_43470(((KeyBind)KeybindSetting.this.get()).getDisplay());
         return this.method_25370() ? class_2561.method_43470("§e> ").method_10852(display.method_27692(class_124.field_1073)).method_27693(" §e<") : display;
      }

      public boolean method_25404(class_11908 input) {
         if (input.method_74231()) {
            KeybindSetting.this.set(KeyBind.none());
         } else {
            KeybindSetting.this.set(KeyBind.of(input.comp_4795()));
         }

         this.method_25365(false);
         return true;
      }
   }
}
