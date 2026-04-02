package com.dwarslooper.cactus.client.systems.config.settings.impl;

import com.google.gson.JsonObject;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_342;
import org.jetbrains.annotations.NotNull;

public class StringSetting extends Setting<String> {
   int minLength = 0;
   int maxLength = 32;

   public StringSetting(String name, String value) {
      super(name, value);
   }

   public StringSetting setMinLength(int min) {
      this.minLength = min;
      return this;
   }

   public StringSetting setMaxLength(int max) {
      this.maxLength = max;
      return this;
   }

   public int getMinLength() {
      return this.minLength;
   }

   public int getMaxLength() {
      return this.maxLength;
   }

   public void set(String value) {
      if (value.length() >= this.minLength && value.length() <= this.maxLength) {
         super.set(value);
      }
   }

   public String get() {
      return (String)super.get();
   }

   public void save(JsonObject object) {
      object.addProperty("value", this.get());
   }

   public String load(JsonObject object) {
      this.set(object.get("value").getAsString());
      return this.get();
   }

   public class_339 buildWidget() {
      return new StringSetting.Widget();
   }

   public class Widget extends Setting<String>.Widget {
      private final class_342 widget;

      public Widget() {
         super();
         this.widget = new class_342(this.textRenderer, this.widgetWidth, 20, class_2561.method_43473());
         this.widget.method_1880(StringSetting.this.getMaxLength());
         this.widget.method_1852(StringSetting.this.get());
         this.widget.method_1887(this.widget.method_1882().isEmpty() ? StringSetting.this.name() : "");
         this.widget.method_1863((s) -> {
            this.widget.method_1887(s.isEmpty() ? StringSetting.this.name() : "");
            StringSetting.this.set(s);
         });
         this.widget.method_1870(false);
         this.widget.method_1872(false);
      }

      public void wrappedRender(class_332 context, int mouseX, int mouseY, float delta) {
         this.widget.method_25358(this.widgetWidth);
         context.method_51448().pushMatrix();
         context.method_51448().translate((float)(-this.method_46426()), (float)(-this.method_46427()));
         this.widget.method_48229(this.method_46426() + this.field_22758 - this.widget.method_25368(), this.method_46427());
         this.widget.method_25394(context, mouseX, mouseY, delta);
         context.method_51448().popMatrix();
      }

      public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
         if (this.widget.method_25370() && click.comp_4798() > (double)(this.method_46426() + this.widgetPosX())) {
            return this.widget.method_25402(click, doubled);
         } else if (super.method_25402(click, doubled)) {
            this.widget.method_25365(true);
            return true;
         } else {
            return false;
         }
      }

      public boolean method_25404(@NotNull class_11908 input) {
         return this.widget.method_25404(input) || super.method_25404(input);
      }

      public boolean method_25400(@NotNull class_11905 input) {
         return this.widget.method_25400(input) || super.method_25400(input);
      }

      public void method_25365(boolean focused) {
         this.widget.method_25365(focused);
         super.method_25365(focused);
      }
   }
}
