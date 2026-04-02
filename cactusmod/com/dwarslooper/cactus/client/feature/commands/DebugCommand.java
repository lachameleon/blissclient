package com.dwarslooper.cactus.client.feature.commands;

import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.systems.profile.AbstractCosmetic;
import com.dwarslooper.cactus.client.systems.profile.ProfileHandler;
import com.dwarslooper.cactus.client.systems.teams.TeamManager;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.google.common.cache.Cache;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Objects;
import net.minecraft.class_2172;

public class DebugCommand extends Command {
   public static boolean texMixinDis = false;

   public DebugCommand() {
      super("debug");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      DebugCommand.DebugOptions[] var2 = DebugCommand.DebugOptions.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         DebugCommand.DebugOptions value = var2[var4];
         builder.then(literal(value.name()).executes((context) -> {
            ChatUtils.warningPrefix("Debugger", this.getTranslatableElement("start", new Object[]{value.name()}));
            value.action.run();
            return 1;
         }));
      }

   }

   public static enum DebugOptions {
      CAPE_MANAGER(AbstractCosmetic::invalidate),
      TITLE_SCREEN_CONTENT(() -> {
         ChatUtils.info("Reload TitleScreen");
      }),
      SKIN_TEX(() -> {
         DebugCommand.texMixinDis = !DebugCommand.texMixinDis;
         ChatUtils.info("Tex Mixin is now " + (DebugCommand.texMixinDis ? "§cdisabled" : "§aenabled"));
      }),
      PROFILE_CACHE(ProfileHandler::invalidateAll),
      TEAMS_DATA;

      final Runnable action;

      private DebugOptions(Runnable action) {
         this.action = action;
      }

      // $FF: synthetic method
      private static DebugCommand.DebugOptions[] $values() {
         return new DebugCommand.DebugOptions[]{CAPE_MANAGER, TITLE_SCREEN_CONTENT, SKIN_TEX, PROFILE_CACHE, TEAMS_DATA};
      }

      static {
         Cache var10004 = TeamManager.CACHE;
         Objects.requireNonNull(var10004);
         TEAMS_DATA = new DebugCommand.DebugOptions("TEAMS_DATA", 4, var10004::invalidateAll);
      }
   }
}
