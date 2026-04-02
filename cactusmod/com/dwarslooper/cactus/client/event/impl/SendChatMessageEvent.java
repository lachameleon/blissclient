package com.dwarslooper.cactus.client.event.impl;

public class SendChatMessageEvent extends EventCancellable {
   private String content;

   public SendChatMessageEvent(String content) {
      this.content = content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public String getContent() {
      return this.content;
   }
}
