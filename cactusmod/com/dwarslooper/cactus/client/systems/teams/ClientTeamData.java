package com.dwarslooper.cactus.client.systems.teams;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.irc.protocol.BufferUtils;
import com.dwarslooper.cactus.client.irc.protocol.packets.teams.TeamRequestPacket;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.ByteBuf;
import java.io.PrintStream;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.class_1792;
import net.minecraft.class_2960;
import net.minecraft.class_7923;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClientTeamData {
   private final int id;
   private final EnumSet<TeamDataType> awaitingLoadResponse = EnumSet.noneOf(TeamDataType.class);
   @NotNull
   private ClientTeamData.Info info;
   @Nullable
   private List<ClientTeamData.FileData> files;
   @Nullable
   private List<ClientTeamData.ToDoEntry> todos;

   public ClientTeamData(int id, @NotNull ClientTeamData.Info info) {
      this.id = id;
      this.info = info;
   }

   public void load(TeamDataType type) {
      if (this.awaitingLoadResponse.add(type)) {
         CactusClient.getInstance().getIrcClient().sendPacket(new TeamRequestPacket(this.id, type));
      }

   }

   public void parse(TeamDataType type, ByteBuf buf) {
      this.awaitingLoadResponse.remove(type);
      System.out.println("Hello, type " + String.valueOf(type));
      switch(type) {
      case INFO:
         this.info = new ClientTeamData.Info(BufferUtils.readString(buf), (class_1792)class_7923.field_41178.method_63535(class_2960.method_60656(BufferUtils.readString(buf))), BufferUtils.readString(buf), BufferUtils.readString(buf), BufferUtils.readUUID(buf), buf.readInt(), buf.readInt(), buf.readBoolean());
         break;
      case FILES:
         this.files = (List)BufferUtils.readCollection(buf, (b) -> {
            return new ClientTeamData.FileData(BufferUtils.readUUID(b), BufferUtils.readString(b), BufferUtils.readString(b), JsonParser.parseString(BufferUtils.readString(b)).getAsJsonObject(), b.readLong(), BufferUtils.readUUID(b));
         });
      }

      System.out.println(this.info);
      if (this.files != null) {
         System.out.println("with files");
         List var10000 = this.files;
         PrintStream var10001 = System.out;
         Objects.requireNonNull(var10001);
         var10000.forEach(var10001::println);
      } else {
         System.out.println("with no known files");
      }

   }

   public void ensureCached() {
      if (!TeamManager.CACHE.asMap().containsKey(this.id)) {
         TeamManager.CACHE.put(this.id, this);
      }

   }

   public int getId() {
      return this.id;
   }

   @NotNull
   public ClientTeamData.Info getInfo() {
      return this.info;
   }

   @Nullable
   public List<ClientTeamData.FileData> getFiles() {
      return this.files;
   }

   @Nullable
   public List<ClientTeamData.ToDoEntry> getTodos() {
      return this.todos;
   }

   public static record Info(String name, class_1792 display, String motd, @Nullable String about, @Nullable UUID owner, int members, int permissions, boolean pendingInvite) {
      public Info(String name, class_1792 display, String motd, @Nullable String about, @Nullable UUID owner, int members, int permissions, boolean pendingInvite) {
         this.name = name;
         this.display = display;
         this.motd = motd;
         this.about = about;
         this.owner = owner;
         this.members = members;
         this.permissions = permissions;
         this.pendingInvite = pendingInvite;
      }

      public boolean isComplete() {
         return this.display != null && this.about != null && this.owner != null && this.permissions != -1;
      }

      public String name() {
         return this.name;
      }

      public class_1792 display() {
         return this.display;
      }

      public String motd() {
         return this.motd;
      }

      @Nullable
      public String about() {
         return this.about;
      }

      @Nullable
      public UUID owner() {
         return this.owner;
      }

      public int members() {
         return this.members;
      }

      public int permissions() {
         return this.permissions;
      }

      public boolean pendingInvite() {
         return this.pendingInvite;
      }
   }

   public static record FileData(UUID id, String name, String type, JsonObject options, long lastModified, UUID creator) {
      public FileData(UUID id, String name, String type, JsonObject options, long lastModified, UUID creator) {
         this.id = id;
         this.name = name;
         this.type = type;
         this.options = options;
         this.lastModified = lastModified;
         this.creator = creator;
      }

      public boolean hasOption(String key) {
         return this.options.has(key);
      }

      public <T> T getOption(String key, T fallback, Function<JsonElement, T> mapper) {
         return this.hasOption(key) ? mapper.apply(this.options.get(key)) : fallback;
      }

      public UUID id() {
         return this.id;
      }

      public String name() {
         return this.name;
      }

      public String type() {
         return this.type;
      }

      public JsonObject options() {
         return this.options;
      }

      public long lastModified() {
         return this.lastModified;
      }

      public UUID creator() {
         return this.creator;
      }
   }

   public static record ToDoEntry(UUID id, String title, String content, UUID creator) {
      public ToDoEntry(UUID id, String title, String content, UUID creator) {
         this.id = id;
         this.title = title;
         this.content = content;
         this.creator = creator;
      }

      public UUID id() {
         return this.id;
      }

      public String title() {
         return this.title;
      }

      public String content() {
         return this.content;
      }

      public UUID creator() {
         return this.creator;
      }
   }
}
