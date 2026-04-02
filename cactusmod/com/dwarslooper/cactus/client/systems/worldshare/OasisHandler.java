package com.dwarslooper.cactus.client.systems.worldshare;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.event.CRunnableClickEvent;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_5244;
import net.minecraft.class_2558.class_10606;
import net.minecraft.class_2568.class_10613;

public class OasisHandler {
   private static final byte PACKET_MC = 0;
   private static final byte PACKET_STATUS = 1;
   private static final byte PACKET_HOST_REQUEST = 1;
   private static final byte PACKET_CHANNEL_OPEN = 3;
   public final int maxPlayers = 8;
   public final IntObjectMap<LocalChannel> childChannels = new IntObjectHashMap();
   public final String token;
   private final NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
   private final IntObjectMap<ArrayBlockingQueue<ByteBuffer>> messageQueue = new IntObjectHashMap();
   private Channel channel;
   private boolean connected = false;
   private static final String HOST = "api.cactusmod.xyz";
   private static final int PORT = 6797;

   protected OasisHandler(String token) {
      this.token = token;
   }

   public void connect() {
      Bootstrap bootstrap = new Bootstrap();
      ((Bootstrap)((Bootstrap)((Bootstrap)bootstrap.group(this.eventLoopGroup)).channel(NioSocketChannel.class)).option(ChannelOption.TCP_NODELAY, true)).handler(new ChannelInitializer<SocketChannel>() {
         protected void initChannel(SocketChannel ch) {
            ChannelPipeline p = ch.pipeline();
            p.addLast(new ChannelHandler[]{OasisHandler.this.new OasisClientHandler()});
         }
      });

      try {
         ChannelFuture future = bootstrap.connect("api.cactusmod.xyz", 6797).sync();
         this.channel = future.channel();
         this.connected = true;
         ChatUtils.info("Connected to Oasis server: api.cactusmod.xyz:6797");
         this.channel.closeFuture().addListener((channelFuture) -> {
            this.eventLoopGroup.shutdownGracefully();
            this.connected = false;
            this.childChannels.values().forEach(Channel::close);
            this.childChannels.clear();
            ChatUtils.warning("Disconnected from Oasis server");
         });
         this.sendHostingRequest();
      } catch (Exception var3) {
         ChatUtils.error("Failed to connect to Oasis server: " + var3.getMessage());
         this.eventLoopGroup.shutdownGracefully();
      }

   }

   private void sendHostingRequest() {
      if (this.connected && this.channel != null) {
         ByteBuf buf = Unpooled.buffer();
         buf.writeByte(1);
         String jwtToken = this.token;
         buf.writeInt(jwtToken.length());
         buf.writeBytes(jwtToken.getBytes(StandardCharsets.UTF_8));
         this.channel.writeAndFlush(buf);
      }
   }

   public void disconnect() {
      if (this.channel != null && this.channel.isOpen()) {
         this.channel.close();
      }

   }

   public void sendMinecraftPacket(int clientId, ByteBuf data) {
      if (this.connected && this.channel != null) {
         ByteBuf buf = Unpooled.buffer();
         buf.writeByte(0);
         buf.writeInt(clientId);
         writeVarInt(buf, data.readableBytes());
         buf.writeBytes(data);
         this.channel.writeAndFlush(buf);
      }
   }

   private static void writeVarInt(ByteBuf buf, int value) {
      do {
         byte temp = (byte)(value & 127);
         value >>>= 7;
         if (value != 0) {
            temp = (byte)(temp | 128);
         }

         buf.writeByte(temp);
      } while(value != 0);

   }

   private static int readVarInt(ByteBuf buf) {
      int numRead = 0;
      int result = 0;

      byte read;
      do {
         read = buf.readByte();
         int value = read & 127;
         result |= value << 7 * numRead;
         ++numRead;
         if (numRead > 5) {
            throw new RuntimeException("VarInt is too big");
         }
      } while((read & 128) != 0);

      return result;
   }

   public void handleChannelOpen(int id) {
      ((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).channel(LocalChannel.class)).handler(new ChannelInitializer<LocalChannel>() {
         public void initChannel(LocalChannel localChannel) {
            localChannel.pipeline().addLast(new ChannelHandler[]{OasisHandler.this.new ChildHandler(id)});
         }
      })).group(this.eventLoopGroup)).connect(new LocalAddress("cactus-relay"));
   }

   public class ChildHandler extends SimpleChannelInboundHandler<ByteBuf> {
      private final int id;
      public boolean closedFromServer;

      public ChildHandler(int id) {
         this.id = id;
      }

      public int getId() {
         return this.id;
      }

      public void channelActive(ChannelHandlerContext ctx) {
         if (OasisHandler.this.messageQueue.containsKey(this.id)) {
            ((ArrayBlockingQueue)OasisHandler.this.messageQueue.remove(this.id)).forEach((buffer) -> {
               ByteBuf buf = ctx.alloc().buffer(buffer.remaining());
               buf.writeBytes(buffer);
               ctx.writeAndFlush(buf);
            });
         }

         OasisHandler.this.childChannels.put(this.id, (LocalChannel)ctx.channel());
      }

      protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) {
         int packetLen = byteBuf.readableBytes();
         ByteBuf buf = Unpooled.buffer();
         buf.writeByte(0);
         buf.writeInt(this.id);
         OasisHandler.writeVarInt(buf, packetLen);
         buf.writeBytes(byteBuf);
         if (OasisHandler.this.channel != null && OasisHandler.this.channel.isActive()) {
            OasisHandler.this.channel.writeAndFlush(buf);
         } else {
            buf.release();
         }

      }

      public void channelInactive(ChannelHandlerContext ctx) {
         if (!this.closedFromServer) {
            ByteBuf buf = Unpooled.buffer();
            buf.writeByte(3);
            buf.writeInt(this.id);
            if (OasisHandler.this.channel != null && OasisHandler.this.channel.isActive()) {
               OasisHandler.this.channel.writeAndFlush(buf);
            } else {
               buf.release();
            }
         }

         OasisHandler.this.childChannels.remove(this.id);
      }
   }

   private class OasisClientHandler extends ChannelInboundHandlerAdapter {
      public void channelRead(ChannelHandlerContext ctx, Object msg) {
         if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf)msg;

            try {
               byte packetId = buf.readByte();
               int clientId;
               switch(packetId) {
               case 0:
                  this.handleMinecraftPacket(buf);
                  break;
               case 1:
                  this.handleStatusPacket(buf);
                  break;
               case 2:
                  clientId = buf.readInt();
                  CactusClient.getLogger().info("Channel open for client ID: {}", clientId);
                  OasisHandler.this.handleChannelOpen(clientId);
                  break;
               case 3:
                  clientId = buf.readInt();
                  CactusClient.getLogger().info("Channel closed for client ID: {}", clientId);
                  LocalChannel channel = (LocalChannel)OasisHandler.this.childChannels.remove(clientId);
                  if (channel != null) {
                     ((OasisHandler.ChildHandler)channel.pipeline().get(OasisHandler.ChildHandler.class)).closedFromServer = true;
                     channel.close();
                  }
                  break;
               default:
                  ChatUtils.warning("Received unknown packet ID: " + packetId);
               }
            } finally {
               buf.release();
            }

         }
      }

      private void handleMinecraftPacket(ByteBuf buf) {
         int clientId = buf.readInt();
         int packetLen = OasisHandler.readVarInt(buf);
         LocalChannel channel = (LocalChannel)OasisHandler.this.childChannels.get(clientId);
         if (channel == null) {
            ByteBuffer buffer = ByteBuffer.allocate(packetLen);
            buf.readBytes(buffer);
            buffer.flip();
            ((ArrayBlockingQueue)OasisHandler.this.messageQueue.computeIfAbsent(clientId, (i) -> {
               return new ArrayBlockingQueue(8);
            })).add(buffer);
         } else {
            ByteBuf clientBuf = channel.alloc().buffer(packetLen);
            buf.readBytes(clientBuf, packetLen);
            channel.writeAndFlush(clientBuf);
         }

      }

      private void handleStatusPacket(ByteBuf buf) {
         int msgLen = buf.readInt();
         byte[] msgBytes = new byte[msgLen];
         buf.readBytes(msgBytes);
         String message = new String(msgBytes, StandardCharsets.UTF_8);
         String error;
         if (message.startsWith("sucess: ")) {
            error = message.substring(8);
            ChatUtils.info((class_2561)class_2561.method_43470("World hosted on").method_27692(class_124.field_1080).method_10852(class_5244.field_41874).method_10852(class_2561.method_43470(error).method_27695(new class_124[]{class_124.field_1060, class_124.field_1073}).method_27694((style) -> {
               return style.method_10958(new class_10606(error)).method_10949(new class_10613(class_2561.method_43470("Click to copy")));
            })));
         } else if (message.startsWith("error: ")) {
            error = message.substring(7);
            ChatUtils.warning((class_2561)class_2561.method_43470("Error hosting world: " + error).method_10852(class_5244.field_41874).method_10852(class_2561.method_43470("[RESTART]").method_27695(new class_124[]{class_124.field_1060, class_124.field_1073}).method_27694((style) -> {
               return style.method_10958(new CRunnableClickEvent(OasisHostManager::createSession)).method_10949(new class_10613(class_2561.method_43470("Click to restart")));
            })));
         } else {
            ChatUtils.warning("Unknown status message: " + message);
         }

      }

      public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
         ChatUtils.error("Error in Oasis connection: " + cause.getMessage());
         cause.printStackTrace();
         ctx.close();
      }
   }
}
