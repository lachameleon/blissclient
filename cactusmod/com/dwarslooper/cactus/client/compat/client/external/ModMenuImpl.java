package com.dwarslooper.cactus.client.compat.client.external;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.gui.screen.impl.CactusSettingsScreen;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuImpl implements ModMenuApi {
   public ConfigScreenFactory<?> getModConfigScreenFactory() {
      try {
         return (parent) -> {
            return new CactusSettingsScreen(CactusConstants.mc.field_1755);
         };
      } catch (Exception var2) {
         CactusClient.getLogger().error("Failed to provide settings screen to ModMenu", var2);
         return null;
      }
   }
}
