package com.dwarslooper.cactus.client.event.impl;

import com.dwarslooper.cactus.client.irc.protocol.PacketIn;
import java.util.stream.Stream;

public record BackendPacketReceiveEvent(PacketIn packet) {
   public BackendPacketReceiveEvent(PacketIn packet) {
      this.packet = packet;
   }

   @SafeVarargs
   public final boolean isOf(Class<? extends PacketIn>... clazz) {
      return Stream.of(clazz).anyMatch((c) -> {
         return c.isInstance(this.packet);
      });
   }

   public PacketIn packet() {
      return this.packet;
   }
}
