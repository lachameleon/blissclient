package com.dwarslooper.cactus.client.mixins.accessor;

import java.util.List;
import net.minecraft.class_641;
import net.minecraft.class_642;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_641.class})
public interface ServerListAccessor {
   @Accessor("field_3749")
   List<class_642> getServerList();
}
