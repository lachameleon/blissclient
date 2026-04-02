package com.dwarslooper.cactus.client.irc.protocol.packets.cape;

import com.dwarslooper.cactus.client.feature.command.arguments.CapeArgumentType;
import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.PacketIn;
import com.dwarslooper.cactus.client.irc.protocol.PacketOut;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class GetAvailableCapesBiDPacket implements PacketOut, PacketIn {
   private static boolean listResult = false;
   private final ArrayList<String> capes = new ArrayList();
   private String selected;

   public GetAvailableCapesBiDPacket(boolean listResult) {
      GetAvailableCapesBiDPacket.listResult = listResult;
   }

   public GetAvailableCapesBiDPacket(ByteBuf buf) {
      String in = BufferUtils.readString(buf);
      this.capes.addAll(Arrays.asList(in.split(",")));
      this.selected = BufferUtils.readString(buf);
   }

   public void write(ByteBuf buf) {
   }

   public void handle(IRCClient client) {
      if (listResult) {
         if (!this.getCapes().isEmpty() && !((String)this.getCapes().getFirst()).isEmpty()) {
            ChatUtils.info("§aYour Capes:");
            Iterator var2 = this.getCapes().iterator();

            while(var2.hasNext()) {
               String capeName = (String)var2.next();
               if (capeName.equals(this.getSelected())) {
                  ChatUtils.info("§8> §6§l" + capeName);
               } else {
                  ChatUtils.info("§6" + capeName);
               }
            }
         } else {
            ChatUtils.error("You don't own any capes!");
         }
      }

      CapeArgumentType.updateAvailableCapes(this.getCapes());
      listResult = false;
   }

   public ArrayList<String> getCapes() {
      return this.capes;
   }

   public String getSelected() {
      return this.selected;
   }
}
