package com.dwarslooper.cactus.client.mixins.accessor;

import java.util.List;
import net.minecraft.class_364;
import net.minecraft.class_4068;
import net.minecraft.class_437;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_437.class})
public interface ScreenWidgetsAccessor {
   @Accessor("field_33816")
   List<class_4068> getRenderables();

   @Invoker("method_37066")
   void removeChild(class_364 var1);
}
