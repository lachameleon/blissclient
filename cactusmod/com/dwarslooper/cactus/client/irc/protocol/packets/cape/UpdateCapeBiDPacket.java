package com.dwarslooper.cactus.client.irc.protocol.packets.cape;

import com.dwarslooper.cactus.client.event.CRunnableClickEvent;
import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketIn;
import com.dwarslooper.cactus.client.irc.protocol.PacketOut;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.class_124;
import net.minecraft.class_1664;
import net.minecraft.class_2561;
import net.minecraft.class_437;
import net.minecraft.class_440;

public class UpdateCapeBiDPacket implements PacketOut, PacketIn {
   private String capeId;
   private int action;

   public static void checkCapeOptionsAndWarn() {
      if (!CactusConstants.mc.field_1690.method_32594(class_1664.field_7559)) {
         ChatUtils.error((class_2561)class_2561.method_43469("irc.capesOptionDisabled", new Object[]{class_2561.method_43471("options.skinCustomisation").method_27692(class_124.field_1073).method_27694((style) -> {
            return style.method_10958(new CRunnableClickEvent(() -> {
               CactusConstants.mc.method_1507(new class_440((class_437)null, CactusConstants.mc.field_1690));
            }));
         })}));
      }

   }

   public UpdateCapeBiDPacket(ByteBuf buf) {
      this.action = buf.readInt();
   }

   public UpdateCapeBiDPacket(String capeId) {
      this.capeId = capeId;
      checkCapeOptionsAndWarn();
   }

   public void write(ByteBuf buf) {
      BufferUtils.writeString(buf, this.capeId);
   }

   public void handle(IRCClient client) {
      if (this.action == 0) {
         ChatUtils.info("§aCape was changed.");
      } else if (this.action == 1) {
         ChatUtils.info("§cYou don't own this cape!");
      }

   }
}
