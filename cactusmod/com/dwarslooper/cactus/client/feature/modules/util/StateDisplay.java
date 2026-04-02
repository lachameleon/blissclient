package com.dwarslooper.cactus.client.feature.modules.util;

import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.EnumSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.IntegerSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;

public class StateDisplay extends Module {
   public Setting<StateDisplay.Pos> position;
   public Setting<StateDisplay.Display> display;
   public Setting<Boolean> showBlockPos;
   public Setting<Boolean> colors;
   public Setting<Integer> scale;

   public StateDisplay() {
      super("state_display", ModuleManager.CATEGORY_UTILITY, (new Module.Options()).set(Module.Flag.EXPERIMENTAL, true));
      this.position = this.mainGroup.add(new EnumSetting("position", StateDisplay.Pos.TopRight));
      this.display = this.mainGroup.add(new EnumSetting("display", StateDisplay.Display.BlockName));
      this.showBlockPos = this.mainGroup.add(new BooleanSetting("showPos", true));
      this.colors = this.mainGroup.add(new BooleanSetting("colors", true));
      this.scale = (new IntegerSetting("scale", 7)).min(1).max(10);
   }

   public static enum Pos {
      TopMiddle,
      TopRight,
      BottomLeft,
      BottomRight;

      // $FF: synthetic method
      private static StateDisplay.Pos[] $values() {
         return new StateDisplay.Pos[]{TopMiddle, TopRight, BottomLeft, BottomRight};
      }
   }

   public static enum Display {
      BlockName,
      BlockID;

      // $FF: synthetic method
      private static StateDisplay.Display[] $values() {
         return new StateDisplay.Display[]{BlockName, BlockID};
      }
   }
}
