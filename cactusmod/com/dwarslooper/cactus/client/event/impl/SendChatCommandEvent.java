package com.dwarslooper.cactus.client.event.impl;

public record SendChatCommandEvent(String command) {
   public SendChatCommandEvent(String command) {
      this.command = command;
   }

   public String command() {
      return this.command;
   }
}
