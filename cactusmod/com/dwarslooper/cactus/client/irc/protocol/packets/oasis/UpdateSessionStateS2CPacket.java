package com.dwarslooper.cactus.client.irc.protocol.packets.oasis;

import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketIn;
import io.netty.buffer.ByteBuf;
import java.io.IOException;

public class UpdateSessionStateS2CPacket implements PacketIn {
   private final UpdateSessionStateS2CPacket.State state;
   private final String message;

   public UpdateSessionStateS2CPacket(ByteBuf buf) {
      this.state = UpdateSessionStateS2CPacket.State.valueOf(BufferUtils.readString(buf));
      this.message = BufferUtils.readString(buf);
   }

   public void handle(IRCClient client) throws IOException {
      client.handle(this);
   }

   public UpdateSessionStateS2CPacket.State getState() {
      return this.state;
   }

   public String getMessage() {
      return this.message;
   }

   public static enum State {
      EXPIRED,
      TOKEN,
      STARTED,
      ERROR;

      // $FF: synthetic method
      private static UpdateSessionStateS2CPacket.State[] $values() {
         return new UpdateSessionStateS2CPacket.State[]{EXPIRED, TOKEN, STARTED, ERROR};
      }
   }
}
