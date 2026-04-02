package com.dwarslooper.cactus.client.systems.ias.account;

import com.dwarslooper.cactus.client.systems.ias.AccountManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Auth {
   public static final String CLIENT_ID = "54fd49e4-2103-4044-9603-2b028c814ec3";
   public static final String REDIRECT_URI = "http://localhost:59125";

   public static Entry<String, String> codeToToken(String code) throws Exception {
      HttpURLConnection conn = (HttpURLConnection)(new URI("https://login.live.com/oauth20_token.srf")).toURL().openConnection();
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      conn.setRequestMethod("POST");
      conn.setConnectTimeout(15000);
      conn.setReadTimeout(15000);
      conn.setDoOutput(true);
      OutputStream out = conn.getOutputStream();

      SimpleImmutableEntry var5;
      try {
         String var10001 = URLEncoder.encode("54fd49e4-2103-4044-9603-2b028c814ec3", StandardCharsets.UTF_8);
         out.write(("client_id=" + var10001 + "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8) + "&grant_type=authorization_code&redirect_uri=" + URLEncoder.encode("http://localhost:59125", StandardCharsets.UTF_8) + "&scope=XboxLive.signin%20XboxLive.offline_access").getBytes(StandardCharsets.UTF_8));
         BufferedReader err;
         if (conn.getResponseCode() < 200 || conn.getResponseCode() > 299) {
            try {
               err = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));

               try {
                  int var10002 = conn.getResponseCode();
                  throw new IllegalArgumentException("codeToToken response: " + var10002 + ", data: " + (String)err.lines().collect(Collectors.joining("\n")));
               } catch (Throwable var10) {
                  try {
                     err.close();
                  } catch (Throwable var7) {
                     var10.addSuppressed(var7);
                  }

                  throw var10;
               }
            } catch (Exception var11) {
               throw new IllegalArgumentException("codeToToken response: " + conn.getResponseCode());
            }
         }

         err = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

         try {
            JsonObject resp = (JsonObject)AccountManager.GSON.fromJson((String)err.lines().collect(Collectors.joining("\n")), JsonObject.class);
            var5 = new SimpleImmutableEntry(resp.get("access_token").getAsString(), resp.get("refresh_token").getAsString());
         } catch (Throwable var9) {
            try {
               err.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         err.close();
      } catch (Throwable var12) {
         if (out != null) {
            try {
               out.close();
            } catch (Throwable var6) {
               var12.addSuppressed(var6);
            }
         }

         throw var12;
      }

      if (out != null) {
         out.close();
      }

      return var5;
   }

   public static Entry<String, String> refreshToken(String refreshToken) throws Exception {
      HttpURLConnection conn = (HttpURLConnection)(new URI("https://login.live.com/oauth20_token.srf")).toURL().openConnection();
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      conn.setRequestMethod("POST");
      conn.setConnectTimeout(15000);
      conn.setReadTimeout(15000);
      conn.setDoOutput(true);
      OutputStream out = conn.getOutputStream();

      SimpleImmutableEntry var5;
      try {
         String var10001 = URLEncoder.encode("54fd49e4-2103-4044-9603-2b028c814ec3", StandardCharsets.UTF_8);
         out.write(("client_id=" + var10001 + "&refresh_token=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8) + "&grant_type=refresh_token&redirect_uri=" + URLEncoder.encode("http://localhost:59125", StandardCharsets.UTF_8) + "&scope=XboxLive.signin%20XboxLive.offline_access").getBytes(StandardCharsets.UTF_8));
         BufferedReader err;
         if (conn.getResponseCode() < 200 || conn.getResponseCode() > 299) {
            try {
               err = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));

               try {
                  int var10002 = conn.getResponseCode();
                  throw new IllegalArgumentException("refreshToken response: " + var10002 + ", data: " + (String)err.lines().collect(Collectors.joining("\n")));
               } catch (Throwable var10) {
                  try {
                     err.close();
                  } catch (Throwable var8) {
                     var10.addSuppressed(var8);
                  }

                  throw var10;
               }
            } catch (Exception var11) {
               throw new IllegalArgumentException("refreshToken response: " + conn.getResponseCode());
            }
         }

         err = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

         try {
            JsonObject resp = (JsonObject)AccountManager.GSON.fromJson((String)err.lines().collect(Collectors.joining("\n")), JsonObject.class);
            var5 = new SimpleImmutableEntry(resp.get("access_token").getAsString(), resp.get("refresh_token").getAsString());
         } catch (Throwable var9) {
            try {
               err.close();
            } catch (Throwable var7) {
               var9.addSuppressed(var7);
            }

            throw var9;
         }

         err.close();
      } catch (Throwable var12) {
         if (out != null) {
            try {
               out.close();
            } catch (Throwable var6) {
               var12.addSuppressed(var6);
            }
         }

         throw var12;
      }

      if (out != null) {
         out.close();
      }

      return var5;
   }

   public static String authXBL(String authToken) throws Exception {
      HttpURLConnection conn = (HttpURLConnection)(new URI("https://user.auth.xboxlive.com/user/authenticate")).toURL().openConnection();
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty("Accept", "application/json");
      conn.setRequestMethod("POST");
      conn.setConnectTimeout(15000);
      conn.setReadTimeout(15000);
      conn.setDoOutput(true);
      OutputStream out = conn.getOutputStream();

      String var7;
      try {
         JsonObject req = new JsonObject();
         JsonObject reqProps = new JsonObject();
         reqProps.addProperty("AuthMethod", "RPS");
         reqProps.addProperty("SiteName", "user.auth.xboxlive.com");
         reqProps.addProperty("RpsTicket", "d=" + authToken);
         req.add("Properties", reqProps);
         req.addProperty("RelyingParty", "http://auth.xboxlive.com");
         req.addProperty("TokenType", "JWT");
         out.write(req.toString().getBytes(StandardCharsets.UTF_8));
         BufferedReader err;
         if (conn.getResponseCode() < 200 || conn.getResponseCode() > 299) {
            try {
               err = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));

               try {
                  int var10002 = conn.getResponseCode();
                  throw new IllegalArgumentException("authXBL response: " + var10002 + ", data: " + (String)err.lines().collect(Collectors.joining("\n")));
               } catch (Throwable var12) {
                  try {
                     err.close();
                  } catch (Throwable var9) {
                     var12.addSuppressed(var9);
                  }

                  throw var12;
               }
            } catch (Exception var13) {
               throw new IllegalArgumentException("authXBL response: " + conn.getResponseCode());
            }
         }

         err = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

         try {
            JsonObject resp = (JsonObject)AccountManager.GSON.fromJson((String)err.lines().collect(Collectors.joining("\n")), JsonObject.class);
            var7 = resp.get("Token").getAsString();
         } catch (Throwable var11) {
            try {
               err.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }

            throw var11;
         }

         err.close();
      } catch (Throwable var14) {
         if (out != null) {
            try {
               out.close();
            } catch (Throwable var8) {
               var14.addSuppressed(var8);
            }
         }

         throw var14;
      }

      if (out != null) {
         out.close();
      }

      return var7;
   }

   public static Entry<String, String> authXSTS(String xblToken) throws Exception {
      HttpURLConnection conn = (HttpURLConnection)(new URI("https://xsts.auth.xboxlive.com/xsts/authorize")).toURL().openConnection();
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty("Accept", "application/json");
      conn.setRequestMethod("POST");
      conn.setConnectTimeout(15000);
      conn.setReadTimeout(15000);
      conn.setDoOutput(true);
      OutputStream out = conn.getOutputStream();

      SimpleImmutableEntry var8;
      try {
         JsonObject req = new JsonObject();
         JsonObject reqProps = new JsonObject();
         JsonArray userTokens = new JsonArray();
         userTokens.add(xblToken);
         reqProps.add("UserTokens", userTokens);
         reqProps.addProperty("SandboxId", "RETAIL");
         req.add("Properties", reqProps);
         req.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
         req.addProperty("TokenType", "JWT");
         out.write(req.toString().getBytes(StandardCharsets.UTF_8));
         BufferedReader err;
         if (conn.getResponseCode() < 200 || conn.getResponseCode() > 299) {
            try {
               err = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));

               try {
                  int var10002 = conn.getResponseCode();
                  throw new IllegalArgumentException("authXSTS response: " + var10002 + ", data: " + (String)err.lines().collect(Collectors.joining("\n")));
               } catch (Throwable var13) {
                  try {
                     err.close();
                  } catch (Throwable var11) {
                     var13.addSuppressed(var11);
                  }

                  throw var13;
               }
            } catch (Exception var14) {
               throw new IllegalArgumentException("authXSTS response: " + conn.getResponseCode());
            }
         }

         err = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

         try {
            JsonObject resp = (JsonObject)AccountManager.GSON.fromJson((String)err.lines().collect(Collectors.joining("\n")), JsonObject.class);
            var8 = new SimpleImmutableEntry(resp.get("Token").getAsString(), resp.getAsJsonObject("DisplayClaims").getAsJsonArray("xui").get(0).getAsJsonObject().get("uhs").getAsString());
         } catch (Throwable var12) {
            try {
               err.close();
            } catch (Throwable var10) {
               var12.addSuppressed(var10);
            }

            throw var12;
         }

         err.close();
      } catch (Throwable var15) {
         if (out != null) {
            try {
               out.close();
            } catch (Throwable var9) {
               var15.addSuppressed(var9);
            }
         }

         throw var15;
      }

      if (out != null) {
         out.close();
      }

      return var8;
   }

   public static String authMinecraft(String userHash, String xstsToken) throws Exception {
      HttpURLConnection conn = (HttpURLConnection)(new URI("https://api.minecraftservices.com/authentication/login_with_xbox")).toURL().openConnection();
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty("Accept", "application/json");
      conn.setRequestMethod("POST");
      conn.setConnectTimeout(15000);
      conn.setReadTimeout(15000);
      conn.setDoOutput(true);
      OutputStream out = conn.getOutputStream();

      String var7;
      try {
         JsonObject req = new JsonObject();
         req.addProperty("identityToken", "XBL3.0 x=" + userHash + ";" + xstsToken);
         out.write(req.toString().getBytes(StandardCharsets.UTF_8));
         BufferedReader err;
         if (conn.getResponseCode() < 200 || conn.getResponseCode() > 299) {
            try {
               err = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));

               try {
                  int var10002 = conn.getResponseCode();
                  throw new IllegalArgumentException("authMinecraft response: " + var10002 + ", data: " + (String)err.lines().collect(Collectors.joining("\n")));
               } catch (Throwable var12) {
                  try {
                     err.close();
                  } catch (Throwable var10) {
                     var12.addSuppressed(var10);
                  }

                  throw var12;
               }
            } catch (Exception var13) {
               throw new IllegalArgumentException("authMinecraft response: " + conn.getResponseCode());
            }
         }

         err = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

         try {
            JsonObject resp = (JsonObject)AccountManager.GSON.fromJson((String)err.lines().collect(Collectors.joining("\n")), JsonObject.class);
            var7 = resp.get("access_token").getAsString();
         } catch (Throwable var11) {
            try {
               err.close();
            } catch (Throwable var9) {
               var11.addSuppressed(var9);
            }

            throw var11;
         }

         err.close();
      } catch (Throwable var14) {
         if (out != null) {
            try {
               out.close();
            } catch (Throwable var8) {
               var14.addSuppressed(var8);
            }
         }

         throw var14;
      }

      if (out != null) {
         out.close();
      }

      return var7;
   }

   public static Entry<UUID, String> getProfile(String accessToken) throws Exception {
      HttpURLConnection conn = (HttpURLConnection)(new URI("https://api.minecraftservices.com/minecraft/profile")).toURL().openConnection();
      conn.setRequestProperty("Authorization", "Bearer " + accessToken);
      conn.setConnectTimeout(15000);
      conn.setReadTimeout(15000);
      BufferedReader err;
      if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 299) {
         err = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

         SimpleImmutableEntry var4;
         try {
            JsonObject resp = (JsonObject)AccountManager.GSON.fromJson((String)err.lines().collect(Collectors.joining("\n")), JsonObject.class);
            var4 = new SimpleImmutableEntry(UUID.fromString(resp.get("id").getAsString().replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5")), resp.get("name").getAsString());
         } catch (Throwable var7) {
            try {
               err.close();
            } catch (Throwable var5) {
               var7.addSuppressed(var5);
            }

            throw var7;
         }

         err.close();
         return var4;
      } else {
         try {
            err = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));

            try {
               int var10002 = conn.getResponseCode();
               throw new IllegalArgumentException("getProfile response: " + var10002 + ", data: " + (String)err.lines().collect(Collectors.joining("\n")));
            } catch (Throwable var8) {
               try {
                  err.close();
               } catch (Throwable var6) {
                  var8.addSuppressed(var6);
               }

               throw var8;
            }
         } catch (Exception var9) {
            throw new IllegalArgumentException("getProfile response: " + conn.getResponseCode());
         }
      }
   }

   public static UUID resolveUUID(String name) {
      try {
         InputStreamReader in = new InputStreamReader((new URI("https://api.mojang.com/users/profiles/minecraft/" + name)).toURL().openStream(), StandardCharsets.UTF_8);

         UUID var2;
         try {
            var2 = UUID.fromString(((JsonObject)AccountManager.GSON.fromJson(in, JsonObject.class)).get("id").getAsString().replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
         } catch (Throwable var5) {
            try {
               in.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }

            throw var5;
         }

         in.close();
         return var2;
      } catch (Exception var6) {
         return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
      }
   }
}
