package com.dwarslooper.cactus.client.util.mixinterface;

import net.minecraft.class_332;
import net.minecraft.class_766;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface IBackgroundAccessImpl {
   void cactus$updateBackground();

   void cactus$renderBackground(class_332 var1, float var2, float var3, CallbackInfo var4);

   class_766 cactus$getCubeMapRenderer();
}
