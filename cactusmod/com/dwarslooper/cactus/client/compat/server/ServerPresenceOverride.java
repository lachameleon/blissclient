package com.dwarslooper.cactus.client.compat.server;

import com.dwarslooper.cactus.client.systems.discord.component.DiscordActivity;
import com.dwarslooper.cactus.client.systems.discord.component.impl.ImageAsset;
import org.jetbrains.annotations.Nullable;

public record ServerPresenceOverride(@Nullable String details, @Nullable String state, @Nullable ImageAsset smallImage) {
   public ServerPresenceOverride(@Nullable String details, @Nullable String state, @Nullable ImageAsset smallImage) {
      this.details = details;
      this.state = state;
      this.smallImage = smallImage;
   }

   public void applyTo(DiscordActivity activity) {
      if (this.details != null) {
         activity.details = this.details;
      }

      if (this.state != null) {
         activity.state = this.state;
      }

      if (this.smallImage != null) {
         activity.smallImage = this.smallImage;
      }

   }

   @Nullable
   public String details() {
      return this.details;
   }

   @Nullable
   public String state() {
      return this.state;
   }

   @Nullable
   public ImageAsset smallImage() {
      return this.smallImage;
   }
}
