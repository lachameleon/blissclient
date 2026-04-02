package com.dwarslooper.cactus.client.feature.modules.util;

import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.class_1263;
import net.minecraft.class_2199;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2343;

public class InventoryTabs extends Module {
   public static boolean nextWasSwitch;
   public static class_2338 corePos;
   public static int listIndex;
   public static List<class_2338> blockPosList = new ArrayList();
   public static List<class_2248> blockList = new ArrayList();
   private static final Collection<class_2248> additionalBlockTypes;
   private static final Collection<Class<? extends class_2248>> additionalBlockClasses;

   public static boolean isContainer(class_2248 block) {
      boolean var10000;
      label17: {
         if (block instanceof class_2343) {
            class_2343 provider = (class_2343)block;
            if (provider.method_10123(class_2338.field_10980, block.method_9564()) instanceof class_1263) {
               break label17;
            }
         }

         if (!additionalBlockTypes.contains(block) && !additionalBlockClasses.stream().anyMatch((clazz) -> {
            return clazz.isInstance(block);
         })) {
            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }

   public InventoryTabs() {
      super("inventory_tabs", ModuleManager.CATEGORY_UTILITY, (new Module.Options()).set(Module.Flag.SERVER_UNSAFE, true).set(Module.Flag.HUD_LISTED, false));
   }

   static {
      additionalBlockTypes = List.of(class_2246.field_10083, class_2246.field_16336, class_2246.field_16335, class_2246.field_16329, class_2246.field_10327, class_2246.field_10485, class_2246.field_16337, class_2246.field_9980, class_2246.field_10443);
      additionalBlockClasses = List.of(class_2199.class);
   }
}
