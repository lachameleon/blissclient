package com.dwarslooper.cactus.client.util.generic;

import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.systems.config.settings.impl.ColorSetting;
import java.awt.Color;
import java.util.List;
import net.minecraft.class_156;
import net.minecraft.class_3532;
import org.joml.Random;

public class ColorUtils {
   private static final double PI_2 = 6.283185307179586D;
   public static List<Color> gradientColors;

   public static Color withAlpha(Color color, float alpha) {
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), convert(alpha));
   }

   public static int withAlpha(int colorInt, float alpha) {
      Color color = new Color(colorInt);
      return (new Color(color.getRed(), color.getGreen(), color.getBlue(), convert(alpha))).getRGB();
   }

   public static int convert(float f) {
      return (int)(Math.max(0.0F, Math.min(1.0F, f)) * 255.0F);
   }

   public static float convert(int i) {
      return (float)Math.max(0, Math.min(255, i)) / 255.0F;
   }

   public static Color randomColor() {
      Random random = new Random();
      return new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
   }

   public static String getHex(int value) {
      return getHex(value, true);
   }

   public static String getHex(int value, boolean trimAlpha) {
      return Integer.toHexString(value).substring(trimAlpha ? 2 : 0);
   }

   public static int appendFullAlpha(int color) {
      return -16777216 | color;
   }

   public static int getAccentRGB() {
      CactusSettings settings = CactusSettings.get();
      ColorSetting.ColorValue value = (ColorSetting.ColorValue)settings.accentColor.get();
      return appendFullAlpha(value.usesRgb() ? getFadingRgbByMillis((21 - (Integer)settings.fadingSpeed.get()) * 1000) : value.value().getRGB());
   }

   public static int getFadingRgb(int speed) {
      return Math.max(1, getFadingRgbByMillis((21 - speed) * 1000));
   }

   public static int getFadingRgbByMillis(int durationMillis) {
      double phase = (double)(class_156.method_658() % (long)durationMillis) / (double)durationMillis;
      int red = (int)(Math.sin(phase * 6.283185307179586D) * 127.0D + 128.0D);
      int green = (int)(Math.sin((phase + 0.6666666666666666D) * 6.283185307179586D) * 127.0D + 128.0D);
      int blue = (int)(Math.sin((phase + 1.3333333333333333D) * 6.283185307179586D) * 127.0D + 128.0D);
      return red << 16 | green << 8 | blue;
   }

   public static int argbToRgba(int argb) {
      int a = argb >> 24 & 255;
      int r = argb >> 16 & 255;
      int g = argb >> 8 & 255;
      int b = argb & 255;
      return r << 24 | g << 16 | b << 8 | a;
   }

   public static Color interpolateColor(Color start, Color end, float t) {
      int r = class_3532.method_48781(t, start.getRed(), end.getRed());
      int g = class_3532.method_48781(t, start.getGreen(), end.getGreen());
      int b = class_3532.method_48781(t, start.getBlue(), end.getBlue());
      int a = class_3532.method_48781(t, start.getAlpha(), end.getAlpha());
      return new Color(r, g, b, a);
   }

   public static Color interpolateGradient(List<Color> colors, float position, boolean breakEdge) {
      if (colors.size() == 1) {
         return (Color)colors.getFirst();
      } else {
         float scaled = position * (float)(colors.size() - (breakEdge ? 1 : 0));
         int startIndex = (int)scaled;
         float local = scaled - (float)startIndex;
         return interpolateColor((Color)colors.get(breakEdge ? startIndex : startIndex % colors.size()), (Color)colors.get((startIndex + 1) % colors.size()), local);
      }
   }
}
