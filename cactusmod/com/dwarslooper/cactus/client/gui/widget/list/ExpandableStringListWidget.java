package com.dwarslooper.cactus.client.gui.widget.list;

import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_4265;
import net.minecraft.class_6379;
import net.minecraft.class_4265.class_4266;

public class ExpandableStringListWidget extends class_4265<ExpandableStringListWidget.Entry> {
   private String placeholderText = "Add ..";

   public ExpandableStringListWidget(int width, int height, int y) {
      super(CactusConstants.mc, width, height, y, 24);
      this.method_25321(new ExpandableStringListWidget.PlaceholderEntry(this.field_22740, this));
   }

   public ExpandableStringListWidget.Entry add(String value) {
      ExpandableStringListWidget.Entry entry = new ExpandableStringListWidget.Entry(this, this.field_22740, this, value);
      ExpandableStringListWidget.PlaceholderEntry placeholder = (ExpandableStringListWidget.PlaceholderEntry)this.method_25396().getLast();
      this.method_25330(placeholder);
      this.method_25321(entry);
      this.method_25321(placeholder);
      return entry;
   }

   protected void addValueFromPlaceHolder(String value, ExpandableStringListWidget.PlaceholderEntry placeHolder) {
      ExpandableStringListWidget.Entry entry = this.add(value);
      placeHolder.method_25395((class_364)null);
      entry.widget.method_46421(placeHolder.widget.method_46426());
      entry.widget.method_46419(placeHolder.widget.method_46427());
      entry.method_25395(entry.widget);
      this.method_25395(entry);
   }

   public void centerScrollOn(ExpandableStringListWidget.Entry entry) {
      super.method_25324(entry);
   }

   protected void remove(ExpandableStringListWidget.Entry entry) {
      this.method_25330(entry);
      this.method_65506();
   }

   protected int method_65507() {
      return this.method_55442() - 6;
   }

   public List<String> getList() {
      return this.method_25396().stream().filter((entry) -> {
         return !(entry instanceof ExpandableStringListWidget.PlaceholderEntry);
      }).map(ExpandableStringListWidget.Entry::getText).toList();
   }

   public void setPlaceholderText(String text) {
      this.placeholderText = text;
   }

   public int method_25322() {
      return this.field_22758 - 12;
   }

   public int method_25342() {
      return this.method_46426() + 4;
   }

   public class PlaceholderEntry extends ExpandableStringListWidget.Entry {
      public PlaceholderEntry(class_310 client, ExpandableStringListWidget owner) {
         super(ExpandableStringListWidget.this, client, owner, "");
         super.deleteButton.field_22764 = false;
      }

      public void method_25343(class_332 context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
         this.widget.method_1887(ExpandableStringListWidget.this.placeholderText);
         if (!super.widget.method_1882().isEmpty()) {
            this.owner.addValueFromPlaceHolder(this.widget.method_1882(), this);
            this.widget.method_1852("");
         }

         super.method_25343(context, mouseX, mouseY, hovered, deltaTicks);
      }
   }

   public class Entry extends class_4266<ExpandableStringListWidget.Entry> {
      public final ExpandableStringListWidget owner;
      public final class_342 widget;
      public final class_4185 deleteButton;

      public Entry(final ExpandableStringListWidget this$0, class_310 client, ExpandableStringListWidget owner, String text) {
         this.owner = owner;
         this.widget = new class_342(client.field_1772, 0, 0, this$0.method_25322() - 20 - 4, 20, class_2561.method_30163(""));
         this.deleteButton = new CTextureButtonWidget(0, 0, 200, (button) -> {
            this.owner.remove(this);
         });
         this.widget.method_1880(255);
         this.widget.method_1852(text);
      }

      public void method_25343(class_332 context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
         int x = this.method_73380();
         int y = this.method_73382();
         int entryWidth = this.method_73387();
         this.widget.method_48229(x, y);
         this.widget.method_25358(entryWidth - 20 - 4);
         this.widget.method_25394(context, mouseX, mouseY, deltaTicks);
         this.deleteButton.method_48229(x + 4 + this.widget.method_25368(), y);
         this.deleteButton.method_25394(context, mouseX, mouseY, deltaTicks);
      }

      protected String getText() {
         return this.widget.method_1882();
      }

      public List<? extends class_6379> method_37025() {
         return ImmutableList.of(this.widget, this.deleteButton);
      }

      public List<? extends class_364> method_25396() {
         return ImmutableList.of(this.widget, this.deleteButton);
      }
   }
}
