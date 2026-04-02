package com.dwarslooper.cactus.client.systems.ias.skins;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.systems.ias.SessionUtils;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.UUIDCache;
import com.dwarslooper.cactus.client.util.networking.FormDataRequest;
import com.mojang.authlib.yggdrasil.ProfileResult;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_1011;
import net.minecraft.class_1068;
import net.minecraft.class_7920;
import net.minecraft.class_8685;
import org.jetbrains.annotations.Nullable;

public class SkinHelper {
   private static final Map<UUID, CompletableFuture<Optional<class_8685>>> SKIN_CACHE = new WeakHashMap();
   private static final String SKIN_UPLOAD_ENDPOINT = "https://api.minecraftservices.com/minecraft/profile/skins";

   public static Map<UUID, CompletableFuture<Optional<class_8685>>> getSkinCache() {
      return SKIN_CACHE;
   }

   public static void uploadAndSetSkin(File skinFile, String variant, String accessToken) throws IOException {
      byte[] multipart = (new FormDataRequest(new ByteArrayOutputStream())).errorHandler(Throwable::printStackTrace).addTextField("variant", variant).addFileField("file", skinFile).bake();
      HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://api.minecraftservices.com/minecraft/profile/skins")).header("Authorization", "Bearer " + accessToken).header("Content-Type", "multipart/form-data; boundary=" + FormDataRequest.BOUNDARY).POST(BodyPublishers.ofByteArray(multipart)).build();

      try {
         HttpClient client = HttpClient.newHttpClient();

         try {
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            int responseCode = response.statusCode();
            if (responseCode != 200) {
               throw new IllegalStateException("Skin update failed (%s): %s".formatted(new Object[]{responseCode, response.body()}));
            }

            CactusClient.getLogger().info("Skin updated successfully.");
         } catch (Throwable var9) {
            if (client != null) {
               try {
                  client.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (client != null) {
            client.close();
         }

      } catch (InterruptedException var10) {
         throw new IllegalStateException(var10);
      }
   }

   public static class_7920 detectSkinType(class_1011 image) {
      int scaleFactor = image.method_4307() / 64;
      return image.method_4311(47 * scaleFactor, 63 * scaleFactor) == 0 ? class_7920.field_41122 : class_7920.field_41123;
   }

   public static class_7920 detectSkinType(BufferedImage image) {
      return (new Color(image.getRGB(47, 63), true)).getAlpha() == 255 ? class_7920.field_41122 : class_7920.field_41123;
   }

   public static class_7920 skinType(class_1011 image) {
      return detectSkinType(image);
   }

   public static class_8685 getCachedSkinOrFetch(@Nullable UUID uuid) {
      return uuid != null && !uuid.equals(UUIDCache.EMPTY_UUID) ? (class_8685)((Optional)((CompletableFuture)SKIN_CACHE.computeIfAbsent(uuid, (id) -> {
         return CompletableFuture.supplyAsync(() -> {
            ProfileResult r = SessionUtils.getApiService().comp_837().fetchProfile(uuid, false);
            return r != null ? r.profile() : null;
         }).thenApply((gameProfile) -> {
            return gameProfile == null ? Optional.empty() : (Optional)CactusConstants.mc.method_1582().method_52863(gameProfile).join();
         });
      })).getNow(Optional.empty())).orElse(class_1068.method_4648(uuid)) : class_1068.method_4648(UUIDCache.EMPTY_UUID);
   }
}
