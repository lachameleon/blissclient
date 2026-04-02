package com.dwarslooper.cactus.client.mixins.client.screen;

import com.dwarslooper.cactus.client.feature.content.ContentPackDependent;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.ServerWidget;
import com.dwarslooper.cactus.client.feature.modules.render.StreamerMode;
import com.dwarslooper.cactus.client.gui.widget.CServerEntryWidget;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_420;
import net.minecraft.class_437;
import net.minecraft.class_500;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ContentPackDependent("server_widget")
@Mixin({class_420.class})
public abstract class DirectConnectionScreenMixin extends class_437 {
   @Shadow
   private class_342 field_2463;
   @Shadow
   @Final
   private class_437 field_21790;
   @Unique
   public CServerEntryWidget entryWidget;
   @Unique
   private String lastAddress = "";
   @Unique
   private long lastUpdated = System.currentTimeMillis();
   @Unique
   private final boolean enabled = ((ServerWidget)ModuleManager.get().get(ServerWidget.class)).shouldAddTo(this);

   protected DirectConnectionScreenMixin(class_2561 title) {
      super(title);
   }

   @Inject(
      method = {"method_25426"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_420;method_25429(Lnet/minecraft/class_364;)Lnet/minecraft/class_364;"
)}
   )
   public void injectInit(CallbackInfo ci) {
      this.method_37063(this.entryWidget = new CServerEntryWidget(this.field_22789 / 2 - 150, 50, 300, 32, (class_500)this.field_21790));
      this.update();
      ((StreamerMode)ModuleManager.get().get(StreamerMode.class)).hiderWidget(this.field_2463);
      this.entryWidget.field_22764 = this.enabled;
   }

   @Inject(
      method = {"method_56131"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void preventInitialFocus(CallbackInfo ci) {
      StreamerMode streamerMode = (StreamerMode)ModuleManager.get().get(StreamerMode.class);
      if (streamerMode.active() && (Boolean)streamerMode.hideServerAddresses.get()) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"method_2169"},
      at = {@At("TAIL")}
   )
   public void fieldChange(CallbackInfo ci) {
      if (this.enabled && this.entryWidget != null) {
         if (!this.lastAddress.equalsIgnoreCase(this.field_2463.method_1882())) {
            this.lastAddress = this.field_2463.method_1882();
            this.update();
         }

      }
   }

   @Inject(
      method = {"method_25394"},
      at = {@At("TAIL")}
   )
   public void render(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      if (this.enabled && this.entryWidget != null) {
         if (this.entryWidget.field_22764) {
            double barProgress = (double)((float)(System.currentTimeMillis() - this.lastUpdated) / 4000.0F);
            boolean waiting = !this.entryWidget.pingSuccessful();
            if (barProgress >= 1.0D) {
               this.lastUpdated = System.currentTimeMillis();
               if (waiting) {
                  this.update();
               }
            }
         }

      }
   }

   @Inject(
      method = {"method_25410"},
      at = {@At("TAIL")}
   )
   public void resize(int width, int height, CallbackInfo ci) {
      if (this.entryWidget != null && this.enabled) {
         this.entryWidget.method_46421(width / 2 - 150);
      }

   }

   @Unique
   public void update() {
      if (this.enabled && this.entryWidget != null) {
         if (this.field_2463.method_1882().split("\\.").length <= 1 && !this.field_2463.method_1882().equalsIgnoreCase("localhost") && !this.field_2463.method_1882().startsWith("localhost:")) {
            this.entryWidget.field_22764 = false;
         } else {
            this.entryWidget.field_22764 = true;
            this.entryWidget.update(this.field_2463.method_1882(), this.field_2463.method_1882());
         }

      }
   }
}
