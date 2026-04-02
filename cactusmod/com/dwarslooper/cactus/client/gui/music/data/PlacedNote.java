package com.dwarslooper.cactus.client.gui.music.data;

public record PlacedNote(int timestamp) {
   public PlacedNote(int timestamp) {
      this.timestamp = timestamp;
   }

   public int timestamp() {
      return this.timestamp;
   }
}
