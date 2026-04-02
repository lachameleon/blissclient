package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.feature.content.ContentPack;
import com.dwarslooper.cactus.client.feature.content.ContentPackManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.class_338;
import net.minecraft.class_3532;
import net.minecraft.class_303.class_7590;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

@Mixin(
   value = {class_338.class},
   priority = 800
)
public abstract class ChatComponentSmoothAnimationsMixin {
   @Unique
   private static final Map<class_7590, Long> cactus$firstSeenMs = new WeakHashMap();

   @WrapOperation(
      method = {"method_71990"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_338$class_11511;accept(Lnet/minecraft/class_303$class_7590;IF)V"
)}
   )
   private void cactus$smoothChatLineAnimation(@Coerce Object consumer, class_7590 line, int messageIndex, float opacity, Operation<Void> original) {
      if (cactus$isSmoothChatAnimationEnabled()) {
         long now = System.currentTimeMillis();
         long firstSeen = (Long)cactus$firstSeenMs.computeIfAbsent(line, (l) -> {
            return now;
         });
         long duration = Math.max(0L, now - firstSeen);
         float fadeInProgress = class_3532.method_15363((float)duration / 120.0F, 0.0F, 1.0F);
         float factor = cactus$easeOutCubic(fadeInProgress);
         opacity *= factor;
      }

      original.call(new Object[]{consumer, line, messageIndex, opacity});
   }

   @Unique
   private static boolean cactus$isSmoothChatAnimationEnabled() {
      ContentPack pack = ContentPackManager.get().ofId("smooth_animations");
      return pack != null && pack.isEnabled();
   }

   @Unique
   private static float cactus$easeOutCubic(float t) {
      float x = 1.0F - t;
      return 1.0F - x * x * x;
   }
}
