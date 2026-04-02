package com.dwarslooper.cactus.client.systems.worldshare;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.event.CRunnableClickEvent;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_5244;
import net.minecraft.class_2558.class_10606;
import net.minecraft.class_2568.class_10613;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class OasisClient extends WebSocketClient {
   public final int maxPlayers = 8;
   public final IntObjectMap<LocalChannel> childChannels = new IntObjectHashMap();
   public final String token;
   private final NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
   private final IntObjectMap<ArrayBlockingQueue<ByteBuffer>> messageQueue = new IntObjectHashMap();

   protected OasisClient(String token) {
      super(URI.create("ws://api.cactusmod.xyz:1416"));
      this.token = token;
   }

   public void onOpen(ServerHandshake handshake) {
      ChatUtils.info("Channel opened: " + handshake.getHttpStatus());
   }

   public void onMessage(String message) {
      JsonObject object = JsonParser.parseString(message).getAsJsonObject();
      if (object.has("DomainAssigned")) {
         String domain = object.get("DomainAssigned").getAsString();
         ChatUtils.info((class_2561)class_2561.method_43470("World hosted on").method_27692(class_124.field_1080).method_10852(class_5244.field_41874).method_10852(class_2561.method_43470(domain).method_27695(new class_124[]{class_124.field_1060, class_124.field_1073}).method_27694((style) -> {
            return style.method_10958(new class_10606(domain)).method_10949(new class_10613(class_2561.method_43470("Click to copy")));
         })));
      } else if (object.has("ClosedInactive")) {
         ChatUtils.warning((class_2561)class_2561.method_43470("Your world hosting session was closed due to inactivity").method_10852(class_5244.field_41874).method_10852(class_2561.method_43470("[RESTART]").method_27695(new class_124[]{class_124.field_1060, class_124.field_1073}).method_27694((style) -> {
            return style.method_10958(new CRunnableClickEvent(OasisHostManager::createSession)).method_10949(new class_10613(class_2561.method_43470("Click to copy")));
         })));
      } else if (object.has("ChannelOpen")) {
         JsonArray arr = object.getAsJsonArray("ChannelOpen");
         CactusClient.getLogger().info("Channel open: {}", arr.toString());
         int id = arr.get(0).getAsInt();
         String info = arr.get(1).getAsString();
         CactusClient.getLogger().info("Player joined: {}", info);
         this.handleChannelOpen(id);
      } else if (object.has("ChannelClosed")) {
         int id = object.get("ChannelClosed").getAsInt();
         CactusClient.getLogger().info("Channel closed: {}", id);
         LocalChannel channel = (LocalChannel)this.childChannels.remove(id);
         if (channel != null) {
            ((OasisClient.ChildHandler)channel.pipeline().get(OasisClient.ChildHandler.class)).closedFromServer = true;
            channel.close();
         }
      } else {
         ChatUtils.warning("Unknown message: " + message);
      }

   }

   public void onMessage(ByteBuffer bytes) {
      int id = bytes.get() & 255;
      ByteBuffer rest = bytes.slice();
      LocalChannel channel = (LocalChannel)this.childChannels.get(id);
      if (channel == null) {
         ((ArrayBlockingQueue)this.messageQueue.computeIfAbsent(id, (i) -> {
            return new ArrayBlockingQueue(8);
         })).add(rest);
      } else {
         ByteBuf buf = channel.alloc().buffer(rest.remaining());
         buf.writeBytes(rest);
         channel.writeAndFlush(buf);
      }

   }

   public void onClose(int code, String reason, boolean remote) {
      this.childChannels.values().forEach(Channel::close);
      this.childChannels.clear();
   }

   public void onError(Exception ex) {
      ChatUtils.error(ex.getMessage());
   }

   public void handleChannelOpen(int id) {
      ((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).channel(LocalChannel.class)).handler(new ChannelInitializer<LocalChannel>() {
         public void initChannel(LocalChannel localChannel) {
            localChannel.pipeline().addLast(new ChannelHandler[]{OasisClient.this.new ChildHandler(id)});
         }
      })).group(this.eventLoopGroup)).connect(new LocalAddress("cactus-relay"));
   }

   public class ChildHandler extends SimpleChannelInboundHandler<ByteBuf> {
      private final int id;
      private boolean closedFromServer;

      public ChildHandler(int id) {
         this.id = id;
      }

      public int getId() {
         return this.id;
      }

      public void channelActive(ChannelHandlerContext ctx) {
         if (OasisClient.this.messageQueue.containsKey(this.id)) {
            ((ArrayBlockingQueue)OasisClient.this.messageQueue.remove(this.id)).forEach((buffer) -> {
               ByteBuf buf = ctx.alloc().buffer(buffer.remaining());
               buf.writeBytes(buffer);
               ctx.writeAndFlush(buf);
            });
         }

         OasisClient.this.childChannels.put(this.id, (LocalChannel)ctx.channel());
      }

      protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
         byte[] buffer = new byte[byteBuf.readableBytes() + 1];
         buffer[0] = (byte)this.id;
         byteBuf.readBytes(buffer, 1, byteBuf.readableBytes());
         OasisClient.this.send(buffer);
      }

      public void channelInactive(ChannelHandlerContext ctx) {
         if (!this.closedFromServer) {
            JsonObject object = new JsonObject();
            object.addProperty("ChannelClosed", this.id);
            OasisClient.this.send(object.toString());
         }

         OasisClient.this.childChannels.remove(this.id);
      }
   }
}
