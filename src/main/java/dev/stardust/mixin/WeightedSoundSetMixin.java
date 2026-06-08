package dev.stardust.mixin;

import java.util.List;
import dev.stardust.modules.MusicTweaks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import io.netty.util.internal.ThreadLocalRandom;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.client.sounds.Weighted;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.ConstantFloat;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Tas [0xTas] <root@0xTas.dev>
 **/
@Mixin(WeighedSoundEvents.class)
public abstract class WeightedSoundSetMixin implements Weighted<Sound> {
    @Shadow
    @Final
    private List<Weighted<Sound>> list;

    // See MusicTweaks.java
    @Inject(method = "getSound(Lnet/minecraft/util/RandomSource;)Lnet/minecraft/client/resources/sounds/Sound;", at = @At("HEAD"), cancellable = true)
    private void mixinGetSound(net.minecraft.util.RandomSource random, CallbackInfoReturnable<Sound> cir) {
        Modules modules = Modules.get();
        if (modules == null) return;
        MusicTweaks tweaks = modules.get(MusicTweaks.class);
        if (tweaks == null || !tweaks.isActive()) return;

        boolean overwrite = false;
        for (Weighted<Sound> sound : this.list) {
            String id = sound.getSound(random).toString();

            if (id.contains("minecraft:music/")) {
                overwrite = true;
                break;
            }
        }

        if (!overwrite) return;
        List<String> soundIDs = tweaks.getSoundSet();
        if (soundIDs.isEmpty()) return;

        float adjustedPitch;
        if (tweaks.randomPitch()) {
            adjustedPitch = 1.0f + tweaks.getRandomPitch();
        } else {
            adjustedPitch = 1.0f + tweaks.getPitchAdjustment();
        }
        float adjustedVolume = tweaks.getClient().options.getFinalSoundSourceVolume(SoundSource.MUSIC) + tweaks.getVolumeAdjustment();

        cir.setReturnValue(
            new Sound(
                Identifier.parse(soundIDs.get(ThreadLocalRandom.current().nextInt(soundIDs.size()))),
                ConstantFloat.of(adjustedVolume),
                ConstantFloat.of(adjustedPitch),
                this.getWeight(), Sound.Type.SOUND_EVENT,
                true, true, 16
            )
        );
    }
}
