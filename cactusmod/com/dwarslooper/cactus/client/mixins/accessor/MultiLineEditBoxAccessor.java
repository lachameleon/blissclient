package com.dwarslooper.cactus.client.mixins.accessor;

import net.minecraft.class_7529;
import net.minecraft.class_7530;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_7529.class})
public interface MultiLineEditBoxAccessor {
   @Accessor("field_39509")
   class_7530 getTextField();
}
