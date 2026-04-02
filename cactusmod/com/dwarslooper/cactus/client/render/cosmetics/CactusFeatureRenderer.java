package com.dwarslooper.cactus.client.render.cosmetics;

import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.systems.profile.AbstractCosmetic;
import com.dwarslooper.cactus.client.systems.profile.ProfileHandler;
import java.util.Objects;
import java.util.Set;
import net.minecraft.class_10055;
import net.minecraft.class_11659;
import net.minecraft.class_3883;
import net.minecraft.class_3887;
import net.minecraft.class_4587;
import net.minecraft.class_591;
import org.jetbrains.annotations.NotNull;

public class CactusFeatureRenderer extends class_3887<class_10055, class_591> {
   public CactusFeatureRenderer(class_3883<class_10055, class_591> context) {
      super(context);
   }

   public void submit(@NotNull class_4587 matrices, @NotNull class_11659 queue, int light, class_10055 entity, float limbAngle, float limbDistance) {
      if (!entity.field_53461) {
         if ((Boolean)CactusSettings.get().showCosmetics.get()) {
            ProfileHandler handler = ProfileHandler.from(entity);
            if (handler != null) {
               handler.ifCosmeticsPresent((list) -> {
                  if (list.has(AbstractCosmetic.Model.class)) {
                     ((Set)Objects.requireNonNull(list.allOf(AbstractCosmetic.Model.class))).forEach((m) -> {
                        m.render(matrices, queue, entity, (class_591)this.method_17165(), 0.0F, light);
                     });
                  }

                  if (list.has(AbstractCosmetic.Wings.class)) {
                     ((Set)Objects.requireNonNull(list.allOf(AbstractCosmetic.Wings.class))).forEach((w) -> {
                        w.render(matrices, queue, entity, (class_591)this.method_17165(), 0.0F, light);
                     });
                  }

               });
            }
         }
      }
   }
}
