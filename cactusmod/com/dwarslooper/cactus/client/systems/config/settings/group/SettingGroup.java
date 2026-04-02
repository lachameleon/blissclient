package com.dwarslooper.cactus.client.systems.config.settings.group;

import com.dwarslooper.cactus.client.systems.ISerializable;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.dwarslooper.cactus.client.systems.config.settings.ICopyable;
import com.dwarslooper.cactus.client.systems.config.settings.IMutableVisibility;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.generic.INamespaced;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.class_2477;
import org.jetbrains.annotations.NotNull;

public class SettingGroup implements ISerializable<SettingGroup>, Iterable<Setting<?>>, ICopyable<SettingGroup>, IMutableVisibility<SettingGroup>, INamespaced {
   private static final String EXPANDED_IDENTIFIER = "_expanded";
   private final List<Setting<?>> settings;
   private final String id;
   public boolean expanded;
   public String namespace;
   private BooleanSupplier visible;

   public SettingGroup(String id) {
      this(id, true);
   }

   public SettingGroup(String id, boolean expanded) {
      this.settings = new ArrayList();
      this.id = id;
      this.expanded = expanded;
   }

   public <T> Setting<T> add(Setting<T> setting) {
      if (setting.id().equals("_expanded")) {
         throw new IllegalStateException("Setting ID must not be same as identifier used for group expansion state '%s'".formatted(new Object[]{"_expanded"}));
      } else {
         this.settings.add(setting);
         setting.setNamespace(this.namespace);
         return setting;
      }
   }

   public String name() {
      return class_2477.method_10517().method_48307(this.id.equals("main") ? "settingGroups.main" : "%s.%s".formatted(new Object[]{this.getNamespace(), this.id}));
   }

   public String id() {
      return this.id;
   }

   public void setNamespace(String namespace) {
      this.namespace = namespace;
   }

   public void withNamespace(String namespace, Consumer<SettingGroup> consumer) {
      String current = this.namespace;
      this.namespace = namespace;
      consumer.accept(this);
      this.namespace = current;
   }

   public int size() {
      return this.settings.size();
   }

   public JsonObject toJson(TreeSerializerFilter filter) {
      JsonObject object = new JsonObject();
      object.addProperty("_expanded", this.expanded);
      this.settings.forEach((setting) -> {
         object.add(setting.id(), setting.toJson(filter.resolve(setting.id())));
      });
      return object;
   }

   public SettingGroup fromJson(JsonObject object) {
      if (object.has("_expanded")) {
         this.expanded = object.get("_expanded").getAsBoolean();
      }

      this.settings.forEach((setting) -> {
         if (object.has(setting.id())) {
            JsonObject json = object.getAsJsonObject(setting.id());
            if (setting.preLoad(json)) {
               setting.fromJson(json);
            }
         }

      });
      return this;
   }

   public String getNamespace() {
      return this.namespace != null ? this.namespace + ".groups" : "settingGroups";
   }

   public SettingGroup copy() {
      SettingGroup sg = new SettingGroup(this.id, this.expanded);
      List var10000 = List.copyOf(this.settings);
      Objects.requireNonNull(sg);
      var10000.forEach(sg::add);
      return sg;
   }

   public List<Setting<?>> getSettings() {
      return this.settings;
   }

   public SettingGroup visibleIf(BooleanSupplier supplier) {
      this.visible = supplier;
      return this;
   }

   public boolean visible() {
      return this.visible == null || this.visible.getAsBoolean();
   }

   @NotNull
   public Iterator<Setting<?>> iterator() {
      return this.settings.iterator();
   }
}
