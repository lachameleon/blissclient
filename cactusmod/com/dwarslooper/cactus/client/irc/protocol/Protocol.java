package com.dwarslooper.cactus.client.irc.protocol;

import com.dwarslooper.cactus.client.irc.protocol.packets.FeedbackBiDPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.KeepAliveBiDPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.SynchronizeSettingsC2SPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.UserInfoRequestC2SPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.UserInfoS2CPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.auth.HandshakeC2SPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.auth.KeyC2SPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.auth.LoginHelloS2CPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.auth.LoginResponseS2CPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.cape.GetAvailableCapesBiDPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.cape.GetCapeBiDPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.cape.UpdateCapeBiDPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.chat.ChatS2CPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.chat.SendChatC2SPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.cosmetic.ClientCosmeticListS2CPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.cosmetic.EmotePlayS2CPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.cosmetic.GetAvailableCosmeticsC2SPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.cosmetic.UpdateCosmeticsC2SPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.cosmetic.UseEmoteC2SPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.link.LinkCodePacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.notification.GenericNotifyS2CPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.oasis.RequestSessionStartC2SPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.oasis.UpdateSessionStateS2CPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.security.ServerActionC2SPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.security.ServerCheckBiDPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.social.JoinMeReceivePacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.social.JoinMeRequestPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.teams.TeamDataPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.teams.TeamMembershipUpdateC2SPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.teams.TeamRequestPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.teams.TeamsStatePacket;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Protocol {
   public static final Map<Integer, Function<ByteBuf, PacketIn>> INCOMING_PACKETS = new HashMap();
   public static final Map<Class<? extends PacketOut>, Integer> OUTGOING_PACKETS = new HashMap();

   public static PacketIn createPacket(int id, ByteBuf buf) {
      Function<ByteBuf, PacketIn> packetClass = (Function)INCOMING_PACKETS.get(id);
      return packetClass == null ? null : (PacketIn)packetClass.apply(buf);
   }

   public static int getPacketID(PacketOut packet) {
      return (Integer)OUTGOING_PACKETS.getOrDefault(packet.getClass(), -1);
   }

   static {
      INCOMING_PACKETS.put(100, LoginHelloS2CPacket::new);
      INCOMING_PACKETS.put(101, LoginResponseS2CPacket::new);
      INCOMING_PACKETS.put(200, KeepAliveBiDPacket::new);
      INCOMING_PACKETS.put(1, ChatS2CPacket::new);
      INCOMING_PACKETS.put(2, GetCapeBiDPacket::new);
      INCOMING_PACKETS.put(3, GetAvailableCapesBiDPacket::new);
      INCOMING_PACKETS.put(4, UpdateCapeBiDPacket::new);
      INCOMING_PACKETS.put(5, FeedbackBiDPacket::new);
      INCOMING_PACKETS.put(6, GenericNotifyS2CPacket::new);
      INCOMING_PACKETS.put(7, ClientCosmeticListS2CPacket::new);
      INCOMING_PACKETS.put(9, ServerCheckBiDPacket::new);
      INCOMING_PACKETS.put(10, UpdateSessionStateS2CPacket::new);
      INCOMING_PACKETS.put(11, UserInfoS2CPacket.Full::new);
      INCOMING_PACKETS.put(14, JoinMeReceivePacket::new);
      INCOMING_PACKETS.put(15, TeamsStatePacket::new);
      INCOMING_PACKETS.put(16, TeamDataPacket::new);
      INCOMING_PACKETS.put(18, EmotePlayS2CPacket::new);
      OUTGOING_PACKETS.put(HandshakeC2SPacket.class, 100);
      OUTGOING_PACKETS.put(KeyC2SPacket.class, 101);
      OUTGOING_PACKETS.put(KeepAliveBiDPacket.class, 200);
      OUTGOING_PACKETS.put(SendChatC2SPacket.class, 1);
      OUTGOING_PACKETS.put(GetCapeBiDPacket.class, 2);
      OUTGOING_PACKETS.put(GetAvailableCapesBiDPacket.class, 3);
      OUTGOING_PACKETS.put(UpdateCapeBiDPacket.class, 4);
      OUTGOING_PACKETS.put(FeedbackBiDPacket.class, 5);
      OUTGOING_PACKETS.put(GetAvailableCosmeticsC2SPacket.class, 7);
      OUTGOING_PACKETS.put(ServerActionC2SPacket.class, 8);
      OUTGOING_PACKETS.put(ServerCheckBiDPacket.class, 9);
      OUTGOING_PACKETS.put(RequestSessionStartC2SPacket.class, 10);
      OUTGOING_PACKETS.put(LinkCodePacket.class, 11);
      OUTGOING_PACKETS.put(UserInfoRequestC2SPacket.class, 12);
      OUTGOING_PACKETS.put(SynchronizeSettingsC2SPacket.class, 13);
      OUTGOING_PACKETS.put(JoinMeRequestPacket.class, 14);
      OUTGOING_PACKETS.put(UpdateCosmeticsC2SPacket.class, 15);
      OUTGOING_PACKETS.put(TeamRequestPacket.class, 16);
      OUTGOING_PACKETS.put(TeamMembershipUpdateC2SPacket.class, 17);
      OUTGOING_PACKETS.put(UseEmoteC2SPacket.class, 18);
   }
}
