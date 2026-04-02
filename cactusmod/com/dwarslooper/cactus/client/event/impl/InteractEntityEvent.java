package com.dwarslooper.cactus.client.event.impl;

import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_3966;
import net.minecraft.class_746;

public class InteractEntityEvent extends ActionEvent {
   private final class_746 player;
   private final class_1297 entity;
   private final class_3966 hitResult;
   private final class_1268 hand;

   public InteractEntityEvent(class_746 player, class_1297 entity, class_3966 hitResult, class_1268 hand) {
      this.player = player;
      this.entity = entity;
      this.hitResult = hitResult;
      this.hand = hand;
   }

   public class_746 getPlayer() {
      return this.player;
   }

   public class_1297 getEntity() {
      return this.entity;
   }

   public class_3966 getHitResult() {
      return this.hitResult;
   }

   public class_1268 getHand() {
      return this.hand;
   }
}
