package com.dwarslooper.cactus.client.feature.modules.render;

import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.group.SettingGroup;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.ColorSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.IntegerSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.awt.Color;
import net.minecraft.class_11658;
import net.minecraft.class_12179;
import net.minecraft.class_12180;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_3965;
import net.minecraft.class_4587;
import net.minecraft.class_239.class_240;

public class BlockOutlines extends Module {
   public SettingGroup sgOutlines;
   public SettingGroup sgFill;
   public Setting<ColorSetting.ColorValue> lineColor;
   public Setting<Integer> lineWidth;
   public Setting<Boolean> fill;
   public Setting<ColorSetting.ColorValue> fillColor;

   public BlockOutlines() {
      super("block_outlines", ModuleManager.CATEGORY_RENDERING, (new Module.Options()).set(Module.Flag.HUD_LISTED, false));
      this.sgOutlines = this.settings.buildGroup("outline");
      this.sgFill = this.settings.buildGroup("fill");
      this.lineColor = this.sgOutlines.add(new ColorSetting("colorOutline", new ColorSetting.ColorValue(Color.BLACK, false)));
      this.lineWidth = this.sgOutlines.add((new IntegerSetting("lineWidth", 1)).min(1).max(40));
      this.fill = this.sgFill.add(new BooleanSetting("fill", true));
      this.fillColor = this.sgFill.add(new ColorSetting("colorFill", new ColorSetting.ColorValue(Color.BLACK, false), true)).visibleIf(() -> {
         return (Boolean)this.fill.get();
      });
   }

   public void renderFill(class_4587 matrices, class_11658 renderStates) {
      if (renderStates.field_63083 != null) {
         class_239 crosshairTarget = CactusConstants.mc.field_1765;
         if (crosshairTarget instanceof class_3965) {
            class_3965 bhr = (class_3965)crosshairTarget;
            if (bhr.method_17783() != class_240.field_1333) {
               if (CactusConstants.mc.field_1687 == null) {
                  return;
               }

               class_2338 pos = bhr.method_17777();
               class_2680 state = CactusConstants.mc.field_1687.method_8320(pos);
               class_265 shape = state.method_26218(CactusConstants.mc.field_1687, pos);
               if (shape.method_1110()) {
                  return;
               }

               class_243 cam = CactusConstants.mc.field_1773.method_19418().method_71156().method_22882();
               Color color = new Color(((ColorSetting.ColorValue)this.fillColor.get()).color(), true);
               matrices.method_22903();
               matrices.method_22904(cam.field_1352, cam.field_1351, cam.field_1350);
               shape.method_1089((minX, minY, minZ, maxX, maxY, maxZ) -> {
                  class_238 box = new class_238((double)pos.method_10263() + minX, (double)pos.method_10264() + minY, (double)pos.method_10260() + minZ, (double)pos.method_10263() + maxX, (double)pos.method_10264() + maxY, (double)pos.method_10260() + maxZ);
                  class_12180.method_75541(box, new class_12179(0, 0.0F, color.getRGB()));
               });
               matrices.method_22909();
               return;
            }
         }

      }
   }
}
