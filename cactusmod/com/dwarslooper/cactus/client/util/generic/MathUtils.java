package com.dwarslooper.cactus.client.util.generic;

import java.util.function.Function;
import net.minecraft.class_2561;
import net.minecraft.class_5250;

public class MathUtils {
   public static String quality(int num, int warn, int bad, int fatal, MathUtils.QualityMode mode) {
      return quality((float)num, (float)warn, (float)bad, (float)fatal, mode, (f) -> {
         return Integer.toString(f.intValue());
      });
   }

   public static String quality(float num, float warn, float bad, float fatal, MathUtils.QualityMode mode) {
      return quality(num, warn, bad, fatal, mode, (f) -> {
         return Float.toString(f);
      });
   }

   public static String quality(float num, float warn, float bad, float fatal, MathUtils.QualityMode mode, Function<Float, String> formatter) {
      String display = "§a";
      if ((mode != MathUtils.QualityMode.DECREMENT || !(num < fatal)) && (mode != MathUtils.QualityMode.INCREMENT || !(num > fatal))) {
         if ((mode != MathUtils.QualityMode.DECREMENT || !(num < bad)) && (mode != MathUtils.QualityMode.INCREMENT || !(num > bad))) {
            if (mode == MathUtils.QualityMode.DECREMENT && num < warn || mode == MathUtils.QualityMode.INCREMENT && num > warn) {
               display = "§e";
            }
         } else {
            display = "§c";
         }
      } else {
         display = "§4";
      }

      return display + (String)formatter.apply(num);
   }

   public static class_2561 createGradientText(double value, double worst, double best, String format) {
      double clampedValue = Math.max(Math.min(value, Math.max(worst, best)), Math.min(worst, best));
      double normalized;
      if (best > worst) {
         normalized = (clampedValue - worst) / (best - worst);
      } else {
         normalized = (clampedValue - best) / (worst - best);
         normalized = 1.0D - normalized;
      }

      int blue = 0;
      float interpolateMid = 0.65F;
      int red;
      int green;
      double scaleUp;
      if (normalized >= (double)interpolateMid) {
         scaleUp = (normalized - (double)interpolateMid) / (1.0D - (double)interpolateMid);
         red = (int)(255.0D * (1.0D - scaleUp));
         green = 255;
      } else {
         scaleUp = normalized / (double)interpolateMid;
         red = 255;
         green = (int)(255.0D * scaleUp);
      }

      int colorValue = red << 16 | green << 8 | blue;
      String content = format != null ? String.format(format, value) : String.valueOf(value);
      class_5250 text = class_2561.method_43470(content);
      return text.method_27694((style) -> {
         return style.method_36139(colorValue);
      });
   }

   public static int parseIntOrZero(String s) {
      try {
         return Integer.parseInt(s);
      } catch (NumberFormatException var2) {
         return 0;
      }
   }

   public static class_2561 createGradientText(double value, double worst, double best) {
      return createGradientText(value, worst, best, (String)null);
   }

   public static int compareDigits(int num1, int num2) {
      String strNum1 = Integer.toString(num1);
      String strNum2 = Integer.toString(num2);
      int max = Math.max(strNum1.length(), strNum2.length());

      for(int i = 0; i < max; ++i) {
         int a = Character.getNumericValue(i < strNum1.length() ? strNum1.charAt(i) : '\u0000');
         int b = Character.getNumericValue(i < strNum2.length() ? strNum2.charAt(i) : '\u0000');
         if (a < b) {
            return -1;
         }

         if (a > b) {
            return 1;
         }
      }

      return Integer.compare(strNum1.length(), strNum2.length());
   }

   public static long calculateETA(float progress, long start) {
      long currentTimeMillis = System.currentTimeMillis();
      long elapsedTimeMillis = currentTimeMillis - start;
      long estimatedTotalTimeMillis = (long)((float)elapsedTimeMillis / progress);
      return estimatedTotalTimeMillis - elapsedTimeMillis;
   }

   public static boolean inBounds(double min, double max, double val) {
      return val >= min && val <= max;
   }

   public static enum QualityMode {
      INCREMENT,
      DECREMENT;

      // $FF: synthetic method
      private static MathUtils.QualityMode[] $values() {
         return new MathUtils.QualityMode[]{INCREMENT, DECREMENT};
      }
   }
}
