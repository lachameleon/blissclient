package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.BetterTooltips;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_124;
import net.minecraft.class_1887;
import net.minecraft.class_2561;
import net.minecraft.class_5244;
import net.minecraft.class_5250;
import net.minecraft.class_6880;
import net.minecraft.class_9636;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin({class_1887.class})
public abstract class EnchantmentMixin {
   @Inject(
      method = {"method_8179"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void replaceLevel(class_6880<class_1887> enchantment, int level, CallbackInfoReturnable<class_2561> cir) {
      BetterTooltips tooltips = (BetterTooltips)ModuleManager.get().get(BetterTooltips.class);
      if (tooltips.active()) {
         class_5250 mutableText = ((class_1887)enchantment.comp_349()).comp_2686().method_27661();
         if (enchantment.method_40220(class_9636.field_51551)) {
            mutableText.method_27692(class_124.field_1061);
         } else {
            mutableText.method_27692(class_124.field_1080);
         }

         if (level != 1 || ((class_1887)enchantment.comp_349()).method_8183() != 1) {
            mutableText.method_10852(class_5244.field_41874);
            mutableText.method_10852(tooltips.getEnchantmentLevelTooltip(level, ((class_1887)enchantment.comp_349()).method_8183()));
         }

         cir.setReturnValue(mutableText);
      }
   }
}
