package com.dwarslooper.cactus.client.irc.protocol.packets.notification;

import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketIn;
import com.dwarslooper.cactus.client.systems.NotificationManager;
import io.netty.buffer.ByteBuf;
import java.io.IOException;

public class GenericNotifyS2CPacket implements PacketIn {
   private final GenericNotifyS2CPacket.MessageType type;
   private final String title;
   private final String content;
   private String textureIdentifier;
   private String author;
   private boolean longContent;
   private int displayDuration;

   public GenericNotifyS2CPacket(ByteBuf buf) {
      this.type = GenericNotifyS2CPacket.MessageType.valueOf(BufferUtils.readString(buf));
      this.title = BufferUtils.readString(buf);
      this.content = BufferUtils.readString(buf);
      if (this.type == GenericNotifyS2CPacket.MessageType.TOAST) {
         this.longContent = buf.readBoolean();
         this.displayDuration = buf.readInt();
         this.textureIdentifier = BufferUtils.readString(buf);
      } else if (this.type == GenericNotifyS2CPacket.MessageType.PERSISTENT) {
         this.author = BufferUtils.readString(buf);
      }

   }

   public boolean isLongContent() {
      return this.longContent;
   }

   public int getDisplayDuration() {
      return this.displayDuration;
   }

   public String getTitle() {
      return this.title;
   }

   public String getContent() {
      return this.content;
   }

   public String getTextureIdentifier() {
      return this.textureIdentifier;
   }

   public GenericNotifyS2CPacket.MessageType getType() {
      return this.type;
   }

   public String getAuthor() {
      return this.author;
   }

   public NotificationManager.NotificationMessage toMessage() {
      if (this.getType() != GenericNotifyS2CPacket.MessageType.PERSISTENT) {
         throw new IllegalStateException("Packet message is not of type PERSISTENT");
      } else {
         return new NotificationManager.NotificationMessage(this.getTitle(), this.getContent(), this.getAuthor(), System.currentTimeMillis(), false);
      }
   }

   public void handle(IRCClient client) throws IOException {
      client.handle(this);
   }

   public static enum MessageType {
      TOAST,
      PERSISTENT;

      // $FF: synthetic method
      private static GenericNotifyS2CPacket.MessageType[] $values() {
         return new GenericNotifyS2CPacket.MessageType[]{TOAST, PERSISTENT};
      }
   }
}
