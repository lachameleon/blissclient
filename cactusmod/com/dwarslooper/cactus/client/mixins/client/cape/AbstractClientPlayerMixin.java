package com.dwarslooper.cactus.client.mixins.client.cape;

import com.dwarslooper.cactus.client.feature.commands.DebugCommand;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.Zoom;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.systems.profile.AbstractCosmetic;
import com.dwarslooper.cactus.client.systems.profile.ProfileHandler;
import net.minecraft.class_2960;
import net.minecraft.class_742;
import net.minecraft.class_8685;
import net.minecraft.class_12079.class_12080;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_742.class})
public abstract class AbstractClientPlayerMixin {
   @Inject(
      method = {"method_52814"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void getSkinTextures(CallbackInfoReturnable<class_8685> cir) {
      if ((Boolean)CactusSettings.get().showCosmetics.get() && !DebugCommand.texMixinDis) {
         class_742 player = (class_742)this;
         class_8685 st = (class_8685)cir.getReturnValue();
         ProfileHandler manager = ProfileHandler.from(player);
         if (manager != null) {
            manager.ifCosmeticsPresent((list) -> {
               AbstractCosmetic.Cape cape = (AbstractCosmetic.Cape)list.singleOf(AbstractCosmetic.Cape.class);
               if (cape != null) {
                  class_2960 t = cape.getCurrentTextureFrame();
                  if (t == null) {
                     return;
                  }

                  cir.setReturnValue(new class_8685(st.comp_1626(), new class_12080(t, (String)null), st.comp_1628(), st.comp_1629(), st.comp_1630()));
               }

            });
         }
      }
   }

   @Inject(
      method = {"method_3118"},
      at = {@At("TAIL")},
      cancellable = true
   )
   public void zoomFovMultiplier(CallbackInfoReturnable<Float> cir) {
      Zoom zoom = (Zoom)ModuleManager.get().get(Zoom.class);
      if (zoom.isPressed() && zoom.mode.get() == Zoom.Mode.Multiply) {
         cir.setReturnValue((float)Math.pow(0.8D, (double)zoom.getZoomMultiply()));
      }

   }
}
