package com.dwarslooper.cactus.client.util.generic;

import com.dwarslooper.cactus.client.CactusClient;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_10799;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;

public class WebImage implements AutoCloseable {
   private final class_2960 identifier;
   private boolean loaded;

   public WebImage(class_2960 identifier) {
      this.identifier = identifier;
   }

   public CompletableFuture<WebImage> load(String url) {
      return this.loaded ? CompletableFuture.completedFuture(this) : CompletableFuture.supplyAsync(() -> {
         try {
            return fetchTexture(url);
         } catch (IOException var2) {
            CactusClient.getLogger().error("Failed to load WebImage", var2);
            return null;
         } catch (URISyntaxException var3) {
            throw new IllegalArgumentException("Invalid URI", var3);
         }
      }).thenApply((texture) -> {
         if (texture == null) {
            return this;
         } else {
            class_310.method_1551().execute(() -> {
               class_310.method_1551().method_1531().method_4616(this.identifier, new class_1043(() -> {
                  return url;
               }, texture));
               this.loaded = true;
            });
            return this;
         }
      });
   }

   public void draw(class_332 context, int x, int y, int width, int height) {
      if (this.isLoaded()) {
         context.method_25290(class_10799.field_56883, this.identifier, x, y, 0.0F, 0.0F, width, height, width, height);
      }

   }

   public void close() {
      class_310.method_1551().method_1531().method_4615(this.identifier);
      this.loaded = false;
   }

   public boolean isLoaded() {
      return this.loaded;
   }

   public class_2960 getIdentifier() {
      return this.identifier;
   }

   public String toString() {
      boolean var10000 = this.loaded;
      return "WebImage{loaded=" + var10000 + ", identifier=" + String.valueOf(this.identifier) + "}";
   }

   public static class_1011 fetchTexture(String url) throws IOException, URISyntaxException {
      HttpURLConnection connection = (HttpURLConnection)(new URI(url)).toURL().openConnection();
      if (connection.getResponseCode() == 200) {
         InputStream in = connection.getInputStream();
         return class_1011.method_4309(in);
      } else {
         return null;
      }
   }
}
