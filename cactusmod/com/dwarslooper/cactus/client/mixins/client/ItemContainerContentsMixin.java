package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.BetterTooltips;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1836;
import net.minecraft.class_2561;
import net.minecraft.class_9288;
import net.minecraft.class_9473;
import net.minecraft.class_1792.class_9635;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({class_9288.class})
public class ItemContainerContentsMixin {
   @Inject(
      method = {"method_57409"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void clearTooltip(class_9635 context, Consumer<class_2561> textConsumer, class_1836 type, class_9473 components, CallbackInfo ci) {
      BetterTooltips module = (BetterTooltips)ModuleManager.get().get(BetterTooltips.class);
      if (module.active() && (Boolean)module.overwriteDefaultTooltip.get()) {
         ci.cancel();
      }

   }
}
