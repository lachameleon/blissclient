package com.dwarslooper.cactus.client.mixins.accessor;

import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3298;
import net.minecraft.class_7367;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin({class_3298.class})
public interface ResourceAccessor {
   @Accessor("field_38685")
   void setInputStream(class_7367<InputStream> var1);
}
