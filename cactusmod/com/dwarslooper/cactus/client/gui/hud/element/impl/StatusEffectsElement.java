package com.dwarslooper.cactus.client.gui.hud.element.impl;

import com.dwarslooper.cactus.client.gui.hud.element.DynamicHudElement;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.ColorSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.EnumSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.IntegerSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import com.dwarslooper.cactus.client.util.generic.RomanNumber;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_1058;
import net.minecraft.class_10725;
import net.minecraft.class_10799;
import net.minecraft.class_1291;
import net.minecraft.class_1292;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_329;
import net.minecraft.class_332;
import net.minecraft.class_5244;
import net.minecraft.class_6880;
import net.minecraft.class_7923;
import net.minecraft.class_9848;
import org.joml.Vector2i;

public class StatusEffectsElement extends DynamicHudElement<StatusEffectsElement> {
   private static final class_2561 INFINITE_TEXT = class_2561.method_43470("Infinite");
   private static final Vector2i BASE_SIZE = new Vector2i(100, 56);
   private final List<StatusEffectsElement.WrappedEffect> currentEffects = new ArrayList();
   private final Setting<Boolean> oneLine;
   private final Setting<Boolean> compact;
   private final Setting<Boolean> showIcon;
   private final Setting<StatusEffectsElement.ColorBarMode> colorBarMode;
   private final Setting<ColorSetting.ColorValue> staticBarColor;
   private final Setting<StatusEffectsElement.InfiniteDisplay> infiniteDisplayMode;
   private final Setting<StatusEffectsElement.AmplifierNotation> amplifierNotation;
   private final Setting<StatusEffectsElement.SortOrder> sortOrder;
   private final Setting<Integer> spacing;
   private final Setting<Integer> maxEffects;
   private final List<StatusEffectsElement.WrappedEffect> SAMPLE_EFFECTS;

   public StatusEffectsElement() {
      super("statusEffects", BASE_SIZE);
      this.oneLine = this.elementGroup.add(new BooleanSetting("oneLine", false)).setCallback((b) -> {
         this.correct();
      });
      Setting var10001 = this.elementGroup.add(new BooleanSetting("compact", false)).setCallback((b) -> {
         this.correct();
      });
      Setting var10002 = this.oneLine;
      Objects.requireNonNull(var10002);
      this.compact = var10001.visibleIf(var10002::get);
      this.showIcon = this.elementGroup.add(new BooleanSetting("showIcon", true)).setCallback((b) -> {
         this.correct();
      }).visibleIf(() -> {
         return !(Boolean)this.oneLine.get();
      });
      this.colorBarMode = this.elementGroup.add(new EnumSetting("colorBarMode", StatusEffectsElement.ColorBarMode.Effect)).setCallback((m) -> {
         this.correct();
      });
      this.staticBarColor = this.elementGroup.add(new ColorSetting("barColor", ColorSetting.ColorValue.of(new Color(8973126), false))).visibleIf(() -> {
         return this.colorBarMode.get() == StatusEffectsElement.ColorBarMode.Static;
      });
      this.infiniteDisplayMode = this.elementGroup.add(new EnumSetting("infiniteDisplayMode", StatusEffectsElement.InfiniteDisplay.Symbol));
      this.amplifierNotation = this.elementGroup.add(new EnumSetting("amplifierNotation", StatusEffectsElement.AmplifierNotation.Roman));
      this.sortOrder = this.elementGroup.add(new EnumSetting("sortOrder", StatusEffectsElement.SortOrder.Default));
      this.spacing = this.elementGroup.add((new IntegerSetting("spacing", 2)).min(0).max(20)).setCallback((i) -> {
         this.correct();
      });
      this.maxEffects = this.elementGroup.add((new IntegerSetting("maxEffects", 2)).min(1).max(class_7923.field_41174.method_10204()));
      this.SAMPLE_EFFECTS = List.of(this.createEffect(class_1294.field_5904, -1, 1), this.createEffect(class_1294.field_5924, 42, 3), this.createEffect(class_1294.field_5909, 62, 0));
   }

   public boolean canResize() {
      return false;
   }

   public StatusEffectsElement duplicate() {
      return new StatusEffectsElement();
   }

   public void created() {
      this.correct();
   }

   public void correct(int width, int height) {
      this.resize(this.getMaxSize(this.SAMPLE_EFFECTS));
      super.correct(width, height);
   }

   public void render(class_332 context, int x, int y, int screenWidth, int screenHeight, float delta, boolean inEditor) {
      this.currentEffects.clear();
      this.resize(this.getMaxSize(this.SAMPLE_EFFECTS));
      if (!inEditor && CactusConstants.mc.field_1724 != null) {
         int i = 1;

         for(Iterator var9 = CactusConstants.mc.field_1724.method_6026().iterator(); var9.hasNext(); ++i) {
            class_1293 statusEffect = (class_1293)var9.next();
            if (i > (Integer)this.maxEffects.get()) {
               break;
            }

            if (statusEffect.method_5592()) {
               this.currentEffects.add(new StatusEffectsElement.WrappedEffect(statusEffect));
            }
         }
      } else {
         this.currentEffects.addAll(this.SAMPLE_EFFECTS);
      }

      StatusEffectsElement.SortOrder sort = (StatusEffectsElement.SortOrder)this.sortOrder.get();
      if (sort != StatusEffectsElement.SortOrder.Default) {
         this.currentEffects.sort((e1, e2) -> {
            int var10000;
            switch(sort.ordinal()) {
            case 1:
               var10000 = Integer.compare(e2.getNameWidth(), e1.getNameWidth());
               break;
            case 2:
               var10000 = Integer.compare(e1.getNameWidth(), e2.getNameWidth());
               break;
            case 3:
               var10000 = Integer.compare(e1.instance().method_5584(), e2.instance().method_5584());
               break;
            case 4:
               var10000 = e1.getNameWithAmplifier().getString().compareTo(e2.getNameWithAmplifier().getString());
               break;
            default:
               throw new IllegalStateException();
            }

            return var10000;
         });
      }

      if (!this.currentEffects.isEmpty()) {
         super.render(context, x, y, screenWidth, screenHeight, delta, inEditor);
      }

   }

   public void renderContent(class_332 context, int elementX, int y, int width, int height, int screenWidth, int screenHeight, float delta, boolean inEditor) {
      int drawY = y + this.getBorderPadding();

      for(Iterator var11 = this.currentEffects.iterator(); var11.hasNext(); drawY += this.getEntryHeight() + this.getPaddingBetween()) {
         StatusEffectsElement.WrappedEffect effect = (StatusEffectsElement.WrappedEffect)var11.next();
         int x = elementX + this.getBorderPadding();
         boolean singleLine = (Boolean)this.oneLine.get();
         class_1058 sprite = class_310.method_1551().method_72703().method_73025(class_10725.field_56385).method_4608(class_329.method_71644(effect.instance().method_5579()));
         int effectColor = class_9848.method_61330(255, ((class_1291)effect.instance().method_5579().comp_349()).method_5556());
         boolean hasColorBar = this.colorBarMode.get() != StatusEffectsElement.ColorBarMode.None;
         int textY;
         if (hasColorBar) {
            int var10000;
            switch(((StatusEffectsElement.ColorBarMode)this.colorBarMode.get()).ordinal()) {
            case 0:
            case 3:
               var10000 = ((ColorSetting.ColorValue)this.staticBarColor.get()).color();
               break;
            case 1:
               var10000 = ((class_1291)effect.instance().method_5579().comp_349()).method_5573() ? -16711936 : -65536;
               break;
            case 2:
               var10000 = effectColor;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
            }

            textY = var10000;
            context.method_25294(x, drawY, x + 2, drawY + this.getEntryHeight(), textY);
            x += 4;
         }

         textY = drawY + ((Boolean)this.oneLine.get() ? 1 : 2);
         if (!singleLine && (Boolean)this.showIcon.get()) {
            context.method_52709(class_10799.field_56883, sprite, x, textY, 18, 18);
            x += 20;
         }

         class_2561 durationText = effect.getDurationAsText();
         class_2561 displayName = effect.getNameWithAmplifier();
         context.method_51439(CactusConstants.mc.field_1772, displayName, x, textY, effectColor, this.textShadows());
         if (durationText != null) {
            if (singleLine) {
               context.method_51439(CactusConstants.mc.field_1772, durationText, (Boolean)this.compact.get() ? x + CactusConstants.mc.field_1772.method_27525(displayName) + 4 : elementX + width - CactusConstants.mc.field_1772.method_27525(durationText) - this.getBorderPadding(), textY, -1, this.textShadows());
            } else {
               class_327 var10001 = CactusConstants.mc.field_1772;
               Objects.requireNonNull(CactusConstants.mc.field_1772);
               context.method_51439(var10001, durationText, x, textY + 9 + 2, -1, this.textShadows());
            }
         }
      }

   }

   public Vector2i getEffectiveSize() {
      return this.getMaxSize(this.currentEffects);
   }

   private Vector2i getMaxSize(Collection<StatusEffectsElement.WrappedEffect> effectList) {
      int p = this.getBorderPadding();
      int height = effectList.size() * this.getEntryHeight() + (effectList.size() - 1) * this.getPaddingBetween() + p;
      int barWidth = this.colorBarMode.get() == StatusEffectsElement.ColorBarMode.None ? 0 : 4;
      int iconWidth = this.canRenderIcon() ? 20 : 0;
      int maxWidth = (Integer)effectList.stream().map((e) -> {
         return barWidth + iconWidth + this.getBorderPadding() + CactusConstants.mc.field_1772.method_27525(e.getNameWithAmplifier()) + ((Boolean)this.oneLine.get() ? 4 + CactusConstants.mc.field_1772.method_27525(e.getDurationAsText()) : 0);
      }).reduce(0, Integer::max);
      return new Vector2i(maxWidth + p, height + p);
   }

   private boolean canRenderIcon() {
      return (Boolean)this.showIcon.get() && !(Boolean)this.oneLine.get();
   }

   private int getBorderPadding() {
      return 4;
   }

   private int getPaddingBetween() {
      return (Integer)this.spacing.get();
   }

   private int getEntryHeight() {
      return (Boolean)this.oneLine.get() ? 10 : 22;
   }

   private StatusEffectsElement.WrappedEffect createEffect(class_6880<class_1291> effect, int seconds, int amplifier) {
      return new StatusEffectsElement.WrappedEffect(new class_1293(effect, 20 * seconds, amplifier));
   }

   public static enum ColorBarMode {
      None,
      Advantage,
      Effect,
      Static;

      // $FF: synthetic method
      private static StatusEffectsElement.ColorBarMode[] $values() {
         return new StatusEffectsElement.ColorBarMode[]{None, Advantage, Effect, Static};
      }
   }

   public static enum InfiniteDisplay {
      Symbol,
      Text,
      HideTime;

      // $FF: synthetic method
      private static StatusEffectsElement.InfiniteDisplay[] $values() {
         return new StatusEffectsElement.InfiniteDisplay[]{Symbol, Text, HideTime};
      }
   }

   public static enum AmplifierNotation {
      Roman,
      Number;

      // $FF: synthetic method
      private static StatusEffectsElement.AmplifierNotation[] $values() {
         return new StatusEffectsElement.AmplifierNotation[]{Roman, Number};
      }
   }

   public static enum SortOrder {
      Default,
      NameLength,
      NameLengthReverse,
      Duration,
      Alphabetical;

      // $FF: synthetic method
      private static StatusEffectsElement.SortOrder[] $values() {
         return new StatusEffectsElement.SortOrder[]{Default, NameLength, NameLengthReverse, Duration, Alphabetical};
      }
   }

   private class WrappedEffect {
      private final class_1293 delegate;

      public WrappedEffect(class_1293 delegate) {
         this.delegate = delegate;
      }

      public class_1293 instance() {
         return this.delegate;
      }

      public class_2561 getDurationAsText() {
         if (this.delegate.method_48559() && StatusEffectsElement.this.infiniteDisplayMode.get() != StatusEffectsElement.InfiniteDisplay.Symbol) {
            return StatusEffectsElement.this.infiniteDisplayMode.get() == StatusEffectsElement.InfiniteDisplay.Text ? StatusEffectsElement.INFINITE_TEXT : null;
         } else {
            return class_1292.method_5577(this.delegate, 1.0F, (Float)Utils.orElse(CactusConstants.mc.field_1687, (w) -> {
               return w.method_54719().method_54748();
            }, 20.0F));
         }
      }

      public class_2561 getNameWithAmplifier() {
         return ((class_1291)this.delegate.method_5579().comp_349()).method_5560().method_27661().method_10852(class_5244.field_41874).method_27693(this.getAmplifierNamed());
      }

      private String getAmplifierNamed() {
         int amplifier = this.delegate.method_5578() + 1;
         return StatusEffectsElement.this.amplifierNotation.get() == StatusEffectsElement.AmplifierNotation.Roman ? RomanNumber.toRoman(amplifier) : Integer.toString(amplifier);
      }

      public int getNameWidth() {
         return CactusConstants.mc.field_1772.method_27525(this.getNameWithAmplifier());
      }
   }
}
