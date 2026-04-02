package com.dwarslooper.cactus.client.gui.screen;

import net.minecraft.class_2477;

public interface ITranslatable {
   String getKey();

   default String getTranslatableElement(String key, Object... args) {
      return class_2477.method_10517().method_4679(this.getKey() + "." + key, key).formatted(args);
   }
}
