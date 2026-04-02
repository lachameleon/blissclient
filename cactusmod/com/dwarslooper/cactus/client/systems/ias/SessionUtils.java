package com.dwarslooper.cactus.client.systems.ias;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.mixins.accessor.MinecraftAccessor;
import com.dwarslooper.cactus.client.mixins.accessor.RealmsClientAccessor;
import com.dwarslooper.cactus.client.mixins.accessor.ReportingContextAccessor;
import com.dwarslooper.cactus.client.systems.ias.account.Auth;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.mixinterface.IRealmsPeriodicCheckers;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_156;
import net.minecraft.class_310;
import net.minecraft.class_320;
import net.minecraft.class_4341;
import net.minecraft.class_4844;
import net.minecraft.class_5520;
import net.minecraft.class_7497;
import net.minecraft.class_7574;
import net.minecraft.class_7853;

public class SessionUtils {
   public static void setSession(String accessToken) throws Exception {
      Entry<UUID, String> authData = Auth.getProfile(accessToken);
      setSession((String)authData.getValue(), (UUID)authData.getKey(), accessToken);
   }

   public static void setSession(String username, UUID uuid, String accessToken) {
      ((MinecraftAccessor)CactusConstants.mc).setUser(new class_320(username, uuid, accessToken, Optional.empty(), Optional.empty()));
      if (accessToken != null) {
         updateSession();
      }

   }

   public static void setOfflineSession(String username) {
      setSession(username, class_4844.method_43344(username), (String)null);
   }

   public static boolean isSessionOfflineMode() {
      return Objects.equals(CactusConstants.mc.method_1548().method_44717(), class_4844.method_43344(CactusConstants.mc.method_1548().method_1676()));
   }

   public static void updateSession() {
      IRCClient irc = CactusClient.getInstance().getIrcClient();
      irc.disconnect();
      String OFFLINE_TOKEN = "cactusmod.xyz";
      class_310 client = class_310.method_1551();
      class_320 session = CactusConstants.mc.method_1548();
      UserApiService userApiService = UserApiService.OFFLINE;
      if (!OFFLINE_TOKEN.equals(session.method_1674())) {
         YggdrasilAuthenticationService authService = createAuthService();
         ((MinecraftAccessor)CactusConstants.mc).setServices(class_7497.method_44143(authService, CactusConstants.mc.field_1697));
         userApiService = authService.createUserApiService(session.method_1674());
      }

      ((MinecraftAccessor)client).setUserApiService(userApiService);
      ((MinecraftAccessor)client).setPlayerSocialManager(new class_5520(client, userApiService));
      ((MinecraftAccessor)client).setProfileKeyPairManager(class_7853.method_46532(userApiService, session, client.field_1697.toPath()));
      ((MinecraftAccessor)client).setReportingContext(class_7574.method_44599(((ReportingContextAccessor)client.method_44377()).getEnvironment(), userApiService));
      ((MinecraftAccessor)client).setProfileFuture(CompletableFuture.supplyAsync(() -> {
         return getApiService().comp_837().fetchProfile(session.method_44717(), true);
      }, class_156.method_55473()));
      class_4341 realmsClient = ((IRealmsPeriodicCheckers)CactusConstants.mc.method_44646()).cactus$getClient();
      ((RealmsClientAccessor)realmsClient).setUsername(session.method_1676());
      ((RealmsClientAccessor)realmsClient).setSessionId(session.method_1675());
      CactusClient.getLogger().info("Minecraft session for {} (uuid={}) has been applied", session.method_1676(), session.method_44717());
      CactusClient.getLogger().info("Profile session for {} (uuid={}) has been applied", CactusConstants.mc.method_53462().name(), CactusConstants.mc.method_53462().id());
      irc.connect();
   }

   public static YggdrasilAuthenticationService createAuthService() {
      return new YggdrasilAuthenticationService(CactusConstants.mc.method_1487());
   }

   public static class_7497 getApiService() {
      return class_310.method_1551().method_73361();
   }
}
