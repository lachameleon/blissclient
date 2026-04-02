package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.util.CrashPreventor;
import com.dwarslooper.cactus.client.gui.screen.impl.SubmitLastCrashScreen;
import com.dwarslooper.cactus.client.util.CactusConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1109;
import net.minecraft.class_128;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin({class_128.class})
public abstract class CrashReportMixin {
   @Inject(
      method = {"method_560"},
      at = {@At("HEAD")}
   )
   private static void playFunny(Throwable cause, String title, CallbackInfoReturnable<class_128> cir) {
      if (CactusClient.hasFinishedLoading()) {
         if (ModuleManager.get().active(CrashPreventor.class)) {
            boolean b = CactusConstants.mc.field_1755 instanceof SubmitLastCrashScreen.CrashedScreen;
            CactusConstants.mc.method_40000(() -> {
               if (class_310.method_1551().method_1483() != null && CactusConstants.mc.method_53466()) {
                  if (!b) {
                     class_310.method_1551().method_1483().method_4873(class_1109.method_4758(class_3414.method_47908(class_2960.method_60655("cactus", "crash")), 1.0F));
                  }

                  CactusConstants.mc.method_1507(new SubmitLastCrashScreen.CrashedScreen());
               }

            });
         }

      }
   }
}
