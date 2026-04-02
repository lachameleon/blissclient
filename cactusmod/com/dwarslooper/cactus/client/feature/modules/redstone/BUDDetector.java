package com.dwarslooper.cactus.client.feature.modules.redstone;

import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.ClientTickEvent;
import com.dwarslooper.cactus.client.feature.content.ContentPackDependent;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.render.RenderHelper;
import com.dwarslooper.cactus.client.render.RenderableObject;
import com.dwarslooper.cactus.client.render.WorldRendering;
import com.dwarslooper.cactus.client.render.objects.BoxObject;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.EnumSetSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.IntegerSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.class_2315;
import net.minecraft.class_2325;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2436;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_2665;
import net.minecraft.class_2671;
import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_4587;
import net.minecraft.class_5250;
import net.minecraft.class_2558.class_10610;
import net.minecraft.class_2568.class_10612;
import net.minecraft.class_4597.class_4598;

@ContentPackDependent("redstone")
public class BUDDetector extends Module implements WorldRendering {
   private final List<class_2338> notify = new ArrayList();
   public Setting<Integer> radius;
   public Setting<Set<BUDDetector.Mode>> mode;
   public Setting<Boolean> highlightPistonArms;
   private final Map<class_2338, BoxObject> renderAsBUD;

   public BUDDetector() {
      super("bud_detector", ModuleManager.CATEGORY_REDSTONE);
      this.radius = this.mainGroup.add((new IntegerSetting("radius", 8)).min(1).max(40));
      this.mode = this.mainGroup.add(new EnumSetSetting("mode", BUDDetector.Mode.class, new BUDDetector.Mode[]{BUDDetector.Mode.Show}));
      this.highlightPistonArms = this.mainGroup.add(new BooleanSetting("pistonArms", true));
      this.renderAsBUD = new WeakHashMap();
   }

   @EventHandler
   public void onTick(ClientTickEvent event) {
      if (CactusConstants.mc.field_1687 != null && CactusConstants.mc.field_1724 != null) {
         Map<class_2338, BoxObject> map = new WeakHashMap();
         int r = (Integer)this.radius.get();
         this.notify.removeIf((blockPosx) -> {
            int dx = CactusConstants.mc.field_1724.method_24515().method_10263() - blockPosx.method_10263();
            int dy = CactusConstants.mc.field_1724.method_24515().method_10264() - blockPosx.method_10264();
            int dz = CactusConstants.mc.field_1724.method_24515().method_10260() - blockPosx.method_10260();
            double distance = Math.sqrt((double)(dx * dx + dy * dy + dz * dz));
            return distance > (double)r;
         });

         for(int x = -r; x < r; ++x) {
            for(int y = -r; y < r; ++y) {
               for(int z = -r; z < r; ++z) {
                  class_2338 blockPos = CactusConstants.mc.field_1724.method_24515().method_10069(x, y, z);
                  class_2680 blockState = CactusConstants.mc.field_1687.method_8320(blockPos);
                  if (this.isValidComponent(blockState) && this.potentiallyBUD(blockPos)) {
                     Color color = Color.MAGENTA;
                     if (this.hasHead(blockPos) && (Boolean)this.highlightPistonArms.get()) {
                        color = Color.CYAN;
                     }

                     if (((Set)this.mode.get()).contains(BUDDetector.Mode.Show)) {
                        map.put(blockPos, new BoxObject(color, RenderableObject.RenderMode.Instant, new class_238(class_243.method_24954(blockPos), class_243.method_24954(blockPos.method_10069(1, 1, 1))), RenderableObject.BoxMode.Both));
                     }

                     if (((Set)this.mode.get()).contains(BUDDetector.Mode.Notify) && !this.notify.contains(blockPos)) {
                        class_5250 var10000 = class_2561.method_43470("§eBlock at ");
                        String var10001 = this.toSpacedBlockPos(blockPos);
                        ChatUtils.warning((class_2561)var10000.method_10852(class_2561.method_43470("§c§n" + var10001).method_27694((style) -> {
                           class_2583 var10000 = style.method_10949(new class_10612(blockState.method_26204().method_8389().method_7854()));
                           String var10003 = this.toSpacedBlockPos(blockPos);
                           return var10000.method_10958(new class_10610("/teleport " + var10003));
                        })).method_10852(class_2561.method_43470(" §eis being BUD-powered!")));
                        this.notify.add(blockPos);
                     }
                  }
               }
            }
         }

         this.renderAsBUD.clear();
         this.renderAsBUD.putAll(map);
         map.clear();
      }
   }

   private class_2680 getOffset(class_2338 original, int x, int y, int z) {
      return CactusConstants.mc.field_1687.method_8320(original.method_10069(x, y, z));
   }

   private boolean potentiallyBUD(class_2338 blockPos) {
      return this.hasRedstone(blockPos.method_10069(0, 2, 0), class_2350.field_11033) || this.hasRedstone(blockPos.method_10069(1, 1, 0), class_2350.field_11039) || this.hasRedstone(blockPos.method_10069(-1, 1, 0), class_2350.field_11034) || this.hasRedstone(blockPos.method_10069(0, 1, 1), class_2350.field_11043) || this.hasRedstone(blockPos.method_10069(0, 1, -1), class_2350.field_11035);
   }

   private boolean hasRedstone(class_2338 blockPos, class_2350 dir) {
      if (CactusConstants.mc.field_1687 == null) {
         return false;
      } else {
         return (CactusConstants.mc.field_1687.method_49807(blockPos, dir) || CactusConstants.mc.field_1687.method_49807(blockPos, class_2350.field_11033)) && (CactusConstants.mc.field_1687.method_8320(blockPos).method_26212(CactusConstants.mc.field_1687, blockPos) || CactusConstants.mc.field_1687.method_8320(blockPos).method_26204() instanceof class_2436);
      }
   }

   private boolean isValidComponent(class_2680 blockState) {
      return blockState.method_26204() instanceof class_2665 || blockState.method_26204() instanceof class_2325 || blockState.method_26204() instanceof class_2315;
   }

   private boolean hasHead(class_2338 blockPos) {
      if (CactusConstants.mc.field_1687 == null) {
         return false;
      } else {
         class_2680 state = this.getOffset(blockPos, 0, 1, 0);
         return state.method_26204() instanceof class_2671 ? CactusConstants.mc.field_1687.method_8320(blockPos).method_28501().contains(class_2741.field_12552) : false;
      }
   }

   private String toSpacedBlockPos(class_2338 blockPos) {
      int var10000 = blockPos.method_10263();
      return var10000 + " " + blockPos.method_10264() + " " + blockPos.method_10260();
   }

   public void onRender(class_4598 immediate, class_4587 matrices, float tickDelta) {
      this.renderAsBUD.forEach((blockPos, boxObject) -> {
         RenderHelper.hitBoxes.add(boxObject);
      });
   }

   public static enum Mode {
      Show,
      Notify;

      // $FF: synthetic method
      private static BUDDetector.Mode[] $values() {
         return new BUDDetector.Mode[]{Show, Notify};
      }
   }
}
