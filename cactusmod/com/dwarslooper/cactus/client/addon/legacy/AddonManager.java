package com.dwarslooper.cactus.client.addon.legacy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import org.jetbrains.annotations.NotNull;

public class AddonManager implements Iterable<CactusAddon> {
   public static final AddonManager INSTANCE = new AddonManager();
   public final List<CactusAddon> ADDONS = new ArrayList();

   public static void init() {
      Iterator var0 = FabricLoader.getInstance().getEntrypointContainers("cactus", CactusAddon.class).iterator();

      while(var0.hasNext()) {
         EntrypointContainer<CactusAddon> entrypoint = (EntrypointContainer)var0.next();
         ModMetadata metadata = entrypoint.getProvider().getMetadata();

         CactusAddon addon;
         try {
            addon = (CactusAddon)entrypoint.getEntrypoint();
         } catch (Exception var7) {
            throw new RuntimeException("Exception during addon init \"%s\".".formatted(new Object[]{metadata.getName()}), var7);
         }

         addon.meta = metadata;
         addon.name = metadata.getName();
         addon.authors = new String[metadata.getAuthors().size()];
         int i = 0;

         Person author;
         for(Iterator var5 = metadata.getAuthors().iterator(); var5.hasNext(); addon.authors[i++] = author.getName()) {
            author = (Person)var5.next();
         }

         INSTANCE.ADDONS.add(addon);
      }

   }

   @NotNull
   public Iterator<CactusAddon> iterator() {
      return this.ADDONS.iterator();
   }
}
