package com.dwarslooper.cactus.client.irc.protocol.packets;

import com.dwarslooper.cactus.client.gui.screen.impl.FeedbackScreen;
import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketIn;
import com.dwarslooper.cactus.client.irc.protocol.PacketOut;
import com.dwarslooper.cactus.client.util.CactusConstants;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.class_437;

public class FeedbackBiDPacket implements PacketOut, PacketIn {
   private String message;
   private int status;

   public FeedbackBiDPacket(ByteBuf buf) {
      this.status = buf.readInt();
   }

   public FeedbackBiDPacket(String message) {
      this.message = message;
   }

   public void handle(IRCClient client) throws IOException {
      class_437 var3 = CactusConstants.mc.field_1755;
      if (var3 instanceof FeedbackScreen) {
         FeedbackScreen feedbackScreen = (FeedbackScreen)var3;
         feedbackScreen.handleFeedbackResponse(this.status);
      }

   }

   public void write(ByteBuf buf) throws IOException {
      BufferUtils.writeString(buf, this.message);
   }
}
