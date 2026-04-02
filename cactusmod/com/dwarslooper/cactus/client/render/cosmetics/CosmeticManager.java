package com.dwarslooper.cactus.client.render.cosmetics;

import java.util.HashMap;
import java.util.Map;

public class CosmeticManager {
   private static final CosmeticManager INSTANCE = new CosmeticManager();
   private final Map<String, CosmeticRenderer> rendererRegistry = new HashMap();

   public static CosmeticManager get() {
      return INSTANCE;
   }

   public CosmeticRenderer getRenderer(String id) {
      return (CosmeticRenderer)this.rendererRegistry.get(id);
   }

   public void registerRenderer(String id, CosmeticRenderer renderer) {
      this.rendererRegistry.put(id, renderer);
   }
}
