package com.dwarslooper.cactus.client.gui.hud.element.impl;

import com.dwarslooper.cactus.client.gui.hud.element.DynamicHudElement;
import com.dwarslooper.cactus.client.systems.config.settings.group.SettingGroup;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.ColorSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.EnumSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.IntegerSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.generic.RomanNumber;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.class_1058;
import net.minecraft.class_10725;
import net.minecraft.class_10799;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_310;
import net.minecraft.class_329;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_9848;
import org.joml.Vector2i;

public class PotionStatusElement extends DynamicHudElement<PotionStatusElement> {
   public static final PotionStatusElement INSTANCE = new PotionStatusElement();
   public SettingGroup generalSettings;
   public SettingGroup displaySettings;
   public SettingGroup colorSettings;
   public SettingGroup barSettings;
   public SettingGroup indicatorSettings;
   public SettingGroup animationSettings;
   public SettingGroup effectFormatSettings;
   public Setting<Boolean> showMilliseconds;
   public Setting<Boolean> sortByDuration;
   public Setting<Boolean> fadeEffects;
   public Setting<Integer> maxVisibleEffects;
   public Setting<Boolean> advancedAnimationSettings;
   public Setting<Boolean> animateEffects;
   public Setting<Integer> animationSpeed;
   public Setting<Boolean> smoothFading;
   public Setting<Boolean> pulseWhenLow;
   public Setting<Integer> pulseLowThreshold;
   public Setting<Boolean> showEffectIcon;
   public Setting<Boolean> useCompactMode;
   public Setting<Integer> effectSpacing;
   public Setting<ColorSetting.ColorValue> effectNameColor;
   public Setting<ColorSetting.ColorValue> effectTimeColor;
   public Setting<ColorSetting.ColorValue> beneficialColor;
   public Setting<ColorSetting.ColorValue> harmfulColor;
   public Setting<ColorSetting.ColorValue> infiniteEffectColor;
   public Setting<PotionStatusElement.ColorBarMode> colorBarMode;
   public Setting<ColorSetting.ColorValue> customBarColor;
   public Setting<Integer> colorBarWidth;
   public Setting<PotionStatusElement.EffectIndicatorMode> indicatorMode;
   public Setting<Boolean> separateBeneficial;
   public Setting<Boolean> showIndicatorTitles;
   public Setting<Boolean> animateOnRemove;
   public Setting<PotionStatusElement.InfiniteEffectDisplay> infiniteEffectDisplay;
   public Setting<PotionStatusElement.AmplifierNotation> amplifierNotation;
   private final Map<String, PotionStatusElement.PotionEffect> previousEffects;
   private final Map<String, PotionStatusElement.EffectAnimation> animatedEffects;
   private long lastUpdateTime;

   public PotionStatusElement() {
      super("potion_status", new Vector2i(156, 80), new Vector2i(5, 10));
      this.generalSettings = this.settings.buildGroup("general");
      this.displaySettings = this.settings.buildGroup("display");
      this.colorSettings = this.settings.buildGroup("colors");
      this.barSettings = this.settings.buildGroup("sidebar");
      this.indicatorSettings = this.settings.buildGroup("indicators");
      this.animationSettings = this.settings.buildGroup("animation");
      this.effectFormatSettings = this.settings.buildGroup("format");
      this.showMilliseconds = this.generalSettings.add(new BooleanSetting("showMilliseconds", false));
      this.sortByDuration = this.generalSettings.add(new BooleanSetting("sortByDuration", true));
      this.fadeEffects = this.generalSettings.add(new BooleanSetting("fadeEffects", true));
      this.maxVisibleEffects = this.generalSettings.add((new IntegerSetting("maxVisibleEffects", 2)).min(1).max(10).visibleIf(() -> {
         return (Boolean)this.fadeEffects.get();
      }));
      this.advancedAnimationSettings = this.animationSettings.add(new BooleanSetting("advancedAnimationSettings", false));
      this.animateEffects = this.animationSettings.add(new BooleanSetting("animateEffects", true));
      this.animationSpeed = this.animationSettings.add((new IntegerSetting("animationSpeed", 1)).min(1).max(30).visibleIf(() -> {
         return (Boolean)this.animateEffects.get();
      }));
      this.smoothFading = this.animationSettings.add((new BooleanSetting("smoothFading", true)).visibleIf(() -> {
         return (Boolean)this.fadeEffects.get() && (Boolean)this.animateEffects.get() && (Boolean)this.advancedAnimationSettings.get();
      }));
      this.pulseWhenLow = this.animationSettings.add((new BooleanSetting("pulseWhenLow", true)).visibleIf(() -> {
         return (Boolean)this.advancedAnimationSettings.get();
      }));
      this.pulseLowThreshold = this.animationSettings.add((new IntegerSetting("pulseLowThreshold", 10)).min(1).max(60).visibleIf(() -> {
         return (Boolean)this.pulseWhenLow.get() && (Boolean)this.advancedAnimationSettings.get();
      }));
      this.showEffectIcon = this.displaySettings.add(new BooleanSetting("showEffectIcon", true));
      this.useCompactMode = this.displaySettings.add(new BooleanSetting("useCompactMode", false));
      this.effectSpacing = this.displaySettings.add((new IntegerSetting("effectSpacing", 2)).min(0).max(10));
      this.effectNameColor = this.colorSettings.add(new ColorSetting("effectNameColor", ColorSetting.ColorValue.of(new Color(16777215), false)));
      this.effectTimeColor = this.colorSettings.add(new ColorSetting("effectTimeColor", ColorSetting.ColorValue.of(new Color(16777215), false)));
      this.beneficialColor = this.colorSettings.add(new ColorSetting("beneficialColor", ColorSetting.ColorValue.of(new Color(5635925), false)));
      this.harmfulColor = this.colorSettings.add(new ColorSetting("harmfulColor", ColorSetting.ColorValue.of(new Color(16733525), false)));
      this.infiniteEffectColor = this.colorSettings.add(new ColorSetting("infiniteEffectColor", ColorSetting.ColorValue.of(new Color(16755200), false)));
      this.colorBarMode = this.barSettings.add(new EnumSetting("colorBarMode", PotionStatusElement.ColorBarMode.BENEFICIAL_HARMFUL));
      this.customBarColor = this.barSettings.add((new ColorSetting("customBarColor", ColorSetting.ColorValue.of(new Color(8939212), false))).visibleIf(() -> {
         return this.colorBarMode.get() == PotionStatusElement.ColorBarMode.CUSTOM;
      }));
      this.colorBarWidth = this.barSettings.add((new IntegerSetting("colorBarWidth", 2)).min(1).max(6).visibleIf(() -> {
         return this.colorBarMode.get() != PotionStatusElement.ColorBarMode.NONE;
      }));
      this.indicatorMode = this.indicatorSettings.add(new EnumSetting("indicatorMode", PotionStatusElement.EffectIndicatorMode.ICON));
      this.separateBeneficial = this.indicatorSettings.add(new BooleanSetting("separateBeneficial", false));
      this.showIndicatorTitles = this.indicatorSettings.add((new BooleanSetting("showIndicatorTitles", true)).visibleIf(() -> {
         return (Boolean)this.separateBeneficial.get();
      }));
      this.animateOnRemove = this.animationSettings.add(new BooleanSetting("animateOnRemove", true));
      this.infiniteEffectDisplay = this.effectFormatSettings.add(new EnumSetting("infiniteEffectDisplay", PotionStatusElement.InfiniteEffectDisplay.SHOW_INFINITE_TEXT));
      this.amplifierNotation = this.effectFormatSettings.add(new EnumSetting("amplifierNotation", PotionStatusElement.AmplifierNotation.ROMAN));
      this.previousEffects = new HashMap();
      this.animatedEffects = new HashMap();
      this.lastUpdateTime = System.currentTimeMillis();
   }

   public PotionStatusElement duplicate() {
      return new PotionStatusElement();
   }

   public void renderContent(class_332 context, int x, int y, int width, int height, int screenWidth, int screenHeight, float delta, boolean inEditor) {
      if (inEditor || class_310.method_1551().field_1724 != null) {
         List<PotionStatusElement.PotionEffect> potionEffects = new ArrayList();
         Map<String, PotionStatusElement.PotionEffect> currentEffects = new HashMap();
         if (inEditor) {
            potionEffects.add(new PotionStatusElement.PotionEffect(10, "Speed", true, new Color(8171462), false));
            potionEffects.add(new PotionStatusElement.PotionEffect(180, "Strength", true, new Color(14188339), true));
            potionEffects.add(new PotionStatusElement.PotionEffect(300, "Regeneration", true, new Color(13458603), false));
            potionEffects.add(new PotionStatusElement.PotionEffect(15, "Weakness", false, new Color(4866583), false));
         } else {
            class_310.method_1551().field_1724.method_6026().forEach((effectInstance) -> {
               if (effectInstance.method_5592()) {
                  PotionStatusElement.PotionEffect effect = new PotionStatusElement.PotionEffect(effectInstance);
                  potionEffects.add(effect);
                  currentEffects.put(effect.getEffectId(), effect);
               }

            });
            long currentTime = System.currentTimeMillis();
            float deltaTime = (float)(currentTime - this.lastUpdateTime) / 1000.0F;
            this.lastUpdateTime = currentTime;
            Iterator it;
            Entry entry;
            if ((Boolean)this.animateOnRemove.get()) {
               it = this.previousEffects.entrySet().iterator();

               while(it.hasNext()) {
                  entry = (Entry)it.next();
                  String effectId = (String)entry.getKey();
                  PotionStatusElement.PotionEffect effect = (PotionStatusElement.PotionEffect)entry.getValue();
                  if (!currentEffects.containsKey(effectId) && !this.animatedEffects.containsKey(effectId)) {
                     this.animatedEffects.put(effectId, new PotionStatusElement.EffectAnimation(this, effect));
                  }
               }
            }

            if ((Boolean)this.animateOnRemove.get()) {
               it = this.animatedEffects.entrySet().iterator();

               while(it.hasNext()) {
                  entry = (Entry)it.next();
                  PotionStatusElement.EffectAnimation animation = (PotionStatusElement.EffectAnimation)entry.getValue();
                  animation.update(deltaTime);
                  if (animation.isDone()) {
                     it.remove();
                  } else if (!currentEffects.containsKey(entry.getKey())) {
                     potionEffects.add(animation.effect);
                  } else {
                     it.remove();
                  }
               }
            }

            this.previousEffects.clear();
            this.previousEffects.putAll(currentEffects);
         }

         if ((Boolean)this.sortByDuration.get()) {
            potionEffects.sort(Comparator.comparingInt(PotionStatusElement.PotionEffect::getRemainingTimeSeconds));
         }

         if ((Boolean)this.separateBeneficial.get()) {
            List<PotionStatusElement.PotionEffect> beneficial = new ArrayList();
            List<PotionStatusElement.PotionEffect> harmful = new ArrayList();
            Iterator var20 = potionEffects.iterator();

            while(var20.hasNext()) {
               PotionStatusElement.PotionEffect effect = (PotionStatusElement.PotionEffect)var20.next();
               if (effect.isBeneficial()) {
                  beneficial.add(effect);
               } else {
                  harmful.add(effect);
               }
            }

            String beneficialTitle = (Boolean)this.showIndicatorTitles.get() ? "Beneficial" : null;
            String harmfulTitle = (Boolean)this.showIndicatorTitles.get() ? "Harmful" : null;
            this.getStatusEffectDisplay(context, x, y, beneficial, beneficialTitle, inEditor);
            int beneficialHeight = this.calculateEffectsHeight(beneficial, beneficialTitle != null);
            this.getStatusEffectDisplay(context, x, y + beneficialHeight + (beneficialHeight > 0 ? 5 : 0), harmful, harmfulTitle, inEditor);
         } else {
            this.getStatusEffectDisplay(context, x, y, potionEffects, (String)null, inEditor);
         }
      }

   }

   private int calculateEffectsHeight(List<PotionStatusElement.PotionEffect> effects, boolean hasTitle) {
      if (effects.isEmpty() && !hasTitle) {
         return 0;
      } else {
         int lineHeight = 10;
         int height = 0;
         if (hasTitle) {
            height += lineHeight + 4;
         }

         for(int i = 0; i < effects.size(); ++i) {
            height += (Boolean)this.showEffectIcon.get() ? 24 : lineHeight;
            height += i < effects.size() - 1 ? (Integer)this.effectSpacing.get() : 0;
         }

         return height;
      }
   }

   private void getStatusEffectDisplay(class_332 context, int x, int y, List<PotionStatusElement.PotionEffect> effects, String title, boolean inEditor) {
      if (!effects.isEmpty() || title != null) {
         int lineHeight = 10;
         int offsetY = 0;
         int barWidth = this.colorBarMode.get() != PotionStatusElement.ColorBarMode.NONE ? (Integer)this.colorBarWidth.get() : 0;
         if (title != null) {
            context.method_51433(class_310.method_1551().field_1772, title, x, y, ((ColorSetting.ColorValue)this.effectNameColor.get()).color(), false);
            offsetY += lineHeight + 4;
         }

         int effectsCount = effects.size();
         int maxEffects = (Boolean)this.fadeEffects.get() ? (Integer)this.maxVisibleEffects.get() : Integer.MAX_VALUE;
         float time = (float)(System.currentTimeMillis() % 10000L) / 1000.0F;
         float animSpeed = (Boolean)this.animateEffects.get() ? (float)(Integer)this.animationSpeed.get() : 1.0F;

         for(int i = 0; i < effectsCount; ++i) {
            PotionStatusElement.PotionEffect effect = (PotionStatusElement.PotionEffect)effects.get(i);
            float fadeAlpha = 1.0F;
            double sin = Math.sin((double)(time * animSpeed + (float)i * 0.5F));
            if ((Boolean)this.fadeEffects.get() && i >= maxEffects) {
               float fadePosition = (float)(i - maxEffects + 1) / 4.0F;
               if ((Boolean)this.smoothFading.get() && (Boolean)this.animateEffects.get()) {
                  float animation = (float)sin * 0.1F;
                  fadeAlpha = (float)Math.exp((double)(-(fadePosition + animation) * 2.0F));
               } else {
                  fadeAlpha = (float)Math.exp((double)(-fadePosition * 2.0F));
               }

               if (fadeAlpha < 0.03F) {
                  continue;
               }
            }

            String effectName = effect.getName();
            int textColor = ((ColorSetting.ColorValue)this.effectNameColor.get()).color();
            int timeColor = effect.isInfinite() ? ((ColorSetting.ColorValue)this.infiniteEffectColor.get()).color() : ((ColorSetting.ColorValue)this.effectTimeColor.get()).color();
            if (this.indicatorMode.get() == PotionStatusElement.EffectIndicatorMode.TEXT_PREFIX && !(Boolean)this.separateBeneficial.get()) {
               if (effect.isBeneficial()) {
                  effectName = "§a+§r " + effectName;
               } else {
                  effectName = "§c-§r " + effectName;
               }
            } else if (this.indicatorMode.get() == PotionStatusElement.EffectIndicatorMode.TEXT_COLOR && !(Boolean)this.separateBeneficial.get()) {
               textColor = effect.isBeneficial() ? ((ColorSetting.ColorValue)this.beneficialColor.get()).color() : ((ColorSetting.ColorValue)this.harmfulColor.get()).color();
            }

            String timeDisplay;
            if (effect.isInfinite()) {
               timeDisplay = effect.getInfiniteText();
            } else if ((Boolean)this.showMilliseconds.get()) {
               timeDisplay = effect.getFormattedTimeWithMilliseconds();
            } else {
               timeDisplay = effect.getFormattedTimeToEnd();
            }

            Color color = effect.getColor();
            float iconAlpha = fadeAlpha;
            float nameAlpha = fadeAlpha;
            float timeAlpha = fadeAlpha;
            if ((Boolean)this.animateEffects.get() && !inEditor) {
               float effectOffset = (float)i * 0.7F;
               float animValue = (float)Math.sin((double)((time + effectOffset) * animSpeed * 0.5F)) * 0.03F;
               iconAlpha = class_3532.method_15363(fadeAlpha * (1.0F + animValue), 0.0F, 1.0F);
               nameAlpha = class_3532.method_15363(fadeAlpha * (1.0F + animValue), 0.0F, 1.0F);
               timeAlpha = class_3532.method_15363(fadeAlpha * (1.0F + animValue), 0.0F, 1.0F);
            }

            int adjustedTextColor = this.applyAlpha(textColor, nameAlpha);
            int adjustedTimeColor = this.applyAlpha(timeColor, timeAlpha);
            int var10000;
            int indicatorColor;
            int adjustedIndicatorColor;
            if ((Boolean)this.showEffectIcon.get() && effect.hasStatusEffect()) {
               if (this.colorBarMode.get() != PotionStatusElement.ColorBarMode.NONE) {
                  switch(((PotionStatusElement.ColorBarMode)this.colorBarMode.get()).ordinal()) {
                  case 1:
                     var10000 = effect.getColor().getRGB();
                     break;
                  case 2:
                     var10000 = effect.isBeneficial() ? ((ColorSetting.ColorValue)this.beneficialColor.get()).color() : ((ColorSetting.ColorValue)this.harmfulColor.get()).color();
                     break;
                  case 3:
                     var10000 = ((ColorSetting.ColorValue)this.customBarColor.get()).color();
                     break;
                  default:
                     var10000 = -1;
                  }

                  indicatorColor = var10000;
                  float barAlpha = iconAlpha;
                  if ((Boolean)this.animateEffects.get() && !effect.isInfinite() && !inEditor) {
                     float remainingPercent = class_3532.method_15363((float)effect.getRemainingTimeSeconds() / (float)(Integer)this.pulseLowThreshold.get(), 0.0F, 1.0F);
                     float barPulse = (float)sin * 0.1F * (1.0F - remainingPercent);
                     barAlpha = class_3532.method_15363(iconAlpha * (1.0F + barPulse), 0.0F, 1.0F);
                  }

                  indicatorColor = this.applyAlpha(indicatorColor, barAlpha);
                  context.method_25294(x, y + offsetY, x + barWidth, y + offsetY + 24, indicatorColor);
               }

               if (!inEditor) {
                  class_1058 sprite = effect.getSprite();
                  if (sprite != null) {
                     context.method_52710(class_10799.field_56883, sprite, x + barWidth + 3, y + offsetY + 3, 18, 18, class_9848.method_61324((int)(iconAlpha * 255.0F), 1, 1, 1));
                  }
               } else {
                  indicatorColor = this.applyAlpha(color.getRGB(), iconAlpha);
                  context.method_25294(x + barWidth + 3, y + offsetY + 3, x + barWidth + 3 + 18, y + offsetY + 3 + 18, indicatorColor);
               }

               if (this.indicatorMode.get() == PotionStatusElement.EffectIndicatorMode.ICON && !(Boolean)this.separateBeneficial.get()) {
                  indicatorColor = effect.isBeneficial() ? ((ColorSetting.ColorValue)this.beneficialColor.get()).color() : ((ColorSetting.ColorValue)this.harmfulColor.get()).color();
                  adjustedIndicatorColor = this.applyAlpha(indicatorColor, iconAlpha);
                  context.method_25294(x + barWidth + 21, y + offsetY + 3, x + barWidth + 24, y + offsetY + 6, adjustedIndicatorColor);
               }

               context.method_51433(class_310.method_1551().field_1772, effectName, x + barWidth + 28, y + offsetY + 6, adjustedTextColor, false);
               context.method_51433(class_310.method_1551().field_1772, timeDisplay, x + barWidth + 28, y + offsetY + 16, adjustedTimeColor, false);
               offsetY += 24 + (Integer)this.effectSpacing.get();
            } else {
               if (this.colorBarMode.get() != PotionStatusElement.ColorBarMode.NONE) {
                  switch(((PotionStatusElement.ColorBarMode)this.colorBarMode.get()).ordinal()) {
                  case 1:
                     var10000 = effect.getColor().getRGB();
                     break;
                  case 2:
                     var10000 = effect.isBeneficial() ? ((ColorSetting.ColorValue)this.beneficialColor.get()).color() : ((ColorSetting.ColorValue)this.harmfulColor.get()).color();
                     break;
                  case 3:
                     var10000 = ((ColorSetting.ColorValue)this.customBarColor.get()).color();
                     break;
                  default:
                     var10000 = -1;
                  }

                  indicatorColor = var10000;
                  indicatorColor = this.applyAlpha(indicatorColor, iconAlpha);
                  context.method_25294(x, y + offsetY, x + barWidth, y + offsetY + lineHeight, indicatorColor);
               }

               indicatorColor = class_310.method_1551().field_1772.method_1727(effectName);
               context.method_51433(class_310.method_1551().field_1772, effectName, x + barWidth + 10, y + offsetY, adjustedTextColor, false);
               adjustedIndicatorColor = (Boolean)this.useCompactMode.get() ? x + barWidth + indicatorColor + 15 : x + barWidth + 100;
               context.method_51433(class_310.method_1551().field_1772, timeDisplay, adjustedIndicatorColor, y + offsetY, adjustedTimeColor, false);
               if (this.indicatorMode.get() == PotionStatusElement.EffectIndicatorMode.ICON && !(Boolean)this.separateBeneficial.get()) {
                  int indicatorColor = effect.isBeneficial() ? ((ColorSetting.ColorValue)this.beneficialColor.get()).color() : ((ColorSetting.ColorValue)this.harmfulColor.get()).color();
                  int adjustedIndicatorColor = this.applyAlpha(indicatorColor, iconAlpha);
                  context.method_25294(x + barWidth + 3, y + offsetY + 2, x + barWidth + 6, y + offsetY + 5, adjustedIndicatorColor);
               }

               offsetY += lineHeight + (Integer)this.effectSpacing.get();
            }
         }

      }
   }

   private int applyAlpha(int color, float alphaMultiplier) {
      int alpha = (int)((float)(color >> 24 & 255) * alphaMultiplier);
      alpha = class_3532.method_15340(alpha, 0, 255);
      return alpha << 24 | color & 16777215;
   }

   public static enum ColorBarMode {
      NONE,
      EFFECT_COLOR,
      BENEFICIAL_HARMFUL,
      CUSTOM;

      // $FF: synthetic method
      private static PotionStatusElement.ColorBarMode[] $values() {
         return new PotionStatusElement.ColorBarMode[]{NONE, EFFECT_COLOR, BENEFICIAL_HARMFUL, CUSTOM};
      }
   }

   public static enum EffectIndicatorMode {
      COLOR_BAR,
      TEXT_COLOR,
      TEXT_PREFIX,
      ICON;

      // $FF: synthetic method
      private static PotionStatusElement.EffectIndicatorMode[] $values() {
         return new PotionStatusElement.EffectIndicatorMode[]{COLOR_BAR, TEXT_COLOR, TEXT_PREFIX, ICON};
      }
   }

   public static enum InfiniteEffectDisplay {
      SHOW_INFINITE_TEXT,
      SHOW_ICON,
      HIDE_TIME;

      // $FF: synthetic method
      private static PotionStatusElement.InfiniteEffectDisplay[] $values() {
         return new PotionStatusElement.InfiniteEffectDisplay[]{SHOW_INFINITE_TEXT, SHOW_ICON, HIDE_TIME};
      }
   }

   public static enum AmplifierNotation {
      ROMAN,
      NUMBER;

      // $FF: synthetic method
      private static PotionStatusElement.AmplifierNotation[] $values() {
         return new PotionStatusElement.AmplifierNotation[]{ROMAN, NUMBER};
      }
   }

   private class PotionEffect {
      private final class_1293 effectInstance;
      private final class_1291 effect;
      private final int remainingTimeSeconds;
      private final String name;
      private final boolean beneficial;
      private final Color color;
      private final boolean ambient;
      private final boolean infinite;
      private final int amplifier;
      private final String effectId;

      public PotionEffect(class_1293 effectInstance) {
         this.effectInstance = effectInstance;
         this.effect = (class_1291)effectInstance.method_5579().comp_349();
         this.infinite = effectInstance.method_48559();
         this.remainingTimeSeconds = this.infinite ? Integer.MAX_VALUE : effectInstance.method_5584() / 20;
         this.amplifier = effectInstance.method_5578();
         String baseName = this.effect.method_5560().getString();
         this.name = baseName + this.getAmplifierString();
         this.beneficial = this.effect.method_5573();
         this.color = new Color(this.effect.method_5556());
         this.ambient = effectInstance.method_5591();
         String var10001 = this.effect.method_5567();
         this.effectId = var10001 + (this.amplifier > 0 ? "_" + this.amplifier : "");
      }

      public PotionEffect(int duration, String name, boolean beneficial, Color color, boolean ambient) {
         this.effectInstance = null;
         this.effect = null;
         this.infinite = duration < 0;
         this.remainingTimeSeconds = duration;
         this.amplifier = 1;
         this.name = name + " II";
         this.beneficial = beneficial;
         this.color = color;
         this.ambient = ambient;
         String var10001 = name.toLowerCase();
         this.effectId = var10001.replace(" ", "_") + "_1";
      }

      private String getAmplifierString() {
         if (this.amplifier <= 0) {
            return "";
         } else {
            return ((PotionStatusElement.AmplifierNotation)PotionStatusElement.this.amplifierNotation.get()).equals(PotionStatusElement.AmplifierNotation.ROMAN) ? " " + RomanNumber.toRoman(this.amplifier + 1) : " " + (this.amplifier + 1);
         }
      }

      public String getFormattedTimeToEnd() {
         if (this.infinite) {
            return "∞";
         } else {
            int seconds = this.remainingTimeSeconds;
            int minutes = seconds / 60;
            seconds %= 60;
            return String.format("%d:%02d", minutes, seconds);
         }
      }

      public String getFormattedTimeWithMilliseconds() {
         if (this.infinite) {
            return "∞";
         } else {
            int seconds = this.remainingTimeSeconds;
            int minutes = seconds / 60;
            seconds %= 60;
            int milliseconds = this.effectInstance != null ? this.effectInstance.method_5584() % 20 * 50 : 0;
            return String.format("%d:%02d.%03d", minutes, seconds, milliseconds);
         }
      }

      public String getInfiniteText() {
         String var10000;
         switch(((PotionStatusElement.InfiniteEffectDisplay)PotionStatusElement.this.infiniteEffectDisplay.get()).ordinal()) {
         case 0:
            var10000 = "Infinite";
            break;
         case 1:
            var10000 = "∞";
            break;
         case 2:
            var10000 = "";
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public String getName() {
         return this.name;
      }

      public Color getColor() {
         return this.color;
      }

      public boolean isBeneficial() {
         return this.beneficial;
      }

      public boolean isInfinite() {
         return this.infinite;
      }

      public boolean hasStatusEffect() {
         return this.effect != null;
      }

      public int getRemainingTimeSeconds() {
         return this.remainingTimeSeconds;
      }

      public class_1058 getSprite() {
         return this.effectInstance != null && this.effect != null ? class_310.method_1551().method_72703().method_73025(class_10725.field_56385).method_4608(class_329.method_71644(this.effectInstance.method_5579())) : null;
      }

      public String getEffectId() {
         return this.effectId;
      }
   }

   private class EffectAnimation {
      final PotionStatusElement.PotionEffect effect;
      long startTime;
      float progress;
      float alpha = 1.0F;

      EffectAnimation(final PotionStatusElement param1, PotionStatusElement.PotionEffect effect) {
         this.effect = effect;
         this.startTime = System.currentTimeMillis();
         this.progress = 0.0F;
      }

      void update(float deltaTime) {
         this.progress = Math.min(1.0F, (float)(System.currentTimeMillis() - this.startTime) / 500.0F);
         this.alpha = 1.0F - this.progress;
      }

      boolean isDone() {
         return this.progress >= 1.0F;
      }
   }

   public static enum PulseTarget {
      EVERYTHING;

      // $FF: synthetic method
      private static PotionStatusElement.PulseTarget[] $values() {
         return new PotionStatusElement.PulseTarget[]{EVERYTHING};
      }
   }
}
