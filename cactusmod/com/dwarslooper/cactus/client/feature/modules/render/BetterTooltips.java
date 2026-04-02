package com.dwarslooper.cactus.client.feature.modules.render;

import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.group.SettingGroup;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.generic.RomanNumber;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_5244;
import net.minecraft.class_5250;
import net.minecraft.class_5684;

public class BetterTooltips extends Module {
   public SettingGroup sg;
   public SettingGroup sgEnchantments;
   public SettingGroup sgTrims;
   public Setting<Boolean> scrollableTooltips;
   public Setting<Boolean> showShulkerTooltip;
   public Setting<Boolean> shulkerColor;
   public Setting<Boolean> overwriteDefaultTooltip;
   public Setting<Boolean> showMapTooltip;
   public Setting<Boolean> showBannerTooltip;
   public Setting<Boolean> showArmorstandTooltip;
   public Setting<Boolean> fixHighEnchantmentNames;
   public Setting<Boolean> showLevelAsNumber;
   public Setting<Boolean> maxLevelOnKey;
   public Setting<Boolean> showArmorTrimTooltip;
   public Setting<Boolean> rotateArmorPreview;

   public BetterTooltips() {
      super("better_tooltips", ModuleManager.CATEGORY_RENDERING, (new Module.Options()).set(Module.Flag.HUD_LISTED, false));
      this.sg = this.settings.getDefault();
      this.sgEnchantments = this.settings.buildGroup("enchantments");
      this.sgTrims = this.settings.buildGroup("armorTrims");
      this.scrollableTooltips = this.sg.add(new BooleanSetting("scrollableTooltips", false));
      this.showShulkerTooltip = this.sg.add(new BooleanSetting("shulkerTooltip", true));
      this.shulkerColor = this.sg.add(new BooleanSetting("shulkerColor", true));
      this.overwriteDefaultTooltip = this.sg.add(new BooleanSetting("overwriteShulkerDefault", true));
      this.showMapTooltip = this.sg.add(new BooleanSetting("mapTooltip", true));
      this.showBannerTooltip = this.sg.add(new BooleanSetting("bannerTooltip", true));
      this.showArmorstandTooltip = this.sg.add(new BooleanSetting("armorstandTooltip", true));
      this.fixHighEnchantmentNames = this.sgEnchantments.add(new BooleanSetting("fixRomanNumerals", true));
      this.showLevelAsNumber = this.sgEnchantments.add(new BooleanSetting("levelAsNumber", false));
      this.maxLevelOnKey = this.sgEnchantments.add(new BooleanSetting("showMaxOnKey", true));
      this.showArmorTrimTooltip = this.sgTrims.add(new BooleanSetting("showTrims", true));
      this.rotateArmorPreview = this.sgTrims.add(new BooleanSetting("rotateTrimPreview", false));
      TooltipComponentCallback.EVENT.register((data) -> {
         if (data instanceof class_5684) {
            class_5684 component = (class_5684)data;
            return component;
         } else {
            return null;
         }
      });
   }

   public class_2561 getEnchantmentLevelTooltip(int level, int maxLevel) {
      boolean asNumber = (Boolean)this.showLevelAsNumber.get();
      boolean fixHigh = (Boolean)this.fixHighEnchantmentNames.get();
      class_5250 text = class_2561.method_43470(asNumber ? Integer.toString(level) : (fixHigh ? RomanNumber.toRoman(level) : class_2561.method_43471("enchantment.level." + level).getString()));
      if ((Boolean)this.maxLevelOnKey.get() && class_310.method_1551().method_74189()) {
         text.method_10852(class_5244.field_41874).method_10852(class_2561.method_43470("/")).method_10852(class_5244.field_41874).method_27693(asNumber ? Integer.toString(maxLevel) : RomanNumber.toRoman(maxLevel));
      }

      return text;
   }
}
