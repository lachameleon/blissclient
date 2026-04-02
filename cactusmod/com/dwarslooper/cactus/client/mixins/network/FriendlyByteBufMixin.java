package com.dwarslooper.cactus.client.mixins.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2540;
import org.spongepowered.asm.mixin.Mixin;

@Environment(EnvType.CLIENT)
@Mixin({class_2540.class})
public abstract class FriendlyByteBufMixin {
}
