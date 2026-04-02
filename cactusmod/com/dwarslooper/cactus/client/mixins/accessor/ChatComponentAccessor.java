package com.dwarslooper.cactus.client.mixins.accessor;

import java.util.List;
import net.minecraft.class_303;
import net.minecraft.class_338;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_338.class})
public interface ChatComponentAccessor {
   @Accessor("field_2061")
   List<class_303> getAllMessages();

   @Accessor("field_2064")
   List<class_303> getTrimmedMessages();
}
