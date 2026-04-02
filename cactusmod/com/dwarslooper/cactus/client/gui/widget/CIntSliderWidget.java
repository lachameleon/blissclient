package com.dwarslooper.cactus.client.gui.widget;

import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import java.util.function.Consumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_1144;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_3532;
import net.minecraft.class_6382;
import net.minecraft.class_8015;
import net.minecraft.class_9848;
import net.minecraft.class_332.class_12228;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class CIntSliderWidget extends class_339 {
   private final int minValue;
   private final int maxValue;
   private int value;
   private boolean sliderFocused;
   private final Consumer<Integer> valueChangedListener;
   private Function<Integer, String> textGetter;

   public CIntSliderWidget(int x, int y, int width, int height, class_2561 text, int minValue, int maxValue, int initialValue, Consumer<Integer> valueChangedListener) {
      super(x, y, width, height, text);
      this.minValue = minValue;
      this.maxValue = maxValue;
      this.value = class_3532.method_15340(initialValue, minValue, maxValue);
      this.valueChangedListener = valueChangedListener;
   }

   public CIntSliderWidget(int x, int y, int width, int height, class_2561 text, int minValue, int maxValue, int initialValue, Consumer<Integer> valueChangedListener, Function<Integer, String> textGetter) {
      this(x, y, width, height, text, minValue, maxValue, initialValue, valueChangedListener);
      this.textGetter = textGetter;
   }

   public void method_48579(class_332 context, int mouseX, int mouseY, float delta) {
      int color = class_9848.method_61318(this.field_22765, 1.0F, 1.0F, 1.0F);
      context.method_52707(class_10799.field_56883, RenderUtils.SLIDER_TEXTURES.method_52729(this.field_22763, false), this.method_46426(), this.method_46427(), this.method_25368(), this.method_25364(), color);
      context.method_52707(class_10799.field_56883, RenderUtils.SLIDER_HANDLE_TEXTURES.method_52729(this.field_22763, this.field_22763 && (this.field_22762 || this.sliderFocused)), this.method_46426() + (int)((double)(this.value - this.minValue) / (double)(this.maxValue - this.minValue) * (double)(this.field_22758 - 8)), this.method_46427(), 8, this.field_22759, color);
      this.method_75799(context.method_75787(this, class_12228.field_63850), this.method_25369(), 2);
   }

   @NotNull
   public class_2561 method_25369() {
      return this.useOnlyTextGetter() ? class_2561.method_43470((String)this.textGetter.apply(this.value)) : super.method_25369().method_27661().method_10852(class_2561.method_43470(": ")).method_27693(this.textGetter != null ? (String)this.textGetter.apply(this.value) : String.valueOf(this.value)).method_27693(this.minValue == 0 && this.maxValue == 100 ? "%" : "");
   }

   public void method_25354(@NotNull class_1144 soundManager) {
   }

   protected void method_47399(@NotNull class_6382 builder) {
   }

   public boolean useOnlyTextGetter() {
      return false;
   }

   public void method_25357(@NotNull class_11909 click) {
      super.method_25354(class_310.method_1551().method_1483());
   }

   private void setValueFromMouse(class_11909 click) {
      double progress = (click.comp_4798() - (double)(this.method_46426() + 4)) / (double)(this.field_22758 - 8);
      double clamped = class_3532.method_15350(progress, 0.0D, 1.0D);
      this.setValue((int)class_3532.method_16436(clamped, (double)this.minValue, (double)this.maxValue));
   }

   public void setValue(int value) {
      int clampedValue = class_3532.method_15340(value, this.minValue, this.maxValue);
      if (clampedValue != this.value) {
         this.value = clampedValue;
         this.valueChangedListener.accept(this.value);
      }

   }

   public void method_25348(@NotNull class_11909 click, boolean doubled) {
      this.setValueFromMouse(click);
   }

   public void method_25365(boolean focused) {
      super.method_25365(focused);
      if (!focused) {
         this.sliderFocused = false;
      } else {
         class_8015 guiNavigationType = class_310.method_1551().method_48186();
         if (guiNavigationType == class_8015.field_41778 || guiNavigationType == class_8015.field_41780) {
            this.sliderFocused = true;
         }

      }
   }

   public boolean method_25404(@NotNull class_11908 input) {
      if (this.sliderFocused) {
         int keyCode = input.comp_4795();
         boolean bl = keyCode == 263;
         if (bl || keyCode == 262) {
            float f = bl ? -1.0F : 1.0F;
            this.setValue((int)((float)this.value + f / (float)(this.field_22758 - 8)));
            return true;
         }
      }

      return false;
   }

   protected void method_25349(@NotNull class_11909 click, double deltaX, double deltaY) {
      this.setValueFromMouse(click);
      super.method_25349(click, deltaX, deltaY);
   }
}
