package com.dwarslooper.cactus.client.event.impl;

import net.minecraft.class_1268;
import net.minecraft.class_3965;
import net.minecraft.class_746;

public class InteractBlockEvent extends ActionEvent {
   private final class_746 player;
   private final class_1268 hand;
   private final class_3965 hitResult;
   private final boolean isInAir;

   public InteractBlockEvent(class_746 player, class_1268 hand, class_3965 hitResult) {
      this.player = player;
      this.hand = hand;
      this.hitResult = hitResult;
      this.isInAir = player.method_73183().method_8320(hitResult.method_17777()).method_26215();
   }

   public class_746 getPlayer() {
      return this.player;
   }

   public class_1268 getHand() {
      return this.hand;
   }

   public class_3965 getHitResult() {
      return this.hitResult;
   }

   public boolean isInAir() {
      return this.isInAir;
   }
}
