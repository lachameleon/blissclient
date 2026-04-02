package com.dwarslooper.cactus.client.mixins.client.screen;

import com.dwarslooper.cactus.client.feature.modules.util.AutoCrafter;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import net.minecraft.class_1661;
import net.minecraft.class_1714;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_465;
import net.minecraft.class_479;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_479.class})
public abstract class CraftingScreenMixin extends class_465<class_1714> {
   @Unique
   protected void method_2389(class_332 context, float delta, int mouseX, int mouseY) {
   }

   public CraftingScreenMixin(class_1714 handler, class_1661 inventory, class_2561 title) {
      super(handler, inventory, title);
   }

   @Inject(
      method = {"method_25426"},
      at = {@At("TAIL")}
   )
   public void init(CallbackInfo ci) {
      if ((Boolean)CactusSettings.get().experiments.get()) {
         this.method_37063(new CButtonWidget(10, 10, 100, 30, class_2561.method_43470("Auto-Craft"), (e) -> {
            if (AutoCrafter.currentPath == null) {
               e.method_25355(class_2561.method_43470("§cNo path!"));
               e.field_22763 = false;
            } else {
               AutoCrafter.currentPath.craft(class_310.method_1551().method_1562(), this.field_2797);
            }
         }));
      }

   }
}
