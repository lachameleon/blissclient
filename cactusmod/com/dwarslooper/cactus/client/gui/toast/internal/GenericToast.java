package com.dwarslooper.cactus.client.gui.toast.internal;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.class_10799;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_1792;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_3417;
import net.minecraft.class_374;
import net.minecraft.class_5250;
import net.minecraft.class_5481;
import net.minecraft.class_8828;
import net.minecraft.class_368.class_369;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GenericToast extends CToast {
   public List<class_5481> lines;
   private Consumer<class_332> iconConsumer;
   private final class_2561 title;
   private GenericToast.Style style;

   public GenericToast(String title, String text) {
      this((class_2561)class_2561.method_43470(title), (class_5250)class_2561.method_43470(text));
   }

   public GenericToast(class_2561 title, class_5250 text) {
      this(title, text, 6000L);
   }

   public GenericToast(class_2561 title, @Nullable class_5250 text, long duration) {
      this(title, (List)(text != null && text.method_10851() != class_8828.field_46625 ? CactusConstants.mc.field_1772.method_1728(text, 170) : ImmutableList.of()), duration);
   }

   public GenericToast(class_2561 title, @NotNull List<class_5481> lines, long duration) {
      this.style = GenericToast.Style.DEFAULT_DARK;
      this.title = title;
      this.lines = lines;
      this.duration = duration;
      Stream var10002 = lines.stream();
      class_327 var10003 = CactusConstants.mc.field_1772;
      Objects.requireNonNull(var10003);
      this.width = Math.max(100, var10002.mapToInt(var10003::method_30880).max().orElse(100)) + 4;
   }

   public int method_29049() {
      return this.width + this.getOffset();
   }

   public int method_29050() {
      return 20 + Math.max(this.lines.size(), 1) * 12;
   }

   private int getOffset() {
      return this.iconConsumer != null ? 28 : (this.style == GenericToast.Style.SYSTEM ? 18 : 8);
   }

   public void drawToast(class_332 context, class_327 textRenderer, long time, int x, int y, double mouseX, double mouseY) {
      int offsetX = this.getOffset();
      int width = this.method_29049();
      int height;
      if (width == 160 && this.lines.size() <= 1) {
         context.method_52706(class_10799.field_56883, this.style.identifier(), 0, 0, width, this.method_29050());
      } else {
         height = this.method_29050();
         int l = Math.min(4, height - 28);
         this.drawPart(context, this.style, width, 0, 0, 28);

         for(int m = 28; m < height - l; m += 10) {
            this.drawPart(context, this.style, width, 16, m, Math.min(16, height - m - l));
         }

         this.drawPart(context, this.style, width, 32 - l, height - l, l);
      }

      if (this.lines.isEmpty()) {
         context.method_51439(CactusConstants.mc.field_1772, this.title, offsetX, 12, -1, false);
      } else {
         context.method_51439(CactusConstants.mc.field_1772, this.title, offsetX, 7, -1, false);

         for(height = 0; height < this.lines.size(); ++height) {
            context.method_51430(CactusConstants.mc.field_1772, (class_5481)this.lines.get(height), offsetX, 18 + height * 12, -1, false);
         }
      }

      if (this.iconConsumer != null) {
         try {
            this.iconConsumer.accept(context);
         } catch (Exception var16) {
         }
      }

   }

   private void drawPart(class_332 context, GenericToast.Style style, int i, int j, int k, int l) {
      int m = j == 0 ? 20 : 5;
      int n = Math.min(60, i - m);
      class_2960 identifier = style.identifier();
      context.method_70846(class_10799.field_56883, identifier, 160, 32, 0, j, 0, k, m, l);

      for(int o = m; o < i - n; o += 64) {
         context.method_70846(class_10799.field_56883, identifier, 160, 32, 32, j, o, k, Math.min(64, i - o - n), l);
      }

      context.method_70846(class_10799.field_56883, identifier, 160, 32, 160 - n, j, i - n, k, n, l);
   }

   public void method_61989(@NotNull class_374 manager, long time) {
      this.visibility = time < this.duration && !this.shouldClose() ? class_369.field_2210 : class_369.field_2209;
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      this.close();
      return false;
   }

   public GenericToast setIcon(class_1792 item) {
      if (item != null) {
         this.iconConsumer = (context) -> {
            try {
               context.method_51445(item.method_7854(), 8, 8);
            } catch (Exception var3) {
               CactusClient.getLogger().error(var3.getMessage());
            }

         };
      }

      return this;
   }

   public GenericToast setIcon(class_2960 identifier) {
      if (identifier != null) {
         this.iconConsumer = (context) -> {
            context.method_25290(class_10799.field_56883, identifier, 8, 8, 0.0F, 0.0F, 16, 16, 16, 16);
         };
      }

      return this;
   }

   public GenericToast setDuration(long duration) {
      this.duration = duration;
      return this;
   }

   public GenericToast setStyle(GenericToast.Style style) {
      this.style = style;
      return this;
   }

   public class_1113 getSound() {
      return class_1109.method_4757(class_3417.field_14561, 1.2F, 1.0F);
   }

   public long getDuration() {
      return this.duration;
   }

   public static enum Style {
      DEFAULT_DARK("advancement"),
      DEFAULT_WHITE("recipe"),
      SYSTEM("system"),
      SQUARE_WHITE("tutorial");

      final class_2960 identifier;

      private Style(String s) {
         this.identifier = class_2960.method_60654("toast/" + s);
      }

      public class_2960 identifier() {
         return this.identifier;
      }

      // $FF: synthetic method
      private static GenericToast.Style[] $values() {
         return new GenericToast.Style[]{DEFAULT_DARK, DEFAULT_WHITE, SYSTEM, SQUARE_WHITE};
      }
   }
}
