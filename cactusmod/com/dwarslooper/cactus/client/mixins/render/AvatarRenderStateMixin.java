package com.dwarslooper.cactus.client.mixins.render;

import com.dwarslooper.cactus.client.systems.profile.ProfileHandler;
import com.dwarslooper.cactus.client.util.mixinterface.IAvatarRenderState;
import net.minecraft.class_10055;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({class_10055.class})
public class AvatarRenderStateMixin implements IAvatarRenderState {
   @Unique
   private ProfileHandler override;

   public void cactus$setProfileHandler(ProfileHandler override) {
      this.override = override;
   }

   public ProfileHandler cactus$getProfileHandler() {
      return this.override;
   }
}
