package com.dwarslooper.cactus.client.mixins.render;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.class_10799;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_355;
import net.minecraft.class_5348;
import net.minecraft.class_640;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(
   value = {class_355.class},
   priority = 1200
)
public abstract class PlayerTabOverlayMixin {
   @Unique
   private static final class_2960 badge = class_2960.method_60655("cactus", "textures/icons/badge.png");

   @WrapOperation(
      method = {"method_1919"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_327;method_27525(Lnet/minecraft/class_5348;)I",
   ordinal = 0
)}
   )
   public int modifyWidth(class_327 instance, class_5348 text, Operation<Integer> original, @Local class_640 playerListEntry) {
      UUID id = playerListEntry.method_2966().id();
      return instance.method_27525(text) + (id != null && featureEnabled() && CactusClient.getInstance().getIrcClient().usesClient(id) ? 10 : 0);
   }

   @WrapOperation(
      method = {"method_1919"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_332;method_27535(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;III)V"
)}
   )
   public void renderBadge(class_332 context, class_327 textRenderer, class_2561 text, int x, int y, int color, Operation<Void> original, @Local GameProfile gameProfile) {
      UUID id = gameProfile.id();
      if (id != null && featureEnabled() && CactusClient.getInstance().getIrcClient().usesClient(id)) {
         context.method_25290(class_10799.field_56883, badge, x, y, 0.0F, 0.0F, 8, 8, 8, 8);
         x += 9;
      }

      context.method_27535(textRenderer, text, x, y, color);
   }

   @Unique
   private static boolean featureEnabled() {
      return (Boolean)CactusSettings.get().showNameBadge.get();
   }
}
