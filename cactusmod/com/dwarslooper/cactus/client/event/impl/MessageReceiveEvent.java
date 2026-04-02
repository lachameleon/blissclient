package com.dwarslooper.cactus.client.event.impl;

import net.minecraft.class_2561;
import net.minecraft.class_7469;
import net.minecraft.class_7591;

public class MessageReceiveEvent extends EventCancellable {
   public class_2561 message;
   public final class_7469 signature;
   public final class_7591 indicator;

   public MessageReceiveEvent(class_2561 message, class_7469 signature, class_7591 indicator) {
      this.message = message;
      this.signature = signature;
      this.indicator = indicator;
   }
}
