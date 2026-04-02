package com.dwarslooper.cactus.client.irc.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import java.util.List;

public class PacketSizer extends ByteToMessageCodec<ByteBuf> {
   protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf in, ByteBuf out) throws Exception {
      int length = in.readableBytes();
      out.ensureWritable(length + 2);
      out.writeShort(length);
      out.writeBytes(in, in.readerIndex(), length);
   }

   protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> outList) throws Exception {
      if (in.readableBytes() > 2) {
         in.markReaderIndex();
         int length = in.readUnsignedShort();
         if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
         }

         outList.add(in.readBytes(length));
      }

   }
}
