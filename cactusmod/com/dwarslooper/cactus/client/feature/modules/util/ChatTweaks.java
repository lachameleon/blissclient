package com.dwarslooper.cactus.client.feature.modules.util;

import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.MessageReceiveEvent;
import com.dwarslooper.cactus.client.event.impl.SendChatCommandEvent;
import com.dwarslooper.cactus.client.event.impl.SendChatMessageEvent;
import com.dwarslooper.cactus.client.event.impl.WorldLeftEvent;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.mixins.accessor.ChatComponentAccessor;
import com.dwarslooper.cactus.client.systems.config.settings.group.SettingGroup;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.StringListSetting;
import com.dwarslooper.cactus.client.systems.ias.skins.SkinHelper;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.UUIDCache;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.class_1109;
import net.minecraft.class_11909;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_303;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_634;
import net.minecraft.class_6382;
import net.minecraft.class_640;
import net.minecraft.class_7532;
import net.minecraft.class_8016;
import net.minecraft.class_8023;
import net.minecraft.class_8685;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChatTweaks extends Module {
   private static final Pattern antiSpamRegex = Pattern.compile(" \\(([0-9]+)\\)$");
   private final List<String> playersDirectMessaged = new ArrayList();
   private String playerCurrentlyMessaging = null;
   private String lastCommandUsed = null;
   public Setting<Boolean> enableEmojis;
   public Setting<Boolean> enableTagging;
   public Setting<Boolean> namePing;
   public Setting<List<String>> customPingKeywords;
   public Setting<Boolean> antiSpam;
   public Setting<Boolean> saveChatWhenClosed;
   private final SettingGroup msgChat;
   public Setting<Boolean> autoMsgChat;
   public Setting<List<String>> directMessageCommands;
   public Setting<Boolean> showMessagedInChatScreen;

   public ChatTweaks() {
      super("chatTweaks", ModuleManager.CATEGORY_UTILITY, (new Module.Options()).set(Module.Flag.HUD_LISTED, false));
      this.enableEmojis = this.mainGroup.add(new BooleanSetting("chatEmojis", true));
      this.enableTagging = this.mainGroup.add(new BooleanSetting("chatTagging", true));
      this.namePing = this.mainGroup.add(new BooleanSetting("namePing", false));
      this.customPingKeywords = this.mainGroup.add(new StringListSetting("pingKeywords", new String[0]));
      this.antiSpam = this.mainGroup.add(new BooleanSetting("antiSpam", false));
      this.saveChatWhenClosed = this.mainGroup.add(new BooleanSetting("saveChat", false));
      this.msgChat = this.settings.buildGroup("msgChat");
      this.autoMsgChat = this.msgChat.add(new BooleanSetting("autoMsgChat", false));
      this.directMessageCommands = this.msgChat.add((new StringListSetting("msgCommands", new String[]{"msg", "w", "tell"})).visibleIf(() -> {
         return (Boolean)this.autoMsgChat.get();
      }));
      this.showMessagedInChatScreen = this.msgChat.add(new BooleanSetting("showMessagedInChatScreen", true));
   }

   public void removePlayerFromMessaged(String name) {
      this.playersDirectMessaged.remove(name);
      if (name.equals(this.playerCurrentlyMessaging)) {
         this.selectPlayerToMessage((String)null);
      }

   }

   public void selectPlayerToMessage(@Nullable String name) {
      this.playerCurrentlyMessaging = name;
      if (name != null && !this.playersDirectMessaged.contains(name)) {
         this.playersDirectMessaged.add(name);
      }

   }

   public List<String> getPlayersDirectMessaged() {
      return this.playersDirectMessaged;
   }

   public String getPlayerCurrentlyMessaging() {
      return this.playerCurrentlyMessaging;
   }

   @EventHandler
   public void commandSend(SendChatCommandEvent event) {
      if ((Boolean)this.autoMsgChat.get()) {
         String[] parts = event.command().split(" ");
         if (parts.length > 1 && ((List)this.directMessageCommands.get()).contains(parts[0])) {
            String name = parts[1];
            if (name.matches("^([a-zA-Z0-9_]{3,16})$")) {
               this.lastCommandUsed = parts[0];
               this.selectPlayerToMessage(name);
            }
         }

      }
   }

   @EventHandler
   public void onQuit(WorldLeftEvent event) {
      if (event.getReason() == WorldLeftEvent.Reason.DISCONNECT) {
         this.playersDirectMessaged.clear();
         this.selectPlayerToMessage((String)null);
      }

   }

   @EventHandler
   public void onMessageSend(SendChatMessageEvent event) {
      if (this.lastCommandUsed != null && this.playerCurrentlyMessaging != null) {
         class_634 var10000 = CactusConstants.mc.method_1562();
         String var10001 = this.lastCommandUsed;
         var10000.method_45730(var10001 + " " + this.playerCurrentlyMessaging + " " + event.getContent());
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void messageReceive(MessageReceiveEvent event) {
      label53: {
         String raw = event.message.getString();
         if (!(Boolean)this.namePing.get() || !raw.contains(CactusConstants.mc.method_1548().method_1676())) {
            Stream var10000 = ((List)this.customPingKeywords.get()).stream();
            Objects.requireNonNull(raw);
            if (!var10000.anyMatch(raw::contains)) {
               break label53;
            }
         }

         CactusConstants.mc.method_1483().method_4873(class_1109.method_4757((class_3414)class_3417.field_14793.comp_349(), 1.0F, 100.0F));
      }

      if ((Boolean)this.antiSpam.get()) {
         try {
            List<class_303> messages = ((ChatComponentAccessor)CactusConstants.mc.field_1705.method_1743()).getAllMessages();
            String messageString = event.message.getString();
            int index = -1;
            Iterator var6 = messages.iterator();

            while(var6.hasNext()) {
               class_303 message = (class_303)var6.next();
               String string = message.comp_893().getString();
               if (string.equals(messageString)) {
                  event.message = this.appendCount(event.message, 2);
                  index = messages.indexOf(message);
                  break;
               }

               Matcher matcher = antiSpamRegex.matcher(string);
               if (matcher.find()) {
                  String group = matcher.group(matcher.groupCount());
                  int number = Integer.parseInt(group);
                  if (string.substring(0, matcher.start()).equals(messageString)) {
                     index = messages.indexOf(message);
                     event.message = this.appendCount(event.message, number + 1);
                     break;
                  }
               }
            }

            if (index != -1) {
               messages.remove(index);
            }
         } catch (Exception var12) {
            var12.printStackTrace();
         }

      }
   }

   private class_2561 appendCount(class_2561 text, int count) {
      return text.method_27661().method_10852(class_2561.method_43470(" (%s)".formatted(new Object[]{count})).method_27692(class_124.field_1080));
   }

   public static class DirectMessagesWidget extends class_339 {
      private final ChatTweaks backing;
      private int maxWidth;
      private static final int listTopYOffset = 26;
      private static final class_2561 UNSELECT_TEXT;
      private static final class_2561 TITLE_TEXT;

      public DirectMessagesWidget(ChatTweaks backing, int x, int y, int width, int height) {
         super(x, y, width, height, class_2561.method_43473());
         this.backing = backing;
      }

      private int getLeft() {
         return this.method_46426() + this.method_25368() - Math.max(this.maxWidth + 2 + 8, Math.max(CactusConstants.mc.field_1772.method_27525(UNSELECT_TEXT), CactusConstants.mc.field_1772.method_27525(TITLE_TEXT)));
      }

      protected void method_48579(@NotNull class_332 context, int mouseX, int mouseY, float deltaTicks) {
         List<String> messaged = this.backing.getPlayersDirectMessaged();
         if (!messaged.isEmpty()) {
            Stream var10001 = messaged.stream();
            class_327 var10002 = CactusConstants.mc.field_1772;
            Objects.requireNonNull(var10002);
            this.maxWidth = var10001.mapToInt(var10002::method_1727).max().orElse(0);
            int y = this.method_46427() + 26;
            int x = this.getLeft();
            context.method_27535(CactusConstants.mc.field_1772, TITLE_TEXT, x, this.method_46427(), -1);
            context.method_27535(CactusConstants.mc.field_1772, UNSELECT_TEXT, x, this.method_46427() + 16, -1);

            for(Iterator var8 = messaged.iterator(); var8.hasNext(); y += 10) {
               String name = (String)var8.next();
               class_640 listEntry = CactusConstants.mc.method_1562().method_2874(name);
               class_8685 textures;
               if (listEntry == null) {
                  UUID uuid = UUIDCache.getOrResolve(name);
                  textures = SkinHelper.getCachedSkinOrFetch(uuid);
               } else {
                  textures = listEntry.method_52810();
               }

               class_7532.method_52722(context, textures, x, y, 8);
               context.method_25303(CactusConstants.mc.field_1772, name, x + 8 + 2, y, name.equals(this.backing.getPlayerCurrentlyMessaging()) ? -11141291 : -1);
            }

         }
      }

      public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
         if (this.field_22763 && this.field_22764 && click.method_74245() == 0 && click.comp_4798() >= (double)this.getLeft()) {
            int index = (int)Math.floor((click.comp_4799() - (double)this.method_46427() - 26.0D) / 10.0D);
            if (index >= 0 && index < this.backing.getPlayersDirectMessaged().size()) {
               String name = (String)this.backing.getPlayersDirectMessaged().get(index);
               this.backing.selectPlayerToMessage(name);
               this.method_25354(CactusConstants.mc.method_1483());
               return true;
            }

            if (click.comp_4799() > (double)(this.method_46427() + 16)) {
               double var10000 = click.comp_4799();
               int var10001 = this.method_46427() + 16;
               Objects.requireNonNull(CactusConstants.mc.field_1772);
               if (var10000 <= (double)(var10001 + 9)) {
                  this.backing.selectPlayerToMessage((String)null);
                  this.method_25354(CactusConstants.mc.method_1483());
                  return true;
               }
            }
         }

         return false;
      }

      protected void method_47399(@NotNull class_6382 builder) {
      }

      @Nullable
      public class_8016 method_48205(@NotNull class_8023 navigation) {
         return null;
      }

      static {
         UNSELECT_TEXT = class_2561.method_43470("Unselect").method_27692(class_124.field_1061);
         TITLE_TEXT = class_2561.method_43470("Direct Messages").method_27692(class_124.field_1073);
      }
   }
}
