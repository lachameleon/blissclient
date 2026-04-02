package com.dwarslooper.cactus.client.feature.modules.util;

import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.MouseScrollEvent;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.IntegerSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.InputUtilWrapper;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import java.util.function.Supplier;
import net.minecraft.class_243;
import net.minecraft.class_2477;
import net.minecraft.class_3532;

public class FlySpeed extends Module {
   public Setting<Integer> flySpeed;
   public Setting<Boolean> enableScroll;
   public Setting<Boolean> stopOnDisable;
   private static int modifier;

   public FlySpeed() {
      super("fly_speed", ModuleManager.CATEGORY_UTILITY);
      this.flySpeed = this.mainGroup.add((new IntegerSetting("speed", 4)).min(1).max(40));
      this.enableScroll = this.mainGroup.add(new BooleanSetting("scroll", true));
      this.stopOnDisable = this.mainGroup.add(new BooleanSetting("stop", false));
   }

   public static int getModifier() {
      return modifier;
   }

   public void onDisable() {
      assert CactusConstants.mc.field_1724 != null;

      if ((Boolean)this.stopOnDisable.get() && CactusConstants.mc.field_1724.method_31549().field_7479) {
         CactusConstants.mc.field_1724.method_5750(new class_243(0.0D, 0.0D, 0.0D));
      }

   }

   public Supplier<String> getHud() {
      return () -> {
         int var10000 = (Integer)this.flySpeed.get();
         return "\\m Speed: " + (var10000 + modifier);
      };
   }

   @EventHandler
   public void onMouse(MouseScrollEvent event) {
      if (this.active() && (Boolean)this.enableScroll.get() && CactusConstants.mc.field_1705 != null && CactusConstants.mc.field_1724 != null && CactusConstants.mc.field_1724.method_31549().field_7479 && InputUtilWrapper.hasControlDown()) {
         IntegerSetting setting = (IntegerSetting)this.flySpeed;
         modifier = (int)class_3532.method_15350((double)modifier + event.getAmount(), (double)(setting.getMin() - setting.get()), (double)(setting.getMax() - setting.get()));
         ChatUtils.actionbar(class_2477.method_10517().method_48307("modules.fly_speed.text.scrollChanged").formatted(new Object[]{setting.get() + modifier}));
         event.cancel();
      }

   }
}
