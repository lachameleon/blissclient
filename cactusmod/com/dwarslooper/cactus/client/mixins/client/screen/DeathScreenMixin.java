package com.dwarslooper.cactus.client.mixins.client.screen;

import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.PositionUtils;
import java.util.Objects;
import net.minecraft.class_124;
import net.minecraft.class_2338;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_418;
import net.minecraft.class_437;
import net.minecraft.class_5250;
import net.minecraft.class_7077;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_418.class})
public abstract class DeathScreenMixin extends class_437 {
   @Unique
   private class_2338 deathPosition;
   @Unique
   private boolean showCoordinates;

   public DeathScreenMixin(class_2561 title) {
      super(title);
   }

   @Inject(
      method = {"method_25426"},
      at = {@At("TAIL")}
   )
   public void addCoordinates(CallbackInfo ci) {
      this.deathPosition = CactusConstants.mc.field_1724.method_24515();
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      int var10005 = 104 + 9;
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      this.method_37063(this.rePosition(new class_7077(0, var10005, 0, 9, class_2561.method_43470("[ Show death coordinates ]").method_27692(class_124.field_1061), (button) -> {
         this.showCoordinates = true;
         this.method_37066(button);
      }, CactusConstants.mc.field_1772)));
   }

   @Inject(
      method = {"method_25394"},
      at = {@At("TAIL")}
   )
   public void injectRender(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      if (this.showCoordinates) {
         class_327 var10001 = this.field_22793;
         class_5250 var10002 = class_2561.method_43470(PositionUtils.toString(this.deathPosition)).method_27692(class_124.field_1061);
         int var10003 = this.field_22789 / 2;
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         context.method_27534(var10001, var10002, var10003, 104 + 9, -1);
      }

   }

   @Unique
   public class_339 rePosition(class_339 widget) {
      class_2561 m = widget.method_25369();
      int w = CactusConstants.mc.field_1772.method_27525(m);
      widget.method_25358(w);
      widget.method_46421((this.field_22789 - w) / 2);
      return widget;
   }
}
