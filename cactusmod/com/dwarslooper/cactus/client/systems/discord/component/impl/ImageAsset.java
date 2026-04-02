package com.dwarslooper.cactus.client.systems.discord.component.impl;

import org.jetbrains.annotations.Nullable;

public record ImageAsset(String key, @Nullable String text) {
   public ImageAsset(String key, @Nullable String text) {
      this.key = key;
      this.text = text;
   }

   public String key() {
      return this.key;
   }

   @Nullable
   public String text() {
      return this.text;
   }
}
