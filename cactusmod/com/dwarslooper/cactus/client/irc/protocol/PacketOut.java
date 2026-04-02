package com.dwarslooper.cactus.client.irc.protocol;

import io.netty.buffer.ByteBuf;
import java.io.IOException;

public interface PacketOut {
   void write(ByteBuf var1) throws IOException;
}
