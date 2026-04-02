package com.dwarslooper.cactus.client.mixins.accessor;

import net.minecraft.class_4341;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_4341.class})
public interface RealmsClientAccessor {
   @Accessor("field_19580")
   @Mutable
   void setUsername(String var1);

   @Accessor("field_19579")
   @Mutable
   void setSessionId(String var1);
}
