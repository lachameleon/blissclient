package com.dwarslooper.cactus.client.irc.protocol.packets.auth;

import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketIn;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class LoginHelloS2CPacket implements PacketIn {
   private final PublicKey publicKey;

   public LoginHelloS2CPacket(ByteBuf buf) {
      byte[] encodedPublicKey = BufferUtils.readBytes(buf, buf.readUnsignedShort());

      try {
         this.publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(encodedPublicKey));
      } catch (Exception var4) {
         throw new IllegalStateException("Failed to create public key from bytes.", var4);
      }
   }

   public PublicKey getPublicKey() {
      return this.publicKey;
   }

   public void handle(IRCClient client) throws IOException {
      client.handle(this);
   }
}
