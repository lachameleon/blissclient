package com.dwarslooper.cactus.client.feature.modules.util;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.compat.server.ServerPresenceOverride;
import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.ClientTickEvent;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.gui.hud.AddHudElementScreen;
import com.dwarslooper.cactus.client.gui.hud.ElementSettingsScreen;
import com.dwarslooper.cactus.client.gui.hud.HudEditorScreen;
import com.dwarslooper.cactus.client.gui.hud.HudSettingsScreen;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.screen.impl.MacroEditScreen;
import com.dwarslooper.cactus.client.gui.screen.impl.MacroListScreen;
import com.dwarslooper.cactus.client.gui.screen.impl.ModuleListScreen;
import com.dwarslooper.cactus.client.gui.screen.impl.ModuleOptionsScreen;
import com.dwarslooper.cactus.client.gui.screen.impl.ScreenshotListScreen;
import com.dwarslooper.cactus.client.gui.screen.window.WindowScreen;
import com.dwarslooper.cactus.client.systems.config.settings.group.SettingGroup;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.StringSetting;
import com.dwarslooper.cactus.client.systems.discord.DiscordPresenceHandler;
import com.dwarslooper.cactus.client.systems.discord.DiscordWriteException;
import com.dwarslooper.cactus.client.systems.discord.component.DiscordActivity;
import com.dwarslooper.cactus.client.systems.discord.component.impl.ImageAsset;
import com.dwarslooper.cactus.client.systems.params.PlaceholderHandler;
import com.dwarslooper.cactus.client.systems.worldshare.OasisClient;
import com.dwarslooper.cactus.client.systems.worldshare.OasisHostManager;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.CactusRemoteMeta;
import com.dwarslooper.cactus.client.util.Utils;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.class_155;
import net.minecraft.class_420;
import net.minecraft.class_422;
import net.minecraft.class_429;
import net.minecraft.class_433;
import net.minecraft.class_437;
import net.minecraft.class_442;
import net.minecraft.class_4667;
import net.minecraft.class_500;
import net.minecraft.class_524;
import net.minecraft.class_525;
import net.minecraft.class_526;
import net.minecraft.class_642;
import net.minecraft.class_2926.class_2927;

public class DiscordPresence extends Module {
   private static final Map<Collection<Class<? extends class_437>>, String> SCREEN_STATES = new LinkedHashMap();
   private final SettingGroup sgCustom;
   public Setting<Boolean> showServerIp;
   public Setting<Boolean> showPlayerCount;
   public Setting<Boolean> showPlayerName;
   public Setting<Boolean> allowServerModify;
   public Setting<String> lineDetails;
   public Setting<String> lineState;
   private final Map<String, ServerPresenceOverride> overrides;
   private DiscordActivity activity;
   private long tickDelay;
   private long smallIconSwitchDelay;

   public DiscordPresence() {
      super("discord_rpc", ModuleManager.CATEGORY_UTILITY, (new Module.Options()).set(Module.Flag.HUD_LISTED, false).set(Module.Flag.RUN_IN_MENU, true));
      this.sgCustom = this.settings.buildGroup("custom");
      this.showServerIp = this.mainGroup.add(new BooleanSetting("showServerIp", false));
      this.showPlayerCount = this.mainGroup.add(new BooleanSetting("showPlayerCount", true));
      this.showPlayerName = this.mainGroup.add(new BooleanSetting("showName", true));
      this.allowServerModify = this.mainGroup.add(new BooleanSetting("allowServerModifications", true));
      this.lineDetails = this.sgCustom.add(new StringSetting("details", ""));
      this.lineState = this.sgCustom.add(new StringSetting("state", ""));
      this.overrides = new HashMap();
      this.tickDelay = 0L;
      this.smallIconSwitchDelay = 0L;
   }

   public void onEnable() {
      DiscordPresenceHandler.INSTANCE.start();
      this.activity = new DiscordActivity();
      this.activity.largeImage = new ImageAsset("cactus", "Cactus Mod v" + String.valueOf(CactusConstants.META.getVersion()));
      this.activity.startTimestamp = System.currentTimeMillis() / 1000L;
      this.update();
   }

   public void onDisable() {
      DiscordPresenceHandler.INSTANCE.stop();
   }

   @EventHandler
   public void onTick(ClientTickEvent event) {
      if (this.tickDelay >= 100L) {
         this.update();
         this.tickDelay = 0L;
      }

      ++this.tickDelay;
   }

   private void update() {
      if (DiscordPresenceHandler.INSTANCE.isConnectionOpen()) {
         this.activity.details = "Playing Minecraft " + class_155.method_16673().comp_4025();
         String state = getState((Boolean)this.showPlayerCount.get(), (Boolean)this.showServerIp.get());
         if (!((String)this.lineState.get()).isEmpty()) {
            state = PlaceholderHandler.get().replacePlaceholders((String)this.lineState.get());
         }

         if (!((String)this.lineDetails.get()).isEmpty()) {
            this.activity.details = PlaceholderHandler.get().replacePlaceholders((String)this.lineDetails.get());
         }

         ++this.smallIconSwitchDelay;
         if (this.smallIconSwitchDelay >= 8L) {
            this.smallIconSwitchDelay = 0L;
         }

         if (this.smallIconSwitchDelay < 4L && (Boolean)this.showPlayerName.get()) {
            String playerName = CactusConstants.mc.method_1548().method_1676();
            this.activity.smallImage = new ImageAsset(CactusRemoteMeta.getPlayerHeadApiUrl(playerName), playerName);
         } else if (this.smallIconSwitchDelay < 8L) {
            this.activity.smallImage = new ImageAsset("minecraft", "Minecraft " + class_155.method_16673().comp_4025());
         }

         this.activity.state = state;
         this.overrides.values().forEach((o) -> {
            o.applyTo(this.activity);
         });
         if (this.activity.isComplete()) {
            try {
               DiscordPresenceHandler.INSTANCE.updateActivity(this.activity);
            } catch (DiscordWriteException var3) {
               DiscordPresenceHandler.INSTANCE.stop();
            }
         }

      }
   }

   public Map<String, ServerPresenceOverride> getOverrides() {
      return this.overrides;
   }

   public static String getState(boolean showPlayerCount, boolean showServerIp) {
      String state = null;
      Iterator var3 = SCREEN_STATES.entrySet().iterator();

      while(var3.hasNext()) {
         Entry<Collection<Class<? extends class_437>>, String> entry = (Entry)var3.next();
         if (((Collection)entry.getKey()).stream().anyMatch((clazz) -> {
            boolean var10000;
            if (!clazz.isInstance(CactusConstants.mc.field_1755)) {
               label26: {
                  class_437 patt0$temp = CactusConstants.mc.field_1755;
                  if (patt0$temp instanceof WindowScreen) {
                     WindowScreen ws = (WindowScreen)patt0$temp;
                     if (clazz.isInstance(ws.parent)) {
                        break label26;
                     }
                  }

                  var10000 = false;
                  return var10000;
               }
            }

            var10000 = true;
            return var10000;
         })) {
            state = (String)entry.getValue();
            break;
         }
      }

      if (state == null) {
         if (Utils.isInWorld()) {
            class_642 serverEntry = CactusConstants.mc.method_1558();
            OasisClient share = ((OasisHostManager)CactusClient.getConfig(OasisHostManager.class)).getClientInstance();
            if (CactusConstants.mc.method_1542()) {
               if (share != null) {
                  String var10000;
                  if (showPlayerCount) {
                     Object[] var10001 = new Object[]{share.childChannels.size() + 1, null};
                     Objects.requireNonNull(share);
                     var10001[1] = 8;
                     var10000 = " (%s/%s)".formatted(var10001);
                  } else {
                     var10000 = "";
                  }

                  state = "Hosting a World" + var10000;
               } else {
                  state = "Playing Singleplayer";
               }
            } else if (serverEntry != null && CactusConstants.mc.method_1562() != null) {
               class_2927 players = serverEntry.field_41861;
               String serverDescriptor = showServerIp ? serverEntry.field_3761 : "a server";
               state = "Playing on " + serverDescriptor + (showPlayerCount ? " (%s/%s)".formatted(new Object[]{CactusConstants.mc.method_1562().method_2880().size(), players != null ? players.comp_1279() : "?"}) : "");
            } else {
               state = "Ingame";
            }
         } else {
            state = "Cactus " + String.valueOf(CactusConstants.META.getVersion());
         }
      }

      return state;
   }

   @SafeVarargs
   private static void addScreenState(String name, Class<? extends class_437>... screens) {
      SCREEN_STATES.put(Arrays.asList(screens), name);
   }

   static {
      addScreenState("In main menu", class_442.class);
      addScreenState("In game menu", class_433.class);
      addScreenState("Browsing worlds", class_526.class);
      addScreenState("Browsing servers", class_500.class);
      addScreenState("Creating a world", class_525.class);
      addScreenState("Editing a world", class_524.class);
      addScreenState("Editing a server", class_422.class, class_420.class);
      addScreenState("In options", class_429.class, class_4667.class);
      addScreenState("Browsing modules", ModuleListScreen.class);
      addScreenState("Editing a module", ModuleOptionsScreen.class);
      addScreenState("Browsing screenshots", ScreenshotListScreen.class);
      addScreenState("Editing HUD Layout", HudEditorScreen.class, HudSettingsScreen.class, AddHudElementScreen.class, ElementSettingsScreen.class);
      addScreenState("Editing Macros", MacroListScreen.class, MacroEditScreen.class);
      addScreenState("In Cactus Settings", CScreen.class);
   }
}
