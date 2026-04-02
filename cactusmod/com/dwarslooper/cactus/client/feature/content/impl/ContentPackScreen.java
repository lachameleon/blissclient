package com.dwarslooper.cactus.client.feature.content.impl;

import com.dwarslooper.cactus.client.feature.content.ContentPackManager;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_5244;
import org.jetbrains.annotations.NotNull;

public class ContentPackScreen extends CScreen {
   private ContentPackListWidget availablePackList;
   private ContentPackListWidget selectedPackList;

   public ContentPackScreen() {
      super("content_packs");
   }

   public void method_25426() {
      super.method_25426();
      class_342 searchField = new class_342(this.field_22793, this.field_22789 / 2 - 100, 24, 200, 20, class_2561.method_43473());
      searchField.method_1863((s) -> {
         this.update(s.isEmpty() ? null : s);
      });
      this.method_37063(searchField);
      int listY = 50;
      int listHeight = this.field_22790 - listY - 70;
      this.availablePackList = new ContentPackListWidget(this, class_2561.method_43471("pack.available.title"));
      this.availablePackList.method_73369(200, listHeight, this.field_22789 / 2 - 4 - 200, listY);
      this.method_37063(this.availablePackList);
      this.selectedPackList = new ContentPackListWidget(this, class_2561.method_43471("pack.selected.title"));
      this.selectedPackList.method_73369(200, listHeight, this.field_22789 / 2 + 4, listY);
      this.method_37063(this.selectedPackList);
      this.method_37063(new CButtonWidget(this.field_22789 / 2 - 100, this.field_22790 - 48 + 12, 200, 20, class_5244.field_24334, (button) -> {
         this.method_25419();
      }));
      this.method_48265(searchField);
      this.update();
   }

   public void update(String search) {
      this.availablePackList.clear();
      this.selectedPackList.clear();
      ContentPackManager.get().getContentPacks().values().forEach((pack) -> {
         if (search == null || pack.matchesSearch(search)) {
            if (pack.isEnabled()) {
               this.selectedPackList.add(pack);
            } else {
               this.availablePackList.add(pack);
            }

         }
      });
      this.availablePackList.method_65506();
      this.selectedPackList.method_65506();
   }

   public void update() {
      this.update((String)null);
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 8, -1);
   }
}
