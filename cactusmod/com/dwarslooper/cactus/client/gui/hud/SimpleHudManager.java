package com.dwarslooper.cactus.client.gui.hud;

import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.HUD;
import com.dwarslooper.cactus.client.systems.SingleInstance;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ServerUtils;
import com.dwarslooper.cactus.client.util.game.TickRateProcessor;
import com.dwarslooper.cactus.client.util.generic.MathUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class SimpleHudManager {
   private final List<Supplier<String>> hudElements = new ArrayList();

   public static SimpleHudManager get() {
      return (SimpleHudManager)SingleInstance.of(SimpleHudManager.class, SimpleHudManager::new, SimpleHudManager::init);
   }

   public HUD getHud() {
      return (HUD)ModuleManager.get().get(HUD.class);
   }

   private void init() {
      this.registerElement(() -> {
         return (Boolean)this.getHud().showCoordinates.get() ? "§f" + CactusConstants.mc.field_1724.method_31477() + " " + CactusConstants.mc.field_1724.method_31478() + " " + CactusConstants.mc.field_1724.method_31479() : null;
      });
      TickRateProcessor.registerHUD(this);
      this.registerElement(() -> {
         return (Boolean)this.getHud().showFPS.get() ? "FPS §8x " + MathUtils.quality(CactusConstants.mc.method_47599(), 30, 10, 4, MathUtils.QualityMode.DECREMENT) : null;
      });
      this.registerElement(() -> {
         if (CactusConstants.mc.method_1562().method_45734() != null && (Boolean)this.getHud().showPing.get()) {
            int var10000 = ServerUtils.ping();
            return "Ping §8x " + MathUtils.quality(var10000, 80, 200, 400, MathUtils.QualityMode.INCREMENT);
         } else {
            return null;
         }
      });
      this.registerElement(() -> {
         boolean any = false;
         if (!(Boolean)this.getHud().showModules.get()) {
            return null;
         } else {
            Iterator var2 = ModuleManager.get().getModules().values().iterator();

            while(var2.hasNext()) {
               Module value = (Module)var2.next();
               if (value.active() && value.getOption(Module.Flag.HUD_LISTED)) {
                  any = true;
               }
            }

            return any ? "\n§7§nTools" : null;
         }
      });
   }

   public void registerElement(Supplier<String> supplier) {
      this.hudElements.add(supplier);
   }

   public List<Supplier<String>> getHudElements() {
      return this.hudElements;
   }
}
