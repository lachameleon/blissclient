package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.feature.content.ContentPack;
import com.dwarslooper.cactus.client.feature.content.ContentPackManager;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.class_2172;
import net.minecraft.class_2561;

public class ContentPackCommand extends Command {
   private static final SuggestionProvider<class_2172> ENABLED_CONTAINERS_SUGGESTION_PROVIDER = (context, builder) -> {
      return class_2172.method_9264(ContentPackManager.get().getContentPacks().values().stream().filter(ContentPack::isEnabled).map(ContentPack::getId), builder);
   };
   private static final SuggestionProvider<class_2172> DISABLED_CONTAINERS_SUGGESTION_PROVIDER = (context, builder) -> {
      return class_2172.method_9264(ContentPackManager.get().getContentPacks().values().stream().filter((contentPack) -> {
         return !contentPack.isEnabled();
      }).map(ContentPack::getId), builder);
   };

   public ContentPackCommand() {
      super("content");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      ((LiteralArgumentBuilder)builder.then(literal("enable").then(argument("pack", StringArgumentType.greedyString()).suggests(DISABLED_CONTAINERS_SUGGESTION_PROVIDER).executes((context) -> {
         String packID = StringArgumentType.getString(context, "pack");
         ContentPack pack = ContentPackManager.get().ofId(packID);
         if (pack == null) {
            ChatUtils.error((class_2561)class_2561.method_43469("commands.content.notExist", new Object[]{packID}));
         } else if (ContentPackManager.get().isEnabled(pack)) {
            ChatUtils.error((class_2561)class_2561.method_43471("commands.content.alreadyEnabled"));
         } else {
            pack.setEnabled(true);
         }

         return 1;
      })))).then(literal("disable").then(argument("pack", StringArgumentType.greedyString()).suggests(ENABLED_CONTAINERS_SUGGESTION_PROVIDER).executes((context) -> {
         String packID = StringArgumentType.getString(context, "pack");
         ContentPack pack = ContentPackManager.get().ofId(packID);
         if (pack == null) {
            ChatUtils.error((class_2561)class_2561.method_43469("commands.content.notExist", new Object[]{packID}));
         } else if (!ContentPackManager.get().isEnabled(pack)) {
            ChatUtils.error((class_2561)class_2561.method_43471("commands.content.alreadyDisabled"));
         } else {
            if (pack.getActivationPolicy() == ContentPack.ActivationPolicy.ALWAYS_ENABLED) {
               ChatUtils.error((class_2561)class_2561.method_43471("commands.content.cantDisable"));
               return 1;
            }

            pack.setEnabled(false);
         }

         return 1;
      })));
   }
}
