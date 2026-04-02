package com.dwarslooper.cactus.client.gui.music.data;

public record PlacedClip(SingleClip ref, int offX) {
   public PlacedClip(SingleClip ref, int offX) {
      this.ref = ref;
      this.offX = offX;
   }

   public SingleClip ref() {
      return this.ref;
   }

   public int offX() {
      return this.offX;
   }
}
