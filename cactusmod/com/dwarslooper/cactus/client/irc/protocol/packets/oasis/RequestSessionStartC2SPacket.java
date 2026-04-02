package com.dwarslooper.cactus.client.irc.protocol.packets.oasis;

import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketOut;
import io.netty.buffer.ByteBuf;
import java.io.IOException;

public class RequestSessionStartC2SPacket implements PacketOut {
   private final String customDomain;
   private final String proxy;

   public RequestSessionStartC2SPacket(String customDomain, String proxy) {
      this.customDomain = customDomain;
      this.proxy = proxy;
   }

   public void write(ByteBuf buf) throws IOException {
      if (this.customDomain != null) {
         BufferUtils.writeString(buf, this.customDomain);
      }

      if (this.proxy != null) {
         BufferUtils.writeString(buf, this.proxy);
      }

   }
}
