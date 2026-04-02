package com.dwarslooper.cactus.client.systems.discord.component.impl;

public record URLButton(String label, String url) {
   public URLButton(String label, String url) {
      this.label = label;
      this.url = url;
   }

   public String label() {
      return this.label;
   }

   public String url() {
      return this.url;
   }
}
