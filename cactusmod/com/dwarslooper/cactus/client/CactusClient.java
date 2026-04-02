package com.dwarslooper.cactus.client;

import com.dwarslooper.cactus.client.addon.v2.AddonHandler;
import com.dwarslooper.cactus.client.addon.v2.ICactusAddon;
import com.dwarslooper.cactus.client.compat.server.ServerCompat;
import com.dwarslooper.cactus.client.event.EventBus;
import com.dwarslooper.cactus.client.feature.commands.ToolCommand;
import com.dwarslooper.cactus.client.feature.content.ContentPackManager;
import com.dwarslooper.cactus.client.gui.screen.impl.HeadBrowserScreen;
import com.dwarslooper.cactus.client.gui.screen.window.UpdateScreen;
import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.systems.ItemGroupSystem;
import com.dwarslooper.cactus.client.systems.RunUtils;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.systems.config.ConfigHandler;
import com.dwarslooper.cactus.client.systems.config.FileConfiguration;
import com.dwarslooper.cactus.client.systems.config.impl.CactusConfig;
import com.dwarslooper.cactus.client.systems.ias.AccountManager;
import com.dwarslooper.cactus.client.systems.ias.SessionUtils;
import com.dwarslooper.cactus.client.systems.ias.account.Account;
import com.dwarslooper.cactus.client.systems.key.KeybindManager;
import com.dwarslooper.cactus.client.systems.localserver.LocalServerManager;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.CactusRemoteMeta;
import com.dwarslooper.cactus.client.util.generic.ColorUtils;
import com.google.gson.Gson;
import java.util.Calendar;
import java.util.Optional;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.minecraft.class_156;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_442;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class CactusClient implements ClientModInitializer {
   private static CactusClient instance;
   public static Gson GSON = new Gson();
   private boolean running = true;
   private IRCClient ircClient;
   private ConfigHandler configHandler;
   private AddonHandler addonHandler;
   public static EventBus EVENT_BUS;

   public void onInitializeClient() {
      instance = this;
      CactusConstants.init();
      this.printHello();
      EVENT_BUS = new EventBus();
      ClientLifecycleEvents.CLIENT_STARTED.register(this::onGameInitDone);
      ClientLifecycleEvents.CLIENT_STOPPING.register(this::clientStop);
      this.registerDefaultEvents();
      this.ircClient = new IRCClient();
      this.configHandler = new ConfigHandler(CactusConstants.DIRECTORY);
      getLogger().info("Loading Addons..");
      this.addonHandler = new AddonHandler();
      this.addonHandler.createDefaultProviders();
      this.addonHandler.discoverAndRegister("cactus");
      this.addonHandler.initializeAll();
      this.configHandler.register();
      ItemGroupSystem.register();
      KeybindManager.init();
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
         getInstance().clientStop((class_310)null);
      }, "CC-ShutdownHook"));
      getLogger().info("Loading Configurations..");
      this.configHandler.reload();
      ServerCompat.register();
      ResourceManagerHelper.registerBuiltinResourcePack(class_2960.method_60655("cactus", "simpleredstone"), CactusConstants.CONTAINER, class_2561.method_30163("Simple Redstone"), ResourcePackActivationType.NORMAL);
      Calendar cal = Calendar.getInstance();
      int month = cal.get(2);
      int dayOfMonth = cal.get(5);
      if (month == 3 && dayOfMonth == 1) {
         CactusConstants.APRILFOOLS = true;
      }

      EVENT_BUS.subscribe(this);
      CactusRemoteMeta.fetch();
      this.addonHandler.callEach(ICactusAddon::onLoadComplete);
      getLogger().info("Done initializing!");
   }

   public void onGameInitDone(class_310 client) {
      if (((CactusConfig)this.configHandler.getConfig(CactusConfig.class)).isFirstInit()) {
      }

      RunUtils.handleLastCrash();
      if ((Boolean)CactusSettings.get().updateNotify.get()) {
         Optional<CactusRemoteMeta.UpdateInfo> updateInfo = CactusRemoteMeta.checkForUpdate();
         updateInfo.ifPresent((info) -> {
            if (CactusConstants.mc.field_1755 instanceof class_442) {
               CactusConstants.mc.method_1507((new UpdateScreen(info.version(), info.downloadUri())).onSubmit((i) -> {
                  if (i == UpdateScreen.UserResponse.DOWNLOAD) {
                     class_156.method_668().method_670("https://cactusmod.xyz/downloads/?version=latest&ref=inGameUpdateNotify");
                  }

               }));
            }
         });
      }

      if (SessionUtils.isSessionOfflineMode() && !AccountManager.getAccountList().isEmpty()) {
         Account account = AccountManager.getDefaultAccount();
         if (account != null) {
            account.login().whenComplete((authData, throwable) -> {
               CactusConstants.mc.execute(() -> {
                  if (throwable == null) {
                     SessionUtils.setSession(authData.name(), authData.uuid(), authData.accessToken());
                     getLogger().info("Logged into default account ({})", authData.name());
                  } else {
                     getLogger().error("Failed to login to default account", throwable);
                  }

               });
            });
         }
      } else if ((Boolean)CactusSettings.get().ircConnectOnStart.get() && !Boolean.getBoolean(System.getProperty("fabric.development"))) {
         this.ircClient.connect();
      }

      if (ContentPackManager.get().isEnabled(ContentPackManager.get().ofId("head_database"))) {
         HeadBrowserScreen.fetchAll((s) -> {
         }).whenComplete((unused, throwable) -> {
            if (throwable == null) {
               HeadBrowserScreen.setCached(true);
            }

         });
      }

      AccountManager.getAccountList().forEach(Account::skin);
   }

   private void registerDefaultEvents() {
      EVENT_BUS.subscribe(new KeybindManager());
      EVENT_BUS.subscribe(ToolCommand.HANDLER);
   }

   private void clientStop(class_310 minecraftClient) {
      if (this.running) {
         this.addonHandler.callEach(ICactusAddon::onShutdown);
         this.configHandler.save();
         LocalServerManager.get().shutdownAll();
         this.running = false;
      }
   }

   private void printHello() {
      String[] welcome = " \n     ,*-.\n     |  |       Initializing {name}\n ,.  |  |       v {version}\n | |_|  | ,.    {development}\n `---.  |_| |\n     |  .--`\n     |  |       Hello, {player}\n     |  |\n ".replace("{name}", CactusConstants.META.getName()).replace("{version}", CactusConstants.VERSION).replace("{development}", CactusConstants.DEVBUILD ? "development" : "release").replace("{player}", CactusConstants.mc.method_1548().method_1676()).split("\n");
      String[] var2 = welcome;
      int var3 = welcome.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String line = var2[var4];
         getLogger().info(line);
      }

   }

   public int getRGB() {
      return ColorUtils.getAccentRGB();
   }

   public IRCClient getIrcClient() {
      return this.ircClient;
   }

   public AddonHandler getAddonHandler() {
      return this.addonHandler;
   }

   public ConfigHandler getConfigHandler() {
      return this.configHandler;
   }

   public static String getPrefix() {
      return ((String)CactusSettings.get().prefix.get()).isEmpty() ? "" : ((String)CactusSettings.get().prefix.get()).replace("&", "§");
   }

   public static boolean hasFinishedLoading() {
      return instance != null && instance.running && instance.getConfigHandler() != null && instance.getConfigHandler().isDoneLoading();
   }

   public static CactusClient getInstance() {
      return instance;
   }

   public static <T extends FileConfiguration<T>> T getConfig(Class<T> clazz) {
      return getInstance().getConfigHandler().getConfig(clazz);
   }

   public static Logger getLogger() {
      return CactusConstants.LOG;
   }
}
