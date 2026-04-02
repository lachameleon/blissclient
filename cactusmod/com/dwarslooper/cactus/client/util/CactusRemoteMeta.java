package com.dwarslooper.cactus.client.util;

import com.dwarslooper.cactus.client.CactusClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

public class CactusRemoteMeta {
   private static JsonObject remote;
   private static boolean IS_OUTDATED;
   private static String PLAYER_HEAD_API_URL;

   public static void fetch() {
      try {
         InputStream stream = (new URI("https://cdn.cactusmod.xyz/client/shared/versions.json")).toURL().openStream();
         remote = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
         PLAYER_HEAD_API_URL = remote.get("playerHeadApiUrl").getAsString();
      } catch (Exception var1) {
         CactusClient.getLogger().error("Failed to fetch metadata", var1);
      }

   }

   public void reset() {
      IS_OUTDATED = false;
      PLAYER_HEAD_API_URL = null;
   }

   public static String getPlayerHeadApiUrl(String usernameOrUUID) {
      return (String)Utils.orElse(PLAYER_HEAD_API_URL, (s) -> {
         return s.replace("{player}", usernameOrUUID);
      }, (Object)null);
   }

   public static boolean isOutdated() {
      return IS_OUTDATED;
   }

   public static Optional<CactusRemoteMeta.UpdateInfo> checkForUpdate() {
      if (remote == null) {
         return Optional.empty();
      } else {
         JsonArray versions = remote.getAsJsonArray("versions");
         String latestStable = remote.get("latestStable").getAsString();
         String downloadUrlString = remote.get("downloadUrl").getAsString();

         URI downloadUri;
         try {
            downloadUri = new URI(downloadUrlString);
         } catch (URISyntaxException var7) {
            CactusClient.getLogger().error("Invalid URL supplied by remote", var7);
            return Optional.empty();
         }

         List<String> versionList = versions.asList().stream().map(JsonElement::getAsString).toList();
         int currentIndex = versionList.indexOf(CactusConstants.VERSION);
         int latestIndex = versionList.indexOf(latestStable);
         if (currentIndex != -1 && currentIndex < latestIndex) {
            IS_OUTDATED = true;
            CactusClient.getLogger().info("There is a new update available!");
            return Optional.of(new CactusRemoteMeta.UpdateInfo(latestStable, downloadUri));
         } else {
            CactusClient.getLogger().info("Your version is up to date!");
            return Optional.empty();
         }
      }
   }

   public static record UpdateInfo(String version, URI downloadUri) {
      public UpdateInfo(String version, URI downloadUri) {
         this.version = version;
         this.downloadUri = downloadUri;
      }

      public String version() {
         return this.version;
      }

      public URI downloadUri() {
         return this.downloadUri;
      }
   }
}
