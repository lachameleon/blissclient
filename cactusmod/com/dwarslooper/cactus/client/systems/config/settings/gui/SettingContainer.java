package com.dwarslooper.cactus.client.systems.config.settings.gui;

import com.dwarslooper.cactus.client.systems.ISerializable;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.dwarslooper.cactus.client.systems.config.settings.group.SettingGroup;
import com.dwarslooper.cactus.client.util.generic.INamespaced;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class SettingContainer implements ISerializable<SettingContainer>, Iterable<SettingGroup>, INamespaced {
   private final Set<SettingGroup> settingGroups = new LinkedHashSet();
   private SettingGroup mainSettingGroup;
   private String namespace;

   public SettingContainer() {
   }

   public SettingContainer(String namespace) {
      this.namespace = namespace;
   }

   public SettingGroup buildGroup(String name) {
      return this.buildGroup(name, this.namespace);
   }

   public SettingGroup buildGroup(String name, String namespace) {
      SettingGroup group = new SettingGroup(name);
      group.setNamespace(namespace);
      return this.appendGroup(group);
   }

   public SettingGroup appendGroup(SettingGroup group) {
      this.settingGroups.add(group);
      return group;
   }

   public SettingGroup getDefault() {
      return this.mainSettingGroup != null ? this.mainSettingGroup : (this.mainSettingGroup = this.buildGroup("main"));
   }

   public JsonObject toJson(TreeSerializerFilter filter) {
      JsonObject object = new JsonObject();
      this.settingGroups.forEach((sg) -> {
         if (sg.size() > 0) {
            object.add(sg.id(), sg.toJson(filter.resolve(sg.id())));
         }

      });
      return object;
   }

   public SettingContainer fromJson(JsonObject object) {
      this.settingGroups.forEach((sg) -> {
         if (object.has(sg.id())) {
            sg.fromJson(object.getAsJsonObject(sg.id()));
         }

      });
      return this;
   }

   @NotNull
   public Iterator<SettingGroup> iterator() {
      return this.settingGroups.iterator();
   }

   public Set<SettingGroup> getSettingGroups() {
      return this.settingGroups;
   }

   public boolean equals(Object obj) {
      if (!super.equals(obj)) {
         return false;
      } else if (obj instanceof SettingContainer) {
         SettingContainer container = (SettingContainer)obj;
         return this.getSettingGroups().equals(container.getSettingGroups());
      } else {
         return false;
      }
   }

   public String getNamespace() {
      return this.namespace;
   }
}
