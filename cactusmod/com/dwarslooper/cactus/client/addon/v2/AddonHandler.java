package com.dwarslooper.cactus.client.addon.v2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddonHandler {
   private final Logger logger = LoggerFactory.getLogger("CactusClient / Addon Handler");
   private final RegistryBus registryBus = new RegistryBus();
   private final List<Addon> addons = new ArrayList();
   private final List<ModMetadata> legacyUnsupported = new ArrayList();

   public void createDefaultProviders() {
   }

   public void discoverAndRegister(String entrypointName) {
      Iterator var2 = FabricLoader.getInstance().getEntrypointContainers(entrypointName, ICactusAddon.class).iterator();

      while(var2.hasNext()) {
         EntrypointContainer<ICactusAddon> entrypoint = (EntrypointContainer)var2.next();
         ModMetadata metadata = entrypoint.getProvider().getMetadata();

         try {
            ICactusAddon lifecycle = (ICactusAddon)entrypoint.getEntrypoint();
            this.addons.add(new Addon(metadata.getId(), metadata.getName(), metadata.getAuthors().stream().map(Person::getName).toList(), metadata, lifecycle));
         } catch (NoClassDefFoundError var6) {
            if (!"com/dwarslooper/cactus/client/addon/CactusAddon".equals(var6.getMessage())) {
               throw var6;
            }

            this.logger.error("Exception thrown during initialization of addon '{}' ({}); This addon uses the legacy addon system which is no longer supported. Please update this addon to use the new system. See https://docs.cactusmod.xyz/advanced/addons for more information.", metadata.getName(), metadata.getId());
            this.legacyUnsupported.add(metadata);
         } catch (Throwable var7) {
            this.logger.error("Exception thrown during initialization of addon '{}' ({})", new Object[]{metadata.getName(), metadata.getId(), var7});
         }
      }

   }

   public void initializeAll() {
      List sortedAddons;
      try {
         sortedAddons = DependencySorter.sortAddonsByDependency(this.addons);
      } catch (IllegalStateException var6) {
         this.logger.error("Failed to sort addons by dependencies", var6);
         return;
      }

      Iterator var2 = sortedAddons.iterator();

      while(var2.hasNext()) {
         Addon addon = (Addon)var2.next();

         try {
            RegistryBus var10000 = this.registryBus;
            ICactusAddon var10002 = addon.lifecycle();
            Objects.requireNonNull(var10002);
            var10000.withRegistrar(addon, var10002::onInitialize);
         } catch (Exception var5) {
            this.logger.error("Failed to initialize addon '{}'", addon.name(), var5);
         }
      }

   }

   public void callEach(Consumer<ICactusAddon> consumer) {
      this.addons.forEach((addon) -> {
         consumer.accept(addon.lifecycle());
      });
   }

   public Logger getLogger() {
      return this.logger;
   }

   public RegistryBus getRegistryBus() {
      return this.registryBus;
   }

   public List<Addon> getAddons() {
      return this.addons;
   }
}
