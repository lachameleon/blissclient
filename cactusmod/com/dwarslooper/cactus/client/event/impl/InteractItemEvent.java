package com.dwarslooper.cactus.client.event.impl;

import net.minecraft.class_1268;
import net.minecraft.class_746;

public class InteractItemEvent extends ActionEvent {
   private final class_746 player;
   private final class_1268 hand;

   public InteractItemEvent(class_746 player, class_1268 hand) {
      this.player = player;
      this.hand = hand;
   }

   public class_746 getPlayer() {
      return this.player;
   }

   public class_1268 getHand() {
      return this.hand;
   }
}
