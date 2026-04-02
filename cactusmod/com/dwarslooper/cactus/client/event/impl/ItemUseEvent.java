package com.dwarslooper.cactus.client.event.impl;

import net.minecraft.class_1799;
import net.minecraft.class_239;
import net.minecraft.class_746;

public class ItemUseEvent extends EventCancellable {
   private final class_746 player;
   private final class_1799 item;
   private final class_239 target;

   public ItemUseEvent(class_746 player, class_1799 item, class_239 target) {
      this.player = player;
      this.item = item;
      this.target = target;
   }

   public class_746 getPlayer() {
      return this.player;
   }

   public class_1799 getItem() {
      return this.item;
   }

   public class_239 getTarget() {
      return this.target;
   }
}
