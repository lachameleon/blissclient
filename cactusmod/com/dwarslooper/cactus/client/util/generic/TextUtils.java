package com.dwarslooper.cactus.client.util.generic;

import com.dwarslooper.cactus.client.util.CactusConstants;
import com.google.common.collect.ImmutableList;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Formatter;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_5244;
import net.minecraft.class_5481;
import org.jetbrains.annotations.Nullable;

public class TextUtils {
   private static final Map<Character, Integer> toSmallCapsMap = new ConcurrentHashMap();

   public static class_2561 boolAsText(boolean toggle) {
      return toggle ? class_5244.field_24332.method_27661().method_27692(class_124.field_1060) : class_5244.field_24333.method_27661().method_27692(class_124.field_1061);
   }

   public static boolean isNumeric(String text, boolean allowEmpty, boolean allowNegative) {
      if (text.isEmpty()) {
         return allowEmpty;
      } else {
         char[] var3 = (allowNegative ? text.replaceAll("^-", "") : text).toCharArray();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            char c = var3[var5];
            if (!Character.isDigit(c)) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isNumeric(String text) {
      return isNumeric(text, false, false);
   }

   public static boolean isNumericOrEmpty(String text) {
      return isNumeric(text) || text.isEmpty();
   }

   public static TextUtils.ProgressData getProgress(long startTime, long total, long current) {
      long eta = current == 0L ? 0L : (total - current) * (System.currentTimeMillis() - startTime) / current;
      String etaHms = current == 0L ? "N/A" : String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta), TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1L), TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1L));
      int percent = (int)(current * 100L / total);
      String var10000 = String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int)Math.log10((double)percent), " "));
      String string = "+" + var10000 + String.format(" %d%% [", percent) + String.join("", Collections.nCopies(percent, "|")) + ">" + String.join("", Collections.nCopies(100 - percent, ".")) + "]" + String.join("", Collections.nCopies((int)Math.log10((double)total) - (int)Math.log10((double)current), " ")) + String.format(" %d/%d, ETA: %s", current, total, etaHms);
      return new TextUtils.ProgressData(percent, total, current, string);
   }

   public static ImmutableList<class_5481> getTextAsList(@Nullable class_2561 text) {
      return text == null ? ImmutableList.of() : ImmutableList.of(text.method_30937());
   }

   public static String toSmallCaps(String s) {
      int len = s.length();
      Formatter smallCaps = new Formatter(new StringBuilder(len));

      for(int i = 0; i < len; ++i) {
         char c = s.charAt(i);
         int small = (Integer)toSmallCapsMap.computeIfAbsent(c, (n) -> {
            return c >= 'A' && c <= 'Z' && c != 'X' ? Character.codePointOf("LATIN LETTER SMALL CAPITAL " + c) : Integer.valueOf(c == 'X' ? 120 : c);
         });
         smallCaps.format("%c", small);
      }

      return smallCaps.toString();
   }

   public static String trimToWidth(String text, int maxWidth, @Nullable String appendOnOverflow) {
      if (CactusConstants.mc.field_1772.method_1727(text) > maxWidth) {
         while(true) {
            if (CactusConstants.mc.field_1772.method_1727(appendOnOverflow != null ? text + appendOnOverflow : text) <= maxWidth) {
               if (appendOnOverflow != null) {
                  text = text + appendOnOverflow;
               }
               break;
            }

            text = text.substring(0, text.length() - 1);
         }
      }

      return text;
   }

   public static String numberAbbreviation(long value) {
      return value < 1000L ? String.valueOf(value) : (String)formatAbbreviate(value, 1000000000L, "b", "#.#").or(() -> {
         return formatAbbreviate(value, 1000000L, "m", "#.#");
      }).or(() -> {
         return formatAbbreviate(value, 1000L, "k", "#.##");
      }).orElse(String.valueOf(value));
   }

   private static Optional<String> formatAbbreviate(long value, long divisor, String suffix, String pattern) {
      if (value < divisor) {
         return Optional.empty();
      } else {
         DecimalFormat df = new DecimalFormat(pattern);
         String var10000 = df.format((double)value / (double)divisor);
         return Optional.of(var10000 + suffix);
      }
   }

   public static record ProgressData(int percent, long total, long current, String display) {
      public ProgressData(int percent, long total, long current, String display) {
         this.percent = percent;
         this.total = total;
         this.current = current;
         this.display = display;
      }

      public int percent() {
         return this.percent;
      }

      public long total() {
         return this.total;
      }

      public long current() {
         return this.current;
      }

      public String display() {
         return this.display;
      }
   }
}
