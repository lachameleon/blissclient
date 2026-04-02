package com.dwarslooper.cactus.client.systems.profile;

import net.minecraft.class_1792;
import net.minecraft.class_2561;
import org.jetbrains.annotations.Nullable;

public interface ICosmetic<T> {
   String getId();

   String getName();

   CosmeticParser getParser();

   @Nullable
   String getGroup();

   class_1792 getDisplayIcon();

   class_2561 getTypeName();

   boolean canEquipWith(ICosmetic<?> var1);

   boolean isStandardEquippable();

   void fetch();

   void drop();
}
