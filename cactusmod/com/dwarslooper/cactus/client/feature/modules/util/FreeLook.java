package com.dwarslooper.cactus.client.feature.modules.util;

import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.impl.KeybindSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.systems.key.KeyBind;
import com.dwarslooper.cactus.client.util.CactusConstants;
import net.minecraft.class_5498;

public class FreeLook extends Module {
   public static float camYaw;
   public static float camPitch;
   private class_5498 originalPerspective;
   private boolean freeLooking;
   public Setting<KeyBind> holdBind;

   public FreeLook() {
      super("freeLook", ModuleManager.CATEGORY_UTILITY);
      this.originalPerspective = class_5498.field_26664;
      this.holdBind = this.mainGroup.add(new KeybindSetting("holdBind", KeyBind.none()));
   }

   public boolean isFreeLooking() {
      KeyBind bind = (KeyBind)this.holdBind.get();
      boolean val = (!bind.isBound() || bind.isPressed() && CactusConstants.mc.field_1755 == null) && this.active();
      if (val != this.freeLooking) {
         this.freeLooking = val;
         this.update();
      }

      return val;
   }

   private void update() {
      if (this.freeLooking) {
         this.originalPerspective = CactusConstants.mc.field_1690.method_31044();
         if (this.originalPerspective == class_5498.field_26664) {
            CactusConstants.mc.field_1690.method_31043(class_5498.field_26665);
         }
      } else {
         CactusConstants.mc.field_1690.method_31043(this.originalPerspective);
      }

   }

   public void onEnable() {
      this.isFreeLooking();
   }

   public void onDisable() {
      this.isFreeLooking();
   }
}
