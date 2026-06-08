package dev.stardust.mixin.accessor;

import com.mojang.blaze3d.audio.Channel;
import javax.annotation.Nullable;
import net.minecraft.client.sounds.ChannelAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChannelAccess.ChannelHandle.class)
public interface SourceManagerAccessor {
    @Accessor("channel")
    @Nullable
    Channel getSource();
}
