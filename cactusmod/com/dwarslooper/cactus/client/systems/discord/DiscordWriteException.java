package com.dwarslooper.cactus.client.systems.discord;

public class DiscordWriteException extends RuntimeException {
   public DiscordWriteException(String message) {
      super(message);
   }

   public DiscordWriteException(String message, Throwable cause) {
      super(message, cause);
   }
}
