package com.dwarslooper.cactus.client.irc.protocol.packets.chat;

import com.dwarslooper.cactus.client.feature.content.ContentPackDependent;
import com.dwarslooper.cactus.client.feature.content.ContentPackManager;
import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketIn;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.class_2561;
import net.minecraft.class_8824;

@ContentPackDependent("social")
public class ChatS2CPacket implements PacketIn {
   private final String raw;
   private final class_2561 text;

   public ChatS2CPacket(ByteBuf buf) {
      this.raw = BufferUtils.readString(buf);
      this.text = (class_2561)class_8824.field_46597.parse(JsonOps.INSTANCE, JsonParser.parseString(this.raw)).getOrThrow();
   }

   public String getRaw() {
      return this.raw;
   }

   public class_2561 getText() {
      return this.text;
   }

   public void handle(IRCClient client) throws IOException {
      if ((Boolean)CactusSettings.get().enableIrcChat.get() && ContentPackManager.isEnabled()) {
         ChatUtils.infoPrefix("CNet", this.text);
      }

   }
}
