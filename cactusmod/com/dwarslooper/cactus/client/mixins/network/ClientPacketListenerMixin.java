package com.dwarslooper.cactus.client.mixins.network;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.compat.server.ServerCompat;
import com.dwarslooper.cactus.client.compat.server.specific.ServerSpecific;
import com.dwarslooper.cactus.client.event.impl.SendChatCommandEvent;
import com.dwarslooper.cactus.client.event.impl.SendChatMessageEvent;
import com.dwarslooper.cactus.client.event.impl.WorldJoinedEvent;
import com.dwarslooper.cactus.client.event.impl.WorldLeftEvent;
import com.dwarslooper.cactus.client.feature.command.CommandManager;
import com.dwarslooper.cactus.client.feature.commands.ChatCommand;
import com.dwarslooper.cactus.client.feature.commands.IRCCommand;
import com.dwarslooper.cactus.client.feature.content.ContentPackManager;
import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.irc.IRCStatus;
import com.dwarslooper.cactus.client.irc.protocol.packets.security.ServerCheckBiDPacket;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.systems.emoji.EmojiManager;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.class_2535;
import net.minecraft.class_2678;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_642;
import net.minecraft.class_7439;
import net.minecraft.class_8588;
import net.minecraft.class_8673;
import net.minecraft.class_8675;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_634.class})
public abstract class ClientPacketListenerMixin extends class_8673 {
   @Unique
   private boolean skipChatSendIntercept;

   protected ClientPacketListenerMixin(class_310 client, class_2535 connection, class_8675 connectionState) {
      super(client, connection, connectionState);
   }

   @Shadow
   public abstract void method_45729(String var1);

   @Inject(
      method = {"method_45729"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onSendChatMessage(String content, CallbackInfo ci) {
      if (!this.skipChatSendIntercept) {
         if (content.startsWith(CommandManager.getIRCPrefix()) && !CommandManager.getIRCPrefix().isEmpty()) {
            if (IRCCommand.checkIfConnectedElseError()) {
               String message = content.substring(CommandManager.getIRCPrefix().length());
               if (!message.isEmpty()) {
                  CactusClient.getInstance().getIrcClient().sendGlobalChat(message);
               }
            } else if (ChatCommand.MODE == ChatCommand.ChatMode.IRC) {
               ChatCommand.MODE = ChatCommand.ChatMode.DEFAULT;
               ChatUtils.infoPrefix("CNet", "Chat changed to §aGlobal§7.");
            }
         } else if (content.startsWith(String.valueOf(CommandManager.getCommandPrefix()))) {
            try {
               CommandManager.get().execute(content.substring(1));
            } catch (CommandSyntaxException var4) {
               ChatUtils.error(var4.getMessage());
            }
         } else if (ChatCommand.MODE == ChatCommand.ChatMode.IRC) {
            if (IRCCommand.checkIfConnectedElseError()) {
               CactusClient.getInstance().getIrcClient().sendGlobalChat(content);
            } else if (ChatCommand.MODE == ChatCommand.ChatMode.IRC) {
               ChatCommand.MODE = ChatCommand.ChatMode.DEFAULT;
               ChatUtils.infoPrefix("CNet", "Chat changed to §aGlobal§7.");
            }
         } else {
            SendChatMessageEvent event = (SendChatMessageEvent)CactusClient.EVENT_BUS.post(new SendChatMessageEvent(content));
            if (!event.isCancelled()) {
               this.skipped(() -> {
                  this.method_45729(event.getContent());
               });
            }
         }

         ci.cancel();
      }
   }

   @Unique
   private void skipped(Runnable runnable) {
      this.skipChatSendIntercept = true;
      runnable.run();
      this.skipChatSendIntercept = false;
   }

   @Inject(
      method = {"method_45730"},
      at = {@At("HEAD")}
   )
   public void onChatCommand(String command, CallbackInfo ci) {
      CactusClient.EVENT_BUS.post(new SendChatCommandEvent(command));
   }

   @Inject(
      method = {"method_43596"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onChatMessage(class_7439 packet, CallbackInfo ci) {
      if (ServerSpecific.currentHandler.handleChatMessage(packet.comp_763())) {
         ci.cancel();
      }

   }

   @Inject(
      method = {"method_11120"},
      at = {@At("HEAD")}
   )
   public void disableSecureChatWarning(class_2678 packet, CallbackInfo ci) {
      if ((Boolean)CactusSettings.get().antiSecureChatWarning.get()) {
         this.field_62020 = true;
      }

   }

   @Inject(
      method = {"method_11120"},
      at = {@At("TAIL")}
   )
   public void injectServerCheck(class_2678 packet, CallbackInfo ci) {
      CactusClient.EVENT_BUS.post(WorldJoinedEvent.INSTANCE);
      EmojiManager.reloadEmojis();
      class_642 server = CactusConstants.mc.method_1558();
      if (ContentPackManager.get().isEnabled(ContentPackManager.get().ofId("server_security"))) {
         IRCClient irc = CactusClient.getInstance().getIrcClient();
         if (irc.getStatus() == IRCStatus.CONNECTED && server != null) {
            irc.sendPacket(new ServerCheckBiDPacket(server.field_3761));
         }
      }

      if (server != null) {
         ServerCompat.joined(server.field_3761);
      }

   }

   @Inject(
      method = {"method_52798"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_7594;method_44945()V"
)}
   )
   public void handleReconfiguration(class_8588 packet, CallbackInfo ci) {
      CactusClient.EVENT_BUS.post(WorldLeftEvent.RECONFIGURING);
   }
}
