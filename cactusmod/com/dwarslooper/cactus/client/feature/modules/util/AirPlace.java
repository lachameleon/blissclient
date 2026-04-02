package com.dwarslooper.cactus.client.feature.modules.util;

import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.InteractItemEvent;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.render.GhostVertexConsumer;
import com.dwarslooper.cactus.client.render.WorldRendering;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.IntegerSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_10889;
import net.minecraft.class_12249;
import net.minecraft.class_1657;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1921;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2680;
import net.minecraft.class_2885;
import net.minecraft.class_3965;
import net.minecraft.class_4587;
import net.minecraft.class_5819;
import net.minecraft.class_7202;
import net.minecraft.class_776;
import net.minecraft.class_4597.class_4598;

public class AirPlace extends Module implements WorldRendering {
   public Setting<Integer> placeDistance;
   public Setting<Boolean> showGhostBlock;

   public AirPlace() {
      super("airPlace", ModuleManager.CATEGORY_UTILITY);
      this.placeDistance = this.mainGroup.add((new IntegerSetting("range", 5)).min(1).max(5));
      this.showGhostBlock = this.mainGroup.add(new BooleanSetting("preview", true));
   }

   public void onEnable() {
      this.ensureCreative();
   }

   @EventHandler
   public void onUse(InteractItemEvent event) {
      if (this.isCreative()) {
         if (this.active() && event.getPlayer().method_5998(event.getHand()).method_7909() instanceof class_1747) {
            class_239 var3 = this.raycast(event.getPlayer());
            if (var3 instanceof class_3965) {
               class_3965 bhr = (class_3965)var3;

               assert CactusConstants.mc.field_1687 != null;

               class_7202 pendingUpdateManager = CactusConstants.mc.field_1687.method_41925().method_41937();

               try {
                  CactusConstants.mc.method_1562().method_52787(new class_2885(event.getHand(), bhr, pendingUpdateManager.method_41942()));
               } catch (Throwable var7) {
                  if (pendingUpdateManager != null) {
                     try {
                        pendingUpdateManager.close();
                     } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                     }
                  }

                  throw var7;
               }

               if (pendingUpdateManager != null) {
                  pendingUpdateManager.close();
               }
            }
         }

      }
   }

   private boolean isCreative() {
      return CactusConstants.mc.field_1724 != null && CactusConstants.mc.field_1724.method_56992();
   }

   private void ensureCreative() {
      if (!this.isCreative()) {
         ChatUtils.error((class_2561)class_2561.method_43471("module.creativeModeRequired"));
      }

   }

   private class_239 raycast(class_1657 player) {
      return player.method_5745((double)(Integer)this.placeDistance.get(), CactusConstants.mc.method_61966().method_60637(false), false);
   }

   public void onRender(class_4598 immediate, class_4587 matrices, float tickDelta) {
      if (this.isCreative() && (Boolean)this.showGhostBlock.get()) {
         assert CactusConstants.mc.field_1724 != null;

         class_1792 item = CactusConstants.mc.field_1724.method_5998(CactusConstants.mc.field_1724.method_6058()).method_7909();
         if (CactusConstants.mc.field_1724.method_56992() && CactusConstants.mc.field_1687 != null && item instanceof class_1747) {
            class_1747 blockItem = (class_1747)item;
            class_239 var7 = this.raycast(CactusConstants.mc.field_1724);
            if (var7 instanceof class_3965) {
               class_3965 bhr = (class_3965)var7;
               if (CactusConstants.mc.field_1687.method_8320(bhr.method_17777()).method_26215()) {
                  class_243 cameraPos = CactusConstants.mc.field_1773.method_19418().method_71156();
                  class_776 blockRenderManager = CactusConstants.mc.method_1541();
                  matrices.method_22903();
                  matrices.method_22904(-cameraPos.field_1352, -cameraPos.field_1351, -cameraPos.field_1350);
                  class_1921 translucentLayer = class_12249.method_75977();
                  class_2338 pos = class_2338.method_49638(bhr.method_17784());
                  class_2680 state = blockItem.method_7711().method_9564();
                  matrices.method_22903();
                  matrices.method_46416((float)pos.method_10263(), (float)pos.method_10264(), (float)pos.method_10260());
                  class_5819 posRandom = class_5819.method_43049(pos.method_10063());
                  List<class_10889> modelParts = new ArrayList();
                  blockRenderManager.method_3349(state).method_68513(posRandom, modelParts);
                  blockRenderManager.method_3355(state, pos, CactusConstants.mc.field_1687, matrices, new GhostVertexConsumer(immediate.method_73477(translucentLayer), 128), false, modelParts);
                  matrices.method_22909();
                  matrices.method_22909();
                  immediate.method_22994(translucentLayer);
               }
            }
         }

      }
   }
}
