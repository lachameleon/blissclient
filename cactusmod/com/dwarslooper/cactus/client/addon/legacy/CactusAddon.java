package com.dwarslooper.cactus.client.addon.legacy;

import com.dwarslooper.cactus.client.feature.command.CommandManager;
import com.dwarslooper.cactus.client.feature.content.ContentPackManager;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import net.fabricmc.loader.api.metadata.ModMetadata;

public abstract class CactusAddon {
   public String name;
   public String[] authors;
   public ModMetadata meta;

   public abstract void onInitialize();

   public abstract void registerCategories(ModuleManager var1);

   public abstract void registerModules(ModuleManager var1);

   public abstract void registerCommands(CommandManager var1);

   public abstract void registerContentPacks(ContentPackManager var1);

   public abstract void onLoaded();

   public abstract void onDisable();
}
