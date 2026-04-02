package com.dwarslooper.cactus.client.event.impl;

import com.dwarslooper.cactus.client.event.ICancellable;
import net.minecraft.class_1269;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ActionEvent implements ICancellable {
   private class_1269 result;

   public void cancel() {
      this.result = class_1269.field_5814;
   }

   public void setCancelled(boolean cancelled) {
      if (cancelled) {
         this.result = class_1269.field_5814;
      }

   }

   public boolean isCancelled() {
      return this.result == class_1269.field_5814;
   }

   public class_1269 getResult() {
      return (class_1269)(this.result == null ? class_1269.field_5811 : this.result);
   }

   public void setResult(class_1269 result) {
      this.result = result;
   }

   public boolean hasModifiedResult() {
      return this.result != null;
   }

   public void applyToCallback(CallbackInfoReturnable<class_1269> cir) {
      cir.setReturnValue(this.result);
   }
}
