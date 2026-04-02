package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.gui.screen.window.WindowScreen;
import com.dwarslooper.cactus.client.gui.widget.BooleanWidget;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.CIntSliderWidget;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import com.dwarslooper.cactus.client.util.generic.ColorUtils;
import java.awt.Color;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_3532;
import net.minecraft.class_364;
import net.minecraft.class_5244;
import net.minecraft.class_9848;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public class ColorPickerScreen extends WindowScreen {
   public static final Pattern HEXADECIMAL_PATTERN = Pattern.compile("\\p{XDigit}+");
   private final int gridSize;
   private int gridX;
   private int gridY;
   private final BiConsumer<Color, Boolean> callback;
   private Color colorValue;
   private final Vector2i slThumbPos;
   private int hueThumbPos;
   private float hue;
   private float saturation;
   private float light;
   private boolean slDown;
   private boolean hueDown;
   private class_342 hexWidget;
   private CIntSliderWidget alphaWidget;
   private boolean isRainbowEnabled;
   private int alpha;
   private boolean allowRgb;
   private final boolean allowAlpha;

   public ColorPickerScreen(Color value, Consumer<Color> callback) {
      this(value, false, callback);
   }

   public ColorPickerScreen(Color value, boolean allowAlpha, Consumer<Color> callback) {
      this(value, false, allowAlpha, (color, bool) -> {
         callback.accept(color);
      });
      this.allowRgb = false;
   }

   public ColorPickerScreen(Color value, boolean isRainbowEnabled, boolean allowAlpha, BiConsumer<Color, Boolean> callback) {
      super("colorPicker", 200, 126);
      this.gridSize = 66;
      this.slThumbPos = new Vector2i();
      this.hueThumbPos = 0;
      this.slDown = false;
      this.hueDown = false;
      this.allowRgb = true;
      this.colorValue = value;
      this.isRainbowEnabled = isRainbowEnabled;
      this.allowAlpha = allowAlpha;
      this.alpha = allowAlpha ? value.getAlpha() : 255;
      this.callback = callback;
   }

   public void method_25426() {
      super.method_25426();
      this.gridX = this.x() + 6;
      this.gridY = this.y() + 6;
      int controlsX = this.gridX + 66 + 5;
      int controlsW = this.x() + this.boxWidth() - controlsX - 5;
      BooleanWidget rgbSetting = new BooleanWidget(controlsX, this.y() + 5, controlsW, 20, class_2561.method_43470("Rainbow"), this.isRainbowEnabled, (b) -> {
         this.isRainbowEnabled = b;
      });
      rgbSetting.field_22763 = this.allowRgb;
      this.method_37063(rgbSetting);
      this.alphaWidget = new CIntSliderWidget(controlsX, this.y() + 5 + 24, controlsW, 20, class_2561.method_43470("Opacity"), 0, 255, this.alpha, (i) -> {
         this.alpha = i;
         this.colorChanged();
      });
      this.alphaWidget.field_22763 = this.allowAlpha;
      this.method_37063(this.alphaWidget);
      this.hexWidget = new class_342(CactusConstants.mc.field_1772, controlsX, this.y() + 5 + 48, controlsW - 24, 20, class_2561.method_43470("HEX"));
      this.hexWidget.method_1852(this.getHexString());
      this.hexWidget.method_1890((string) -> {
         return HEXADECIMAL_PATTERN.matcher(string).matches();
      });
      this.hexWidget.method_1863(this::setFromHexString);
      this.method_37063(this.hexWidget);
      this.method_37063(new CButtonWidget(this.x() + 5, this.y() + this.boxHeight() - 25, 60, 20, class_5244.field_24335, (button) -> {
         this.method_25419();
      }));
      this.method_37063(new CButtonWidget(this.x() + this.boxWidth() - 60 - 5, this.y() + this.boxHeight() - 25, 60, 20, class_5244.field_24334, (button) -> {
         this.callback.accept(this.colorValue, this.isRainbowEnabled);
         this.method_25419();
      }));
      this.updateFromColor();
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      this.setThumb();
      context.method_73198(this.gridX - 1, this.gridY - 1, 68, 68, Color.GRAY.getRGB());
      RenderUtils.fillGradientHorizontal(context, this.gridX, this.gridY, this.gridX + 66, this.gridY + 66, Color.WHITE.getRGB(), Color.HSBtoRGB(this.hue, 1.0F, 1.0F));
      context.method_25296(this.gridX, this.gridY, this.gridX + 66, this.gridY + 66, 0, Color.BLACK.getRGB());
      context.method_25294(this.gridX, this.slThumbPos.y, this.gridX + 66, this.slThumbPos.y + 1, Color.WHITE.getRGB());
      context.method_25294(this.slThumbPos.x, this.gridY, this.slThumbPos.x + 1, this.gridY + 66, Color.WHITE.getRGB());
      int hueTop = this.gridY + 66 + 2 + 4;
      context.method_73198(this.gridX - 1, hueTop - 1, this.boxWidth() - 10, 20, Color.GRAY.getRGB());
      RenderUtils.fillRainbow(context, this.gridX, hueTop, this.x() + this.boxWidth() - 6, hueTop + 18);
      context.method_25294(this.hueThumbPos, hueTop, this.hueThumbPos + 1, hueTop + 18, Color.WHITE.getRGB());
      int indicatorTop = this.y() + 5 + 48;
      context.method_73198(this.x() + this.boxWidth() - 5 - 20, indicatorTop, 20, 20, Color.GRAY.getRGB());
      context.method_25294(this.x() + this.boxWidth() - 5 - 20 + 1, indicatorTop + 1, this.x() + this.boxWidth() - 5 - 1, indicatorTop + 20 - 1, this.colorValue.getRGB());
   }

   public boolean method_25402(class_11909 click, boolean doubled) {
      this.method_25395((class_364)null);
      this.setFromMouse(click.comp_4798(), click.comp_4799(), true);
      return super.method_25402(click, doubled);
   }

   public boolean method_25403(class_11909 click, double deltaX, double deltaY) {
      this.setFromMouse(click.comp_4798(), click.comp_4799(), false);
      return super.method_25403(click, deltaX, deltaY);
   }

   public boolean method_25406(@NotNull class_11909 click) {
      this.slDown = this.hueDown = false;
      return super.method_25406(click);
   }

   public int getHueWidth() {
      return this.x() + this.boxWidth() - 5 - 2 - this.gridX;
   }

   public void setFromMouse(double mouseX, double mouseY, boolean takeover) {
      boolean sl = this.setFromSL(mouseX, mouseY, takeover);
      boolean h = this.setFromHUE(mouseX, mouseY, takeover);
      if (sl || h) {
         this.colorChanged();
         this.method_25395((class_364)null);
      }

   }

   public boolean setFromSL(double mouseX, double mouseY, boolean takeover) {
      if (this.slDown || mouseX > (double)this.gridX && mouseX <= (double)(this.gridX + 66) && mouseY > (double)this.gridY && mouseY <= (double)(this.gridY + 66)) {
         if (takeover) {
            this.slDown = true;
            this.hueDown = false;
         }

         this.saturation = class_3532.method_15363((float)(mouseX - (double)this.gridX) / 66.0F, 0.0F, 1.0F);
         this.light = class_3532.method_15363(1.0F - (float)(mouseY - (double)this.gridY) / 66.0F, 0.0F, 1.0F);
         return true;
      } else {
         return false;
      }
   }

   public boolean setFromHUE(double mouseX, double mouseY, boolean takeover) {
      if (this.hueDown || mouseX >= (double)this.gridX && mouseX < (double)(this.x() + this.boxWidth() - 5) && mouseY > (double)(this.y() + this.boxHeight() - 5 - 24 - 20) && mouseY < (double)(this.y() + this.boxHeight() - 5 - 24 - 2)) {
         if (takeover) {
            this.hueDown = true;
            this.slDown = false;
         }

         this.hue = class_3532.method_15363((float)(mouseX - (double)this.gridX) / (float)this.getHueWidth(), 0.0F, 1.0F);
         return true;
      } else {
         return false;
      }
   }

   public void setThumb() {
      int x = (int)class_3532.method_15363((float)this.gridX + 66.0F * this.saturation, (float)this.gridX, (float)(this.gridX + 66));
      int y = (int)class_3532.method_15363((float)this.gridY + 66.0F * (1.0F - this.light), (float)this.gridY, (float)(this.gridY + 66));
      this.slThumbPos.set(x, y);
      this.hueThumbPos = (int)class_3532.method_15363((float)this.gridX + (float)this.getHueWidth() * this.hue, (float)this.gridX, (float)(this.gridX + this.getHueWidth()));
   }

   public void updateFromColor() {
      float[] hsbValues = Color.RGBtoHSB(this.colorValue.getRed(), this.colorValue.getGreen(), this.colorValue.getBlue(), (float[])null);
      this.hue = hsbValues[0];
      this.saturation = hsbValues[1];
      this.light = hsbValues[2];
      this.colorChanged();
   }

   public void colorChanged() {
      this.colorValue = new Color(class_9848.method_61330(this.alpha, Color.HSBtoRGB(this.hue, this.saturation, this.light)), true);
      if (!this.hexWidget.method_25370()) {
         this.hexWidget.method_1852(this.getHexString());
      }

      if (!this.alphaWidget.method_25370()) {
         this.alphaWidget.setValue(this.alpha);
      }

      this.setThumb();
   }

   public String getHexString() {
      return ColorUtils.getHex(this.colorValue.getRGB(), !this.allowAlpha);
   }

   public void setFromHexString(String val) {
      try {
         int parsed = Integer.parseInt(val, 16);
         int alphaBits = parsed >> 24 & 255;
         this.alpha = alphaBits != 0 && this.allowAlpha ? alphaBits : 255;
         this.colorValue = new Color(class_9848.method_61330(this.alpha, parsed), true);
         if (this.hexWidget.method_25370()) {
            this.updateFromColor();
         }
      } catch (Exception var4) {
      }

   }
}
