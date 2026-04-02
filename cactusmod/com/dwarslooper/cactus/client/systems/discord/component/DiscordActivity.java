package com.dwarslooper.cactus.client.systems.discord.component;

import com.dwarslooper.cactus.client.systems.discord.component.impl.ImageAsset;
import com.dwarslooper.cactus.client.systems.discord.component.impl.URLButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class DiscordActivity {
   public String state;
   public String details;
   public long startTimestamp = -1L;
   public ImageAsset largeImage;
   public ImageAsset smallImage;
   public URLButton firstButton;
   public URLButton secondButton;

   public DiscordActivity() {
   }

   public DiscordActivity(String state, String details, long startTimestamp, ImageAsset largeImage, ImageAsset smallImage, URLButton firstButton, URLButton secondButton) {
      this.state = state;
      this.details = details;
      this.startTimestamp = startTimestamp;
      this.largeImage = largeImage;
      this.smallImage = smallImage;
      this.firstButton = firstButton;
      this.secondButton = secondButton;
   }

   public JsonObject compile() {
      JsonObject root = new JsonObject();
      JsonObject timestamps = new JsonObject();
      JsonObject assets = new JsonObject();
      JsonArray buttons = new JsonArray();
      if (this.state != null) {
         root.addProperty("state", this.state);
      }

      if (this.details != null) {
         root.addProperty("details", this.details);
      }

      if (this.startTimestamp >= 0L) {
         timestamps.addProperty("start", this.startTimestamp);
      }

      if (this.largeImage != null) {
         assets.addProperty("large_image", this.largeImage.key());
         if (this.largeImage.text() != null) {
            assets.addProperty("large_text", this.largeImage.text());
         }
      }

      if (this.smallImage != null) {
         assets.addProperty("small_image", this.smallImage.key());
         if (this.smallImage.text() != null) {
            assets.addProperty("small_text", this.smallImage.text());
         }
      }

      URLButton[] var5 = new URLButton[]{this.firstButton, this.secondButton};
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         URLButton b = var5[var7];
         if (b != null) {
            JsonObject button = new JsonObject();
            button.addProperty("label", b.label());
            button.addProperty("url", b.url());
            buttons.add(button);
         }
      }

      root.add("timestamps", timestamps);
      root.add("assets", assets);
      if (!buttons.isEmpty()) {
         root.add("buttons", buttons);
      }

      return root;
   }

   public boolean isComplete() {
      return this.details != null && !this.details.isBlank();
   }
}
