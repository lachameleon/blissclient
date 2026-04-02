package com.dwarslooper.cactus.client.feature.modules.render;

import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.group.SettingGroup;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.IntegerSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;

public class HUD extends Module {
   private final SettingGroup sgSimple;
   public Setting<Boolean> showSimple;
   public Setting<Boolean> hideScoreboardInF3;
   public Setting<Boolean> connectElements;
   public Setting<Boolean> showCoordinates;
   public Setting<Boolean> showFPS;
   public Setting<Boolean> showPing;
   public Setting<Boolean> showModules;
   public Setting<Boolean> showTPS;
   public Setting<Boolean> darkenBackground;
   public Setting<Integer> scale;

   public HUD() {
      super("hud", ModuleManager.CATEGORY_RENDERING, (new Module.Options()).set(Module.Flag.HUD_LISTED, false));
      this.sgSimple = this.settings.buildGroup("simple").visibleIf(this::hasSimple);
      this.showSimple = this.mainGroup.add(new BooleanSetting("simple", false));
      this.hideScoreboardInF3 = this.mainGroup.add(new BooleanSetting("hideSidebarF3", true));
      this.connectElements = this.mainGroup.add(new BooleanSetting("connectElements", true));
      this.showCoordinates = this.sgSimple.add(new BooleanSetting("coordinates", true));
      this.showFPS = this.sgSimple.add(new BooleanSetting("fps", true));
      this.showPing = this.sgSimple.add(new BooleanSetting("ping", true));
      this.showModules = this.sgSimple.add(new BooleanSetting("modules", true));
      this.showTPS = this.sgSimple.add(new BooleanSetting("tps", true));
      this.darkenBackground = this.sgSimple.add(new BooleanSetting("darkBackground", true));
      this.scale = this.sgSimple.add((new IntegerSetting("scale", 4)).min(1).max(8));
   }

   private boolean hasSimple() {
      return (Boolean)this.showSimple.get();
   }
}
