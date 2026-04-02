package com.dwarslooper.cactus.client.mixins.client.screen;

import com.dwarslooper.cactus.client.mixins.accessor.ButtonAccessor;
import com.dwarslooper.cactus.client.util.CactusConstants;
import net.minecraft.class_2561;
import net.minecraft.class_410;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_6599;
import net.minecraft.class_4185.class_4241;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_6599.class})
public abstract class KeyBindsScreenMixin {
   @Shadow
   private class_4185 field_34802;

   @Inject(
      method = {"method_31387"},
      at = {@At("TAIL")}
   )
   public void modify(CallbackInfo ci) {
      ButtonAccessor resetButton = (ButtonAccessor)this.field_34802;
      class_4241 action = resetButton.getOnPress();
      resetButton.setOnPress((button) -> {
         class_437 current = CactusConstants.mc.field_1755;
         CactusConstants.mc.method_1507(new class_410((b) -> {
            if (b) {
               action.onPress(button);
            }

            CactusConstants.mc.method_1507(current);
         }, class_2561.method_43470("Are you sure you want to reset all keys?"), class_2561.method_43470("This will reset all keybindings set by you to their default state.")));
      });
   }
}
