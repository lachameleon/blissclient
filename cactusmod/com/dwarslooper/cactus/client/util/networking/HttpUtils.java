package com.dwarslooper.cactus.client.util.networking;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.util.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class HttpUtils {
   public static final float ERROR_NO_PROGRESS_AVAILABLE = -2.0F;
   public static final float ERROR_FAILED = -3.0F;

   public static String fetchUTF(String url) {
      return (String)Utils.orElse(fetchRaw(url), String::new, (Object)null);
   }

   public static JsonElement fetchJson(String url) {
      return (JsonElement)Utils.orElse(fetchUTF(url), JsonParser::parseString, (Object)null);
   }

   public static byte[] fetchRaw(String url) {
      try {
         InputStream input = (new URI(url)).toURL().openStream();

         byte[] var5;
         try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];

            while(true) {
               int bytesRead;
               if ((bytesRead = input.read(buffer)) == -1) {
                  var5 = byteArrayOutputStream.toByteArray();
                  break;
               }

               byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
         } catch (Throwable var7) {
            if (input != null) {
               try {
                  input.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (input != null) {
            input.close();
         }

         return var5;
      } catch (Exception var8) {
         return null;
      }
   }

   public static void downloadAndReturnProgress(String fileURL, Consumer<Float> consumer, File saveDir) {
      downloadAndReturnProgress(fileURL, consumer, saveDir, (String)null);
   }

   public static void downloadAndReturnProgress(String fileURL, Consumer<Float> consumer, File saveDir, String fileName) {
      try {
         URL url = (new URI(fileURL)).toURL();
         HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
         httpConn.setConnectTimeout(3000);
         httpConn.setReadTimeout(1000);
         int responseCode = httpConn.getResponseCode();
         if (responseCode != 200) {
            CactusClient.getLogger().warn("No file to download. Server replied with HTTP code: {}", responseCode);
         } else {
            int contentLength = httpConn.getContentLength();
            boolean canReport = contentLength != -1;
            if (!canReport) {
               consumer.accept(-2.0F);
            }

            File file = new File(saveDir, fileName != null ? fileName : url.getFile());
            BufferedInputStream inputStream = new BufferedInputStream(httpConn.getInputStream());
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int totalBytesRead = 0;

            while(true) {
               int bytesRead;
               if ((bytesRead = inputStream.read(buffer)) == -1) {
                  if (!canReport) {
                     consumer.accept(1.0F);
                  }

                  outputStream.close();
                  inputStream.close();
                  CactusClient.getLogger().info("File saved to {} as '{}'", saveDir, fileName);
                  break;
               }

               if (Thread.currentThread().isInterrupted()) {
                  outputStream.close();
                  inputStream.close();
                  httpConn.disconnect();
                  CactusClient.getLogger().info("File download interrupted");
                  if (file.exists()) {
                     file.delete();
                  }

                  consumer.accept(-3.0F);
                  return;
               }

               outputStream.write(buffer, 0, bytesRead);
               if (canReport) {
                  totalBytesRead += bytesRead;
                  consumer.accept((float)totalBytesRead / (float)contentLength);
               }
            }
         }

         httpConn.disconnect();
      } catch (URISyntaxException | IOException var15) {
         CactusClient.getLogger().error("Error while downloading file", var15);
      }

   }

   public static HttpRequest createRequest(String url, Function<Builder, Builder> builder, JsonObject jsonBody) {
      return createRequest(url, builder, BodyPublishers.ofString(jsonBody.toString(), StandardCharsets.UTF_8), "application/json");
   }

   public static HttpRequest createRequest(String url, Function<Builder, Builder> builder, Map<String, String> formParams) {
      StringBuilder formBody = new StringBuilder();
      formParams.forEach((key, value) -> {
         if (!formBody.isEmpty()) {
            formBody.append("&");
         }

         formBody.append(URLEncoder.encode(key, StandardCharsets.UTF_8));
         formBody.append("=");
         formBody.append(URLEncoder.encode(value, StandardCharsets.UTF_8));
      });
      return createRequest(url, builder, BodyPublishers.ofString(formBody.toString(), StandardCharsets.UTF_8), "application/x-www-form-urlencoded");
   }

   private static HttpRequest createRequest(String url, Function<Builder, Builder> builder, BodyPublisher bodyPublisher, String contentType) {
      Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(url)).header("Content-Type", contentType);
      requestBuilder = (Builder)builder.apply(requestBuilder);
      return requestBuilder.method(requestBuilder.build().method(), bodyPublisher).build();
   }

   private static JsonObject parseJsonResponse(HttpResponse<String> response) {
      return JsonParser.parseString((String)response.body()).getAsJsonObject();
   }

   public static <T> T parseInputStream(InputStream inputStream, Class<T> clazz) throws IOException {
      return CactusClient.GSON.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), clazz);
   }

   public static JsonElement requestJson(String url) {
      return fetchJson(url);
   }
}
