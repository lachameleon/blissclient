package com.dwarslooper.cactus.client.systems.ias;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.systems.config.ConfigHandler;
import com.dwarslooper.cactus.client.systems.config.FileConfiguration;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.dwarslooper.cactus.client.systems.ias.account.Account;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jetbrains.annotations.NotNull;

public class AccountManager extends FileConfiguration<AccountManager> {
   public static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor((r) -> {
      return new Thread(r, "IAS Executor");
   });
   public static final Gson GSON = new Gson();
   private static final List<Account> accountList = new ArrayList();
   private static Account originalSession;
   private static Account defaultAccount;

   public AccountManager(ConfigHandler handler) {
      super("accounts", handler);
   }

   public boolean exportAndImportByDefault() {
      return false;
   }

   public void filePreprocess(File file) {
      File legacy = new File(CactusConstants.DIRECTORY, "account_storage.json");
      if (legacy.exists()) {
         legacy.renameTo(file);
      }

   }

   public void init() {
      if (!SessionUtils.isSessionOfflineMode()) {
         originalSession = new Account(CactusConstants.mc.method_1548().method_1676(), CactusConstants.mc.method_1548().method_1674(), (String)null, CactusConstants.mc.method_1548().method_44717());
      }

   }

   private static Account loadAccount(JsonObject json) {
      return new Account(json.get("name").getAsString(), json.get("accessToken").getAsString(), json.get("refreshToken").getAsString(), UUID.fromString(json.get("uuid").getAsString()));
   }

   private JsonArray saveAccounts() {
      JsonArray array = new JsonArray();
      Iterator var2 = accountList.iterator();

      while(var2.hasNext()) {
         Account a = (Account)var2.next();
         JsonObject obj = this.saveAccount(a);
         array.add(obj);
      }

      return array;
   }

   private JsonObject saveAccount(@NotNull Account account) {
      JsonObject obj = new JsonObject();
      obj.addProperty("name", account.name());
      obj.addProperty("accessToken", account.accessToken());
      obj.addProperty("refreshToken", account.refreshToken());
      obj.addProperty("uuid", account.uuid().toString());
      return obj;
   }

   public JsonObject toJson(TreeSerializerFilter filter) {
      JsonObject object = new JsonObject();
      object.addProperty("important", "DO NOT SHARE THIS FILE WITH OTHERS! SHARING THIS FILE MIGHT GIVE SOMEONE ELSE ACCESS TO YOUR MINECRAFT ACCOUNT!");
      if (defaultAccount != null) {
         object.addProperty("defaultAccount", defaultAccount.uuid().toString());
      }

      object.add("accounts", this.saveAccounts());
      return object;
   }

   public AccountManager fromJson(JsonObject object) {
      accountList.clear();
      Iterator var2 = object.getAsJsonArray("accounts").iterator();

      while(var2.hasNext()) {
         JsonElement element = (JsonElement)var2.next();
         Account account = loadAccount(element.getAsJsonObject());
         accountList.add(account);
      }

      if (object.has("defaultAccount")) {
         String defaultAccountUUID = object.get("defaultAccount").getAsString();
         accountList.stream().filter((acc) -> {
            return acc.uuid().toString().equals(defaultAccountUUID);
         }).findFirst().ifPresentOrElse((accountx) -> {
            defaultAccount = accountx;
         }, () -> {
            CactusClient.getLogger().warn("Default account was not found in account list, resetting default account");
         });
      }

      return this;
   }

   public static void setDefaultAccount(Account account) {
      defaultAccount = account;
   }

   public static List<Account> getAccountList() {
      return accountList;
   }

   public static Account getOriginalSession() {
      return originalSession;
   }

   public static Account getDefaultAccount() {
      return defaultAccount;
   }
}
