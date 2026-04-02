package com.dwarslooper.cactus.client.mixins.render.sodium;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.RenderTweaks;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.caffeinemc.mods.sodium.client.util.FogParameters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({SodiumWorldRenderer.class})
public class SodiumWorldRendererMixin {
   @Unique
   private static final FogParameters CACTUS_DISABLED_FOG = new FogParameters(0.0F, 0.0F, 0.0F, 0.0F, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);

   @ModifyVariable(
      method = {"setupTerrain"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private FogParameters cactus$disableFog(FogParameters fogParameters) {
      RenderTweaks module = (RenderTweaks)ModuleManager.get().get(RenderTweaks.class);
      return module.active() && (Boolean)module.noFog.get() ? CACTUS_DISABLED_FOG : fogParameters;
   }
}
