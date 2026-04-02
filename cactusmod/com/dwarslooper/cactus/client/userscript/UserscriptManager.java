package com.dwarslooper.cactus.client.userscript;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.userscript.impl.ButtonModifier;
import com.dwarslooper.cactus.client.userscript.impl.ItemPreset;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class UserscriptManager {
   private static final HashMap<String, Userscript> scripts = new HashMap();

   public static HashMap<String, Userscript> getScripts() {
      return scripts;
   }

   public static void load(File file) throws FileNotFoundException {
      if (scripts.containsKey(file.getName())) {
         unload(file.getName());
      }

      FileReader reader = new FileReader(file);
      Userscript userscript = createMatching((JsonObject)CactusClient.GSON.fromJson(reader, JsonObject.class));
      if (userscript != null) {
         userscript.load();
         scripts.put(file.getName(), userscript);
      }

   }

   public static void unload(String name) {
      Userscript userscript = (Userscript)scripts.get(name);
      if (userscript != null) {
         userscript.unload();
         scripts.remove(name);
      }

   }

   public static Userscript createMatching(JsonObject object) {
      try {
         JsonObject properties = object.getAsJsonObject("properties");
         String type = properties.get("type").getAsString();
         String version = properties.get("version").getAsString();
         if (version.equals(CactusConstants.SCRIPT_VERSION)) {
            Userscript userscript = instance(type, object);
            userscript.init();
            return userscript;
         }
      } catch (Exception var5) {
         ChatUtils.error("Failed to load Userscript");
         ChatUtils.error(var5.getMessage());
      }

      return null;
   }

   public static Userscript instance(String type, JsonObject object) {
      byte var3 = -1;
      switch(type.hashCode()) {
      case -1377687758:
         if (type.equals("button")) {
            var3 = 3;
         }
         break;
      case -788047292:
         if (type.equals("widget")) {
            var3 = 2;
         }
         break;
      case -470197877:
         if (type.equals("item_preset")) {
            var3 = 0;
         }
         break;
      case 3242771:
         if (type.equals("item")) {
            var3 = 1;
         }
      }

      Object var10000;
      switch(var3) {
      case 0:
      case 1:
         var10000 = new ItemPreset(object);
         break;
      case 2:
      case 3:
         var10000 = new ButtonModifier(object);
         break;
      default:
         throw new NoSuchElementException("Invalid Type");
      }

      return (Userscript)var10000;
   }
}
