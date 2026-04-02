package com.dwarslooper.cactus.client.irc;

public enum IRCStatus {
   DISCONNECTED,
   CONNECTING,
   CONNECTED;

   public String display() {
      return this.name().toLowerCase();
   }

   // $FF: synthetic method
   private static IRCStatus[] $values() {
      return new IRCStatus[]{DISCONNECTED, CONNECTING, CONNECTED};
   }
}
