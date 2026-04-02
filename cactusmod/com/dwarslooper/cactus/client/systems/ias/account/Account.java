package com.dwarslooper.cactus.client.systems.ias.account;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.systems.ias.AccountManager;
import com.dwarslooper.cactus.client.systems.ias.skins.SkinHelper;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.class_8685;
import org.jetbrains.annotations.NotNull;

public class Account {
   private String name;
   private String accessToken;
   private String refreshToken;
   private UUID uuid;

   public Account(String name, String accessToken, String refreshToken, @NotNull UUID uuid) {
      this.name = name;
      this.accessToken = accessToken;
      this.refreshToken = refreshToken;
      this.uuid = uuid;
   }

   public UUID uuid() {
      return this.uuid;
   }

   public String name() {
      return this.name;
   }

   public class_8685 skin() {
      return SkinHelper.getCachedSkinOrFetch(this.uuid);
   }

   public String accessToken() {
      return this.accessToken;
   }

   public String refreshToken() {
      return this.refreshToken;
   }

   public CompletableFuture<Account> login(Consumer<String> progressHandler) {
      CompletableFuture<Account> cf = new CompletableFuture();
      AccountManager.EXECUTOR.execute(() -> {
         try {
            this.refresh(progressHandler);
            cf.complete(this);
         } catch (Exception var4) {
            CactusClient.getLogger().error("Unable to login/refresh Microsoft account.", var4);
            cf.completeExceptionally(var4);
         }

      });
      return cf;
   }

   public CompletableFuture<Account> login() {
      return this.login((s) -> {
      });
   }

   private void refresh(Consumer<String> progressHandler) throws Exception {
      try {
         CactusClient.getLogger().info("Refreshing...");
         progressHandler.accept("getProfile");
         Entry<UUID, String> profile = Auth.getProfile(this.accessToken);
         CactusClient.getLogger().info("Access token is valid.");
         this.uuid = (UUID)profile.getKey();
         this.name = (String)profile.getValue();
      } catch (Exception var10) {
         if (this.refreshToken == null) {
            progressHandler.accept("§cUnable to refresh token.");
            return;
         }

         try {
            progressHandler.accept("refreshToken");
            Entry<String, String> authRefreshTokens = Auth.refreshToken(this.refreshToken);
            String refreshToken = (String)authRefreshTokens.getValue();
            progressHandler.accept("authXbl");
            String xblToken = Auth.authXBL((String)authRefreshTokens.getKey());
            progressHandler.accept("authXSTS");
            Entry<String, String> xstsTokenUserhash = Auth.authXSTS(xblToken);
            progressHandler.accept("authMinecraft");
            String accessToken = Auth.authMinecraft((String)xstsTokenUserhash.getValue(), (String)xstsTokenUserhash.getKey());
            progressHandler.accept("getProfile");
            Entry<UUID, String> profile = Auth.getProfile(accessToken);
            CactusClient.getLogger().info("Refreshed.");
            this.uuid = (UUID)profile.getKey();
            this.name = (String)profile.getValue();
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
         } catch (Exception var9) {
            var9.addSuppressed(var10);
            throw var9;
         }
      }

   }

   public boolean equals(Object obj) {
      boolean var10000;
      if (obj instanceof Account) {
         Account account = (Account)obj;
         if (account.uuid().equals(this.uuid())) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }
}
