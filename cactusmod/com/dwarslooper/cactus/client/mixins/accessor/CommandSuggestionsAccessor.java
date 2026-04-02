package com.dwarslooper.cactus.client.mixins.accessor;

import net.minecraft.class_342;
import net.minecraft.class_4717;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_4717.class})
public interface CommandSuggestionsAccessor {
   @Accessor("field_21599")
   class_342 getInput();
}
