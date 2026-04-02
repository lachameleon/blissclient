package com.dwarslooper.cactus.client.compat.server.specific.impl.tjc;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.compat.server.ServerPresenceOverride;
import com.dwarslooper.cactus.client.compat.server.specific.IServerHandler;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.util.DiscordPresence;
import com.dwarslooper.cactus.client.systems.discord.component.impl.ImageAsset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_2561;

public class TjcHandler implements IServerHandler {
   public static final List<TjcHandler.TjcFriend> friends = new ArrayList();
   public static int currentCoins = -1;

   public void handleFriendMessage(List<class_2561> components) {
      if (components.size() == 5 && ((class_2561)components.get(2)).getString().matches(".*Du hast aktuell \\d* Freunde.*")) {
         List<class_2561> friendElements = ((class_2561)components.get(4)).method_10855();
         friends.clear();
         if (friendElements.isEmpty()) {
            CactusClient.getLogger().info("No friends? :(");
         } else {
            Iterator var3 = friendElements.iterator();

            while(var3.hasNext()) {
               class_2561 sib = (class_2561)var3.next();
               if (sib.method_10866().method_10973() != null) {
                  CactusClient.getLogger().info("{} > {}", sib.method_10866().method_10973().method_27721().equals("green"), sib.getString());
                  friends.add(new TjcHandler.TjcFriend(sib.getString(), sib.method_10866().method_10973().method_27721().equals("green"), "TBD"));
               }
            }
         }
      }

   }

   public void handleScoreBoardUpdate(String[] lines) {
   }

   public boolean handleChatMessage(class_2561 content) {
      List<class_2561> siblings = content.method_10855();
      if (!siblings.isEmpty() && ((class_2561)siblings.getFirst()).getString().matches(".*Freunde.*")) {
         this.handleFriendMessage(siblings);
      }

      return false;
   }

   public void handleActive() {
      ServerPresenceOverride override = new ServerPresenceOverride("Playing on TheJoCraft Network", (String)null, new ImageAsset("https://cdn.discordapp.com/icons/251008837983797259/8fe40f8fe81e5651a1edeaadb52b6dd3.png", "thejocraft.net"));
      ((DiscordPresence)ModuleManager.get().get(DiscordPresence.class)).getOverrides().put("server:custom->tjc", override);
   }

   public void handleInactive() {
      ((DiscordPresence)ModuleManager.get().get(DiscordPresence.class)).getOverrides().remove("server:custom->tjc");
   }

   public static record TjcFriend(String name, boolean online, String lastSeen) {
      public TjcFriend(String name, boolean online, String lastSeen) {
         this.name = name;
         this.online = online;
         this.lastSeen = lastSeen;
      }

      public String name() {
         return this.name;
      }

      public boolean online() {
         return this.online;
      }

      public String lastSeen() {
         return this.lastSeen;
      }
   }
}
