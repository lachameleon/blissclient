package com.dwarslooper.cactus.client.util.client;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.class_156;
import net.minecraft.class_2479;
import net.minecraft.class_2481;
import net.minecraft.class_2487;
import net.minecraft.class_2489;
import net.minecraft.class_2491;
import net.minecraft.class_2494;
import net.minecraft.class_2495;
import net.minecraft.class_2497;
import net.minecraft.class_2499;
import net.minecraft.class_2501;
import net.minecraft.class_2503;
import net.minecraft.class_2516;
import net.minecraft.class_2519;
import net.minecraft.class_2520;
import net.minecraft.class_5627;

public class NbtFormatter implements class_5627 {
   private static final Map<String, List<String>> ENTRY_ORDER_OVERRIDES = (Map)class_156.method_654(Maps.newHashMap(), (map) -> {
      map.put("{}", Lists.newArrayList(new String[]{"DataVersion", "author", "size", "data", "entities", "palette", "palettes"}));
      map.put("{}.data.[].{}", Lists.newArrayList(new String[]{"pos", "state", "nbt"}));
      map.put("{}.entities.[].{}", Lists.newArrayList(new String[]{"blockPos", "pos"}));
   });
   private static final Set<String> IGNORED_PATHS = Sets.newHashSet(new String[]{"{}.size.[]", "{}.data.[].{}", "{}.palette.[].{}", "{}.entities.[].{}"});
   private static final Pattern SIMPLE_NAME = Pattern.compile("[A-Za-z\\d._+-]+");
   private static final String KEY_VALUE_SEPARATOR = String.valueOf(':');
   private static final String ENTRY_SEPARATOR = String.valueOf(',');
   private static final int NAME_COLOR = 5636095;
   private static final int STRING_COLOR = 5635925;
   private static final int NUMBER_COLOR = 16755200;
   private static final int TYPE_SUFFIX_COLOR = 16733525;
   private final String prefix;
   private final int indentationLevel;
   private final List<String> pathParts;
   private NbtFormatter.RGBColorText result;

   public NbtFormatter() {
      this("    ", 0, Lists.newArrayList());
   }

   public NbtFormatter(String prefix, int indentationLevel, List<String> pathParts) {
      this.prefix = prefix;
      this.indentationLevel = indentationLevel;
      this.pathParts = pathParts;
   }

   protected static String escapeName(String name) {
      return SIMPLE_NAME.matcher(name).matches() ? name : class_2519.method_10706(name);
   }

   public NbtFormatter.RGBColorText apply(class_2520 element) {
      element.method_32289(this);
      return this.result;
   }

   public void method_32302(class_2519 element) {
      this.result = new NbtFormatter.RGBColorText(class_2519.method_10706((String)element.method_68658().get()), 5635925);
   }

   public void method_32291(class_2481 element) {
      this.result = (new NbtFormatter.RGBColorText(element.method_10702(), 16755200)).append("b", 16733525);
   }

   public void method_32301(class_2516 element) {
      this.result = (new NbtFormatter.RGBColorText(element.method_10702(), 16755200)).append("s", 16733525);
   }

   public void method_32297(class_2497 element) {
      this.result = new NbtFormatter.RGBColorText(element.method_10702(), 16755200);
   }

   public void method_32300(class_2503 element) {
      this.result = (new NbtFormatter.RGBColorText(element.method_10702(), 16755200)).append("L", 16733525);
   }

   public void method_32295(class_2494 element) {
      this.result = (new NbtFormatter.RGBColorText(element.method_10700(), 16755200)).append("f", 16733525);
   }

   public void method_32293(class_2489 element) {
      this.result = (new NbtFormatter.RGBColorText(element.method_10697(), 16755200)).append("d", 16733525);
   }

   public void method_32290(class_2479 element) {
      NbtFormatter.RGBColorText stringBuilder = (new NbtFormatter.RGBColorText("[")).append("B", 16733525).append(";");
      byte[] bs = element.method_10521();

      for(int i = 0; i < bs.length; ++i) {
         stringBuilder.append(" ").append(bs[i].makeConcatWithConstants<invokedynamic>(bs[i]), 16755200).append("B", 16733525);
         if (i != bs.length - 1) {
            stringBuilder.append(ENTRY_SEPARATOR);
         }
      }

      stringBuilder.append("]");
      this.result = new NbtFormatter.RGBColorText(stringBuilder);
   }

   public void method_32296(class_2495 element) {
      NbtFormatter.RGBColorText stringBuilder = (new NbtFormatter.RGBColorText("[")).append("I", 16733525).append(";");
      int[] is = element.method_10588();

      for(int i = 0; i < is.length; ++i) {
         stringBuilder.append(" ").append(is[i].makeConcatWithConstants<invokedynamic>(is[i]), 16755200);
         if (i != is.length - 1) {
            stringBuilder.append(ENTRY_SEPARATOR);
         }
      }

      stringBuilder.append("]");
      this.result = new NbtFormatter.RGBColorText(stringBuilder);
   }

   public void method_32299(class_2501 element) {
      NbtFormatter.RGBColorText stringBuilder = (new NbtFormatter.RGBColorText("[")).append("L", 16733525).append(";");
      long[] ls = element.method_10615();

      for(int i = 0; i < ls.length; ++i) {
         stringBuilder.append(" ").append(ls[i].makeConcatWithConstants<invokedynamic>(ls[i]), 16755200).append("L", 16733525);
         if (i != ls.length - 1) {
            stringBuilder.append(ENTRY_SEPARATOR);
         }
      }

      stringBuilder.append("]");
      this.result = new NbtFormatter.RGBColorText(stringBuilder);
   }

   public void method_32298(class_2499 element) {
      if (element.isEmpty()) {
         this.result = new NbtFormatter.RGBColorText("[]");
      } else {
         NbtFormatter.RGBColorText stringBuilder = new NbtFormatter.RGBColorText("[");
         this.pushPathPart("[]");
         String string = IGNORED_PATHS.contains(this.joinPath()) ? "" : this.prefix;
         if (!string.isEmpty()) {
            stringBuilder.append("\n");
         }

         for(int i = 0; i < element.size(); ++i) {
            stringBuilder.append(Strings.repeat(string, this.indentationLevel + 1));
            stringBuilder.append((new NbtFormatter(string, this.indentationLevel + 1, this.pathParts)).apply(element.method_10534(i)));
            if (i != element.size() - 1) {
               stringBuilder.append(ENTRY_SEPARATOR).append(string.isEmpty() ? " " : "\n");
            }
         }

         if (!string.isEmpty()) {
            stringBuilder.append("\n").append(Strings.repeat(string, this.indentationLevel));
         }

         stringBuilder.append("]");
         this.result = new NbtFormatter.RGBColorText(stringBuilder);
         this.popPathPart();
      }

   }

   public void method_32292(class_2487 compound) {
      if (compound.method_33133()) {
         this.result = new NbtFormatter.RGBColorText("{}");
      } else {
         NbtFormatter.RGBColorText stringBuilder = new NbtFormatter.RGBColorText("{");
         this.pushPathPart("{}");
         String string = IGNORED_PATHS.contains(this.joinPath()) ? "" : this.prefix;
         if (!string.isEmpty()) {
            stringBuilder.append("\n");
         }

         Collection<String> collection = this.getSortedNames(compound);
         Iterator iterator = collection.iterator();

         while(iterator.hasNext()) {
            String string2 = (String)iterator.next();
            class_2520 nbtElement = compound.method_10580(string2);
            this.pushPathPart(string2);
            stringBuilder.append(Strings.repeat(string, this.indentationLevel + 1)).append(escapeName(string2), 5636095).append(KEY_VALUE_SEPARATOR).append(" ").append((new NbtFormatter(string, this.indentationLevel + 1, this.pathParts)).apply((class_2520)Objects.requireNonNull(nbtElement)));
            this.popPathPart();
            if (iterator.hasNext()) {
               stringBuilder.append(ENTRY_SEPARATOR).append(string.isEmpty() ? " " : "\n");
            }
         }

         if (!string.isEmpty()) {
            stringBuilder.append("\n").append(Strings.repeat(string, this.indentationLevel));
         }

         stringBuilder.append("}");
         this.result = new NbtFormatter.RGBColorText(stringBuilder);
         this.popPathPart();
      }

   }

   private void popPathPart() {
      this.pathParts.remove(this.pathParts.size() - 1);
   }

   private void pushPathPart(String part) {
      this.pathParts.add(part);
   }

   protected List<String> getSortedNames(class_2487 compound) {
      Set<String> set = Sets.newHashSet(compound.method_10541());
      List<String> list = Lists.newArrayList();
      List<String> list2 = (List)ENTRY_ORDER_OVERRIDES.get(this.joinPath());
      if (list2 != null) {
         Iterator var5 = list2.iterator();

         while(var5.hasNext()) {
            String string = (String)var5.next();
            if (set.remove(string)) {
               list.add(string);
            }
         }

         if (!set.isEmpty()) {
            Stream<String> var10000 = set.stream().sorted();
            Objects.requireNonNull(list);
            Objects.requireNonNull(list);
            var10000.forEach(list::add);
         }
      } else {
         list.addAll(set);
         Collections.sort(list);
      }

      return list;
   }

   public String joinPath() {
      return String.join(".", this.pathParts);
   }

   public void method_32294(class_2491 element) {
   }

   public static class RGBColorText {
      public static final NbtFormatter.RGBColorText.RGBEntry NEWLINE = new NbtFormatter.RGBColorText.RGBEntry("\n", 16777215);
      final List<NbtFormatter.RGBColorText.RGBEntry> entries = new ArrayList();

      public RGBColorText(NbtFormatter.RGBColorText text) {
         this.append(text);
      }

      public RGBColorText(String value) {
         this.append(value, 16777215);
      }

      public RGBColorText(Object value, int color) {
         this.append(String.valueOf(value), color);
      }

      public NbtFormatter.RGBColorText append(String text, int color) {
         if (text.equals("\n")) {
            this.entries.add(NEWLINE);
         } else {
            this.entries.add(new NbtFormatter.RGBColorText.RGBEntry(text, color));
         }

         return this;
      }

      public NbtFormatter.RGBColorText append(String text) {
         return this.append(text, 16777215);
      }

      public NbtFormatter.RGBColorText append(NbtFormatter.RGBColorText c) {
         this.entries.addAll(c.getEntries());
         return this;
      }

      public List<NbtFormatter.RGBColorText.RGBEntry> getEntries() {
         return this.entries;
      }

      public static record RGBEntry(String value, int color) {
         public RGBEntry(String value, int color) {
            this.value = value;
            this.color = color;
         }

         public String value() {
            return this.value;
         }

         public int color() {
            return this.color;
         }
      }
   }
}
