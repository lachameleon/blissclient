package com.dwarslooper.cactus.client.feature.module;

import java.util.function.Predicate;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2477;

public class Category {
   private final String id;
   private final class_1799 icon;
   private final Predicate<Module> matcher;
   public static final Category ALL;
   public static final Category FAVORITES;

   public Category(String id, class_1799 icon, Predicate<Module> matcher) {
      this.id = id;
      this.icon = icon;
      this.matcher = matcher;
   }

   public Category(String id, class_1799 icon) {
      this.id = id;
      this.icon = icon;
      this.matcher = (module) -> {
         return this.equals(module.getCategory());
      };
   }

   public Category(String id) {
      this(id, (class_1799)null);
   }

   public boolean contains(Module module) {
      return this.matcher.test(module);
   }

   public boolean visibleInAll() {
      return true;
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return class_2477.method_10517().method_4679("module_categories." + this.id, this.id);
   }

   public class_1799 getIcon() {
      return this.icon;
   }

   static {
      ALL = new Category("all", class_1802.field_8251.method_7854(), (m) -> {
         return m.getCategory() == null || m.getCategory().visibleInAll();
      });
      FAVORITES = new Category("favorites", class_1802.field_8137.method_7854(), Module::isFavorite);
   }
}
