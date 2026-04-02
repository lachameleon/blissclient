package com.dwarslooper.cactus.client.mixins.accessor;

import net.minecraft.class_7569;
import net.minecraft.class_7574;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_7574.class})
public interface ReportingContextAccessor {
   @Accessor("field_40820")
   class_7569 getEnvironment();
}
