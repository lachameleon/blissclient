package com.dwarslooper.cactus.client.systems.discord.connection.impl;

import com.dwarslooper.cactus.client.systems.discord.DiscordWriteException;
import com.dwarslooper.cactus.client.systems.discord.connection.DiscordSocketConnection;
import java.io.File;
import java.io.IOException;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CompletableFuture;

public class UnixDiscordConnection extends DiscordSocketConnection {
   private static final CompletableFuture<String> TEMP_DIR = CompletableFuture.supplyAsync(() -> {
      String[] envVars = new String[]{"XDG_RUNTIME_DIR", "TMPDIR", "TMP", "TEMP"};
      String[] var1 = envVars;
      int var2 = envVars.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String envVar = var1[var3];
         String value = System.getenv(envVar);
         if (value != null && !value.isEmpty()) {
            return value;
         }
      }

      return "/tmp";
   });
   private SocketChannel socketChannel;

   public boolean openSocket() {
      for(int i = 0; i < 10; ++i) {
         String socketFileName = String.format("%s/discord-ipc-%d", TEMP_DIR.join(), i);
         File socketFile = new File(socketFileName);
         if (socketFile.exists()) {
            try {
               this.socketChannel = SocketChannel.open(UnixDomainSocketAddress.of(socketFileName));
               return true;
            } catch (IOException var5) {
            }
         }
      }

      return false;
   }

   public boolean closeSocket() {
      if (!this.isOpen()) {
         return false;
      } else {
         try {
            this.socketChannel.close();
            this.socketChannel = null;
            return true;
         } catch (IOException var2) {
            return false;
         }
      }
   }

   public boolean isOpen() {
      return this.socketChannel != null && this.socketChannel.isOpen();
   }

   public void writeSocket(ByteBuffer buffer) throws DiscordWriteException {
      try {
         this.socketChannel.write(buffer);
      } catch (IOException var3) {
         throw new DiscordWriteException("Failed to write to Discord socket", var3);
      }
   }
}
