package dev.stardust.mixin;

import dev.stardust.modules.MusicTweaks;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.Music;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MusicTweaksMinecraftClientMixin {
    @Inject(method = "getSituationalMusic", at = @At("HEAD"), cancellable = true)
    private void stardust$musicTweaks(CallbackInfoReturnable<Music> cir) {
        Modules modules = Modules.get();
        if (modules == null) return;

        MusicTweaks tweaks = modules.get(MusicTweaks.class);
        if (tweaks == null || !tweaks.isActive()) return;

        Music type = tweaks.getType();
        if (type != null) cir.setReturnValue(type);
    }
}
