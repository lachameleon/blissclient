package com.dwarslooper.cactus.client.systems.emoji;

public record EmojiCode(String id, String emoji) {
   public EmojiCode(String id, String emoji) {
      this.id = id;
      this.emoji = emoji;
   }

   public String getCode() {
      return ":" + this.id + ":";
   }

   public String getFormatted() {
      String var10000 = this.getCode();
      return var10000 + " " + this.emoji();
   }

   public boolean match(String code) {
      return this.getFormatted().equals(code);
   }

   public String id() {
      return this.id;
   }

   public String emoji() {
      return this.emoji;
   }
}
