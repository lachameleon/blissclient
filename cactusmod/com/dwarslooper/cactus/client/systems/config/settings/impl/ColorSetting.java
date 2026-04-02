package com.dwarslooper.cactus.client.systems.config.settings.impl;

import com.dwarslooper.cactus.client.gui.screen.impl.ColorPickerScreen;
import com.dwarslooper.cactus.client.render.RenderHelper;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.systems.config.settings.ICopyable;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.ColorUtils;
import com.google.gson.JsonObject;
import java.awt.Color;
import net.minecraft.class_10799;
import net.minecraft.class_11909;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_4185;
import net.minecraft.class_9848;
import org.jetbrains.annotations.NotNull;

public class ColorSetting extends Setting<ColorSetting.ColorValue> {
   private final boolean allowAlpha;

   public ColorSetting(String id, ColorSetting.ColorValue value) {
      this(id, value, false);
   }

   public ColorSetting(String id, ColorSetting.ColorValue value, boolean allowOpacity) {
      super(id, value);
      this.allowAlpha = allowOpacity;
   }

   public void save(JsonObject object) {
      object.addProperty("color", ((ColorSetting.ColorValue)this.get()).value().getRGB());
      object.addProperty("rgb", ((ColorSetting.ColorValue)this.get()).usesRgb());
   }

   public ColorSetting.ColorValue load(JsonObject object) {
      int color = object.get("color").getAsInt();
      this.set(new ColorSetting.ColorValue(new Color(color, this.allowAlpha && color >>> 24 != 0), object.get("rgb").getAsBoolean()));
      return (ColorSetting.ColorValue)this.get();
   }

   public class_339 buildWidget() {
      return new ColorSetting.Widget();
   }

   public static record ColorValue(Color value, boolean usesRgb) implements ICopyable<ColorSetting.ColorValue> {
      public ColorValue(Color value, boolean usesRgb) {
         this.value = value;
         this.usesRgb = usesRgb;
      }

      public int color() {
         return this.usesRgb ? class_9848.method_61330(this.value.getAlpha(), ColorUtils.getFadingRgb((Integer)CactusSettings.get().fadingSpeed.get())) : this.value.getRGB();
      }

      public static ColorSetting.ColorValue of(Color value, boolean usesRgb) {
         return new ColorSetting.ColorValue(value, usesRgb);
      }

      public static ColorSetting.ColorValue white() {
         return new ColorSetting.ColorValue(Color.WHITE, false);
      }

      public ColorSetting.ColorValue copy() {
         return new ColorSetting.ColorValue(this.value, this.usesRgb);
      }

      public Color value() {
         return this.value;
      }

      public boolean usesRgb() {
         return this.usesRgb;
      }
   }

   public class Widget extends Setting<ColorSetting.ColorValue>.Widget {
      private String hexValue;

      public Widget() {
         super();
         this.update();
      }

      public void wrappedRender(class_332 context, int mouseX, int mouseY, float delta) {
         context.method_52706(class_10799.field_56883, class_4185.field_45339.comp_1604(), this.field_22758 - this.widgetWidth, 0, this.widgetWidth, 20);
         this.drawOverlay(context, this.field_22758 - this.widgetWidth, this.widgetWidth);
         context.method_25303(this.textRenderer, this.hexValue, this.field_22758 - this.widgetWidth + 2 + 4, this.textYCenter, class_9848.method_61330(255, ((ColorSetting.ColorValue)ColorSetting.this.get()).value().getRGB()));
         int x = this.field_22758 - 4 - this.field_22759 + 8;
         context.method_25294(x, 4, this.field_22758 - 4, this.field_22759 - 4, ((ColorSetting.ColorValue)ColorSetting.this.get()).value().getRGB());
         RenderHelper.drawBorder(context, x, 4, this.field_22758 - 4 - x, this.field_22759 - 8, -16777216);
      }

      public void method_25348(@NotNull class_11909 click, boolean doubled) {
         CactusConstants.mc.method_1507(new ColorPickerScreen(((ColorSetting.ColorValue)ColorSetting.this.get()).value(), ((ColorSetting.ColorValue)ColorSetting.this.get()).usesRgb(), ColorSetting.this.allowAlpha, (color, rgb) -> {
            ColorSetting.this.set(new ColorSetting.ColorValue(color, rgb));
            this.update();
         }));
      }

      public void update() {
         this.hexValue = "#" + ColorUtils.getHex(((ColorSetting.ColorValue)ColorSetting.this.get()).value().getRGB(), !ColorSetting.this.allowAlpha);
      }
   }
}
