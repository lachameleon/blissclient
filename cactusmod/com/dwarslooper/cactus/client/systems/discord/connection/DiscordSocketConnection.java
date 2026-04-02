package com.dwarslooper.cactus.client.systems.discord.connection;

import com.dwarslooper.cactus.client.systems.discord.DiscordWriteException;
import com.google.gson.JsonObject;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public abstract class DiscordSocketConnection {
   public abstract boolean openSocket();

   public abstract boolean closeSocket();

   public abstract boolean isOpen();

   public abstract void writeSocket(ByteBuffer var1) throws DiscordWriteException;

   public void sendBuffer(int opcode, String jsonString) {
      byte[] jsonBytes = jsonString.getBytes(StandardCharsets.UTF_8);
      ByteBuffer buffer = ByteBuffer.allocate(jsonBytes.length + 8);
      buffer.putInt(Integer.reverseBytes(opcode));
      buffer.putInt(Integer.reverseBytes(jsonBytes.length));
      buffer.put(jsonBytes);
      buffer.rewind();
      this.writeSocket(buffer);
   }

   public void handshake(long appId) {
      JsonObject handshake = new JsonObject();
      handshake.addProperty("v", 1);
      handshake.addProperty("client_id", String.valueOf(appId));
      this.sendBuffer(0, handshake.toString());
   }
}
