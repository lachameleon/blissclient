package com.dwarslooper.cactus.client.irc.protocol.packets.security;

import com.dwarslooper.cactus.client.gui.screen.impl.ReportServerScreen;
import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketOut;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.class_642;

public class ServerActionC2SPacket implements PacketOut {
   private String validationReason;
   private ReportServerScreen.ReportReason reportReason;
   private final String address;

   public ServerActionC2SPacket(class_642 info, String validationReason) {
      this.address = info.field_3761;
      this.validationReason = validationReason;
   }

   public ServerActionC2SPacket(class_642 info, ReportServerScreen.ReportReason reportReason) {
      this.address = info.field_3761;
      this.reportReason = reportReason;
   }

   public void write(ByteBuf buf) throws IOException {
      if (this.validationReason != null) {
         BufferUtils.writeString(buf, "validate");
         BufferUtils.writeString(buf, this.address);
         BufferUtils.writeString(buf, this.validationReason);
      } else {
         if (this.reportReason == null) {
            throw new IllegalArgumentException("Invalid packet data provided");
         }

         BufferUtils.writeString(buf, "report");
         BufferUtils.writeString(buf, this.address);
         BufferUtils.writeString(buf, this.reportReason.id());
      }

   }
}
