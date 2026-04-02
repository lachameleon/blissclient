package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.util.Utils;
import net.minecraft.class_4267.class_4270;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_4270.class})
public abstract class ServerEntryMixin {
   @ModifyArgs(
      method = {"method_25343"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_332;method_25303(Lnet/minecraft/class_327;Ljava/lang/String;III)V",
   ordinal = 0
)
   )
   public void formatName(Args args) {
      String name = (String)args.get(1);
      args.set(1, Utils.replaceTypeableFormattingChars(name));
   }
}
