package com.dwarslooper.cactus.client.event.impl;

import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;

public class AttackBlockEvent extends ActionEvent {
   private final class_1657 player;
   private final class_1937 world;
   private final class_1268 hand;
   private final class_2338 pos;
   private final class_2350 direction;

   public AttackBlockEvent(class_1657 player, class_1937 world, class_1268 hand, class_2338 pos, class_2350 direction) {
      this.player = player;
      this.world = world;
      this.hand = hand;
      this.pos = pos;
      this.direction = direction;
   }

   public class_1657 getPlayer() {
      return this.player;
   }

   public class_1937 getWorld() {
      return this.world;
   }

   public class_1268 getHand() {
      return this.hand;
   }

   public class_2338 getPos() {
      return this.pos;
   }

   public class_2350 getDirection() {
      return this.direction;
   }
}
