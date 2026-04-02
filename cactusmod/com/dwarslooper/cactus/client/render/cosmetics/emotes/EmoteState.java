package com.dwarslooper.cactus.client.render.cosmetics.emotes;

import net.minecraft.class_243;

public record EmoteState(EmoteAnimation animation, boolean cancelOnMove, class_243 initialPlayerPosition) {
   public EmoteState(EmoteAnimation animation, boolean cancelOnMove, class_243 initialPlayerPosition) {
      this.animation = animation;
      this.cancelOnMove = cancelOnMove;
      this.initialPlayerPosition = initialPlayerPosition;
   }

   public EmoteAnimation animation() {
      return this.animation;
   }

   public boolean cancelOnMove() {
      return this.cancelOnMove;
   }

   public class_243 initialPlayerPosition() {
      return this.initialPlayerPosition;
   }
}
