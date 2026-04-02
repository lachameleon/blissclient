package com.dwarslooper.cactus.client.systems.discord.connection.impl;

import com.dwarslooper.cactus.client.systems.discord.DiscordWriteException;
import com.dwarslooper.cactus.client.systems.discord.connection.DiscordSocketConnection;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class WinDiscordConnection extends DiscordSocketConnection {
   private RandomAccessFile randomAccessFile;

   public boolean openSocket() {
      int i = 0;

      while(i < 10) {
         try {
            this.randomAccessFile = new RandomAccessFile("\\\\\\\\?\\\\pipe\\\\discord-ipc-" + i, "rw");
            return true;
         } catch (FileNotFoundException var3) {
            ++i;
         }
      }

      return false;
   }

   public boolean closeSocket() {
      if (!this.isOpen()) {
         return false;
      } else {
         try {
            this.randomAccessFile.close();
            this.randomAccessFile = null;
            return true;
         } catch (IOException var2) {
            return false;
         }
      }
   }

   public boolean isOpen() {
      return this.randomAccessFile != null && this.randomAccessFile.getChannel().isOpen();
   }

   public void writeSocket(ByteBuffer buffer) throws DiscordWriteException {
      try {
         this.randomAccessFile.write(buffer.array());
      } catch (IOException var3) {
         throw new DiscordWriteException("Failed to write to socket", var3);
      }
   }
}
