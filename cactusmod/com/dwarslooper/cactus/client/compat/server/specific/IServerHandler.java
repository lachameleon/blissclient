package com.dwarslooper.cactus.client.compat.server.specific;

import net.minecraft.class_2561;

public interface IServerHandler {
   IServerHandler NONE = new IServerHandler() {
      public void handleScoreBoardUpdate(String[] lines) {
      }

      public boolean handleChatMessage(class_2561 content) {
         return false;
      }
   };

   void handleScoreBoardUpdate(String[] var1);

   boolean handleChatMessage(class_2561 var1);

   default void handleActive() {
   }

   default void handleInactive() {
   }
}
