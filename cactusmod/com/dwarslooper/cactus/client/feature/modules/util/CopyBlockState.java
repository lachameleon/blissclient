package com.dwarslooper.cactus.client.feature.modules.util;

import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.EnumSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.StringListSetting;
import java.util.List;

public class CopyBlockState extends Module {
   public Setting<CopyBlockState.Mode> listType;
   public Setting<List<String>> properties;
   public Setting<Boolean> appendToLore;

   public CopyBlockState() {
      super("cbs", ModuleManager.CATEGORY_UTILITY);
      this.listType = this.mainGroup.add(new EnumSetting("listType", CopyBlockState.Mode.Blacklist));
      this.properties = this.mainGroup.add(new StringListSetting("properties", new String[]{"facing", "face", "shape", "rotation", "powered", "instrument"}));
      this.appendToLore = this.mainGroup.add(new BooleanSetting("appendToLore", true));
   }

   public static enum Mode {
      Whitelist,
      Blacklist;

      // $FF: synthetic method
      private static CopyBlockState.Mode[] $values() {
         return new CopyBlockState.Mode[]{Whitelist, Blacklist};
      }
   }
}
