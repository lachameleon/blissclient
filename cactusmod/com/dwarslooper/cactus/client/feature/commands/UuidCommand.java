package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.systems.ias.account.Auth;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_2172;
import net.minecraft.class_2561;
import net.minecraft.class_2558.class_10606;

public class UuidCommand extends Command {
   private static final SuggestionProvider<class_2172> ONLINE_PLAYERS_SUGGESTION_PROVIDER = (context, builder) -> {
      return class_2172.method_9264(CactusConstants.mc.method_1562().method_2880().stream().map((p) -> {
         return p.method_2966().name();
      }), builder);
   };

   public UuidCommand() {
      super("uuid");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(argument("username", StringArgumentType.string()).suggests(ONLINE_PLAYERS_SUGGESTION_PROVIDER).executes((context) -> {
         String username = StringArgumentType.getString(context, "username");
         CompletableFuture.supplyAsync(() -> {
            return Auth.resolveUUID(username);
         }).thenAccept((uuid) -> {
            if (uuid != null) {
               ChatUtils.info((class_2561)class_2561.method_43469("commands.uuid.success", new Object[]{username, uuid.toString()}).method_27694((style) -> {
                  return style.method_10958(new class_10606(uuid.toString()));
               }));
            } else {
               ChatUtils.error((class_2561)class_2561.method_43469("commands.uuid.fail", new Object[]{username}));
            }

         });
         return 1;
      }));
   }
}
