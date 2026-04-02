package com.dwarslooper.cactus.client.util.networking;

import com.dwarslooper.cactus.client.CactusClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

public class ImgurHelper {
   private static final String IMGUR_ENDPOINT = "https://api.imgur.com/3/upload";
   private static final String APPLICATION_ID = "8e60abb068ccff6";

   public static CompletableFuture<JsonObject> uploadImage(File file) {
      CompletableFuture<JsonObject> future = new CompletableFuture();
      CompletableFuture.runAsync(() -> {
         try {
            HttpURLConnection con = (HttpURLConnection)(new URI("https://api.imgur.com/3/upload")).toURL().openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Client-ID 8e60abb068ccff6");
            con.setReadTimeout(100000);
            con.connect();
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            String base64 = Base64.getEncoder().encodeToString(fileBytes);
            String encoded = URLEncoder.encode(base64, StandardCharsets.UTF_8);
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write("image=" + encoded);
            writer.flush();
            writer.close();
            InputStream stream = con.getResponseCode() >= 200 && con.getResponseCode() < 400 ? con.getInputStream() : con.getErrorStream();
            StringBuilder response = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String line;
            try {
               while((line = reader.readLine()) != null) {
                  response.append(line).append("\n");
               }
            } catch (Throwable var13) {
               try {
                  reader.close();
               } catch (Throwable var12) {
                  var13.addSuppressed(var12);
               }

               throw var13;
            }

            reader.close();
            JsonObject responseJson = JsonParser.parseString(response.toString()).getAsJsonObject();
            if (!responseJson.get("success").getAsBoolean()) {
               throw new IllegalStateException("Upload response responded with non-success status");
            }

            CactusClient.getLogger().info("Imgur upload response {}", responseJson);
            future.complete(responseJson.getAsJsonObject("data"));
         } catch (Exception var14) {
            future.completeExceptionally(var14);
         }

      });
      return future;
   }
}
