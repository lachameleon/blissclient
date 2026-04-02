package com.dwarslooper.cactus.client.mixins.render;

import com.dwarslooper.cactus.client.util.mixinterface.IEntityRenderState;
import java.util.UUID;
import net.minecraft.class_10017;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({class_10017.class})
public class EntityRenderStateMixin implements IEntityRenderState {
   @Unique
   private UUID uuid;

   public void cactus$setUUID(UUID uuid) {
      this.uuid = uuid;
   }

   public UUID cactus$getUUID() {
      return this.uuid;
   }
}
