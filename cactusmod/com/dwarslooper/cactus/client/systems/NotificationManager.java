package com.dwarslooper.cactus.client.systems;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.systems.config.ConfigHandler;
import com.dwarslooper.cactus.client.systems.config.FileConfiguration;
import com.dwarslooper.cactus.client.systems.config.InternalOnly;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

@InternalOnly
public class NotificationManager extends FileConfiguration<NotificationManager> {
   public boolean muted;
   public List<NotificationManager.NotificationMessage> messages = new ArrayList();

   public static NotificationManager get() {
      return (NotificationManager)CactusClient.getConfig(NotificationManager.class);
   }

   public NotificationManager(ConfigHandler handler) {
      super("messages", handler);
   }

   public JsonObject toJson(TreeSerializerFilter filter) {
      JsonObject object = new JsonObject();
      object.addProperty("muted", this.muted);
      JsonArray array = new JsonArray();
      this.messages.forEach((msg) -> {
         array.add(msg.toJson(TreeSerializerFilter.ALL));
      });
      object.add("messages", array);
      return object;
   }

   public NotificationManager fromJson(JsonObject object) {
      this.muted = object.get("muted").getAsBoolean();
      this.messages.clear();
      object.getAsJsonArray("messages").forEach((jsonElement) -> {
         this.messages.add(new NotificationManager.NotificationMessage(jsonElement.getAsJsonObject()));
      });
      return this;
   }

   public static boolean hasUnread() {
      return getUnread() > 0L;
   }

   public static long getUnread() {
      return get().messages.stream().filter((msg) -> {
         return !msg.read;
      }).count();
   }

   public static void add(NotificationManager.NotificationMessage message) {
      get().messages.add(message);
   }

   public static class NotificationMessage implements ISerializable<NotificationManager.NotificationMessage> {
      private String title;
      private String content;
      private String author;
      private long timestamp;
      private boolean read;

      public NotificationMessage(String title, String content, String author, long timestamp, boolean read) {
         this.title = title;
         this.content = content;
         this.author = author;
         this.timestamp = timestamp;
         this.read = read;
      }

      public NotificationMessage(JsonObject object) {
         this("", "", "", 0L, false);
         this.fromJson(object);
      }

      public String getTitle() {
         return this.title;
      }

      public String getContent() {
         return this.content;
      }

      public String getAuthor() {
         return this.author;
      }

      public long getTimestamp() {
         return this.timestamp;
      }

      public boolean isRead() {
         return this.read;
      }

      public void setRead(boolean read) {
         this.read = read;
      }

      public JsonObject toJson(TreeSerializerFilter filter) {
         JsonObject object = new JsonObject();
         object.addProperty("title", this.title);
         object.addProperty("content", this.content);
         object.addProperty("author", this.author);
         object.addProperty("timestamp", this.timestamp);
         object.addProperty("read", this.read);
         return object;
      }

      public NotificationManager.NotificationMessage fromJson(JsonObject object) {
         this.title = object.get("title").getAsString();
         this.content = object.get("content").getAsString();
         this.author = object.get("author").getAsString();
         this.timestamp = object.get("timestamp").getAsLong();
         this.read = object.get("read").getAsBoolean();
         return this;
      }
   }
}
