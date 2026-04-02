package com.dwarslooper.cactus.client.util;

import com.dwarslooper.cactus.client.CactusClient;
import java.io.File;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.class_310;
import net.minecraft.class_7887;
import net.minecraft.class_7225.class_7874;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CactusConstants {
   public static final File DIRECTORY = new File(FabricLoader.getInstance().getGameDir().toString(), "cactus");
   public static final String WEBSITE = "https://cactusmod.xyz";
   public static final String DISCORD_INVITE = "https://cactusmod.xyz/go/?discord";
   public static class_310 mc;
   public static ModContainer CONTAINER;
   public static ModMetadata META;
   public static String VERSION;
   public static boolean DEVBUILD;
   public static String SCRIPT_VERSION = "1.0";
   public static class_7874 WRAPPER_LOOKUP;
   public static final Logger LOG = LoggerFactory.getLogger(CactusClient.class);
   public static boolean APRILFOOLS = false;

   public static void init() {
      mc = class_310.method_1551();
      CONTAINER = (ModContainer)FabricLoader.getInstance().getModContainer("cactus").orElseThrow();
      META = CONTAINER.getMetadata();
      DEVBUILD = META.getCustomValue("dev").getAsBoolean() || Boolean.getBoolean(System.getProperty("fabric.development"));
      SCRIPT_VERSION = META.getCustomValue("scriptVersion").getAsString();
      VERSION = META.getVersion().getFriendlyString();
      WRAPPER_LOOKUP = class_7887.method_46817();
   }

   public static boolean isDoneInitializing() {
      return mc != null;
   }
}
