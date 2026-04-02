package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.util.ChatTweaks;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.class_2172;

public class MsgChatCommand extends Command {
   private final SuggestionProvider<class_2172> PAST_CHATS = (context, builder) -> {
      return class_2172.method_9265(this.module().getPlayersDirectMessaged(), builder);
   };

   public MsgChatCommand() {
      super("msgchat");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      ((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder.then(literal("exit").executes((context) -> {
         this.module().selectPlayerToMessage((String)null);
         return 1;
      }))).then(literal("clear").executes((context) -> {
         ChatTweaks module = this.module();
         module.getPlayersDirectMessaged().clear();
         module.selectPlayerToMessage((String)null);
         return 1;
      }))).then(literal("select").then(argument("name", StringArgumentType.string()).suggests(this.PAST_CHATS).executes((context) -> {
         String name = StringArgumentType.getString(context, "name");
         this.module().selectPlayerToMessage(name);
         return 1;
      })));
   }

   private ChatTweaks module() {
      return (ChatTweaks)ModuleManager.get().get(ChatTweaks.class);
   }
}
