package com.dwarslooper.cactus.client.feature.modules.render;

import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.google.gson.JsonObject;

public class NameTags extends Module {
   public Setting<Boolean> showOwnName;
   public Setting<Boolean> hideNameTagsInF1;

   public NameTags() {
      super("name_tags", ModuleManager.CATEGORY_RENDERING, (new Module.Options()).set(Module.Flag.RUN_IN_MENU, true));
      this.showOwnName = this.mainGroup.add(new BooleanSetting("ownName", true));
      this.hideNameTagsInF1 = this.mainGroup.add(new BooleanSetting("hideInF1", false));
   }

   public Module fromJson(JsonObject object) {
      try {
         JsonObject settings = object.getAsJsonObject("settings");
         if (settings != null && (settings.has("nick") || settings.has("skin"))) {
            ((StreamerMode)ModuleManager.get().get(StreamerMode.class)).settings.fromJson(settings);
         }
      } catch (Exception var3) {
      }

      return super.fromJson(object);
   }
}
