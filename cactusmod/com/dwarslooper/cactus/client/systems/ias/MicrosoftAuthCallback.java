package com.dwarslooper.cactus.client.systems.ias;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.systems.ias.account.Account;
import com.dwarslooper.cactus.client.systems.ias.account.Auth;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.class_1074;

public class MicrosoftAuthCallback implements Closeable {
   public static final String MICROSOFT_AUTH_URL = "https://login.live.com/oauth20_authorize.srf?client_id=54fd49e4-2103-4044-9603-2b028c814ec3&response_type=code&scope=XboxLive.signin%20XboxLive.offline_access&redirect_uri=http://localhost:59125&prompt=select_account";
   private HttpServer server;
   private Consumer<String> progressHandler;
   private CompletableFuture<Account> completableFuture;
   private String done;

   public CompletableFuture<Account> start(Consumer<String> progressHandler, String done) {
      this.progressHandler = progressHandler;
      this.done = done;
      this.completableFuture = new CompletableFuture();

      try {
         this.server = HttpServer.create(new InetSocketAddress("localhost", 59125), 0);
         this.server.createContext("/", this::handleRequest);
         this.server.start();
         CactusClient.getLogger().info("Started Microsoft authentication callback server.");
      } catch (Exception var4) {
         CactusClient.getLogger().error("Unable to run the Microsoft authentication callback server.", var4);
         this.close();
         this.completableFuture.completeExceptionally(var4);
      }

      return this.completableFuture;
   }

   private void handleRequest(HttpExchange ex) {
      CactusClient.getLogger().info("Microsoft authentication callback request: {}", ex.getRemoteAddress());

      try {
         BufferedReader in = new BufferedReader(new InputStreamReader((InputStream)Objects.requireNonNull(MicrosoftAuthCallback.class.getResourceAsStream("/assets/cactus/authFinished.html")), StandardCharsets.UTF_8));
         this.progressHandler.accept("Preparing");
         CactusClient.getLogger().info("Done: {}", this.done);
         byte[] b = ((String)in.lines().collect(Collectors.joining("\n"))).replace("%response%", this.done).replace("%access_notice_text%", class_1074.method_4662("gui.screen.add_account.accessNotice", new Object[0])).replace("%access_notice_link%", class_1074.method_4662("gui.screen.add_account.accessNoticeLink", new Object[0])).replace("%complete%", class_1074.method_4662("gui.screen.add_account.complete", new Object[0])).getBytes(StandardCharsets.UTF_8);
         ex.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
         ex.sendResponseHeaders(307, (long)b.length);
         OutputStream os = ex.getResponseBody();

         try {
            os.write(b);
         } catch (Throwable var8) {
            if (os != null) {
               try {
                  os.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (os != null) {
            os.close();
         }

         this.close();
         AccountManager.EXECUTOR.execute(() -> {
            try {
               this.completableFuture.complete(this.auth(this.progressHandler, ex.getRequestURI().getQuery()));
            } catch (Exception var3) {
               CactusClient.getLogger().error("Unable to authenticate via Microsoft.", var3);
               this.completableFuture.completeExceptionally(var3);
            }

         });
      } catch (Exception var9) {
         CactusClient.getLogger().error("Unable to process request on Microsoft authentication callback server.", var9);
         this.close();
         this.completableFuture.completeExceptionally(var9);
      }

   }

   private Account auth(Consumer<String> progressHandler, String query) throws Exception {
      CactusClient.getLogger().info("Authenticating...");
      if (query == null) {
         throw new NullPointerException("query must not be null");
      } else if (query.equals("error=access_denied&error_description=The user has denied access to the scope requested by the client application.")) {
         return null;
      } else if (!query.startsWith("code=")) {
         throw new IllegalStateException("query=" + query);
      } else {
         progressHandler.accept("codeToToken");
         Entry<String, String> authRefreshTokens = Auth.codeToToken(query.replace("code=", ""));
         String refreshToken = (String)authRefreshTokens.getValue();
         progressHandler.accept("authXBL");
         String xblToken = Auth.authXBL((String)authRefreshTokens.getKey());
         progressHandler.accept("authXSTS");
         Entry<String, String> xstsTokenUserhash = Auth.authXSTS(xblToken);
         progressHandler.accept("authMinecraft");
         String accessToken = Auth.authMinecraft((String)xstsTokenUserhash.getValue(), (String)xstsTokenUserhash.getKey());
         progressHandler.accept("getProfile");
         Entry<UUID, String> profile = Auth.getProfile(accessToken);
         CactusClient.getLogger().info("Authenticated.");
         return new Account((String)profile.getValue(), accessToken, refreshToken, (UUID)profile.getKey());
      }
   }

   public void close() {
      try {
         if (this.server != null) {
            this.server.stop(0);
            CactusClient.getLogger().info("Stopped Microsoft authentication callback server.");
         }
      } catch (Exception var2) {
         CactusClient.getLogger().error("Unable to stop the Microsoft authentication callback server.", var2);
      }

   }
}
