package com.dwarslooper.cactus.client.systems.hdb;

import net.minecraft.class_1792;
import net.minecraft.class_1802;

public enum TagCategory {
   ALPHABET("alphabet", "Alphabet", class_1802.field_8529),
   ANIMALS("animals", "Animals", class_1802.field_8606),
   BLOCKS("blocks", "Blocks", class_1802.field_20390),
   DECORATION("decoration", "Decoration", class_1802.field_8892),
   FOOD_DRINKS("food-drinks", "Food & Drinks", class_1802.field_8279),
   HUMANS("humans", "Humans", class_1802.field_8575),
   HUMANOID("humanoid", "Humanoid", class_1802.field_8694),
   MISCELLANEOUS("miscellaneous", "Miscellaneous", class_1802.field_8106),
   MONSTERS("monsters", "Monsters", class_1802.field_8470),
   PLANTS("plants", "Plants", class_1802.field_17535);

   private final String id;
   private final String display;
   private final class_1792 icon;

   private TagCategory(String id, String display, class_1792 icon) {
      this.id = id;
      this.display = display;
      this.icon = icon;
   }

   public String getId() {
      return this.id;
   }

   public String getDisplay() {
      return this.display;
   }

   public class_1792 getIcon() {
      return this.icon;
   }

   // $FF: synthetic method
   private static TagCategory[] $values() {
      return new TagCategory[]{ALPHABET, ANIMALS, BLOCKS, DECORATION, FOOD_DRINKS, HUMANS, HUMANOID, MISCELLANEOUS, MONSTERS, PLANTS};
   }
}
