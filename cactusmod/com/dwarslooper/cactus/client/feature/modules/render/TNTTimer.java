package com.dwarslooper.cactus.client.feature.modules.render;

import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import java.text.DecimalFormat;
import net.minecraft.class_124;
import net.minecraft.class_2561;

public class TNTTimer extends Module {
   static TNTTimer instance;
   public final DecimalFormat decimalFormat = new DecimalFormat("0.00");
   public Setting<Boolean> color;

   public TNTTimer() {
      super("tnt_timer", ModuleManager.CATEGORY_RENDERING);
      this.color = this.mainGroup.add(new BooleanSetting("color", true));
      instance = this;
   }

   public static TNTTimer getInstance() {
      return instance;
   }

   public class_2561 getTime(double ticks) {
      class_124 format = class_124.field_1068;
      double timing = ticks / 20.0D;
      if (!(Boolean)this.color.get()) {
         return class_2561.method_30163(this.decimalFormat.format(timing)).method_27661().method_27692(class_124.field_1068);
      } else {
         if (timing > 7.0D) {
            format = class_124.field_1062;
         } else if (timing > 6.0D) {
            format = class_124.field_1075;
         } else if (timing > 4.0D) {
            format = class_124.field_1077;
         } else if (timing > 3.0D) {
            format = class_124.field_1060;
         } else if (timing > 2.0D) {
            format = class_124.field_1065;
         } else if (timing > 1.0D) {
            format = class_124.field_1061;
         } else if (timing > 0.0D) {
            format = class_124.field_1079;
         }

         return class_2561.method_30163(this.decimalFormat.format(timing)).method_27661().method_27692(format);
      }
   }
}
