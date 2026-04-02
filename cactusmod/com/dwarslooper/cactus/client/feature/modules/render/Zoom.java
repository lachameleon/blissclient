package com.dwarslooper.cactus.client.feature.modules.render;

import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.MouseScrollEvent;
import com.dwarslooper.cactus.client.event.impl.RawKeyEvent;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.EnumSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.KeybindSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.systems.key.KeyBind;
import com.dwarslooper.cactus.client.util.CactusConstants;
import net.minecraft.class_3532;

public class Zoom extends Module {
   private int zoomFovMultiply = 8;
   private float zoomSetMultiply = 2.0F;
   public Setting<KeyBind> keyBind;
   public Setting<Boolean> cinematicCamera;
   public Setting<Boolean> hideHand;
   public Setting<Zoom.Mode> mode;

   public Zoom() {
      super("zoom", ModuleManager.CATEGORY_RENDERING);
      this.keyBind = this.mainGroup.add(new KeybindSetting("key", KeyBind.of(67)));
      this.cinematicCamera = this.mainGroup.add(new BooleanSetting("cinematic", false));
      this.hideHand = this.mainGroup.add(new BooleanSetting("hideHand", false));
      this.mode = this.mainGroup.add(new EnumSetting("mode", Zoom.Mode.Multiply));
   }

   public boolean isPressed() {
      return this.active() && ((KeyBind)this.keyBind.get()).isPressed() && CactusConstants.mc.field_1755 == null;
   }

   public int getZoomMultiply() {
      return this.zoomFovMultiply;
   }

   public float getZoomSet() {
      return this.zoomSetMultiply;
   }

   @EventHandler
   public void onKey(RawKeyEvent event) {
   }

   @EventHandler
   public void onMouse(MouseScrollEvent event) {
      if (((KeyBind)this.keyBind.get()).isPressed()) {
         event.cancel();
         double value = event.getAmount();
         if ((double)this.zoomFovMultiply + value <= 35.0D && (double)this.zoomFovMultiply + value > 0.0D) {
            this.zoomFovMultiply += (int)value;
         }

         this.zoomSetMultiply = class_3532.method_15363((float)((double)this.zoomSetMultiply * (1.0D + value * 0.10000000149011612D)), 1.0F, 200.0F);
      }

   }

   public static enum Mode {
      Multiply,
      Set;

      // $FF: synthetic method
      private static Zoom.Mode[] $values() {
         return new Zoom.Mode[]{Multiply, Set};
      }
   }
}
