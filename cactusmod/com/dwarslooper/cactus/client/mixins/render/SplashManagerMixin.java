package com.dwarslooper.cactus.client.mixins.render;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.util.networking.HttpUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.class_2561;
import net.minecraft.class_4008;
import net.minecraft.class_8519;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_4008.class})
public abstract class SplashManagerMixin {
   @Unique
   private static final Random random = new Random();
   @Unique
   private final List<class_2561> cactusSplashes = this.getCactusSplashes();

   @Inject(
      method = {"method_18174"},
      at = {@At("TAIL")},
      cancellable = true
   )
   public void getSplashes(CallbackInfoReturnable<class_8519> cir) {
      if (Math.random() < 0.2D) {
         try {
            cir.setReturnValue(new class_8519((class_2561)this.cactusSplashes.get(random.nextInt(this.cactusSplashes.size()))));
         } catch (Exception var3) {
            CactusClient.getLogger().error("Failed to load custom splash text", var3);
         }
      }

   }

   @Unique
   private List<class_2561> getCactusSplashes() {
      try {
         JsonArray array = HttpUtils.fetchJson("https://cdn.cactusmod.xyz/client/shared/splashes.json").getAsJsonArray();
         return new ArrayList(array.asList().stream().map(JsonElement::getAsString).map((s) -> {
            return class_2561.method_43470(s).method_54663(-256);
         }).toList());
      } catch (Exception var2) {
         return List.of(class_2561.method_43470("No splashes for the offline folk 3:"));
      }
   }
}
