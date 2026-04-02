package com.dwarslooper.cactus.client.mixins.accessor;

import java.util.Map;
import java.util.UUID;
import net.minecraft.class_337;
import net.minecraft.class_345;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_337.class})
public interface BossHealthOverlayAccessor {
   @Accessor("field_2060")
   Map<UUID, class_345> cactus$getEvents();
}
