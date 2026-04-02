package com.dwarslooper.cactus.client.gui.hud;

import com.dwarslooper.cactus.client.gui.hud.element.impl.HudElement;
import com.dwarslooper.cactus.client.systems.config.settings.group.SettingGroup;

public class ElementBackedSettingGroup extends SettingGroup {
   private final HudElement<?> parent;

   public ElementBackedSettingGroup(HudElement<?> parent, String id) {
      super(id);
      this.parent = parent;
   }

   public String name() {
      return this.parent.getName();
   }
}
