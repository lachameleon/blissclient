package com.dwarslooper.cactus.client.event.impl;

import com.dwarslooper.cactus.client.irc.IRCStatus;

public record ServiceConnectionStateEvent(IRCStatus status) {
   public ServiceConnectionStateEvent(IRCStatus status) {
      this.status = status;
   }

   public IRCStatus status() {
      return this.status;
   }
}
