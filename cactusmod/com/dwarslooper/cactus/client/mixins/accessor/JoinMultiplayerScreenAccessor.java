package com.dwarslooper.cactus.client.mixins.accessor;

import net.minecraft.class_4267;
import net.minecraft.class_500;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_500.class})
public interface JoinMultiplayerScreenAccessor {
   @Accessor("field_3043")
   class_4267 getServerSelectionList();
}
