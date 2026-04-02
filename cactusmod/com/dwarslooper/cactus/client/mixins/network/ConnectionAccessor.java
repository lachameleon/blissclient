package com.dwarslooper.cactus.client.mixins.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2535;
import net.minecraft.class_2598;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin({class_2535.class})
public interface ConnectionAccessor {
   @Accessor("field_11643")
   class_2598 getReceiving();
}
