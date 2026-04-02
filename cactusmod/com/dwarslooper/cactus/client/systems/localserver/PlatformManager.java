package com.dwarslooper.cactus.client.systems.localserver;

import com.dwarslooper.cactus.client.util.generic.DisplayNamable;
import com.dwarslooper.cactus.client.util.networking.HttpUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.class_2477;

public class PlatformManager {
   public static List<String> fetchVersionFor(PlatformManager.Platform platform) {
      return (List)platform.versions().get();
   }

   public static enum Platform implements DisplayNamable {
      LATEST_VANILLA("Vanilla (Unmodified)", "vanilla", () -> {
         JsonObject manifest = HttpUtils.fetchJson("https://launchermeta.mojang.com/mc/game/version_manifest_v2.json").getAsJsonObject();
         return manifest.getAsJsonArray("versions").asList().stream().map((element) -> {
            return element.getAsJsonObject().get("id").getAsString();
         }).toList();
      }),
      LATEST_PAPER("Paper (Plugins)", "paper", () -> {
         JsonObject project = HttpUtils.fetchJson("https://api.papermc.io/v2/projects/paper").getAsJsonObject();
         List<String> versions = new ArrayList(project.getAsJsonArray("versions").asList().stream().map(JsonElement::getAsString).toList());
         Collections.reverse(versions);
         return versions;
      }),
      LATEST_FABRIC("FabricMC (Mods)", "fabric", () -> {
         JsonArray manifest = HttpUtils.fetchJson("https://meta.fabricmc.net/v2/versions/game").getAsJsonArray();
         return manifest.asList().stream().map((element) -> {
            String version = element.getAsJsonObject().get("version").getAsString();
            return element.getAsJsonObject().get("stable").getAsBoolean() ? version + " (Stable)" : version;
         }).toList();
      }),
      CUSTOM("Custom ...", "custom", () -> {
         return Collections.singletonList("");
      });

      private final String fancyName;
      private final String id;
      private final Supplier<List<String>> versions;

      private Platform(String fancyName, String id, Supplier<List<String>> availableVersions) {
         this.fancyName = fancyName;
         this.versions = availableVersions;
         this.id = id;
      }

      public String getDisplayName() {
         return class_2477.method_10517().method_4679("gui.screen.create_local_server.platform." + this.id, this.fancyName);
      }

      public Supplier<List<String>> versions() {
         return this.versions;
      }

      public String getId() {
         return this.id;
      }

      // $FF: synthetic method
      private static PlatformManager.Platform[] $values() {
         return new PlatformManager.Platform[]{LATEST_VANILLA, LATEST_PAPER, LATEST_FABRIC, CUSTOM};
      }
   }
}
