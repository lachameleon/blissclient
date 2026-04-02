package com.dwarslooper.cactus.client.feature.modules.redstone;

import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.InteractBlockEvent;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.impl.EnumSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import net.minecraft.class_1268;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2350.class_2351;
import net.minecraft.class_2350.class_2352;
import net.minecraft.class_2828.class_2831;

public class AutoRepeater extends Module {
   public static float pYaw = 0.0F;
   public static float pPitch = 0.0F;
   public static boolean waiting = false;
   public Setting<AutoRepeater.Ticks> ticks;

   public AutoRepeater() {
      super("auto_repeater", ModuleManager.CATEGORY_REDSTONE, (new Module.Options()).set(Module.Flag.HUD_LISTED, false).set(Module.Flag.EXPERIMENTAL, true));
      this.ticks = this.mainGroup.add(new EnumSetting("ticks", AutoRepeater.Ticks.T1));
   }

   @EventHandler
   public void prePlaceLookTick(InteractBlockEvent event) {
      class_243 pos = event.getHitResult().method_17784();
      if (event.getHand() == class_1268.field_5808 && CactusConstants.mc.field_1724 != null) {
         class_2350 facing = null;
         double mLow = 0.2D;
         double mHigh = 1.0D - mLow;
         double xd = Math.abs(pos.method_10216() % 1.0D);
         double yd = Math.abs(pos.method_10214() % 1.0D);
         double zd = Math.abs(pos.method_10215() % 1.0D);
         boolean xdm = xd == 0.0D || xd > mLow && xd < mHigh;
         boolean ydm = yd == 0.0D || yd > mLow && yd < mHigh;
         boolean zdm = zd == 0.0D || zd > mLow && zd < mHigh;
         if (!xdm || !ydm || !zdm) {
            if (xd < mLow && zdm && ydm) {
               facing = class_2350.field_11039;
            } else if (xd > mHigh && zdm && ydm) {
               facing = class_2350.field_11034;
            } else if (zd < mLow && xdm && ydm) {
               facing = class_2350.field_11043;
            } else if (zd > mHigh && xdm && ydm) {
               facing = class_2350.field_11035;
            } else if (yd < mLow && xdm && zdm) {
               facing = class_2350.field_11033;
            } else if (yd > mHigh && xdm && zdm) {
               facing = class_2350.field_11036;
            }

            if (facing != null) {
               System.out.println(facing.toString());
               System.out.println(facing.method_10144());
               pYaw = CactusConstants.mc.field_1724.method_36454();
               pPitch = CactusConstants.mc.field_1724.method_36455();
               boolean isVertical = facing.method_10166() == class_2351.field_11052;
               float yaw = isVertical ? CactusConstants.mc.field_1724.method_36454() : facing.method_10144();
               float pitch = !isVertical ? CactusConstants.mc.field_1724.method_36455() : (facing.method_10171() == class_2352.field_11060 ? 90.0F : -90.0F);
               ChatUtils.infoPrefix("ABP", "Sending packet with yaw: " + yaw + ", pitch: " + pitch);
               CactusConstants.mc.field_1724.method_36456(yaw);
               CactusConstants.mc.field_1724.method_36457(pitch);
               System.out.println("angles set");
               CactusConstants.mc.method_1562().method_52787(new class_2831(yaw, pitch, CactusConstants.mc.field_1724.method_24828(), CactusConstants.mc.field_1724.field_5976));
            } else {
               ChatUtils.error("no good? facing is broken.");
            }

         }
      }
   }

   public static enum Ticks {
      T1,
      T2,
      T3;

      // $FF: synthetic method
      private static AutoRepeater.Ticks[] $values() {
         return new AutoRepeater.Ticks[]{T1, T2, T3};
      }
   }
}
