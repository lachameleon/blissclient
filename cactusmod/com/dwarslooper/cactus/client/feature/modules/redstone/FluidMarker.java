package com.dwarslooper.cactus.client.feature.modules.redstone;

import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.AttackBlockEvent;
import com.dwarslooper.cactus.client.event.impl.ClientTickEvent;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.render.RenderHelper;
import com.dwarslooper.cactus.client.render.RenderableObject;
import com.dwarslooper.cactus.client.render.WorldRendering;
import com.dwarslooper.cactus.client.render.objects.BoxObject;
import com.dwarslooper.cactus.client.systems.config.settings.impl.EnumSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.IntegerSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.awt.Color;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Supplier;
import net.minecraft.class_1269;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_3610;
import net.minecraft.class_4587;
import net.minecraft.class_4597.class_4598;

public class FluidMarker extends Module implements WorldRendering {
   public Setting<Integer> radius;
   public Setting<FluidMarker.Mode> mode;
   private final Map<class_2338, BoxObject> renderAsDangerous;

   public FluidMarker() {
      super("fluid_marker", ModuleManager.CATEGORY_REDSTONE, (new Module.Options()).set(Module.Flag.SERVER_UNSAFE, true));
      this.radius = this.mainGroup.add((new IntegerSetting("radius", 8)).min(1).max(20));
      this.mode = this.mainGroup.add(new EnumSetting("mode", FluidMarker.Mode.Highlight));
      this.renderAsDangerous = new WeakHashMap();
   }

   public Supplier<String> getHud() {
      return () -> {
         String var10000 = this.radius.getText();
         return "\\m Radius: " + var10000 + "\n\\m Mode: " + this.mode.getText();
      };
   }

   private boolean isBreakForbidden(class_2338 pos) {
      return this.active() && this.mode.get() == FluidMarker.Mode.Prevent && this.getRenderedAsDangerous().containsKey(pos);
   }

   @EventHandler
   public void onAttack(AttackBlockEvent event) {
      if (this.isBreakForbidden(event.getPos())) {
         event.setResult(class_1269.field_5814);
      }

   }

   @EventHandler
   public void onTick(ClientTickEvent event) {
      int r = (Integer)this.radius.get();
      if (CactusConstants.mc.field_1687 != null && CactusConstants.mc.field_1724 != null) {
         Map<class_2338, BoxObject> map = new WeakHashMap();

         for(int x = -r; x < r; ++x) {
            for(int y = -r; y < r; ++y) {
               for(int z = -r; z < r; ++z) {
                  class_2338 blockPos = CactusConstants.mc.field_1724.method_24515().method_10069(x, y, z);
                  class_2680 blockState = CactusConstants.mc.field_1687.method_8320(blockPos);
                  if (this.potentiallyDangerous(blockState)) {
                     Set<class_2338> pos = new HashSet();
                     if (blockState.method_26227().method_15761() > 1) {
                        this.addIfValid(pos, blockPos.method_10069(1, 0, 0));
                        this.addIfValid(pos, blockPos.method_10069(-1, 0, 0));
                        this.addIfValid(pos, blockPos.method_10069(0, 0, 1));
                        this.addIfValid(pos, blockPos.method_10069(0, 0, -1));
                     }

                     this.addIfValid(pos, blockPos.method_10069(0, -1, 0));

                     class_2338 pos0;
                     Color color;
                     for(Iterator var10 = pos.iterator(); var10.hasNext(); map.put(pos0, new BoxObject(color, RenderableObject.RenderMode.Instant, new class_238(class_243.method_24954(pos0), class_243.method_24954(pos0.method_10069(1, 1, 1))), RenderableObject.BoxMode.Both))) {
                        pos0 = (class_2338)var10.next();
                        color = Color.RED;
                        if (blockState.method_28498(class_2741.field_12508) && this.waterlogged(blockState)) {
                           color = Color.ORANGE;
                        }
                     }
                  }
               }
            }
         }

         this.renderAsDangerous.clear();
         this.renderAsDangerous.putAll(map);
         map.clear();
      }
   }

   private void addIfValid(Set<class_2338> list, class_2338 blockPos) {
      class_2680 blockState = CactusConstants.mc.field_1687.method_8320(blockPos);
      if ((blockState.method_26227().method_15769() || this.waterlogged(blockState)) && !blockState.method_26215()) {
         list.add(blockPos);
      }

   }

   private boolean potentiallyDangerous(class_2680 blockState) {
      class_3610 fluidState = blockState.method_26227();
      return !fluidState.method_15769();
   }

   private boolean waterlogged(class_2680 blockState) {
      return blockState.method_28498(class_2741.field_12508) ? (Boolean)blockState.method_11654(class_2741.field_12508) : false;
   }

   public void onRender(class_4598 immediate, class_4587 matrices, float tickDelta) {
      this.renderAsDangerous.forEach((blockPos, boxObject) -> {
         RenderHelper.hitBoxes.add(boxObject);
      });
   }

   public Map<class_2338, BoxObject> getRenderedAsDangerous() {
      return this.renderAsDangerous;
   }

   public static enum Mode {
      Highlight,
      Prevent;

      // $FF: synthetic method
      private static FluidMarker.Mode[] $values() {
         return new FluidMarker.Mode[]{Highlight, Prevent};
      }
   }
}
