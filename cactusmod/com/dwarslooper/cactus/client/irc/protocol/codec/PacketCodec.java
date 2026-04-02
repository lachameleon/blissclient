package com.dwarslooper.cactus.client.irc.protocol.codec;

import com.dwarslooper.cactus.client.irc.protocol.PacketIn;
import com.dwarslooper.cactus.client.irc.protocol.PacketOut;
import com.dwarslooper.cactus.client.irc.protocol.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.util.List;

public class PacketCodec extends ByteToMessageCodec<PacketOut> {
   protected void encode(ChannelHandlerContext channelHandlerContext, PacketOut outgoingPacket, ByteBuf out) throws Exception {
      int id = Protocol.getPacketID(outgoingPacket);
      if (id < 0) {
         out.release();
         throw new EncoderException(":( Bad packet class: " + outgoingPacket.getClass().getName());
      } else {
         out.writeByte(id);
         outgoingPacket.write(out);
      }
   }

   protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> outList) throws Exception {
      if (in.readableBytes() > 0) {
         int id = in.readUnsignedByte();
         PacketIn incomingPacket = Protocol.createPacket(id, in);
         if (incomingPacket == null) {
            in.release();
            throw new DecoderException(":( Bad packet ID: " + id);
         }

         outList.add(incomingPacket);
      }

   }
}
