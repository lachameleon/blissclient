package com.dwarslooper.cactus.client.mixins.client.screen;

import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_342;
import net.minecraft.class_437;
import net.minecraft.class_471;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_471.class})
public abstract class AnvilScreenMixin extends class_437 {
   @Shadow
   private class_342 field_2821;
   @Unique
   private static final class_2960 btnTex = class_2960.method_60654("textures/gui/sprites/statistics/item_crafted.png");
   @Unique
   private static boolean format = false;
   @Unique
   private final boolean enabled;

   @Shadow
   protected abstract void method_2403(String var1);

   public AnvilScreenMixin(class_2561 title) {
      super(title);
      this.enabled = (Boolean)CactusSettings.get().experiments.get();
   }

   @Inject(
      method = {"method_25445"},
      at = {@At("TAIL")}
   )
   public void setup(CallbackInfo ci) {
      if (this.enabled) {
         int x = this.field_2821.method_46426() + this.field_2821.method_25368() - 20;
         int y = this.field_2821.method_46427() - 20;
      }
   }

   @ModifyVariable(
      method = {"method_2403"},
      at = @At("HEAD"),
      ordinal = 0,
      argsOnly = true
   )
   public String formatItemName(String name) {
      if (!this.enabled) {
         return name;
      } else {
         return format ? name.replaceAll("&", "§") : name;
      }
   }
}
