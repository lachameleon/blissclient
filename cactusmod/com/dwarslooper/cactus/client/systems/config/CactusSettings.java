package com.dwarslooper.cactus.client.systems.config;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.gui.screen.impl.ButtonOptions;
import com.dwarslooper.cactus.client.systems.ISerializable;
import com.dwarslooper.cactus.client.systems.UpdateReason;
import com.dwarslooper.cactus.client.systems.config.impl.CactusConfig;
import com.dwarslooper.cactus.client.systems.config.settings.group.SettingGroup;
import com.dwarslooper.cactus.client.systems.config.settings.gui.SettingContainer;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.ColorListSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.ColorSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.EnumSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.IntegerSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.StringSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.SubGroupSetting;
import com.dwarslooper.cactus.client.util.generic.ColorUtils;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_2561;
import net.minecraft.class_5244;

public class CactusSettings implements ISerializable<CactusSettings> {
   public final SettingContainer settings = new SettingContainer();
   private final SettingGroup sg;
   private final SettingGroup sgIrc;
   private final SettingGroup sgButtons;
   private final SettingGroup sgLoadingOverlay;
   private final SettingGroup sgExperiments;
   public Setting<String> prefix;
   public Setting<String> commandPrefix;
   public Setting<Boolean> customClientBrand;
   public Setting<ColorSetting.ColorValue> accentColor;
   public Setting<Integer> fadingSpeed;
   public Setting<Boolean> updateNotify;
   public Setting<CactusSettings.ToggleFeedback> toggleFeedback;
   public Setting<Boolean> antiSecureChatWarning;
   public Setting<Boolean> disableTelemetry;
   public Setting<String> customLevelDirectory;
   public Setting<Integer> chatHistoryLength;
   public Setting<Boolean> preLoadHotbar;
   public Setting<Boolean> loadingOverlayUseVanilla;
   public Setting<Boolean> loadingOverlayShowSprite;
   public Setting<ColorSetting.ColorValue> loadingOverlayBackgroundColor;
   public Setting<ColorSetting.ColorValue> loadingOverlayProgressFillColor;
   public Setting<ColorSetting.ColorValue> loadingOverlayProgressBorderColor;
   public Setting<Boolean> ircConnectOnStart;
   public Setting<Boolean> enableIrcChat;
   public Setting<String> ircShortPrefix;
   public Setting<Boolean> showCosmetics;
   public Setting<Boolean> showNameBadge;
   public Setting<Boolean> compactUIDesign;
   public Setting<Boolean> collapseOther;
   public Setting<CactusSettings.ModuleButtonStyle> moduleWidgetStateStyle;
   public Setting<Boolean> favoritesOnTop;
   public Setting<Boolean> accountWidgetMultiplayer;
   public Setting<Boolean> accountStatusTextMultiplayer;
   public Setting<Boolean> dragDropReorderServers;
   public Setting<SettingContainer> buttonOptions;
   public Setting<Boolean> allowCusModification;
   public Setting<Boolean> showModuleDescriptionOnHover;
   public Setting<Boolean> cactusWindowIconAndTitle;
   public Setting<Boolean> allowSliderOverdrive;
   public Setting<Boolean> experiments;
   public Setting<Boolean> fancyText;
   public Setting<Boolean> legacyModuleScreen;
   public Setting<List<String>> gradientColors;
   public Setting<Boolean> uiDebug;

   public CactusSettings() {
      this.sg = this.settings.getDefault();
      this.sgIrc = this.settings.buildGroup("irc");
      this.sgButtons = this.settings.buildGroup("widgets");
      this.sgLoadingOverlay = this.settings.buildGroup("loadingOverlay");
      this.sgExperiments = this.settings.buildGroup("experiments");
      this.prefix = this.sg.add(new StringSetting("prefix", "&aCactus &8»"));
      this.commandPrefix = this.sg.add(new StringSetting("commandPrefix", "#"));
      this.customClientBrand = this.sg.add(new BooleanSetting("clientBrand", true));
      this.accentColor = this.sg.add(new ColorSetting("accentColor", new ColorSetting.ColorValue(Color.GREEN, false)));
      this.fadingSpeed = this.sg.add((new IntegerSetting("rgbFadingSpeed", 4)).min(1).max(20));
      this.updateNotify = this.sg.add(new BooleanSetting("updateNotify", true));
      this.toggleFeedback = this.sg.add(new EnumSetting("toggleFeedback", CactusSettings.ToggleFeedback.Actionbar));
      this.antiSecureChatWarning = this.sg.add(new BooleanSetting("noChatWarning", true));
      this.disableTelemetry = this.sg.add(new BooleanSetting("disableTelemetry", true));
      this.customLevelDirectory = this.sg.add((new StringSetting("levelDirectory", "")).setMaxLength(2000).setExperimental(true));
      this.chatHistoryLength = this.sg.add((new IntegerSetting("chatHistoryLength", 100, IntegerSetting.EditorStyle.Input, (value) -> {
         String var10000;
         switch(value) {
         case 1:
            var10000 = class_5244.field_24337.getString();
            break;
         case 100:
            var10000 = class_2561.method_43471("generator.minecraft.normal").getString();
            break;
         case 2000:
            var10000 = "Very long";
            break;
         default:
            var10000 = value.toString();
         }

         return var10000;
      })).min(1).max(Integer.MAX_VALUE).sliderMax(2000));
      this.preLoadHotbar = this.sg.add(new BooleanSetting("preLoadHotbar", true));
      this.loadingOverlayUseVanilla = this.sgLoadingOverlay.add(new BooleanSetting("loadingOverlayUseVanilla", false));
      this.loadingOverlayShowSprite = this.sgLoadingOverlay.add(new BooleanSetting("loadingOverlayShowSprite", true)).visibleIf(() -> {
         return !(Boolean)this.loadingOverlayUseVanilla.get();
      });
      this.loadingOverlayBackgroundColor = this.sgLoadingOverlay.add(new ColorSetting("loadingOverlayBackgroundColor", ColorSetting.ColorValue.of(new Color(-15460324, true), false), true)).visibleIf(() -> {
         return !(Boolean)this.loadingOverlayUseVanilla.get();
      });
      this.loadingOverlayProgressFillColor = this.sgLoadingOverlay.add(new ColorSetting("loadingOverlayProgressFillColor", ColorSetting.ColorValue.of(new Color(-8328396, true), false), true)).visibleIf(() -> {
         return !(Boolean)this.loadingOverlayUseVanilla.get();
      });
      this.loadingOverlayProgressBorderColor = this.sgLoadingOverlay.add(new ColorSetting("loadingOverlayProgressBorderColor", ColorSetting.ColorValue.of(new Color(-13617610, true), false), true)).visibleIf(() -> {
         return !(Boolean)this.loadingOverlayUseVanilla.get();
      });
      this.ircConnectOnStart = this.sgIrc.add(new BooleanSetting("connectIrcOnStart", true));
      this.enableIrcChat = this.sgIrc.add(new BooleanSetting("enableIrcChat", false));
      this.ircShortPrefix = this.sgIrc.add((new StringSetting("ircShortPrefix", "")).setMaxLength(2).visibleIf(() -> {
         return (Boolean)this.enableIrcChat.get();
      }));
      this.showCosmetics = this.sgIrc.add(new BooleanSetting("showCosmetics", true));
      this.showNameBadge = this.sgIrc.add(new BooleanSetting("showNameBadge", true));
      this.compactUIDesign = this.sgButtons.add(new BooleanSetting("compactUi", false));
      this.collapseOther = this.sgButtons.add(new BooleanSetting("collapseOther", true));
      this.moduleWidgetStateStyle = this.sgButtons.add(new EnumSetting("moduleStyle", CactusSettings.ModuleButtonStyle.FillActive));
      this.favoritesOnTop = this.sgButtons.add(new BooleanSetting("favoritesOnTop", false));
      this.accountWidgetMultiplayer = this.sgButtons.add(new BooleanSetting("multiplayerAccountsWidget", true));
      this.accountStatusTextMultiplayer = this.sgButtons.add(new BooleanSetting("multiplayerAccountStatusText", true));
      this.dragDropReorderServers = this.sgButtons.add(new BooleanSetting("dragDropReorderServers", true));
      this.buttonOptions = this.sgButtons.add(new SubGroupSetting("widgets", ButtonOptions.get().settings, ButtonOptions.ButtonsScreen::new));
      this.allowCusModification = this.sgButtons.add(new BooleanSetting("scriptModifyWidgets", true));
      this.showModuleDescriptionOnHover = this.sgButtons.add(new BooleanSetting("describeModulesWhenHovered", false));
      this.cactusWindowIconAndTitle = this.sgButtons.add(new BooleanSetting("cactusWindowInfoUpdate", true));
      this.allowSliderOverdrive = this.sgButtons.add(new BooleanSetting("sliderOverdrive", false));
      this.experiments = this.sgExperiments.add(new BooleanSetting(this, "experiments", false) {
         public Setting.SyncData getSyncData() {
            return new Setting.SyncData("experimentsEnabled", this.toJson(TreeSerializerFilter.ALL));
         }
      }).setCallback((b) -> {
         if (!b) {
            ModuleManager.get().getModules().values().stream().filter((m) -> {
               return m.getOption(Module.Flag.EXPERIMENTAL);
            }).forEach((m) -> {
               m.active(false, UpdateReason.UPDATE_STATE_FROM_CONFIG);
            });
         }

      });
      this.fancyText = this.sgExperiments.add(new BooleanSetting("fancyText", false));
      this.legacyModuleScreen = this.sgExperiments.add(new BooleanSetting("legacyModuleUI", false));
      this.gradientColors = this.sgExperiments.add(new ColorListSetting(this, "gradientColors", new String[]{"#FF0000", "#FFFF00", "#00FF00", "#00FFFF", "#0000FF", "#FF00FF"}) {
         public void set(List<String> value) {
            super.set(value);
            ColorUtils.gradientColors = value.stream().map((cs) -> {
               try {
                  return Color.decode(cs);
               } catch (Exception var2) {
                  return null;
               }
            }).filter(Objects::nonNull).toList();
         }
      });
      this.uiDebug = this.sgExperiments.add(new BooleanSetting("uiDebug", false));
   }

   public JsonObject toJson(TreeSerializerFilter filter) {
      return this.settings.toJson(filter.resolve("settings"));
   }

   public CactusSettings fromJson(JsonObject object) {
      this.settings.fromJson(object);
      return this;
   }

   public static CactusSettings get() {
      return (CactusSettings)((CactusConfig)CactusClient.getConfig(CactusConfig.class)).getSubConfig(CactusSettings.class);
   }

   public static enum ToggleFeedback {
      None,
      Actionbar,
      Chat,
      Toast;

      // $FF: synthetic method
      private static CactusSettings.ToggleFeedback[] $values() {
         return new CactusSettings.ToggleFeedback[]{None, Actionbar, Chat, Toast};
      }
   }

   public static enum ModuleButtonStyle {
      Vanilla,
      Fill,
      FillActive,
      Border,
      BorderActive,
      TextColor;

      // $FF: synthetic method
      private static CactusSettings.ModuleButtonStyle[] $values() {
         return new CactusSettings.ModuleButtonStyle[]{Vanilla, Fill, FillActive, Border, BorderActive, TextColor};
      }
   }
}
