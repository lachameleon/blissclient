package dev.stardust.mixin;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.*;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.*;
import dev.stardust.modules.MusicTweaks;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.mojang.blaze3d.audio.Channel;
import dev.stardust.mixin.accessor.SourceManagerAccessor;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Tas [0xTas] <root@0xTas.dev>
 **/
@Mixin(SoundEngine.class)
public class SoundSystemMixin {
    @Shadow
    @Final
    private Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;

    @Unique
    @Mutable
    private int totalTicksPlaying;
    @Unique
    private boolean dirtyPitch = false;
    @Unique
    private boolean dirtyVolume = false;


    // See MusicTweaks.java
    @Inject(method = "tickInGameSound()V", at = @At("TAIL"))
    private void mixinTick(CallbackInfo ci) {
        Modules modules = Modules.get();
        if (modules == null ) return;
        MusicTweaks tweaks = modules.get(MusicTweaks.class);

        boolean playing = false;
        @Nullable String songID = null;
        for (SoundInstance instance : instanceToChannel.keySet()) {
            Sound sound = instance.getSound();
            if (sound == null) continue;

            String location = sound.getPath().toString();
            if (!location.startsWith("minecraft:sounds/music/") && !sound.toString().contains("minecraft:records/")) continue;
            ChannelAccess.ChannelHandle sourceManager = this.instanceToChannel.get(instance);
            songID = location.substring(location.lastIndexOf('/') + 1);

            if (sourceManager == null) continue;
            Channel source = ((SourceManagerAccessor) sourceManager).getSource();
            if (source == null) continue;

            playing = true;
            tweaks.setCurrentSong(sound.toString());
            if (tweaks.isActive() && !tweaks.randomPitch()) {
                this.dirtyPitch = true;
                source.setPitch(1.0f + tweaks.getPitchAdjustment());
            } else if (tweaks.isActive() && tweaks.randomPitch() && tweaks.trippyPitch()) {
                this.dirtyPitch = true;
                source.setPitch(tweaks.getNextPitchStep(instance.getPitch())); // !!
            } else if (!tweaks.isActive() && this.dirtyPitch) {
                source.setPitch(1f);
                this.dirtyPitch = false;
            }
            if (tweaks.isActive()) {
                this.dirtyVolume = true;
                source.setVolume(Mth.clamp(tweaks.getClient().options.getFinalSoundSourceVolume(instance.getSource()) + tweaks.getVolumeAdjustment(), 0.0f, 4.0f));
            } else if (this.dirtyVolume) {
                this.dirtyVolume = false;
                source.setVolume(tweaks.getClient().options.getFinalSoundSourceVolume(instance.getSource()));
            }
        }
        if (playing) {
            ++this.totalTicksPlaying;
        } else {
            this.totalTicksPlaying = 0;
        }

        if (tweaks.isActive() && this.totalTicksPlaying % 30 == 0 && tweaks.shouldDisplayNowPlaying() && songID != null) {
            if (this.totalTicksPlaying <= 90 || !tweaks.shouldFadeOut()) {
                String songName = tweaks.getSongName(songID);

                // See NarratorManagerMixin.java lol
                switch (tweaks.getDisplayMode()) {
                    case Chat -> tweaks.sendNowPlayingMessage(songName);
                    case Record -> tweaks.getClient().gui.setNowPlaying(Component.nullToEmpty(songName));
                }
            }
        }
    }
}
