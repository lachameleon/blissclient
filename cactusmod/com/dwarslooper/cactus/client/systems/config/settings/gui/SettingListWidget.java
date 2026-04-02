package com.dwarslooper.cactus.client.systems.config.settings.gui;

import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.class_332;
import net.minecraft.class_4265;
import net.minecraft.class_350.class_351;

public class SettingListWidget extends class_4265<SettingListEntry> {
   protected final Map<SettingListEntry, Boolean> entries = new LinkedHashMap();

   public SettingListWidget(SettingContainer container, int width, int height, int y, int itemHeight) {
      super(CactusConstants.mc, width, height, y, itemHeight);
      container.forEach((group) -> {
         if (!group.getSettings().isEmpty()) {
            Map<SettingListEntry.SettingEntry, Boolean> settings = (Map)group.getSettings().stream().map((s) -> {
               return new SettingListEntry.SettingEntry(s, group);
            }).collect(Collectors.toMap((s) -> {
               return s;
            }, SettingListEntry.SettingEntry::isVisible, (e1, e2) -> {
               return e1;
            }, LinkedHashMap::new));
            this.entries.put(new SettingListEntry.GroupEntry(group), group.visible());
            this.entries.putAll(settings);
         }

      });
      this.update();
   }

   private void update() {
      this.method_25339();
      this.entries.forEach((entry, visible) -> {
         if (visible) {
            this.addEntry(entry);
         } else if (this.method_25334() == entry) {
            this.method_25313((class_351)null);
         }

      });
      this.method_65506();
   }

   public int method_25322() {
      return Math.max(302, CactusConstants.mc.method_22683().method_4486() - 300);
   }

   public int method_25342() {
      return (this.method_25368() - this.method_25322()) / 2;
   }

   protected void method_25311(class_332 context, int mouseX, int mouseY, float delta) {
      boolean update = this.entries.entrySet().stream().filter((entry) -> {
         boolean b = (Boolean)entry.getValue();
         boolean n = ((SettingListEntry)entry.getKey()).isVisible();
         entry.setValue(n);
         return b != n;
      }).count() > 0L;
      if (update) {
         this.update();
      }

      super.method_25311(context, mouseX, mouseY, delta);
   }

   protected void renderSelection(class_332 context, SettingListEntry entry, int color) {
   }

   public int addEntry(SettingListEntry entry) {
      return super.method_25321(entry);
   }

   public boolean method_25401(double mouseX, double mouseY, double amountX, double amountY) {
      this.method_44382(this.method_44387() - amountY * 2.0D * (double)this.field_62109 / 2.0D);
      return true;
   }

   protected int method_65507() {
      return this.method_55442() - 6;
   }
}
