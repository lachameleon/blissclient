package com.dwarslooper.cactus.client.systems.config.settings.gui;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.systems.config.ConfigHelper;
import com.dwarslooper.cactus.client.systems.config.settings.group.SettingGroup;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.ABValue;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_1109;
import net.minecraft.class_11909;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_3417;
import net.minecraft.class_364;
import net.minecraft.class_5250;
import net.minecraft.class_6379;
import net.minecraft.class_4265.class_4266;
import org.jetbrains.annotations.NotNull;

public abstract class SettingListEntry extends class_4266<SettingListEntry> {
   public abstract boolean isVisible();

   public static class GroupEntry extends SettingListEntry {
      private static final ABValue<String> expandState = ABValue.of("▼", "▶");
      private final SettingGroup group;
      private final class_5250 name;
      private final int textWidth;

      public GroupEntry(SettingGroup group) {
         this.group = group;
         this.name = class_2561.method_43470(this.group.name()).method_27695(new class_124[]{class_124.field_1068, class_124.field_1067});
         this.textWidth = CactusConstants.mc.field_1772.method_27525(this.name);
      }

      public void method_25343(class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
         int x = this.method_46426();
         int y = this.method_46427();
         int entryWidth = this.method_25368();
         int entryHeight = this.method_25364();
         int textX = x + (entryWidth - this.textWidth) / 2;
         int color = CactusClient.getInstance().getRGB();
         int lineY = y + entryHeight / 2 - 2;
         context.method_51738(x, textX - 4, lineY, color);
         context.method_51738(textX + this.textWidth + 3, x + entryWidth - 1 - 22, lineY, color);
         class_327 var10001 = CactusConstants.mc.field_1772;
         class_5250 var10002 = this.name;
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         context.method_27535(var10001, var10002, textX, y + (entryHeight - 9) / 2, CactusClient.getInstance().getRGB());
         var10001 = CactusConstants.mc.field_1772;
         String var13 = (String)expandState.fromBoolean(this.group.expanded);
         int var10003 = x + entryWidth - 10 - 2;
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         context.method_25303(var10001, var13, var10003, y + (entryHeight - 9) / 2, -1);
      }

      public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
         this.group.expanded = !this.group.expanded;
         CactusConstants.mc.method_1483().method_4873(class_1109.method_47978(class_3417.field_15015, 1.0F));
         return true;
      }

      @NotNull
      public List<? extends class_364> method_25396() {
         return ImmutableList.of();
      }

      @NotNull
      public List<? extends class_6379> method_37025() {
         return ImmutableList.of();
      }

      public boolean isVisible() {
         return this.group.visible();
      }
   }

   public static class SettingEntry extends SettingListEntry {
      private final Setting<?> setting;
      private final SettingGroup parentGroup;
      private class_339 widget;
      private final CTextureButtonWidget resetButton = new CTextureButtonWidget(0, 0, 300, (button) -> {
         this.reset();
      });

      public SettingEntry(Setting<?> setting, SettingGroup parentGroup) {
         this.setting = setting;
         this.parentGroup = parentGroup;
         this.widget = setting.buildWidget();
         ConfigHelper.addDescriptionIfPresent(setting, this.widget);
      }

      public void reset() {
         this.setting.reset();
         this.widget = this.setting.buildWidget();
      }

      public void method_25343(@NotNull class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
         int x = this.method_46426();
         int y = this.method_46427();
         int entryWidth = this.method_25368();
         this.widget.method_48229(x, y);
         this.widget.method_25358(entryWidth - 22);
         this.widget.method_25394(context, mouseX, mouseY, tickDelta);
         this.resetButton.method_48229(x + entryWidth - 20, y);
         this.resetButton.field_22763 = !this.setting.isDefault();
         this.resetButton.method_25394(context, mouseX, mouseY, tickDelta);
      }

      @NotNull
      public List<? extends class_364> method_25396() {
         return ImmutableList.of(this.widget, this.resetButton);
      }

      @NotNull
      public List<? extends class_6379> method_37025() {
         return ImmutableList.of(this.widget, this.resetButton);
      }

      public boolean isVisible() {
         return this.parentGroup.expanded && this.parentGroup.visible() && this.setting.visible();
      }
   }
}
