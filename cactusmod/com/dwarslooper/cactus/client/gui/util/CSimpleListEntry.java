package com.dwarslooper.cactus.client.gui.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_364;
import net.minecraft.class_4069;
import net.minecraft.class_4280;
import net.minecraft.class_4280.class_4281;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

public abstract class CSimpleListEntry<T extends class_4281<T>> extends class_4281<T> implements class_4069 {
   public List<Pair<class_339, Function<CSimpleListEntry.WidgetEntry, CSimpleListEntry.WidgetEntry>>> widgetList = new ArrayList();
   public class_4280<T> owner;
   @Nullable
   public class_364 focused;
   private boolean dragging;

   public CSimpleListEntry(class_4280<T> owner) {
      this.owner = owner;
   }

   @NotNull
   public class_2561 method_37006() {
      return class_2561.method_43473();
   }

   public void addWidget(class_339 widget, Function<CSimpleListEntry.WidgetEntry, CSimpleListEntry.WidgetEntry> function) {
      if (!this.widgetList.stream().map(Pair::getA).toList().contains(widget)) {
         this.widgetList.add(new Pair(widget, function));
      }

   }

   public void method_25343(@NotNull class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
   }

   public void render(class_332 context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
      this.widgetList.forEach((pair) -> {
         CSimpleListEntry.WidgetEntry entry = (CSimpleListEntry.WidgetEntry)((Function)pair.getB()).apply(new CSimpleListEntry.WidgetEntry(x, y, entryWidth, entryHeight));
         ((class_339)pair.getA()).method_48229(entry.x, entry.y);
         ((class_339)pair.getA()).method_25394(context, mouseX, mouseY, tickDelta);
      });
   }

   public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
      super.method_25402(click, doubled);
      return super.method_25402(click, doubled);
   }

   @NotNull
   public List<? extends class_364> method_25396() {
      return this.widgetList.stream().map(Pair::getA).toList();
   }

   public final boolean method_25397() {
      return this.dragging;
   }

   public final void method_25398(boolean dragging) {
      this.dragging = dragging;
   }

   @Nullable
   public class_364 method_25399() {
      return this.focused;
   }

   public void method_25395(@Nullable class_364 focused) {
      if (this.focused != null) {
         this.focused.method_25365(false);
      }

      if (focused != null) {
         focused.method_25365(true);
      }

      this.focused = focused;
   }

   public static class WidgetEntry {
      public int x;
      public int y;
      public final int entryWidth;
      public final int entryHeight;

      public WidgetEntry(int x, int y, int entryWidth, int entryHeight) {
         this.x = x;
         this.y = y;
         this.entryWidth = entryWidth;
         this.entryHeight = entryHeight;
      }
   }
}
