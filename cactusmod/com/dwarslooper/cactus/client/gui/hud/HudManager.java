package com.dwarslooper.cactus.client.gui.hud;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.addon.v2.RegistryBus;
import com.dwarslooper.cactus.client.gui.hud.element.DynamicHudElement;
import com.dwarslooper.cactus.client.gui.hud.element.StaticHudElement;
import com.dwarslooper.cactus.client.gui.hud.element.impl.ArmorElement;
import com.dwarslooper.cactus.client.gui.hud.element.impl.BlockStateElement;
import com.dwarslooper.cactus.client.gui.hud.element.impl.BossbarElement;
import com.dwarslooper.cactus.client.gui.hud.element.impl.GraphElement;
import com.dwarslooper.cactus.client.gui.hud.element.impl.HudElement;
import com.dwarslooper.cactus.client.gui.hud.element.impl.ImageElement;
import com.dwarslooper.cactus.client.gui.hud.element.impl.KeystrokesElement;
import com.dwarslooper.cactus.client.gui.hud.element.impl.MultiLineTextElement;
import com.dwarslooper.cactus.client.gui.hud.element.impl.ScoreboardElement;
import com.dwarslooper.cactus.client.gui.hud.element.impl.StatusEffectsElement;
import com.dwarslooper.cactus.client.systems.ISerializable;
import com.dwarslooper.cactus.client.systems.config.ConfigHandler;
import com.dwarslooper.cactus.client.systems.config.FileConfiguration;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.dwarslooper.cactus.client.systems.config.settings.group.SettingGroup;
import com.dwarslooper.cactus.client.systems.config.settings.gui.SettingContainer;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.IntegerSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public class HudManager extends FileConfiguration<HudManager> implements Iterable<HudElement<?>> {
   private final Set<HudElement<?>> elementRegistry = new LinkedHashSet();
   private final Map<Class<? extends HudElement<?>>, List<PresetConfig<?>>> presetRegistry = new LinkedHashMap();
   private final List<HudElement<?>> elements = new ArrayList();
   private final HudManager.Settings options = new HudManager.Settings();

   public static HudManager getInstance() {
      return (HudManager)CactusClient.getConfig(HudManager.class);
   }

   public HudManager.Settings getSettings() {
      return this.options;
   }

   public HudManager(ConfigHandler handler) {
      super("hud", handler);
   }

   public void init() {
      this.elementRegistry.add(new MultiLineTextElement());
      this.elementRegistry.add(new ImageElement());
      this.elementRegistry.add(new KeystrokesElement());
      this.elementRegistry.add(new ArmorElement());
      this.elementRegistry.add(new BlockStateElement());
      this.elementRegistry.add(new ScoreboardElement());
      this.elementRegistry.add(new StatusEffectsElement());
      this.elementRegistry.add(new BossbarElement());
      this.elementRegistry.add(new GraphElement());
      RegistryBus var10000 = getMainRegistryBus();
      Set var10002 = this.elementRegistry;
      Objects.requireNonNull(var10002);
      var10000.completeAndTake(HudElement.class, var10002::add);
      this.registerPreset(MultiLineTextElement.class, "helloworld", (e) -> {
         e.lines.set(List.of("Hello World!"));
      });
      this.registerPreset(MultiLineTextElement.class, "position", (e) -> {
         e.lines.set(List.of("{player.x} {player.y} {player.z}"));
      });
      this.registerPreset(MultiLineTextElement.class, "performance", (e) -> {
         e.lines.set(List.of("FPS: {client.fps}", "TPS: {server.tps}", "Ping: {server.ping}"));
      });
      this.registerPreset(MultiLineTextElement.class, "clock", (e) -> {
         e.lines.set(List.of("{client.time}"));
      });
      this.registerPreset(MultiLineTextElement.class, "version", (e) -> {
         e.lines.set(List.of("Cactus {cactus.version}"));
      });
      getMainRegistryBus().completeAndTake(PresetConfig.class, this::registerPreset);
      if (this.isFirstInit()) {
         this.elementRegistry.forEach((ahe) -> {
            HudElement<?> e = ahe.buildDefault();
            if (e != null) {
               this.addFromRegistry(e, (element) -> {
               });
            }

         });
      }

   }

   public JsonObject toJson(TreeSerializerFilter filter) {
      JsonObject object = new JsonObject();
      JsonArray active = new JsonArray();
      Iterator var4 = this.elements.iterator();

      while(var4.hasNext()) {
         HudElement<?> element = (HudElement)var4.next();
         String var10001 = element.getId();
         BiConsumer var10002 = (s, o) -> {
            active.add(o);
         };
         Objects.requireNonNull(element);
         filter.checkSerializeAndPass(var10001, var10002, element::toJson);
      }

      object.add("settings", this.options.toJson(filter.resolve("settings")));
      object.add("elements", active);
      return object;
   }

   public HudManager fromJson(JsonObject object) {
      this.options.fromJson(object.getAsJsonObject("settings"));
      this.elements.clear();
      List<JsonObject> list = object.getAsJsonArray("elements").asList().stream().map(JsonElement::getAsJsonObject).toList();
      list.forEach((json) -> {
         this.elementRegistry.stream().filter((e) -> {
            return e.getId().equalsIgnoreCase(json.get("id").getAsString());
         }).findFirst().ifPresentOrElse((e) -> {
            this.addFromRegistry(e, (instance) -> {
               instance.fromJson(json);
            });
         }, () -> {
            CactusClient.getLogger().error("Tried to load hud element with data {}, but said element was not found in registry", json.toString());
         });
      });
      return this;
   }

   @NotNull
   public Iterator<HudElement<?>> iterator() {
      return this.elements.iterator();
   }

   public List<HudElement<?>> getElements() {
      return this.elements;
   }

   public Set<HudElement<?>> getElementRegistry() {
      return this.elementRegistry;
   }

   public <T extends HudElement<?>> boolean addFromRegistry(T element, Consumer<T> configurator) {
      Object instance;
      label27: {
         instance = null;
         if (element instanceof StaticHudElement) {
            StaticHudElement<?> fhe = (StaticHudElement)element;
            if (!this.elements.contains(fhe.getInstance())) {
               this.elements.add(instance = fhe.getInstance());
               break label27;
            }
         }

         if (element instanceof DynamicHudElement) {
            DynamicHudElement<?> mhe = (DynamicHudElement)element;
            this.elements.add(instance = mhe.duplicate());
         }
      }

      boolean b = instance != null;
      if (b) {
         if (configurator != null) {
            configurator.accept(instance);
         }

         CactusClient.EVENT_BUS.subscribe(instance);
         ((HudElement)instance).created();
         ((HudElement)instance).update();
      }

      return b;
   }

   public boolean remove(HudElement<?> element) {
      CactusClient.EVENT_BUS.unsubscribe(element);
      HudElement<?> e = this.selfOrInstance(element, false);
      e.removed();
      return this.elements.remove(e);
   }

   public HudElement<?> selfOrInstance(HudElement<?> element, boolean duplicateIfDynamic) {
      Object var10000;
      if (element instanceof StaticHudElement) {
         StaticHudElement<?> eStatic = (StaticHudElement)element;
         var10000 = eStatic.getInstance();
      } else if (duplicateIfDynamic && element instanceof DynamicHudElement) {
         DynamicHudElement<?> eDynamic = (DynamicHudElement)element;
         var10000 = eDynamic.duplicate();
      } else {
         var10000 = element;
      }

      return (HudElement)var10000;
   }

   private <T extends HudElement<?>> void registerPreset(PresetConfig<T> preset) {
      ((List)this.presetRegistry.computeIfAbsent(preset.target(), (k) -> {
         return new ArrayList();
      })).add(preset);
   }

   private <T extends HudElement<?>> void registerPreset(Class<T> target, String id, Consumer<T> configurator) {
      this.registerPreset(new PresetConfig(target, id, configurator));
   }

   public <T extends HudElement<?>> List<PresetConfig<?>> getPresets(Class<T> type) {
      return (List)this.presetRegistry.getOrDefault(type, Collections.emptyList());
   }

   public static class Settings implements ISerializable<HudManager.Settings> {
      protected final SettingContainer settings = new SettingContainer("hud");
      private final SettingGroup mainGroup;
      public final Setting<Boolean> snap;
      public final Setting<Integer> snapDistance;
      public final Setting<Boolean> keyMove;
      public final Setting<Boolean> highlightAnchorRegion;

      public Settings() {
         this.mainGroup = this.settings.getDefault();
         this.snap = this.mainGroup.add(new BooleanSetting("snap", true));
         this.snapDistance = this.mainGroup.add((new IntegerSetting("snapDistance", 8)).min(2).max(20));
         this.keyMove = this.mainGroup.add(new BooleanSetting("keyMove", true));
         this.highlightAnchorRegion = this.mainGroup.add(new BooleanSetting("highlightAnchorRegion", true));
      }

      public JsonObject toJson(TreeSerializerFilter filter) {
         return this.settings.toJson(filter);
      }

      public HudManager.Settings fromJson(JsonObject object) {
         this.settings.fromJson(object);
         return this;
      }
   }
}
