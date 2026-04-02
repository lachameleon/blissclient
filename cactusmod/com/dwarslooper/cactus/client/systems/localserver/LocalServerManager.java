package com.dwarslooper.cactus.client.systems.localserver;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.systems.SingleInstance;
import com.dwarslooper.cactus.client.systems.localserver.instance.LocalServer;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.networking.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class LocalServerManager {
   private final List<LocalServer> servers = new ArrayList();
   public final Gson gson = new Gson();
   private final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
   private final File DIRECTORY;

   public static LocalServerManager get() {
      return (LocalServerManager)SingleInstance.of(LocalServerManager.class, LocalServerManager::new, LocalServerManager::init);
   }

   public LocalServerManager() {
      this.DIRECTORY = new File(CactusConstants.DIRECTORY, "servers");
   }

   public void init() {
      this.DIRECTORY.mkdirs();
      if (this.DIRECTORY.isDirectory()) {
         Arrays.stream((File[])Objects.requireNonNull(this.DIRECTORY.listFiles(File::isDirectory))).forEach((dir) -> {
            File properties = new File(dir, ".csp");
            if (!properties.exists()) {
               CactusClient.getLogger().error("Failed to load local server from dir '{}': Missing CSP file!", dir.getName());
            } else {
               try {
                  JsonObject root = (JsonObject)this.gson.fromJson(new FileReader(properties), JsonObject.class);
                  this.addServer(new LocalServer(root, dir));
               } catch (FileNotFoundException var4) {
                  CactusClient.getLogger().error("Failed to load JSON from CSP file for '{}'", dir.getName(), var4);
               }

            }
         });
      }

   }

   public List<LocalServer> getServers() {
      return this.servers;
   }

   public void shutdownAll() {
      Iterator var1 = this.servers.iterator();

      while(var1.hasNext()) {
         LocalServer server = (LocalServer)var1.next();
         server.scheduleStop();
      }

      CactusClient.getLogger().info("Stopped local servers!");
   }

   public LocalServer createServer(String name, PlatformManager.Platform platform, int port) {
      return new LocalServer(name, platform, true, port);
   }

   public LocalServer addServer(LocalServer server) {
      this.servers.add(server);
      return server;
   }

   public File getDirectory() {
      return this.DIRECTORY;
   }

   public String formatToValidName(String name) {
      return name.replaceAll("[^a-zA-Z0-9 ]", "").replaceAll(" ", "_");
   }

   public Future<?> downloadPlatformJar(PlatformManager.Platform platform, File directory, Consumer<Float> consumer, String version) {
      try {
         String serverDownloadURL;
         String cleanVersion;
         JsonObject versionData;
         Runnable var10000;
         switch(platform) {
         case LATEST_VANILLA:
            versionData = HttpUtils.fetchJson("https://launchermeta.mojang.com/mc/game/version_manifest_v2.json").getAsJsonObject();
            Optional<JsonObject> versionMeta = versionData.getAsJsonArray("versions").asList().stream().map(JsonElement::getAsJsonObject).filter((object) -> {
               return object.get("id").getAsString().equalsIgnoreCase(version);
            }).findFirst();
            if (versionMeta.isEmpty()) {
               throw new IllegalStateException("Version " + version + "was provided by PlatformManager#fetchVersionFor but is missing in manifest!");
            }

            JsonObject meta = HttpUtils.fetchJson(((JsonObject)versionMeta.get()).get("url").getAsString()).getAsJsonObject();
            serverDownloadURL = meta.getAsJsonObject("downloads").getAsJsonObject("server").get("url").getAsString();
            var10000 = () -> {
               HttpUtils.downloadAndReturnProgress(serverDownloadURL, consumer, directory, "server.jar");
            };
            break;
         case LATEST_PAPER:
            versionData = HttpUtils.fetchJson("https://api.papermc.io/v2/projects/paper/versions/" + version).getAsJsonObject();
            JsonArray builds = versionData.getAsJsonArray("builds");
            int latestBuild = builds.get(builds.size() - 1).getAsInt();
            JsonObject buildData = HttpUtils.fetchJson("https://api.papermc.io/v2/projects/paper/versions/" + version + "/builds/" + latestBuild).getAsJsonObject();
            cleanVersion = buildData.getAsJsonObject("downloads").getAsJsonObject("application").get("name").getAsString();
            var10000 = () -> {
               HttpUtils.downloadAndReturnProgress("https://api.papermc.io/v2/projects/paper/versions/" + version + "/builds/" + latestBuild + "/downloads/" + cleanVersion, consumer, directory, "server.jar");
            };
            break;
         case LATEST_FABRIC:
            JsonArray loaders = HttpUtils.fetchJson("https://meta.fabricmc.net/v2/versions/loader").getAsJsonArray();
            String loader = loaders.get(0).getAsJsonObject().getAsJsonPrimitive("version").getAsString();
            JsonArray installers = HttpUtils.fetchJson("https://meta.fabricmc.net/v2/versions/installer").getAsJsonArray();
            serverDownloadURL = installers.get(0).getAsJsonObject().getAsJsonPrimitive("version").getAsString();
            cleanVersion = version.split(" ")[0];
            String serverDownloadURL = "https://meta.fabricmc.net/v2/versions/loader/" + cleanVersion + "/" + loader + "/" + serverDownloadURL + "/server/jar";
            var10000 = () -> {
               HttpUtils.downloadAndReturnProgress(serverDownloadURL, consumer, directory, "server.jar");
            };
            break;
         default:
            var10000 = () -> {
            };
         }

         Runnable runnable = var10000;
         return this.EXECUTOR.submit(runnable);
      } catch (Exception var12) {
         CactusClient.getLogger().error("Failed to download platform JAR", var12);
         return null;
      }
   }
}
