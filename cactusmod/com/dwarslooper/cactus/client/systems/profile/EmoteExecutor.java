package com.dwarslooper.cactus.client.systems.profile;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.irc.protocol.packets.cosmetic.UseEmoteC2SPacket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class EmoteExecutor {
   private EmoteExecutor() {
   }

   public static List<AbstractCosmetic.Emote> getAvailableEmotes() {
      List<AbstractCosmetic.Emote> emotes = new ArrayList();
      Iterator var1 = ProfileHandler.getClientAvailableCosmetics(false).iterator();

      while(var1.hasNext()) {
         AbstractCosmetic<?> cosmetic = (AbstractCosmetic)var1.next();
         if (cosmetic instanceof AbstractCosmetic.Emote) {
            AbstractCosmetic.Emote emote = (AbstractCosmetic.Emote)cosmetic;
            emotes.add(emote);
         }
      }

      return emotes;
   }

   public static void useEmote(AbstractCosmetic.Emote emote) {
      if (emote != null) {
         useEmote(emote.getId());
      }

   }

   public static void useEmote(String emoteId) {
      if (emoteId != null && !emoteId.isBlank()) {
         CactusClient.getInstance().getIrcClient().sendPacket(new UseEmoteC2SPacket(emoteId));
      }
   }
}
