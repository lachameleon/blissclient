package com.dwarslooper.cactus.client.systems.config.settings;

import java.util.function.BooleanSupplier;

public interface IMutableVisibility<T> {
   boolean visible();

   T visibleIf(BooleanSupplier var1);

   static void all(BooleanSupplier supplier, IMutableVisibility<?>... instances) {
      IMutableVisibility[] var2 = instances;
      int var3 = instances.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         IMutableVisibility<?> instance = var2[var4];
         instance.visibleIf(supplier);
      }

   }
}
