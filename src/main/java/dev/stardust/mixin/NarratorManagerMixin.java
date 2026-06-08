package dev.stardust.mixin;

import dev.stardust.modules.MusicTweaks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.GameNarrator;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Tas [0xTas] <root@0xTas.dev>
 **/
@Mixin(GameNarrator.class)
public class NarratorManagerMixin {
    // See SoundSystemMixin.java
    @Inject(method = "saySystemChatQueued(Lnet/minecraft/network/chat/Component;)V", at = @At("HEAD"), cancellable = true)
    private void mixinNarrate(Component text, CallbackInfo ci) {
        Modules modules = Modules.get();
        if (modules == null ) return;
        MusicTweaks tweaks = modules.get(MusicTweaks.class);

        if (tweaks == null || !tweaks.isActive()) return;
        if (text.getString().startsWith("Now Playing: ") || text.getString().contains("§2§oNow Playing§r§7: ")) ci.cancel();
    }
}
