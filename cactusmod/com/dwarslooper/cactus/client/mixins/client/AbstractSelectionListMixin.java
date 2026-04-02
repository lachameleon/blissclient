package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.gui.widget.GridScrollableWidget;
import net.minecraft.class_350;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_350.class})
public class AbstractSelectionListMixin {
   @Inject(
      method = {"method_73367"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void catchReposition(CallbackInfo ci) {
      if (this instanceof GridScrollableWidget) {
         GridScrollableWidget<?> grid = (GridScrollableWidget)this;
         grid.repositionGrid();
         ci.cancel();
      }

   }
}
