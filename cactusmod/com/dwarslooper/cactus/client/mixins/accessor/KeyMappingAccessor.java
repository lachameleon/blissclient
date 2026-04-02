package com.dwarslooper.cactus.client.mixins.accessor;

import java.util.Map;
import net.minecraft.class_304;
import net.minecraft.class_3675.class_306;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_304.class})
public interface KeyMappingAccessor {
   @Accessor("field_1658")
   Map<class_306, class_304> getKeys();

   @Invoker("method_1425")
   void resetPressState();
}
