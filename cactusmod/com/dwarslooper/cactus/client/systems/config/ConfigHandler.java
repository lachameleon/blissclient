package com.dwarslooper.cactus.client.systems.config;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.feature.command.CommandManager;
import com.dwarslooper.cactus.client.feature.content.ContentPackManager;
import com.dwarslooper.cactus.client.feature.macro.MacroManager;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.gui.hud.HudManager;
import com.dwarslooper.cactus.client.systems.NotificationManager;
import com.dwarslooper.cactus.client.systems.config.impl.CactusConfig;
import com.dwarslooper.cactus.client.systems.config.impl.SavedLevelsConfig;
import com.dwarslooper.cactus.client.systems.config.profiles.ProfileManager;
import com.dwarslooper.cactus.client.systems.ias.AccountManager;
import com.dwarslooper.cactus.client.systems.snippet.SnippetManager;
import com.dwarslooper.cactus.client.systems.teams.TeamManager;
import com.dwarslooper.cactus.client.systems.waypoints.WaypointManager;
import com.dwarslooper.cactus.client.systems.worldshare.OasisHostManager;
import com.dwarslooper.cactus.client.util.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.class_156;

public class ConfigHandler {
   private static final Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
   private final Map<Class<? extends FileConfiguration<?>>, FileConfiguration<?>> configurations = new LinkedHashMap();
   private final Set<FileConfiguration<?>> loadedConfigurations = new HashSet();
   private final File directory;
   private boolean enteredLoading;

   public ConfigHandler(File directory) {
      directory.mkdirs();
      Utils.ensureDirectory(directory);
      this.directory = directory;
   }

   private void register(FileConfiguration<?> config) {
      this.configurations.put(config.getClass(), config);
      config.wrappedInitialize();
   }

   public <T extends FileConfiguration<T>> T getConfig(Class<? extends FileConfiguration<T>> clazz) {
      return (FileConfiguration)this.configurations.get(clazz);
   }

   public boolean save(FileConfiguration<?> config) {
      return this.save(config, this.directory, TreeSerializerFilter.ALL);
   }

   public boolean save(FileConfiguration<?> config, File source, TreeSerializerFilter filter) {
      File file = config.getFile(source);

      try {
         file.getParentFile().mkdirs();
         if (!file.exists()) {
            file.createNewFile();
         }

         JsonObject root = (JsonObject)Utils.orElse(config.toJson(filter), new JsonObject());
         config.getSubConfigurations().forEach((clazz, wrapper) -> {
            root.add(wrapper.key(), wrapper.serializable().toJson(filter.resolve(wrapper.key())));
         });
         Files.write(file.toPath(), gson.toJson(root).getBytes(), new OpenOption[0]);
         return true;
      } catch (Exception var6) {
         CactusClient.getLogger().error("Failed to save config", var6);
         return false;
      }
   }

   public boolean load(FileConfiguration<?> config) {
      return this.load(config, this.directory);
   }

   public boolean load(FileConfiguration<?> config, File source) {
      File file = config.getFile(source);

      try {
         config.filePreprocess(file);
         if (!file.exists()) {
            return false;
         } else {
            JsonObject root = (JsonObject)gson.fromJson(new FileReader(file), JsonObject.class);
            if (root == null) {
               return false;
            } else {
               try {
                  config.fromJson(root);
                  config.getSubConfigurations().forEach((clazz, wrapper) -> {
                     wrapper.serializable().fromJson((JsonObject)root.get(wrapper.key()));
                  });
                  return true;
               } catch (Exception var8) {
                  File backupsDir = new File(file.getParentFile(), "backups");
                  backupsDir.mkdirs();
                  File backup = new File(backupsDir, "%s-%s.backup.json".formatted(new Object[]{config.getName(), Long.toString(System.currentTimeMillis(), 16)}));
                  Files.write(backup.toPath(), gson.toJson(root).getBytes(), new OpenOption[0]);
                  CactusClient.getLogger().error("Failed to correctly load configurations for '{}'!", config.getName(), var8);
                  CactusClient.getLogger().error("The config was reset to default and a backup created: {}", backup.getName());
                  return false;
               }
            }
         }
      } catch (Exception var9) {
         CactusClient.getLogger().error("Failed to load config", var9);
         return false;
      }
   }

   public void register() {
      this.register(new CactusConfig(this));
      this.register(new ModuleManager(this));
      this.register(new CommandManager(this));
      this.register(new AccountManager(this));
      this.register(new MacroManager(this));
      this.register(new WaypointManager(this));
      this.register(new ContentPackManager(this));
      this.register(new NotificationManager(this));
      this.register(new HudManager(this));
      this.register(new OasisHostManager(this));
      this.register(new SnippetManager(this));
      this.register(new TeamManager(this));
      this.register(new SavedLevelsConfig(this));
      this.register(new ProfileManager(this));
      CactusClient.getInstance().getAddonHandler().getRegistryBus().provideService(ConfigHandler.class, this);
      CactusClient.getInstance().getAddonHandler().getRegistryBus().completeAndTake(FileConfiguration.class, this::register);
   }

   public void reload() {
      this.enteredLoading = true;
      long start = class_156.method_658();
      if (this.runFailable(() -> {
         this.configurations.values().forEach((fileConfiguration) -> {
            this.load(fileConfiguration);
            this.loadedConfigurations.add(fileConfiguration);
         });
      }, (f) -> {
         CactusClient.getLogger().error("One or more configurations failed to load", f);
      })) {
         CactusClient.getLogger().info("Cactus config loaded! ({}ms)", class_156.method_658() - start);
      }

   }

   public void save() {
      if (this.isDoneLoading()) {
         long start = class_156.method_658();
         if (this.runFailable(() -> {
            this.configurations.values().forEach(this::save);
         }, (f) -> {
            CactusClient.getLogger().error("One or more configurations failed to save", f);
         })) {
            CactusClient.getLogger().info("Cactus config saved! ({}ms)", class_156.method_658() - start);
         }

      }
   }

   private boolean runFailable(Runnable runnable, Consumer<Throwable> failHandler) {
      try {
         runnable.run();
         return true;
      } catch (Exception var4) {
         failHandler.accept(var4);
         return false;
      }
   }

   public boolean isLoaded(FileConfiguration<?> config) {
      return this.loadedConfigurations.contains(config);
   }

   public boolean isDoneLoading() {
      return this.enteredLoading && this.loadedConfigurations.size() == this.configurations.size();
   }

   public static Gson getGson() {
      return gson;
   }

   public Map<Class<? extends FileConfiguration<?>>, FileConfiguration<?>> getConfigurations() {
      return this.configurations;
   }

   public Set<FileConfiguration<?>> getLoadedConfigurations() {
      return this.loadedConfigurations;
   }

   public File getDirectory() {
      return this.directory;
   }

   public List<File> getFiles(File directory) {
      Utils.ensureDirectory(directory);
      return this.getConfigurations().values().stream().map((config) -> {
         return config.getFile(directory);
      }).toList();
   }
}
