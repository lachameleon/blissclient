package dev.stardust.mixin.accessor;

import javax.annotation.Nullable;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MusicManager.class)
public interface MusicTrackerAccessor {
    @Accessor("nextSongDelay")
    void setTimeUntilNextSong(int time);

    @Accessor("currentMusic")
    @Nullable
    SoundInstance getCurrent();
}
