package com.dwarslooper.cactus.client.feature.macro;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.systems.config.ConfigHandler;
import com.dwarslooper.cactus.client.systems.config.FileConfiguration;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.dwarslooper.cactus.client.systems.key.KeyBind;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class MacroManager extends FileConfiguration<MacroManager> {
   private final List<Macro> macros = new ArrayList();

   public static MacroManager get() {
      return (MacroManager)CactusClient.getConfig(MacroManager.class);
   }

   public MacroManager(ConfigHandler handler) {
      super("macros", handler);
   }

   public List<Macro> getMacros() {
      return this.macros;
   }

   public Optional<Macro> getMacro(String name) {
      return this.macros.stream().filter((macro) -> {
         return macro.getName().equalsIgnoreCase(name);
      }).findFirst();
   }

   public boolean addIfAbsent(Macro macro) {
      if (this.canAdd(macro.getName())) {
         this.macros.add(macro);
         return true;
      } else {
         return false;
      }
   }

   public boolean canAdd(String name) {
      return this.macros.stream().noneMatch((m) -> {
         return m.getName().equalsIgnoreCase(name);
      });
   }

   public boolean removeMacro(Macro macro) {
      return this.macros.remove(macro);
   }

   public JsonObject toJson(TreeSerializerFilter filter) {
      JsonObject object = new JsonObject();
      this.getMacros().forEach((macro) -> {
         JsonObject obj = new JsonObject();
         obj.addProperty("bind", macro.getKeyBinding().key());
         obj.addProperty("enabled", macro.isEnabled());
         JsonArray actions = new JsonArray();
         macro.getActions().forEach((action) -> {
            JsonObject actionData = new JsonObject();
            actionData.addProperty("type", action.type().name());
            actionData.addProperty("data", action.data().toString());
            actions.add(actionData);
         });
         obj.add("actions", actions);
         object.add(macro.getName(), obj);
      });
      return object;
   }

   public MacroManager fromJson(JsonObject object) {
      Iterator var2 = object.keySet().iterator();

      while(var2.hasNext()) {
         String key = (String)var2.next();
         JsonObject obj = object.getAsJsonObject(key);
         List<Action<?>> actions = new ArrayList();
         obj.getAsJsonArray("actions").asList().forEach((action) -> {
            actions.add(new Action(action.getAsJsonObject().get("data").getAsString(), Action.Type.valueOf(action.getAsJsonObject().get("type").getAsString())));
         });
         this.addIfAbsent(new Macro(key, KeyBind.of(obj.get("bind").getAsInt()), actions, obj.get("enabled").getAsBoolean()));
      }

      return this;
   }
}
