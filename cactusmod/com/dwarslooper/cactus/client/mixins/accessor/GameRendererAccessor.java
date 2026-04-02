package com.dwarslooper.cactus.client.mixins.accessor;

import net.minecraft.class_757;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_757.class})
public interface GameRendererAccessor {
   @Invoker("method_3202")
   boolean rendersBlockOutline();
}
