package com.dwarslooper.cactus.client.mixins.client.cape;

import com.dwarslooper.cactus.client.feature.commands.DebugCommand;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.StreamerMode;
import com.mojang.authlib.GameProfile;
import java.util.function.Supplier;
import net.minecraft.class_310;
import net.minecraft.class_640;
import net.minecraft.class_7920;
import net.minecraft.class_8685;
import net.minecraft.class_12079.class_12080;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_640.class})
public abstract class PlayerInfoMixin {
   @Shadow
   @Final
   private GameProfile field_3741;
   @Shadow
   @Nullable
   private Supplier<class_8685> field_45607;

   @Inject(
      method = {"method_52810"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getSkinTextures(CallbackInfoReturnable<class_8685> cir) {
      if (this.field_45607 != null) {
         class_8685 skinTextures = (class_8685)this.field_45607.get();
         if (skinTextures != null && !DebugCommand.texMixinDis) {
            if (this.field_3741.name().equals(class_310.method_1551().method_1548().method_1676())) {
               StreamerMode streamerMode = (StreamerMode)ModuleManager.get().get(StreamerMode.class);
               if (streamerMode.shouldChangeClientSkin()) {
                  class_7920 skinType = streamerMode.getSkinModel();
                  cir.setReturnValue(new class_8685(new class_12080(StreamerMode.SKIN_IDENTIFIER, (String)null), skinTextures.comp_1627(), skinTextures.comp_1628(), skinType, skinTextures.comp_1630()));
               }
            }

         }
      }
   }
}
