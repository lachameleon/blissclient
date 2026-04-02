package com.dwarslooper.cactus.client.mixins.accessor;

import java.util.Comparator;
import net.minecraft.class_350;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_350.class})
public interface AbstractSelectionListAccessor {
   @Invoker("method_25337")
   int getRowY(int var1);

   @Invoker("method_73372")
   void invokeSort(Comparator<?> var1);
}
