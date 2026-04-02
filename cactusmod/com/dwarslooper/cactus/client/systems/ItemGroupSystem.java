package com.dwarslooper.cactus.client.systems;

import com.dwarslooper.cactus.client.feature.content.ContentPackManager;
import com.dwarslooper.cactus.client.gui.screen.impl.HeadBrowserScreen;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.class_1657;
import net.minecraft.class_1761;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2378;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_7923;
import net.minecraft.class_1761.class_7705;
import net.minecraft.class_1761.class_7916;
import net.minecraft.class_1761.class_8128;

public class ItemGroupSystem {
   public static List<class_1761> hdbGroups = new ArrayList();

   public static void register() {
      String[] var0 = HeadBrowserScreen.getTags();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         String tag = var0[var2];
         class_1761 hdbGroup = FabricItemGroup.builder().method_47321(class_2561.method_43470("Head Database (%s)".formatted(new Object[]{tag}))).method_47320(() -> {
            return HeadBrowserScreen.isCached() ? (class_1799)HeadBrowserScreen.getItemsFromCat(tag).getFirst() : class_1802.field_8575.method_7854();
         }).method_47317((displayContext, entries) -> {
            if (ContentPackManager.get().isEnabled(ContentPackManager.get().ofId("head_database")) && HeadBrowserScreen.isCached()) {
               List<class_1799> stacks = HeadBrowserScreen.getItemsFromCat(tag);
               stacks.forEach((itemStack) -> {
                  entries.method_45417(itemStack, class_7705.field_40191);
               });
            }

         }).method_47324();
         class_2378.method_10230(class_7923.field_44687, class_2960.method_60655("cactus", "hdb-group." + tag), hdbGroup);
         hdbGroups.add(hdbGroup);
      }

   }

   public static void reloadAll() {
      if (CactusConstants.mc.field_1687 != null) {
         class_1657 player = CactusConstants.mc.field_1724;
         class_8128 context = new class_8128(CactusConstants.mc.method_1562().method_45735(), (Boolean)CactusConstants.mc.field_1690.method_47395().method_41753() && player.method_7338(), player.method_73183().method_30349());
         forceReload(context);
      }
   }

   public static void forceReload(class_8128 displayContext) {
      class_7923.field_44687.method_10220().filter((group) -> {
         return group.method_47312() == class_7916.field_41052;
      }).forEach((group) -> {
         group.method_47306(displayContext);
      });
      class_7923.field_44687.method_10220().filter((group) -> {
         return group.method_47312() != class_7916.field_41052;
      }).forEach((group) -> {
         group.method_47306(displayContext);
      });
   }
}
