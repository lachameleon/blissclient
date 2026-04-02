package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.systems.profile.AbstractCosmetic;
import com.dwarslooper.cactus.client.systems.profile.CosmeticParser;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_3262;
import net.minecraft.class_3304;
import net.minecraft.class_3902;
import net.minecraft.class_4011;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin({class_3304.class})
public class ReloadableResourceManagerMixin {
   @Inject(
      method = {"method_18232"},
      at = {@At("RETURN")}
   )
   public void hookReloadFinished(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<class_3902> initialStage, List<class_3262> packs, CallbackInfoReturnable<class_4011> cir) {
      class_4011 reload = (class_4011)cir.getReturnValue();
      if (reload != null) {
         reload.method_18364().thenAccept((t) -> {
            class_310.method_1551().execute(() -> {
               CosmeticParser.executePendingPostInitTasks();
               AbstractCosmetic.DATA_CACHE.asMap().values().forEach((cosmetic) -> {
                  if (cosmetic instanceof AbstractCosmetic.Model) {
                     AbstractCosmetic.Model model = (AbstractCosmetic.Model)cosmetic;
                     model.rebuildModelData();
                  }

               });
            });
         });
      }
   }
}
