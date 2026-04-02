package com.dwarslooper.cactus.client.irc.protocol;

import com.dwarslooper.cactus.client.irc.IRCClient;
import java.io.IOException;

public interface PacketIn {
   void handle(IRCClient var1) throws IOException;
}
