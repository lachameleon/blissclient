package com.dwarslooper.cactus.client.feature.content;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.feature.command.Command;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.gui.screen.impl.HeadBrowserScreen;
import com.dwarslooper.cactus.client.systems.config.ConfigHandler;
import com.dwarslooper.cactus.client.systems.config.FileConfiguration;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import com.dwarslooper.cactus.client.util.generic.CallerSensitive;
import com.dwarslooper.cactus.client.util.mixinterface.IBackgroundAccessImpl;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.class_1802;

public class ContentPackManager extends FileConfiguration<ContentPackManager> {
   private final Map<String, ContentPack> contentPacks = new LinkedHashMap();

   public static ContentPackManager get() {
      return (ContentPackManager)CactusClient.getConfig(ContentPackManager.class);
   }

   public void init() {
      this.registerPack(new ContentPack("social", ContentPack.ActivationPolicy.DEFAULT_ENABLED, class_1802.field_8674));
      this.registerPack(new ContentPack("no_realms", ContentPack.ActivationPolicy.DEFAULT_DISABLED, class_1802.field_8687));
      this.registerPack(new ContentPack("redstone", ContentPack.ActivationPolicy.DEFAULT_ENABLED, class_1802.field_8725));
      this.registerPack(new ContentPack("server_security", ContentPack.ActivationPolicy.DEFAULT_ENABLED, class_1802.field_16315));
      this.registerPack(new ContentPack("dark_mode", ContentPack.ActivationPolicy.DEFAULT_DISABLED, class_1802.field_27019, (pack) -> {
         RenderUtils.darkMode = pack.isEnabled();
      }));
      this.registerPack(new ContentPack("title_screen", ContentPack.ActivationPolicy.DEFAULT_ENABLED, class_1802.field_8892, (pack) -> {
         if (CactusConstants.mc.field_1755 != null) {
            ((IBackgroundAccessImpl)CactusConstants.mc.field_1755).cactus$updateBackground();
         }

      }));
      this.registerPack(new ContentPack("creative_tools", ContentPack.ActivationPolicy.DEFAULT_ENABLED, class_1802.field_8688));
      this.registerPack(new ContentPack("world_hosting", ContentPack.ActivationPolicy.DEFAULT_ENABLED, class_1802.field_8207));
      this.registerPack(new ContentPack("head_database", ContentPack.ActivationPolicy.DEFAULT_DISABLED, class_1802.field_8398, (pack) -> {
         if (pack.isEnabled()) {
            HeadBrowserScreen.showWarning();
         }

      }));
      this.registerPack(new ContentPack("customize", ContentPack.ActivationPolicy.DEFAULT_DISABLED, class_1802.field_42716));
      this.registerPack(new ContentPack("smooth_animations", ContentPack.ActivationPolicy.DEFAULT_DISABLED, class_1802.field_8153));
      getMainRegistryBus().completeAndTake(ContentPack.class, (contentPack, addon) -> {
         contentPack.setOwner(addon);
         this.registerPack(contentPack);
      });
   }

   public ContentPackManager(ConfigHandler handler) {
      super("content_packs", handler);
      this.contentPacks.put("cactus", new ContentPack("cactus", ContentPack.ActivationPolicy.ALWAYS_ENABLED, class_1802.field_17520));
   }

   public void registerPack(ContentPack contentPack) {
      if (!this.contentPacks.containsKey(contentPack.getId())) {
         if (this.isFirstInit() && contentPack.getActivationPolicy() == ContentPack.ActivationPolicy.DEFAULT_ENABLED) {
            contentPack.setEnabled(true);
         }

         this.contentPacks.put(contentPack.getId(), contentPack);
      } else {
         CactusClient.getLogger().error("Can't register content pack '{}'; ID is already in use", contentPack.getId());
      }

   }

   public boolean isEnabled(Class<?> clazz) {
      if (clazz == null) {
         return false;
      } else if (clazz.isAnnotationPresent(ContentPackDependent.class)) {
         ContentPackDependent a = (ContentPackDependent)clazz.getAnnotation(ContentPackDependent.class);
         return this.isEnabled(this.ofId(a.value()));
      } else {
         return true;
      }
   }

   @CallerSensitive
   public static boolean isEnabled() {
      return get().isEnabled(Utils.getDirectCallerClass());
   }

   /** @deprecated */
   @Deprecated
   public boolean isEnabled(Module module) {
      return this.isEnabled(module.getClass());
   }

   /** @deprecated */
   @Deprecated
   public boolean isEnabled(Command command) {
      return this.isEnabled(command.getClass());
   }

   public boolean isEnabled(ContentPack contentPack) {
      return contentPack == null || contentPack.isEnabled();
   }

   public ContentPack ofId(String id) {
      return (ContentPack)this.contentPacks.get(id);
   }

   public Map<String, ContentPack> getContentPacks() {
      return this.contentPacks;
   }

   public JsonObject toJson(TreeSerializerFilter filter) {
      JsonObject object = new JsonObject();
      this.getContentPacks().values().forEach((contentPack) -> {
         if (contentPack.getActivationPolicy() != ContentPack.ActivationPolicy.ALWAYS_ENABLED) {
            object.addProperty(contentPack.getId(), contentPack.isEnabled());
         }
      });
      return object;
   }

   public ContentPackManager fromJson(JsonObject object) {
      Iterator var2 = object.keySet().iterator();

      while(var2.hasNext()) {
         String name = (String)var2.next();
         ContentPack pack = this.ofId(name);
         if (pack != null) {
            pack.setEnabled(object.get(name).getAsBoolean());
         } else {
            CactusClient.getLogger().error("Tried to load missing content pack '{}'", name);
         }
      }

      return this;
   }
}
