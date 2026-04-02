package com.dwarslooper.cactus.client.systems.discord;

import com.dwarslooper.cactus.client.systems.discord.component.DiscordActivity;
import com.dwarslooper.cactus.client.systems.discord.connection.DiscordSocketConnection;
import com.dwarslooper.cactus.client.systems.discord.connection.impl.UnixDiscordConnection;
import com.dwarslooper.cactus.client.systems.discord.connection.impl.WinDiscordConnection;
import com.google.gson.JsonObject;
import java.util.UUID;
import net.minecraft.class_156;
import net.minecraft.class_156.class_158;

public class DiscordPresenceHandler {
   public static DiscordPresenceHandler INSTANCE = new DiscordPresenceHandler(1136243409707876353L);
   private final long appId;
   private DiscordSocketConnection connection;

   public DiscordPresenceHandler(long appId) {
      this.appId = appId;
   }

   public void start() {
      if (!this.isConnectionOpen()) {
         if (class_156.method_668() == class_158.field_1133) {
            this.connection = new WinDiscordConnection();
         } else {
            this.connection = new UnixDiscordConnection();
         }

         if (this.connection.openSocket()) {
            this.connection.handshake(this.appId);
         }

      }
   }

   public void stop() {
      if (this.connection != null) {
         this.connection.closeSocket();
      }

   }

   public boolean isConnectionOpen() {
      return this.connection != null && this.connection.isOpen();
   }

   public void updateActivity(DiscordActivity activity) {
      JsonObject commandArgs = new JsonObject();
      commandArgs.addProperty("pid", ProcessHandle.current().pid());
      commandArgs.add("activity", activity.compile());
      JsonObject command = new JsonObject();
      command.addProperty("nonce", UUID.randomUUID().toString());
      command.addProperty("cmd", "SET_ACTIVITY");
      command.add("args", commandArgs);
      this.connection.sendBuffer(1, command.toString());
   }
}
