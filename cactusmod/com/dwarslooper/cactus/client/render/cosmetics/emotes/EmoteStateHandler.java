package com.dwarslooper.cactus.client.render.cosmetics.emotes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.class_630;

public class EmoteStateHandler {
   private static final Map<UUID, EmoteState> animationStates = new HashMap();

   public static Map<UUID, EmoteState> getAnimationStates() {
      return animationStates;
   }

   public static Optional<EmoteState> get(UUID uuid) {
      return Optional.ofNullable((EmoteState)animationStates.get(uuid));
   }

   public static void updateCleanStates() {
      animationStates.entrySet().removeIf((e) -> {
         return ((EmoteState)e.getValue()).animation().isDone();
      });
   }

   public static void resetScale(class_630... parts) {
      class_630[] var1 = parts;
      int var2 = parts.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         class_630 part = var1[var3];
         part.field_37938 = part.field_37939 = part.field_37940 = 1.0F;
      }

   }
}
