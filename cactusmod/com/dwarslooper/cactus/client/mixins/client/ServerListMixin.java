package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.util.mixinterface.IMultiplayerAddIndexImpl;
import java.util.List;
import net.minecraft.class_641;
import net.minecraft.class_642;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_641.class})
public abstract class ServerListMixin implements IMultiplayerAddIndexImpl {
   @Shadow
   @Final
   private List<class_642> field_3749;
   @Unique
   private int addIndex;

   public int cactus$getAddIndex() {
      return this.addIndex;
   }

   public void cactus$setAddIndex(int index) {
      this.addIndex = index;
   }

   @Inject(
      method = {"method_2988"},
      at = {@At(
   value = "INVOKE",
   target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
   shift = Shift.AFTER
)}
   )
   public void injectAddNormal(class_642 serverInfo, boolean hidden, CallbackInfo ci) {
      if (this.addIndex >= 0) {
         this.field_3749.remove(serverInfo);
         this.field_3749.add(Math.min(this.addIndex, this.field_3749.size()), serverInfo);
      }

   }
}
