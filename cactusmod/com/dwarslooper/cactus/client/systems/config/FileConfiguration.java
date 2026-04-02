package com.dwarslooper.cactus.client.systems.config;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.addon.v2.RegistryBus;
import com.dwarslooper.cactus.client.systems.ISerializable;
import com.dwarslooper.cactus.client.util.Utils;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.class_2477;

public abstract class FileConfiguration<T> implements ISerializable<T> {
   private final ConfigHandler handler;
   private final String name;
   private final File file;
   private final Map<Class<? extends ISerializable<?>>, FileConfiguration.SubConfigWrapper<? extends ISerializable<?>>> subConfigurations = new LinkedHashMap();
   private final boolean firstInit;
   private boolean isInitializing;

   public FileConfiguration(String name, ConfigHandler handler) {
      this.handler = handler;
      this.name = name;
      this.file = new File(handler.getDirectory(), name + ".json");
      this.firstInit = !this.file.exists();
   }

   protected final void wrappedInitialize() {
      this.isInitializing = true;
      this.init();
      this.isInitializing = false;
   }

   public void init() {
   }

   public void filePreprocess(File file) {
   }

   public boolean exportAndImportByDefault() {
      return true;
   }

   public Map<Class<? extends ISerializable<?>>, FileConfiguration.SubConfigWrapper<? extends ISerializable<?>>> getSubConfigurations() {
      return this.subConfigurations;
   }

   public <E extends ISerializable<E>> E getSubConfig(Class<? extends ISerializable<E>> clazz) {
      return ((FileConfiguration.SubConfigWrapper)this.subConfigurations.get(clazz)).serializable();
   }

   public <E> void appendConfig(String key, ISerializable<E> serializable) {
      this.subConfigurations.put(serializable.getClass(), new FileConfiguration.SubConfigWrapper(key, serializable));
   }

   public boolean isLoaded() {
      return this.getHandler().isLoaded(this);
   }

   public ConfigHandler getHandler() {
      return this.handler;
   }

   public File getFile() {
      return this.file;
   }

   public File getFile(File directory) {
      Utils.ensureDirectory(directory);
      return new File(directory, this.file.getName());
   }

   public String getName() {
      return this.name;
   }

   public String getDisplayName() {
      return class_2477.method_10517().method_4679("configurations." + this.name + ".name", this.name);
   }

   public boolean isFirstInit() {
      return this.firstInit;
   }

   public boolean isInternal() {
      return this.getClass().isAnnotationPresent(InternalOnly.class);
   }

   public boolean isInitializing() {
      return this.isInitializing;
   }

   public final void warnIfCalledOutsideOfInit(String name) {
      if (!this.isInitializing) {
         CactusClient.getLogger().warn("{} is expected to be called during initialization, but {} is currently not initializing.", name, this.getClass().getSimpleName());
      }

   }

   protected static RegistryBus getMainRegistryBus() {
      return CactusClient.getInstance().getAddonHandler().getRegistryBus();
   }

   public static record SubConfigWrapper<T extends ISerializable<?>>(String key, T serializable) {
      public SubConfigWrapper(String key, T serializable) {
         this.key = key;
         this.serializable = serializable;
      }

      public String key() {
         return this.key;
      }

      public T serializable() {
         return this.serializable;
      }
   }
}
