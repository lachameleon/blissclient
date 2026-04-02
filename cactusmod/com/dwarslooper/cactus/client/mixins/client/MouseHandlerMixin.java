package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.event.impl.MouseClickEvent;
import com.dwarslooper.cactus.client.event.impl.MouseScrollEvent;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.BetterTooltips;
import com.dwarslooper.cactus.client.feature.modules.render.Zoom;
import com.dwarslooper.cactus.client.gui.toast.internal.CToast;
import com.dwarslooper.cactus.client.mixins.accessor.ToastManagerAccessor;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.InputUtilWrapper;
import com.dwarslooper.cactus.client.util.Utils;
import com.dwarslooper.cactus.client.util.modules.TooltipScrollHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.class_11910;
import net.minecraft.class_312;
import net.minecraft.class_368;
import net.minecraft.class_374.class_375;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_312.class})
public abstract class MouseHandlerMixin {
   @Inject(
      method = {"method_1598"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
      double d = ((Boolean)CactusConstants.mc.field_1690.method_42439().method_41753() ? Math.signum(vertical) : vertical) * (Double)CactusConstants.mc.field_1690.method_41806().method_41753();
      MouseScrollEvent event = new MouseScrollEvent(window, horizontal, vertical, d);
      CactusClient.EVENT_BUS.post(event);
      if (event.isCancelled()) {
         ci.cancel();
      } else {
         BetterTooltips tooltips = (BetterTooltips)ModuleManager.get().get(BetterTooltips.class);
         if (tooltips != null && tooltips.active() && (Boolean)tooltips.scrollableTooltips.get()) {
            if (InputUtilWrapper.hasShiftDown()) {
               TooltipScrollHandler.scrollHorizontal(horizontal);
            } else {
               TooltipScrollHandler.scrollVertical(vertical);
            }
         }

      }
   }

   @Inject(
      method = {"method_1601"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onClick(long window, class_11910 input, int action, CallbackInfo ci) {
      MouseClickEvent event = new MouseClickEvent(window, input.comp_4801(), action, input.comp_4797());
      CactusClient.EVENT_BUS.post(event);
      if (event.isCancelled()) {
         ci.cancel();
      } else {
         if (CactusConstants.mc.field_1755 != null && action == 1) {
            double mouseX = Utils.getMouseX();
            double mouseY = Utils.getMouseY();
            class_375[] var11 = (class_375[])((ToastManagerAccessor)CactusConstants.mc.method_1566()).getVisibleToasts().toArray(new class_375[0]);
            int var12 = var11.length;

            for(int var13 = 0; var13 < var12; ++var13) {
               class_375<?> toastEntry = var11[var13];
               ToastManagerAccessor.EntryAccessor entry = (ToastManagerAccessor.EntryAccessor)toastEntry;
               float posY = entry.getToast().method_71808(entry.getFirstSlotIndex());
               float posX = entry.getToast().method_71809(CactusConstants.mc.method_22683().method_4486(), 1.0F);
               if (mouseX > (double)posX && mouseY > (double)posY && mouseY < (double)(posY + (float)entry.getToast().method_29050())) {
                  class_368 var19 = entry.getToast();
                  if (var19 instanceof CToast) {
                     CToast cToast = (CToast)var19;
                     if (cToast.mouseClicked(mouseX - (double)posX, mouseY - (double)posY, input.comp_4801())) {
                        ci.cancel();
                     }
                  }
               }
            }
         }

      }
   }

   @ModifyExpressionValue(
      method = {"method_1606"},
      at = {@At(
   value = "FIELD",
   target = "Lnet/minecraft/class_315;field_1914:Z"
)}
   )
   public boolean updateSmoothCamera(boolean original) {
      Zoom zoom = (Zoom)ModuleManager.get().get(Zoom.class);
      return zoom.isPressed() ? (Boolean)zoom.cinematicCamera.get() : original;
   }
}
