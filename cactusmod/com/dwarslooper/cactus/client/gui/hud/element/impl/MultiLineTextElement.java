package com.dwarslooper.cactus.client.gui.hud.element.impl;

import com.dwarslooper.cactus.client.gui.hud.element.DynamicHudElement;
import com.dwarslooper.cactus.client.render.GradientTextRenderer;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.ColorListSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.ColorSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.EnumSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.IntegerSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.StringListSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.StringSetting;
import com.dwarslooper.cactus.client.systems.params.PlaceholderHandler;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_332;
import org.joml.Vector2i;

public class MultiLineTextElement extends DynamicHudElement<MultiLineTextElement> {
   public Setting<List<String>> lines;
   public Setting<Integer> spacing;
   public Setting<MultiLineTextElement.Alignment> alignment;
   public Setting<String> placeholderFormat;
   public Setting<Boolean> gradient;
   public Setting<List<String>> gradientColors;
   private List<String> reformatted;
   private List<Color> parsedGradientColors;
   private int maxLineWidth;

   public MultiLineTextElement() {
      super("text");
      this.lines = this.elementGroup.add(new StringListSetting("text", new String[0]));
      this.spacing = this.elementGroup.add((new IntegerSetting("spacing", 2)).max(40));
      this.alignment = this.elementGroup.add(new EnumSetting("alignment", MultiLineTextElement.Alignment.Left));
      this.placeholderFormat = this.elementGroup.add(new StringSetting("format", PlaceholderHandler.DEFAULT_FORMAT));
      this.gradient = this.elementGroup.add(new BooleanSetting("gradient", false));
      this.gradientColors = this.elementGroup.add((new ColorListSetting("gradientColors", new String[]{"#88EB46", "#00801D"}) {
         public void set(List<String> value) {
            super.set(value);
            MultiLineTextElement.this.parseColors();
         }
      }).visibleIf(() -> {
         return (Boolean)this.gradient.get();
      }));
      this.parsedGradientColors = new ArrayList();
   }

   private void parseColors() {
      this.parsedGradientColors = ((List)this.gradientColors.get()).stream().map((cs) -> {
         try {
            return Color.decode(cs);
         } catch (Exception var2) {
            return null;
         }
      }).filter(Objects::nonNull).toList();
   }

   public MultiLineTextElement duplicate() {
      return new MultiLineTextElement();
   }

   public void configureDefault(boolean createdWithPreset) {
      if (createdWithPreset) {
         this.resize(new Vector2i(this.getMinSize()));
      }

   }

   public void created() {
      this.parseColors();
   }

   public void renderContent(class_332 context, int x, int y, int width, int height, int screenWidth, int screenHeight, float delta, boolean inEditor) {
      for(int i = 0; i < this.reformatted.size(); ++i) {
         String s = (String)this.reformatted.get(i);
         int lineX = ((MultiLineTextElement.Alignment)this.alignment.get()).align(x + 4, width - 8, s);
         int var10000 = y + 4;
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         int lineY = var10000 + (9 + (Integer)this.spacing.get()) * i;
         if ((Boolean)this.gradient.get()) {
            GradientTextRenderer.renderGradientText(context, Utils.clearFormattingChars(s), lineX, lineY, this.parsedGradientColors, System.currentTimeMillis(), 2.0F, GradientTextRenderer.Animation.LINEAR_LOOPBACK_FORWARD, true, this.textShadows());
         } else {
            context.method_51433(CactusConstants.mc.field_1772, (String)this.reformatted.get(i), lineX, lineY, ((ColorSetting.ColorValue)this.textColor.get()).color(), this.textShadows());
         }
      }

   }

   public void render(class_332 context, int x, int y, int screenWidth, int screenHeight, float delta, boolean inEditor) {
      List<String> entries = ((List)this.lines.get()).stream().map((s) -> {
         return Utils.replaceTypeableFormattingChars(PlaceholderHandler.get().replacePlaceholders(s, (String)this.placeholderFormat.get()));
      }).toList();
      if (!entries.equals(this.reformatted)) {
         this.reformatted = entries;
         this.maxLineWidth = (Integer)this.reformatted.stream().map((t) -> {
            return CactusConstants.mc.field_1772.method_1727(t);
         }).max(Integer::compare).orElse(0);
         this.update();
      }

      super.render(context, x, y, screenWidth, screenHeight, delta, inEditor);
   }

   public void renderBackground(class_332 context, int x, int y, int width, int height, float delta, boolean inEditor) {
      int w = Math.max(width, this.maxLineWidth + 8);
      int posX = ((MultiLineTextElement.Alignment)this.alignment.get()).align(x, width, w);
      super.renderBackground(context, posX, y, w, height, delta, inEditor);
      if (inEditor) {
         int color = -1442775296;
         context.method_51738(x, x + width - 1, y, color);
         context.method_51738(x, x + width - 1, y + height - 1, color);
         context.method_51742(x, y, y + height - 1, color);
         context.method_51742(x + width - 1, y, y + height - 1, color);
      }

   }

   public Vector2i getMinSize() {
      Vector2i var10000;
      if (this.reformatted != null) {
         int var10002 = ((List)this.lines.get()).size() > 1 ? 40 : this.maxLineWidth + 8;
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         var10000 = new Vector2i(var10002, Math.max(8, (9 + (Integer)this.spacing.get()) * ((List)this.lines.get()).size() + 4));
      } else {
         var10000 = super.getMinSize();
      }

      return var10000;
   }

   public static enum Alignment {
      Left((x, w, text) -> {
         return x;
      }),
      Center((x, w, text) -> {
         return x + (w - text) / 2;
      }),
      Right((x, w, text) -> {
         return x + w - text;
      });

      private final MultiLineTextElement.AlignmentFunction function;

      private Alignment(MultiLineTextElement.AlignmentFunction function) {
         this.function = function;
      }

      public int align(int x, int width, String text) {
         return this.align(x, width, CactusConstants.mc.field_1772.method_1727(text));
      }

      public int align(int x, int width, int textWidth) {
         return this.function.align(x, width, textWidth);
      }

      // $FF: synthetic method
      private static MultiLineTextElement.Alignment[] $values() {
         return new MultiLineTextElement.Alignment[]{Left, Center, Right};
      }
   }

   private interface AlignmentFunction {
      int align(int var1, int var2, int var3);
   }
}
