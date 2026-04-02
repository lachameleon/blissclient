package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.util.AntiAdvertise;
import com.dwarslooper.cactus.client.gui.hud.HudManager;
import com.dwarslooper.cactus.client.gui.hud.element.impl.BossbarElement;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.class_1259;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_337;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({class_337.class})
public abstract class BossHealthOverlayMixin {
   @Unique
   private static final int TITLE_PADDING = 4;
   @Unique
   private boolean cactus$useCustomPos = false;
   @Unique
   private int cactus$baseX = 0;
   @Unique
   private int cactus$baseY = 0;
   @Unique
   private int cactus$index = 0;

   @WrapOperation(
      method = {"method_1796"},
      at = {@At(
   value = "INVOKE",
   target = "Ljava/util/Map;values()Ljava/util/Collection;"
)}
   )
   private Collection<class_1259> onPacket(Map<UUID, class_1259> instance, Operation<Collection<?>> original) {
      AntiAdvertise antiAds = (AntiAdvertise)ModuleManager.get().get(AntiAdvertise.class);
      return (Collection)(antiAds.active() && (Boolean)antiAds.blockBossBar.get() ? instance.values().stream().filter((bossBar) -> {
         return !antiAds.isAd(bossBar.method_5414());
      }).toList() : instance.values());
   }

   @Inject(
      method = {"method_1796"},
      at = {@At("HEAD")}
   )
   private void cactus$onRenderHead(class_332 context, CallbackInfo ci) {
      this.cactus$index = 0;
      this.cactus$useCustomPos = HudManager.getInstance().getElements().contains(BossbarElement.INSTANCE);
      if (this.cactus$useCustomPos) {
         int sw = context.method_51421();
         int sh = context.method_51443();
         Vector2i abs = BossbarElement.INSTANCE.getAbsolutePosition(sw, sh);
         this.cactus$baseX = abs.x();
         Objects.requireNonNull(class_310.method_1551().field_1772);
         int fh = 9;
         this.cactus$baseY = abs.y() + fh + 4;
      }

   }

   @ModifyArgs(
      method = {"method_1796"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_337;method_1799(Lnet/minecraft/class_332;IILnet/minecraft/class_1259;)V"
)
   )
   private void cactus$modifyBarArgs(Args args) {
      if (this.cactus$useCustomPos) {
         int x = this.cactus$baseX;
         int y = this.cactus$baseY + this.cactus$index * 19;
         args.set(1, x);
         args.set(2, y);
      }
   }

   @ModifyArgs(
      method = {"method_1796"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_332;method_27535(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;III)V"
)
   )
   private void cactus$modifyTitleArgs(Args args) {
      if (this.cactus$useCustomPos) {
         class_327 tr = (class_327)args.get(0);
         class_2561 text = (class_2561)args.get(1);
         int textWidth = tr.method_27525(text);
         int barX = this.cactus$baseX;
         int barY = this.cactus$baseY + this.cactus$index * 19;
         int titleX = barX + (182 - textWidth) / 2;
         Objects.requireNonNull(tr);
         int titleY = barY - (9 + 4);
         args.set(2, titleX);
         args.set(3, titleY);
         ++this.cactus$index;
      }
   }
}
