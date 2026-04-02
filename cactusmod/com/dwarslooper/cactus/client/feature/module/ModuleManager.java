package com.dwarslooper.cactus.client.feature.module;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.WorldJoinedEvent;
import com.dwarslooper.cactus.client.event.impl.WorldLeftEvent;
import com.dwarslooper.cactus.client.feature.modules.redstone.AntiContainerDrop;
import com.dwarslooper.cactus.client.feature.modules.redstone.AutoRepeater;
import com.dwarslooper.cactus.client.feature.modules.redstone.AutoTop;
import com.dwarslooper.cactus.client.feature.modules.redstone.BUDDetector;
import com.dwarslooper.cactus.client.feature.modules.redstone.FluidMarker;
import com.dwarslooper.cactus.client.feature.modules.render.AmbientTweaks;
import com.dwarslooper.cactus.client.feature.modules.render.BetterTooltips;
import com.dwarslooper.cactus.client.feature.modules.render.BlockOutlines;
import com.dwarslooper.cactus.client.feature.modules.render.CustomCrosshair;
import com.dwarslooper.cactus.client.feature.modules.render.Fullbright;
import com.dwarslooper.cactus.client.feature.modules.render.HUD;
import com.dwarslooper.cactus.client.feature.modules.render.ItemCount;
import com.dwarslooper.cactus.client.feature.modules.render.ItemHud;
import com.dwarslooper.cactus.client.feature.modules.render.NameTags;
import com.dwarslooper.cactus.client.feature.modules.render.PlayerHider;
import com.dwarslooper.cactus.client.feature.modules.render.RenderTweaks;
import com.dwarslooper.cactus.client.feature.modules.render.ServerWidget;
import com.dwarslooper.cactus.client.feature.modules.render.StreamerMode;
import com.dwarslooper.cactus.client.feature.modules.render.TNTTimer;
import com.dwarslooper.cactus.client.feature.modules.render.ViewTweaks;
import com.dwarslooper.cactus.client.feature.modules.render.Waypoints;
import com.dwarslooper.cactus.client.feature.modules.render.Zoom;
import com.dwarslooper.cactus.client.feature.modules.util.AirPlace;
import com.dwarslooper.cactus.client.feature.modules.util.AntiAdvertise;
import com.dwarslooper.cactus.client.feature.modules.util.AntiForcePack;
import com.dwarslooper.cactus.client.feature.modules.util.AutoCrafter;
import com.dwarslooper.cactus.client.feature.modules.util.AutoReconnect;
import com.dwarslooper.cactus.client.feature.modules.util.CameraMode;
import com.dwarslooper.cactus.client.feature.modules.util.ChatTweaks;
import com.dwarslooper.cactus.client.feature.modules.util.CopyBlockState;
import com.dwarslooper.cactus.client.feature.modules.util.CrashPreventor;
import com.dwarslooper.cactus.client.feature.modules.util.DiscordPresence;
import com.dwarslooper.cactus.client.feature.modules.util.EmoteQuickSelector;
import com.dwarslooper.cactus.client.feature.modules.util.FarmUtils;
import com.dwarslooper.cactus.client.feature.modules.util.FlySpeed;
import com.dwarslooper.cactus.client.feature.modules.util.FreeLook;
import com.dwarslooper.cactus.client.feature.modules.util.InventoryTabs;
import com.dwarslooper.cactus.client.feature.modules.util.QuitConfirm;
import com.dwarslooper.cactus.client.feature.modules.util.Replace;
import com.dwarslooper.cactus.client.feature.modules.util.StateDisplay;
import com.dwarslooper.cactus.client.feature.modules.util.Ticker;
import com.dwarslooper.cactus.client.feature.modules.util.ToolBreakWarning;
import com.dwarslooper.cactus.client.feature.modules.util.TransferConfirm;
import com.dwarslooper.cactus.client.gui.toast.internal.CToast;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.systems.config.ConfigHandler;
import com.dwarslooper.cactus.client.systems.config.FileConfiguration;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.dwarslooper.cactus.client.util.generic.TextUtils;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_374;
import net.minecraft.class_5250;
import net.minecraft.class_368.class_369;
import org.jetbrains.annotations.NotNull;

public class ModuleManager extends FileConfiguration<ModuleManager> {
   public static Category CATEGORY_UTILITY;
   public static Category CATEGORY_REDSTONE;
   public static Category CATEGORY_RENDERING;
   private final Map<Class<? extends Module>, Module> modules = new LinkedHashMap();
   private final List<Category> categories;

   public static ModuleManager get() {
      return (ModuleManager)CactusClient.getConfig(ModuleManager.class);
   }

   public void init() {
      CactusClient.EVENT_BUS.subscribe(this);
      this.registerCategory(CATEGORY_UTILITY);
      this.registerCategory(CATEGORY_REDSTONE);
      this.registerCategory(CATEGORY_RENDERING);
      getMainRegistryBus().completeAndTake(Category.class, this::registerCategory);
      this.registerModule(new Fullbright());
      this.registerModule(new FreeLook());
      this.registerModule(new ChatTweaks());
      this.registerModule(new HUD());
      this.registerModule(new Zoom());
      this.registerModule(new BlockOutlines());
      this.registerModule(new CustomCrosshair());
      this.registerModule(new FarmUtils());
      this.registerModule(new BetterTooltips());
      this.registerModule(new AirPlace());
      this.registerModule(new Replace());
      this.registerModule(new ViewTweaks());
      this.registerModule(new AmbientTweaks());
      this.registerModule(new NameTags());
      this.registerModule(new StreamerMode());
      this.registerModule(new AutoTop());
      this.registerModule(new CopyBlockState());
      this.registerModule(new AntiAdvertise());
      this.registerModule(new ServerWidget());
      this.registerModule(new ItemCount());
      this.registerModule(new TNTTimer());
      this.registerModule(new FluidMarker());
      this.registerModule(new BUDDetector());
      this.registerModule(new AntiForcePack());
      this.registerModule(new PlayerHider());
      this.registerModule(new CameraMode());
      this.registerModule(new QuitConfirm());
      this.registerModule(new FlySpeed());
      this.registerModule(new Waypoints());
      this.registerModule(new InventoryTabs());
      this.registerModule(new AntiContainerDrop());
      this.registerModule(new DiscordPresence());
      this.registerModule(new AutoReconnect());
      this.registerModule(new TransferConfirm());
      this.registerModule(new ToolBreakWarning());
      this.registerModule(new RenderTweaks());
      this.registerModule(new CrashPreventor());
      this.registerModule(new EmoteQuickSelector());
      this.registerModule(new StateDisplay());
      this.registerModule(new ItemHud());
      this.registerModule(new AutoRepeater());
      this.registerModule(new AutoCrafter());
      this.registerModule(new Ticker());
      getMainRegistryBus().completeAndTake(Module.class, this::registerModule);
      if (this.isFirstInit()) {
         List.of(DiscordPresence.class, BetterTooltips.class, ChatTweaks.class, HUD.class).forEach((clazz) -> {
            this.get(clazz).active(true);
         });
      }

   }

   public ModuleManager(ConfigHandler handler) {
      super("modules", handler);
      this.categories = new ArrayList(List.of(Category.ALL, Category.FAVORITES));
   }

   public void registerModule(Module module) {
      this.modules.put(module.getClass(), module);
   }

   public void registerCategory(Category category) {
      this.categories.add(category);
   }

   public Map<Class<? extends Module>, Module> getModules() {
      return this.modules;
   }

   public List<Category> getCategories() {
      return this.categories;
   }

   public Category getCategory(String name) {
      return (Category)this.getCategories().stream().filter((category) -> {
         return category.getName().equalsIgnoreCase(name);
      }).findFirst().orElse((Object)null);
   }

   public <T extends Module> T get(Class<T> clazz) {
      return (Module)this.modules.get(clazz);
   }

   public <T extends Module> boolean active(Class<T> clazz) {
      return this.get(clazz).active();
   }

   public String formatToID(String input) {
      return input.toLowerCase(Locale.ROOT).replace(" ", "").replaceAll("[^a-zA-Z0-9]", "");
   }

   public Module get(String name) {
      return (Module)this.getModules().values().stream().filter((module) -> {
         return module.getID().equalsIgnoreCase(name);
      }).findFirst().orElse((Object)null);
   }

   public static void sendToggleFeedback(Module module) {
      if (CactusConstants.mc.method_53466()) {
         class_2561 state = class_2561.method_43469("module.toggleFeedback", new Object[]{module.getDisplayName(), TextUtils.boolAsText(module.active())});
         switch((CactusSettings.ToggleFeedback)CactusSettings.get().toggleFeedback.get()) {
         case Chat:
            ChatUtils.info((class_2561)state);
            break;
         case Actionbar:
            ChatUtils.actionbar((class_2561)state);
            break;
         case Toast:
            CToast toast = new CToast() {
               private final class_5250 text = class_2561.method_43470(module.getDisplayName()).method_27693(" » ").method_10852(TextUtils.boolAsText(module.active()));
               private final int width;

               {
                  this.width = CactusConstants.mc.field_1772.method_27525(this.text) + 8;
               }

               public void drawToast(class_332 context, class_327 textRenderer, long time, int x, int y, double mouseX, double mouseY) {
                  context.method_51738(0, this.method_29049(), 0, -14737633);
                  context.method_51738(0, this.method_29049(), this.method_29050() - 1, -14737633);
                  context.method_51742(0, 0, this.method_29050() - 1, -14737633);
                  context.method_51742(this.method_29049() - 1, 0, this.method_29050() - 1, -14737633);
                  context.method_25294(1, 1, this.method_29049() - 1, this.method_29050() - 1, 1998528287);
                  class_327 var10001 = CactusConstants.mc.field_1772;
                  class_5250 var10002 = this.text;
                  int var10004 = this.method_29050();
                  Objects.requireNonNull(CactusConstants.mc.field_1772);
                  context.method_27535(var10001, var10002, 4, (var10004 - 9) / 2 + 1, -1);
               }

               public void method_61989(@NotNull class_374 manager, long time) {
                  this.visibility = time < this.duration && !this.shouldClose() ? class_369.field_2210 : class_369.field_2209;
               }

               public boolean mouseClicked(double mouseX, double mouseY, int button) {
                  this.close();
                  return true;
               }

               public int method_29050() {
                  Objects.requireNonNull(CactusConstants.mc.field_1772);
                  return 9 + 6;
               }

               public int method_29049() {
                  return this.width;
               }
            };
            toast.duration = 1000L;
            CactusConstants.mc.method_1566().method_1999(toast);
         case None:
         }

      }
   }

   @EventHandler
   public void joinWorld(WorldJoinedEvent event) {
      this.getModules().values().forEach((module) -> {
         if (module.active() && !module.getOption(Module.Flag.RUN_IN_MENU)) {
            updateState(module, true);
         }

      });
   }

   @EventHandler
   public void leaveWorld(WorldLeftEvent event) {
      this.getModules().values().forEach((module) -> {
         if (module.active() && !module.getOption(Module.Flag.RUN_IN_MENU)) {
            updateState(module, false);
         }

      });
   }

   public JsonObject toJson(TreeSerializerFilter filter) {
      JsonObject object = new JsonObject();
      Iterator var3 = this.modules.values().iterator();

      while(var3.hasNext()) {
         Module module = (Module)var3.next();
         if (module != null) {
            String var10001 = module.getID();
            Objects.requireNonNull(object);
            BiConsumer var10002 = object::add;
            Objects.requireNonNull(module);
            filter.checkSerializeAndPass(var10001, var10002, module::toJson);
         }
      }

      return object;
   }

   public ModuleManager fromJson(JsonObject object) {
      Iterator var2 = this.modules.values().iterator();

      while(var2.hasNext()) {
         Module module = (Module)var2.next();
         if (module != null && object.has(module.getID())) {
            module.fromJson(object.getAsJsonObject(module.getID()));
         }
      }

      return this;
   }

   protected static void updateState(Module module, boolean enabled) {
      if (enabled) {
         CactusClient.EVENT_BUS.subscribe(module);
         module.onEnable();
      } else {
         CactusClient.EVENT_BUS.unsubscribe(module);
         module.onDisable();
      }

   }

   static {
      CATEGORY_UTILITY = new Category("utility", class_1802.field_8403.method_7854());
      CATEGORY_REDSTONE = new Category("redstone", class_1802.field_8725.method_7854());
      CATEGORY_RENDERING = new Category("rendering", class_1802.field_27070.method_7854());
   }
}
