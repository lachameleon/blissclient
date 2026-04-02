package com.dwarslooper.cactus.client.mixins.accessor;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({YggdrasilAuthenticationService.class})
public interface YggdrasilAuthenticationServiceAccessor {
}
