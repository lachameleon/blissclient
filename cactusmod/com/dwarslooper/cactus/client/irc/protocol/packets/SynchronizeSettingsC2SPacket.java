package com.dwarslooper.cactus.client.irc.protocol.packets;

import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketOut;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.List;

public class SynchronizeSettingsC2SPacket implements PacketOut {
   private final List<Setting<?>> settings;

   public SynchronizeSettingsC2SPacket(List<Setting<?>> settings) {
      this.settings = settings;
   }

   public void write(ByteBuf buf) throws IOException {
      BufferUtils.writeCollection(buf, this.settings, (b, setting) -> {
         BufferUtils.writeString(b, setting.getSyncData().toString());
      });
   }
}
