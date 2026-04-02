package com.dwarslooper.cactus.client.feature.module;

import com.dwarslooper.cactus.client.gui.hud.SimpleHudManager;
import com.dwarslooper.cactus.client.gui.screen.impl.CheatWarningScreen;
import com.dwarslooper.cactus.client.systems.ISerializable;
import com.dwarslooper.cactus.client.systems.UpdateReason;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.systems.config.CactusSystemConfig;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.dwarslooper.cactus.client.systems.config.settings.group.SettingGroup;
import com.dwarslooper.cactus.client.systems.config.settings.gui.SettingContainer;
import com.dwarslooper.cactus.client.systems.key.KeyBind;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.INamespaced;
import com.google.gson.JsonObject;
import java.util.EnumMap;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.class_1074;
import net.minecraft.class_2477;
import net.minecraft.class_2561;

public abstract class Module implements ISerializable<Module>, INamespaced {
   private final String id;
   private final Category category;
   private final Module.Options options;
   public final SettingContainer settings;
   public final SettingGroup mainGroup;
   private KeyBind bind;
   private boolean toggleOnRelease;
   private boolean favorite;
   private boolean active;

   public Module(String id) {
      this(id, (Category)null);
   }

   public Module(String id, Category category) {
      this(id, category, new Module.Options());
   }

   public Module(String id, Category category, Module.Options options) {
      this.bind = KeyBind.none();
      this.id = id;
      this.category = category;
      this.options = options;
      this.settings = new SettingContainer(this.getNamespace());
      this.mainGroup = this.settings.getDefault();
      this.regHud();
   }

   public void regHud() {
      SimpleHudManager.get().registerElement(() -> {
         return this.enableHud() ? this.getDisplayName() : null;
      });
      Supplier<String> hud = this.getHud();
      if (hud != null) {
         SimpleHudManager.get().registerElement(() -> {
            return this.enableHud() ? (String)this.getHud().get() : null;
         });
      }

   }

   public Supplier<String> getHud() {
      return null;
   }

   public String getDisplayName() {
      return class_2477.method_10517().method_4679(this.getNamespace() + ".name", this.id);
   }

   public String getDescription() {
      return class_2477.method_10517().method_4679(this.getNamespace() + ".description", (String)null);
   }

   public String getID() {
      return this.id.toLowerCase().replace("_", "");
   }

   public KeyBind getBind() {
      return this.bind;
   }

   public Category getCategory() {
      return this.category;
   }

   public void setBind(KeyBind bind) {
      this.bind = bind;
   }

   public void toggle() {
      this.active(!this.active());
   }

   public boolean sendsToggleFeedback() {
      return true;
   }

   public Module active(boolean active) {
      return this.active(active, UpdateReason.USER_INPUT_EXPECT_FEEDBACK);
   }

   public Module active(boolean active, UpdateReason updateReason) {
      if (active && updateReason != UpdateReason.UPDATE_STATE_FROM_CONFIG && this.getOption(Module.Flag.SERVER_UNSAFE) && CactusConstants.mc.field_1772 != null && !CactusSystemConfig.skipModuleCheatWarning && !(CactusConstants.mc.field_1755 instanceof CheatWarningScreen)) {
         CactusConstants.mc.method_1507(new CheatWarningScreen(CactusConstants.mc.field_1755, class_2561.method_43471("gui.screen.unsafeWarning.unsafeModule"), this::active));
         return this;
      } else {
         this.active = active;
         if (this.sendsToggleFeedback() && updateReason.showsFeedback()) {
            ModuleManager.sendToggleFeedback(this);
         }

         if (this.getOption(Module.Flag.RUN_IN_MENU) || CactusConstants.mc.field_1687 != null && CactusConstants.mc.method_53466()) {
            ModuleManager.updateState(this, active);
         }

         return this;
      }
   }

   public boolean active() {
      return this.active;
   }

   public boolean shouldToggleOnBindRelease() {
      return this.toggleOnRelease;
   }

   public void setToggleOnRelease(boolean toggleOnRelease) {
      this.toggleOnRelease = toggleOnRelease;
   }

   public void setFavorite(boolean favorite) {
      this.favorite = favorite;
   }

   public boolean isFavorite() {
      return this.favorite;
   }

   public void onEnable() {
   }

   public void onDisable() {
   }

   public boolean experiment() {
      return this.getOption(Module.Flag.EXPERIMENTAL) && (Boolean)CactusSettings.get().experiments.get();
   }

   public Module fromJson(JsonObject object) {
      this.active(object.get("active").getAsBoolean(), UpdateReason.UPDATE_STATE_FROM_CONFIG);
      if (object.has("bind")) {
         this.setBind(KeyBind.of(object.get("bind").getAsInt()));
      }

      if (object.has("toggleOnRelease")) {
         this.toggleOnRelease = object.get("toggleOnRelease").getAsBoolean();
      }

      if (object.has("favorite")) {
         this.favorite = object.get("favorite").getAsBoolean();
      }

      this.settings.fromJson(object.getAsJsonObject("settings"));
      return this;
   }

   public JsonObject toJson(TreeSerializerFilter filter) {
      JsonObject object = new JsonObject();
      object.addProperty("active", this.active());
      object.addProperty("toggleOnRelease", this.toggleOnRelease);
      object.addProperty("favorite", this.favorite);
      Objects.requireNonNull(object);
      BiConsumer var10002 = object::add;
      SettingContainer var10003 = this.settings;
      Objects.requireNonNull(var10003);
      filter.checkSerializeAndPass("settings", var10002, var10003::toJson);
      if (this.getBind().isBound()) {
         object.addProperty("bind", this.getBind().key());
      }

      return object;
   }

   public String getNamespace() {
      return "modules." + this.id;
   }

   public String translate(String key, Object... args) {
      return class_1074.method_4662(this.getNamespace() + "." + key, args);
   }

   private boolean enableHud() {
      return this.active() && (Boolean)SimpleHudManager.get().getHud().showModules.get() && this.getOption(Module.Flag.HUD_LISTED);
   }

   public boolean getOption(Module.Flag flag) {
      return this.options.get(flag);
   }

   public static class Options {
      private final EnumMap<Module.Flag, Boolean> options = new EnumMap(Module.Flag.class);

      public Module.Options set(Module.Flag flag, boolean b) {
         this.options.put(flag, b);
         return this;
      }

      public boolean get(Module.Flag flag) {
         return (Boolean)this.options.getOrDefault(flag, flag.getDefault());
      }
   }

   public static enum Flag {
      SERVER_UNSAFE(false),
      HUD_LISTED(true),
      EXPERIMENTAL(false),
      RUN_IN_MENU(false);

      private final boolean defaultValue;

      private Flag(boolean defaultValue) {
         this.defaultValue = defaultValue;
      }

      public boolean getDefault() {
         return this.defaultValue;
      }

      // $FF: synthetic method
      private static Module.Flag[] $values() {
         return new Module.Flag[]{SERVER_UNSAFE, HUD_LISTED, EXPERIMENTAL, RUN_IN_MENU};
      }
   }
}
