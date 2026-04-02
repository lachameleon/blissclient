package com.dwarslooper.cactus.client.mixins.render;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.StreamerMode;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.util.generic.TextUtils;
import net.minecraft.class_5223;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({class_5223.class})
public abstract class StringDecomposerMixin {
   @ModifyArg(
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_5223;method_27473(Ljava/lang/String;ILnet/minecraft/class_2583;Lnet/minecraft/class_2583;Lnet/minecraft/class_5224;)Z",
   ordinal = 0
),
      method = {"method_27472(Ljava/lang/String;ILnet/minecraft/class_2583;Lnet/minecraft/class_5224;)Z"},
      index = 0
   )
   private static String adjustText(String text) {
      if (CactusClient.hasFinishedLoading()) {
         String s = ((StreamerMode)ModuleManager.get().get(StreamerMode.class)).hideName(text);
         return (Boolean)CactusSettings.get().fancyText.get() ? TextUtils.toSmallCaps(s.toUpperCase()) : s;
      } else {
         return text;
      }
   }
}
