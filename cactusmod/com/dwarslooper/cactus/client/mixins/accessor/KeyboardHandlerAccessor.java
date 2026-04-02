package com.dwarslooper.cactus.client.mixins.accessor;

import net.minecraft.class_309;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_309.class})
public interface KeyboardHandlerAccessor {
   @Invoker("method_1465")
   void copyTarget(boolean var1, boolean var2);
}
