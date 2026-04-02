package com.dwarslooper.cactus.client.systems.key;

import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.MinecraftKeyEvent;
import com.dwarslooper.cactus.client.feature.macro.Macro;
import com.dwarslooper.cactus.client.feature.macro.MacroManager;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.gui.screen.impl.ModuleListScreen;
import com.dwarslooper.cactus.client.gui.screen.impl.ToolSelectionScreen;
import com.dwarslooper.cactus.client.systems.SingleInstance;
import com.dwarslooper.cactus.client.util.CactusConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.class_2960;
import net.minecraft.class_304;
import net.minecraft.class_304.class_11900;
import net.minecraft.class_3675.class_307;

public class KeybindManager {
   public static class_304 menuBind;
   public static class_304 toolsBind;
   private static final class_11900 CATEGORY = class_11900.method_74698(class_2960.method_60655("cactus", "keybind"));

   public KeybindManager get() {
      return (KeybindManager)SingleInstance.of(KeybindManager.class, KeybindManager::new);
   }

   public static void init() {
      menuBind = KeyBindingHelper.registerKeyBinding(new class_304("keybind.menu", class_307.field_1668, 344, CATEGORY));
      toolsBind = KeyBindingHelper.registerKeyBinding(new class_304("keybind.tools", class_307.field_1668, 44, CATEGORY));
      ClientTickEvents.END_CLIENT_TICK.register((client) -> {
         if (menuBind.method_1436()) {
            client.method_1507(new ModuleListScreen());
         }

         if (toolsBind.method_1436()) {
            client.method_1507(new ToolSelectionScreen());
         }

      });
   }

   @EventHandler
   public void onKeyboardInput(MinecraftKeyEvent event) {
      if (CactusConstants.mc.field_1755 == null) {
         ModuleManager.get().getModules().values().stream().filter((module) -> {
            return module.getBind().key() == event.getKey();
         }).forEach((module) -> {
            if (event.getAction() == 1 || module.shouldToggleOnBindRelease() && event.getAction() == 0) {
               module.toggle();
            }

         });
         if (event.getAction() == 1) {
            MacroManager.get().getMacros().stream().filter((macro) -> {
               return macro.getKeyBinding().key() == event.getKey();
            }).forEach(Macro::run);
         }
      }

   }

   public static String getKeyName(int key) {
      return class_307.field_1668.method_1447(key).method_27445().getString();
   }
}
