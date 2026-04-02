package com.dwarslooper.cactus.client.systems.config.settings.impl;

import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import com.dwarslooper.cactus.client.util.generic.TextUtils;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.class_10799;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_342;
import net.minecraft.class_3532;
import net.minecraft.class_5250;
import org.jetbrains.annotations.NotNull;

public class IntegerSetting extends Setting<Integer> {
   private IntegerSetting.EditorStyle editorStyle;
   private int min;
   private int max;
   private int sliderMin;
   private int sliderMax;

   public IntegerSetting(String name, int value) {
      this(name, value, IntegerSetting.EditorStyle.Slider);
   }

   public IntegerSetting(String name, int value, IntegerSetting.EditorStyle editorStyle) {
      super(name, value);
      this.min = 0;
      this.max = 200;
      this.sliderMin = this.min;
      this.sliderMax = this.max;
      this.editorStyle = editorStyle;
   }

   public IntegerSetting(String name, int value, Function<Integer, String> textGetter) {
      this(name, value, IntegerSetting.EditorStyle.Slider, textGetter);
   }

   public IntegerSetting(String name, int value, IntegerSetting.EditorStyle editorStyle, Function<Integer, String> textGetter) {
      super(name, value, textGetter);
      this.min = 0;
      this.max = 200;
      this.sliderMin = this.min;
      this.sliderMax = this.max;
      this.editorStyle = editorStyle;
   }

   /** @deprecated */
   @Deprecated(
      since = "0.12.1",
      forRemoval = true
   )
   public IntegerSetting setMin(int min) {
      return this.min(min);
   }

   /** @deprecated */
   @Deprecated(
      since = "0.12.1",
      forRemoval = true
   )
   public IntegerSetting setMax(int max) {
      return this.max(max);
   }

   public IntegerSetting min(int min) {
      this.min = min;
      return this;
   }

   public IntegerSetting max(int max) {
      this.max = max;
      return this;
   }

   public IntegerSetting sliderMin(int min) {
      this.sliderMin = min;
      return this;
   }

   public IntegerSetting sliderMax(int max) {
      this.sliderMax = max;
      return this;
   }

   public void setEditorStyle(IntegerSetting.EditorStyle editorStyle) {
      this.editorStyle = editorStyle;
   }

   public int getMin() {
      return this.min;
   }

   public int getMax() {
      return this.max;
   }

   public int getSliderMin() {
      return Math.max(this.sliderMin, this.min);
   }

   public int getSliderMax() {
      return Math.min(this.sliderMax, this.max);
   }

   public IntegerSetting.EditorStyle getEditorStyle() {
      return this.editorStyle;
   }

   public void set(Integer value) {
      super.set(class_3532.method_15340(value, this.min, this.max));
   }

   public Integer get() {
      return (Integer)super.get();
   }

   public void save(JsonObject object) {
      object.addProperty("editor", this.getEditorStyle().name());
      object.addProperty("value", this.get());
   }

   public Integer load(JsonObject object) {
      if (object.has("editor")) {
         this.setEditorStyle(IntegerSetting.EditorStyle.valueOf(object.get("editor").getAsString()));
      }

      this.set(object.get("value").getAsInt());
      return this.get();
   }

   public class_339 buildWidget() {
      return new IntegerSetting.Widget();
   }

   public static enum EditorStyle {
      Slider,
      Input;

      // $FF: synthetic method
      private static IntegerSetting.EditorStyle[] $values() {
         return new IntegerSetting.EditorStyle[]{Slider, Input};
      }
   }

   public class Widget extends Setting<Integer>.Widget {
      public int value = IntegerSetting.this.get();
      public boolean isSlider;
      private final class_342 widget;
      private boolean sliderDown;

      public Widget() {
         super();
         this.isSlider = IntegerSetting.this.getEditorStyle() == IntegerSetting.EditorStyle.Slider;
         this.widget = new class_342(this.textRenderer, this.widgetWidth, 20, class_2561.method_43473());
         this.widget.method_1890((s) -> {
            return TextUtils.isNumeric(s, true, true);
         });
         this.widget.method_1852(IntegerSetting.this.get().toString());
         this.widget.method_1863((s) -> {
            boolean invalid = s.isEmpty() || s.equals("-");
            int i = Integer.MIN_VALUE;

            try {
               i = Integer.parseInt(s);
            } catch (NumberFormatException var5) {
            }

            if (i >= IntegerSetting.this.getMin() && i <= IntegerSetting.this.getMax() && !invalid) {
               this.widget.method_1868(-2039584);
               this.setValue(i);
            } else {
               this.widget.method_1868(-43691);
            }

         });
         this.widget.method_1870(false);
         this.widget.method_1872(false);
      }

      public class_2960 getHandleTexture(int mouseX) {
         return RenderUtils.SLIDER_HANDLE_TEXTURES.method_52729(true, this.field_22762 && mouseX > this.method_46426() + this.widgetPosX());
      }

      public void wrappedRender(class_332 context, int mouseX, int mouseY, float delta) {
         class_327 var10001 = this.textRenderer;
         class_5250 var10002 = class_2561.method_43470(this.isSlider ? "↔" : "T").method_27692(this.isToggleHovered((double)mouseX, (double)mouseY) ? class_124.field_1068 : class_124.field_1080);
         int var10003 = this.widgetPosX() - 8;
         int var10004 = this.method_25364();
         Objects.requireNonNull(this.textRenderer);
         context.method_27534(var10001, var10002, var10003, (var10004 - 9) / 2 + 1, -1);
         if (this.isSlider) {
            context.method_52706(class_10799.field_56883, RenderUtils.SLIDER_TEXTURES.comp_1604(), this.widgetPosX(), 0, this.widgetWidth, this.method_25364());
            context.method_52706(class_10799.field_56883, this.getHandleTexture(mouseX), this.widgetPosX() + (int)((double)(class_3532.method_15340(this.value, IntegerSetting.this.getSliderMin(), IntegerSetting.this.getSliderMax()) - IntegerSetting.this.getSliderMin()) / (double)this.sliderDifference() * (double)(this.widgetWidth - 8)), 0, 8, this.field_22759);
            var10001 = this.textRenderer;
            String var5 = IntegerSetting.this.getText();
            var10003 = this.field_22758 - this.widgetWidth / 2;
            var10004 = this.method_25364();
            Objects.requireNonNull(this.textRenderer);
            context.method_25300(var10001, var5, var10003, (var10004 - 9) / 2 + 1, Color.WHITE.getRGB());
         } else {
            this.widget.method_25358(this.widgetWidth);
            context.method_51448().pushMatrix();
            context.method_51448().translate((float)(-this.method_46426()), (float)(-this.method_46427()));
            this.widget.method_48229(this.method_46426() + this.field_22758 - this.widget.method_25368(), this.method_46427());
            this.widget.method_25394(context, mouseX, mouseY, delta);
            context.method_51448().popMatrix();
         }

      }

      protected void method_25349(@NotNull class_11909 click, double offsetX, double offsetY) {
         if (this.isSlider && this.sliderDown) {
            this.setValueFromMouse(click.comp_4798());
         }

         super.method_25349(click, offsetX, offsetY);
      }

      public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
         if (!this.isSlider && this.widget.method_25370() && click.comp_4798() > (double)(this.method_46426() + this.widgetPosX())) {
            return this.widget.method_25402(click, doubled);
         } else {
            if (this.isToggleHovered(click.comp_4798(), click.comp_4799())) {
               this.isSlider = !this.isSlider;
               this.widget.method_1852(IntegerSetting.this.get().toString());
               IntegerSetting.this.setEditorStyle(this.isSlider ? IntegerSetting.EditorStyle.Slider : IntegerSetting.EditorStyle.Input);
            }

            if (super.method_25402(click, doubled)) {
               if (!this.isSlider) {
                  this.widget.method_25365(true);
               } else if (click.comp_4798() >= (double)(this.method_46426() + this.widgetPosX())) {
                  this.sliderDown = true;
                  this.setValueFromMouse(click.comp_4798());
               }

               return true;
            } else {
               return false;
            }
         }
      }

      public boolean method_25406(@NotNull class_11909 click) {
         this.sliderDown = false;
         return super.method_25406(click);
      }

      public boolean method_25404(@NotNull class_11908 input) {
         if (!this.isSlider || input.comp_4795() != 262 && input.comp_4795() != 263) {
            return this.widget.method_25404(input) || super.method_25404(input);
         } else {
            this.setValue(this.value + (input.comp_4795() == 262 ? 1 : -1) * (input.method_74239() ? 10 : 1));
            return true;
         }
      }

      public boolean method_25400(@NotNull class_11905 input) {
         return !this.isSlider && this.widget.method_25400(input) || super.method_25400(input);
      }

      public void method_25365(boolean focused) {
         this.widget.method_25365(focused);
         super.method_25365(focused);
      }

      private boolean isToggleHovered(double mouseX, double mouseY) {
         return mouseX >= (double)(this.method_46426() + this.widgetPosX() - 16) && mouseX < (double)(this.method_46426() + this.widgetPosX()) && mouseY >= (double)this.method_46427() && mouseY < (double)(this.method_46427() + this.method_25364());
      }

      private void setValueFromMouse(double mouseX) {
         int value = (int)((double)IntegerSetting.this.getSliderMin() + (mouseX - (double)(this.method_46426() + this.widgetPosX() - 4)) / (double)this.widgetWidth * (double)this.sliderDifference());
         this.setValue((Boolean)CactusSettings.get().allowSliderOverdrive.get() ? value : class_3532.method_15340(value, IntegerSetting.this.getSliderMin(), IntegerSetting.this.getSliderMax()));
      }

      public void setValue(int value) {
         int clampedValue = class_3532.method_15340(value, IntegerSetting.this.getMin(), IntegerSetting.this.getMax());
         if (clampedValue != this.value) {
            this.value = clampedValue;
            IntegerSetting.this.set(this.value);
         }

      }

      public int sliderDifference() {
         return IntegerSetting.this.getSliderMax() - IntegerSetting.this.getSliderMin();
      }

      public int difference() {
         return IntegerSetting.this.getMax() - IntegerSetting.this.getMin();
      }
   }
}
