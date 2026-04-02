package com.dwarslooper.cactus.client.irc.protocol.packets.auth;

import com.dwarslooper.cactus.client.irc.protocol.PacketOut;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class KeyC2SPacket implements PacketOut {
   private final byte[] encryptedSecretKey;

   public KeyC2SPacket(SecretKey secretKey, PublicKey publicKey) {
      try {
         Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
         cipher.init(1, publicKey);
         this.encryptedSecretKey = cipher.doFinal(secretKey.getEncoded());
      } catch (GeneralSecurityException var4) {
         throw new IllegalStateException("Failed to decrypt data.", var4);
      }
   }

   public void write(ByteBuf buf) throws IOException {
      buf.writeShort(this.encryptedSecretKey.length);
      buf.writeBytes(this.encryptedSecretKey);
   }
}
