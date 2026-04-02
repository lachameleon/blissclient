package com.dwarslooper.cactus.client.irc;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.event.impl.BackendPacketReceiveEvent;
import com.dwarslooper.cactus.client.event.impl.ServiceConnectionStateEvent;
import com.dwarslooper.cactus.client.feature.commands.IRCCommand;
import com.dwarslooper.cactus.client.feature.commands.TestCommand;
import com.dwarslooper.cactus.client.gui.screen.impl.NotificationsScreen;
import com.dwarslooper.cactus.client.gui.toast.ToastSystem;
import com.dwarslooper.cactus.client.gui.toast.internal.GenericToast;
import com.dwarslooper.cactus.client.irc.protocol.PacketIn;
import com.dwarslooper.cactus.client.irc.protocol.PacketOut;
import com.dwarslooper.cactus.client.irc.protocol.codec.PacketCodec;
import com.dwarslooper.cactus.client.irc.protocol.codec.PacketSizer;
import com.dwarslooper.cactus.client.irc.protocol.packets.auth.HandshakeC2SPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.auth.KeyC2SPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.auth.LoginHelloS2CPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.chat.SendChatC2SPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.notification.GenericNotifyS2CPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.oasis.UpdateSessionStateS2CPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.social.JoinMeReceivePacket;
import com.dwarslooper.cactus.client.systems.NotificationManager;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.systems.profile.ProfileHandler;
import com.dwarslooper.cactus.client.systems.worldshare.OasisHostManager;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.PublicKey;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.crypto.SecretKey;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1802;
import net.minecraft.class_2477;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3515;
import net.minecraft.class_437;
import net.minecraft.class_2558.class_10606;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
public class IRCClient extends SimpleChannelInboundHandler<PacketIn> {
   public static Supplier<String> NOT_CONNECTED = () -> {
      return class_2477.method_10517().method_48307("irc.not_connected");
   };
   public static Supplier<String> RATELIMIT = () -> {
      return class_2477.method_10517().method_48307("irc.ratelimit");
   };
   public static final Logger LOGGER = LoggerFactory.getLogger("CactusClient / Network Dispatcher");
   public static boolean connectionDenied = false;
   public static final int MAX_RECONNECT_COUNT = 10;
   private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor((r) -> {
      return new Thread(r, "IRC Executor");
   });
   private final EventLoopGroup loopGroup = new NioEventLoopGroup();
   private InetSocketAddress address;
   private IRCStatus status;
   private int reconnectCount;
   private Channel channel;
   private SecretKey secretKey;
   private String clientApiKey;

   public IRCClient() {
      this.status = IRCStatus.DISCONNECTED;
      this.reconnectCount = 0;
      this.channel = null;
   }

   public IRCStatus getStatus() {
      return this.status;
   }

   public InetSocketAddress getAddress() {
      return this.address;
   }

   public static boolean connected() {
      return CactusClient.getInstance().getIrcClient().getStatus() == IRCStatus.CONNECTED;
   }

   public void submitTask(Runnable runnable) {
      this.executorService.submit(runnable);
   }

   public void connect() {
      if (this.reconnectCount >= 10) {
         this.reconnectCount = 0;
      } else {
         this.disconnect();
         if (this.status == IRCStatus.DISCONNECTED) {
            this.status(IRCStatus.CONNECTING);
            this.executorService.submit(() -> {
               if (IRCCommand.customAddress == null) {
                  try {
                     label61: {
                        InputStream is = (new URI("http://backend.dwarslooper.com/cactus/irc/address.txt")).toURL().openStream();

                        label53: {
                           try {
                              String[] b = (new String(is.readAllBytes())).split(":");
                              if (b.length == 3) {
                                 LOGGER.info("Error while getting IRC Address from Website: Invalid IP format!");
                                 break label53;
                              }

                              this.address = new InetSocketAddress(b[0], Integer.parseInt(b[1]));
                           } catch (Throwable var6) {
                              if (is != null) {
                                 try {
                                    is.close();
                                 } catch (Throwable var4) {
                                    var6.addSuppressed(var4);
                                 }
                              }

                              throw var6;
                           }

                           if (is != null) {
                              is.close();
                           }
                           break label61;
                        }

                        if (is != null) {
                           is.close();
                        }

                        return;
                     }
                  } catch (Exception var7) {
                     LOGGER.error("Error while getting IRC Address from Website: {}", String.valueOf(var7));
                     return;
                  }
               } else {
                  this.address = IRCCommand.customAddress;
               }

               Bootstrap bx = (Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).channel(NioSocketChannel.class)).handler(new ChannelInitializer<Channel>() {
                  protected void initChannel(@NotNull Channel c) {
                     c.config().setOption(ChannelOption.TCP_NODELAY, true);
                     ChannelPipeline pipeline = c.pipeline();
                     pipeline.addLast(new ChannelHandler[]{new ReadTimeoutHandler(120L, TimeUnit.SECONDS)});
                     pipeline.addLast(new ChannelHandler[]{new PacketSizer()});
                     pipeline.addLast(new ChannelHandler[]{new PacketCodec()});
                     pipeline.addLast(new ChannelHandler[]{IRCClient.this});
                  }
               })).group(this.loopGroup)).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
               LOGGER.info("Service connecting to {}", this.address);

               try {
                  bx.connect(this.address).syncUninterruptibly();
               } catch (Exception var5) {
                  LOGGER.error("Something went wrong with the backend while connecting", var5);
               }

            });
         }
      }
   }

   public void disconnect() {
      if (this.channel != null) {
         if (this.channel.isOpen()) {
            this.status(IRCStatus.DISCONNECTED);
            this.channel.disconnect();
            this.channel.close();
            this.channel = null;
         } else {
            this.status(IRCStatus.DISCONNECTED);
            this.channel = null;
         }
      } else {
         this.status(IRCStatus.DISCONNECTED);
      }

   }

   public void channelActive(@NotNull ChannelHandlerContext ctx) {
      this.channel = ctx.channel();
      this.status(IRCStatus.CONNECTED);
      this.sendPacket(new HandshakeC2SPacket(CactusConstants.mc.method_1548().method_1676()));
   }

   public void channelInactive(@NotNull ChannelHandlerContext ctx) {
      this.channel = null;
      this.secretKey = null;
      if (this.status == IRCStatus.CONNECTED && !connectionDenied) {
         ++this.reconnectCount;
         this.executorService.schedule(this::connect, 5L, TimeUnit.SECONDS);
      }

      if (this.status != IRCStatus.DISCONNECTED) {
         ChatUtils.infoPrefix("CNet", "§cService disconnected");
      }

      this.status(IRCStatus.DISCONNECTED);
      LOGGER.info("Service disconnected");
   }

   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
      if (cause != null) {
         LOGGER.error("CNet error", cause);
      } else {
         LOGGER.error("CNet error");
      }

      this.disconnect();
      this.executorService.schedule(this::connect, 5L, TimeUnit.SECONDS);
   }

   public void sendPacket(PacketOut packet) {
      if (this.channel != null && this.channel.isOpen()) {
         this.channel.writeAndFlush(packet);
      }
   }

   protected void channelRead0(ChannelHandlerContext channelHandlerContext, PacketIn incomingPacket) throws Exception {
      incomingPacket.handle(this);
      CactusClient.EVENT_BUS.post(new BackendPacketReceiveEvent(incomingPacket));
   }

   public void handle(LoginHelloS2CPacket packet) {
      try {
         PublicKey publicKey = packet.getPublicKey();
         this.secretKey = class_3515.method_15239();
         String serverID = (new BigInteger(class_3515.method_15240("cactusclient", publicKey, this.secretKey))).toString(16);
         CactusConstants.mc.method_73361().comp_837().joinServer(CactusConstants.mc.method_1548().method_44717(), CactusConstants.mc.method_1548().method_1674(), serverID);
         if (this.status != IRCStatus.CONNECTED) {
            throw new IllegalStateException("Tried to login to CNet after connection was closed");
         }

         this.sendPacket(new KeyC2SPacket(this.secretKey, publicKey));
         this.clientApiKey = Utils.computeSecureEncoded(512, this.secretKey.getEncoded());
         if (CactusConstants.DEVBUILD) {
            LOGGER.info("CNet Session key: '{}'", this.clientApiKey);
         }
      } catch (Exception var4) {
         notification(true, "Service", "§cAuthentication failed.");
         LOGGER.error("CNet Authentication failed", var4);
         this.disconnect();
      }

   }

   public void handle(GenericNotifyS2CPacket packet) throws IOException {
      if (packet.getType() == GenericNotifyS2CPacket.MessageType.TOAST) {
         if (packet.isLongContent()) {
            GenericToast toast = (new GenericToast(packet.getTitle(), packet.getContent())).setStyle(GenericToast.Style.SYSTEM).setDuration((long)packet.getDisplayDuration());
            CactusConstants.mc.method_1566().method_1999(toast);
         } else {
            class_2960 identifier = null;
            if (!packet.getTextureIdentifier().isEmpty()) {
               if (packet.getTextureIdentifier().startsWith("http")) {
                  try {
                     BufferedInputStream in = new BufferedInputStream((new URI(packet.getTextureIdentifier())).toURL().openStream());
                     class_1011 nativeImage = class_1011.method_4309(in);
                     in.close();
                     identifier = class_2960.method_60655("cactus", "dynamic/toast");
                     class_310.method_1551().method_1531().method_4616(identifier, new class_1043(() -> {
                        return "cactus:toast";
                     }, nativeImage));
                  } catch (URISyntaxException var6) {
                     LOGGER.error("Can't parse texture url", var6);
                  }
               } else {
                  identifier = class_2960.method_60654(packet.getTextureIdentifier());
               }
            }

            CactusConstants.mc.execute(() -> {
               GenericToast toast = (new GenericToast(packet.getTitle(), packet.getContent())).setStyle(GenericToast.Style.DEFAULT_DARK).setDuration((long)packet.getDisplayDuration()).setIcon(identifier);
               CactusConstants.mc.method_1566().method_1999(toast);
            });
         }
      } else if (packet.getType() == GenericNotifyS2CPacket.MessageType.PERSISTENT) {
         NotificationManager.NotificationMessage message = packet.toMessage();
         NotificationManager.add(message);
         GenericToast toast = getMessageToast();
         CactusConstants.mc.method_1566().method_1999(toast);
         class_437 var5 = CactusConstants.mc.field_1755;
         if (var5 instanceof NotificationsScreen) {
            NotificationsScreen screen = (NotificationsScreen)var5;
            screen.messageListWidget.update();
         }
      }

   }

   public void handle(UpdateSessionStateS2CPacket packet) {
      if (packet.getState() == UpdateSessionStateS2CPacket.State.TOKEN) {
         LOGGER.info("Server created authorization token: {}", packet.getMessage());
         ((OasisHostManager)CactusClient.getConfig(OasisHostManager.class)).handleToken(packet.getMessage());
         ChatUtils.info((class_2561)class_2561.method_43470("Copy Token (5 seconds)").method_27694((style) -> {
            return style.method_10958(new class_10606(packet.getMessage()));
         }));
      } else if (packet.getState() == UpdateSessionStateS2CPacket.State.EXPIRED) {
         ChatUtils.error("Request expired. Try again.");
      } else if (packet.getState() == UpdateSessionStateS2CPacket.State.STARTED) {
         ChatUtils.info("Session started!");
      } else if (packet.getState() == UpdateSessionStateS2CPacket.State.ERROR) {
         String var10000 = String.valueOf(packet.getState());
         ChatUtils.error(var10000 + " -> " + packet.getMessage());
      }

   }

   public void handle(JoinMeReceivePacket packet) {
      String address = packet.getServerAddress();
      TestCommand.JoinMeToast toast = new TestCommand.JoinMeToast(10000L, packet.getPlayerName(), address);
      CactusConstants.mc.method_1566().method_1999(toast);
   }

   public static void notification(boolean fail, String title, String text) {
      CactusConstants.mc.execute(() -> {
         ToastSystem.displayMessage(title, text, fail ? class_1802.field_8077 : class_1802.field_27069, 2000L);
      });
   }

   public boolean usesClient(UUID uuid) {
      if (CactusConstants.mc.method_52701(uuid)) {
         return true;
      } else {
         return ProfileHandler.exists(uuid) && ProfileHandler.fromProfile(uuid).isCactus();
      }
   }

   public void sendGlobalChat(String message) {
      if ((Boolean)CactusSettings.get().enableIrcChat.get()) {
         this.sendPacket(new SendChatC2SPacket(message));
      } else {
         ChatUtils.error((class_2561)class_2561.method_43471("irc.chatNotEnabled"));
      }

   }

   @Nullable
   public SecretKey getSecretKey() {
      return this.secretKey;
   }

   @NotNull
   private static GenericToast getMessageToast() {
      GenericToast toast = new GenericToast(class_2477.method_10517().method_48307("gui.screen.notifications.toastTitle"), class_2477.method_10517().method_48307("gui.screen.notifications.unread").formatted(new Object[]{NotificationManager.getUnread()})) {
         public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!(CactusConstants.mc.field_1755 instanceof NotificationsScreen)) {
               CactusConstants.mc.method_1507(new NotificationsScreen(CactusConstants.mc.field_1755));
            }

            return true;
         }
      };
      toast.setIcon(class_1802.field_8407);
      return toast;
   }

   private void status(IRCStatus status) {
      this.status = status;
      CactusClient.EVENT_BUS.post(new ServiceConnectionStateEvent(status));
   }
}
