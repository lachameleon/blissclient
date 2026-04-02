package com.dwarslooper.cactus.client.irc.protocol;

import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BufferUtils {
   public static String readString(ByteBuf buffer) {
      int length = buffer.readUnsignedShort();
      String string = buffer.toString(buffer.readerIndex(), length, StandardCharsets.UTF_8);
      buffer.readerIndex(buffer.readerIndex() + length);
      return string;
   }

   public static void writeString(ByteBuf buffer, String string) {
      byte[] bs = string.getBytes(StandardCharsets.UTF_8);
      buffer.writeShort(bs.length);
      buffer.writeBytes(bs);
   }

   public static byte[] readBytes(ByteBuf buf, int length) {
      byte[] bytes = new byte[length];
      buf.readBytes(bytes);
      return bytes;
   }

   public static <T> void writeCollection(ByteBuf buf, Collection<T> collection, BiConsumer<ByteBuf, T> writer) {
      buf.writeShort(collection.size());
      Iterator var3 = collection.iterator();

      while(var3.hasNext()) {
         T object = var3.next();
         writer.accept(buf, object);
      }

   }

   public static <T, C extends Collection<T>> C readCollection(ByteBuf buf, Function<ByteBuf, T> reader) {
      int i = buf.readShort();
      Collection<T> collection = new ArrayList();

      for(int j = 0; j < i; ++j) {
         collection.add(reader.apply(buf));
      }

      return collection;
   }

   public static void writeUUID(ByteBuf buf, UUID uuid) {
      buf.writeLong(uuid.getMostSignificantBits());
      buf.writeLong(uuid.getLeastSignificantBits());
   }

   public static UUID readUUID(ByteBuf buf) {
      return new UUID(buf.readLong(), buf.readLong());
   }
}
