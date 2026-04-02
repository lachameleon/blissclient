package com.dwarslooper.cactus.client.mixins.render;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.PlayerHider;
import com.dwarslooper.cactus.client.gui.screen.impl.CosmeticsListScreen;
import com.dwarslooper.cactus.client.render.cosmetics.CactusFeatureRenderer;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.mixinterface.IAvatarRenderState;
import com.dwarslooper.cactus.client.util.mixinterface.IBipedEntityModel;
import net.minecraft.class_10055;
import net.minecraft.class_1007;
import net.minecraft.class_11659;
import net.minecraft.class_11890;
import net.minecraft.class_11901;
import net.minecraft.class_12075;
import net.minecraft.class_4587;
import net.minecraft.class_591;
import net.minecraft.class_742;
import net.minecraft.class_922;
import net.minecraft.class_5617.class_5618;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_1007.class})
public abstract class AvatarRendererMixin<AvatarlikeEntity extends class_11890 & class_11901> extends class_922<class_742, class_10055, class_591> {
   public AvatarRendererMixin(class_5618 ctx, class_591 model, float shadowRadius) {
      super(ctx, model, shadowRadius);
   }

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   public void init(class_5618 ctx, boolean slim, CallbackInfo ci) {
      this.method_4046(new CactusFeatureRenderer(this));
   }

   @Inject(
      method = {"method_62604(Lnet/minecraft/class_11890;Lnet/minecraft/class_10055;F)V"},
      at = {@At("TAIL")}
   )
   public void interceptRender(AvatarlikeEntity player, class_10055 renderState, float f, CallbackInfo ci) {
      PlayerHider ph = (PlayerHider)ModuleManager.get().get(PlayerHider.class);
      if (ph.active() && !player.equals(CactusConstants.mc.field_1724) && player.method_5739(CactusConstants.mc.field_1724) <= (float)(Integer)ph.hideDistance.get()) {
         renderState.field_53333 = true;
      }

      if (player instanceof CosmeticsListScreen.PreviewPlayer) {
         CosmeticsListScreen.PreviewPlayer pp = (CosmeticsListScreen.PreviewPlayer)player;
         ((IAvatarRenderState)renderState).cactus$setProfileHandler(pp.getProfileOverride());
      }

   }

   public void submit(class_10055 livingEntityRenderState, class_4587 matrixStack, class_11659 orderedRenderCommandQueue, class_12075 cameraRenderState) {
      ((IBipedEntityModel)this.method_4038()).cactus$setApplyEmote(true);
      ((class_591)this.method_4038()).method_62110(livingEntityRenderState);
      super.method_4054(livingEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
      ((IBipedEntityModel)this.method_4038()).cactus$setApplyEmote(false);
   }
}
