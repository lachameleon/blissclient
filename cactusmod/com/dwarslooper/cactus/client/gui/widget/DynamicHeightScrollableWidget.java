package com.dwarslooper.cactus.client.gui.widget;

import java.util.Iterator;
import net.minecraft.class_11909;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_350;
import net.minecraft.class_6382;
import net.minecraft.class_350.class_351;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class DynamicHeightScrollableWidget<E extends DynamicHeightScrollableWidget.DynamicEntry<E>> extends class_350<E> {
   @Nullable
   private E hoveredEntry;
   protected int headerHeight = 0;

   public DynamicHeightScrollableWidget(class_310 client, int width, int height, int y) {
      super(client, width, height, y, 1);
   }

   public void method_48579(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      this.hoveredEntry = this.getEntryAt((double)mouseX, (double)mouseY);
      super.method_48579(context, mouseX, mouseY, delta);
   }

   protected void method_25311(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      int i = this.method_25342();
      int j = this.method_25322();
      int y = this.method_46427() + this.headerHeight - (int)this.method_44387() + 4;
      int index = 0;

      for(Iterator var9 = this.method_25396().iterator(); var9.hasNext(); ++index) {
         E entry = (DynamicHeightScrollableWidget.DynamicEntry)var9.next();
         int entryHeight = entry.method_25364();
         int entryBottom = y + entryHeight;
         if (entryBottom >= this.method_46427() && y <= this.method_55443()) {
            entry.render(context, index, y, i, j, entryHeight, mouseX, mouseY, entry == this.hoveredEntry, delta);
         }

         y += entryHeight;
      }

   }

   public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
      return super.method_25402(click, doubled);
   }

   public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.method_44382(this.method_44387() - verticalAmount * 20.0D);
      return true;
   }

   @Nullable
   public E getEntryAt(double x, double y) {
      int halfRow = this.method_25322() / 2;
      int center = this.method_46426() + this.field_22758 / 2;
      int left = center - halfRow;
      int right = center + halfRow;
      if (!(x < (double)left) && !(x > (double)right) && !(y < (double)this.method_46427()) && !(y >= (double)this.method_55443())) {
         double relY = y - (double)this.method_46427() - (double)this.headerHeight + this.method_44387() - 4.0D;
         if (relY < 0.0D) {
            return null;
         } else {
            int currY = 0;

            int entryHeight;
            for(Iterator var12 = this.method_25396().iterator(); var12.hasNext(); currY += entryHeight) {
               E entry = (DynamicHeightScrollableWidget.DynamicEntry)var12.next();
               entryHeight = entry.method_25364();
               if (relY >= (double)currY && relY < (double)(currY + entryHeight)) {
                  return entry;
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   protected int method_65507() {
      return this.method_46426() + this.method_25368() - 8;
   }

   @Nullable
   public E getHovered() {
      return this.hoveredEntry;
   }

   protected void method_47399(@NotNull class_6382 builder) {
   }

   public int method_25337(int index) {
      int y = this.method_46427() + 4 - (int)this.method_44387() + this.headerHeight;

      for(int i = 0; i < index; ++i) {
         y += ((DynamicHeightScrollableWidget.DynamicEntry)this.method_25396().get(i)).method_25364();
      }

      return y;
   }

   public int method_25319(int index) {
      return this.method_25337(index) + ((DynamicHeightScrollableWidget.DynamicEntry)this.method_25396().get(index)).method_25364();
   }

   protected void centerScrollOn(E entry) {
      int index = this.method_25396().indexOf(entry);
      if (index >= 0) {
         int before = 0;

         int entryHeight;
         for(entryHeight = 0; entryHeight < index; ++entryHeight) {
            before += ((DynamicHeightScrollableWidget.DynamicEntry)this.method_25396().get(entryHeight)).method_25364();
         }

         entryHeight = entry.method_25364();
         this.method_44382((double)((float)before + (float)entryHeight / 2.0F - (float)this.field_22759 / 2.0F));
      }
   }

   protected void ensureVisible(E entry) {
      int index = this.method_25396().indexOf(entry);
      if (index >= 0) {
         int entryTop = this.method_25337(index);
         int entryHeight = entry.method_25364();
         int j = entryTop - this.method_46427() - 4 - entryHeight;
         if (j < 0) {
            this.method_44382(this.method_44387() + (double)j);
         }

         int k = this.method_55443() - entryTop - entryHeight - entryHeight;
         if (k < 0) {
            this.method_44382(this.method_44387() + (double)(-k));
         }

      }
   }

   protected int getMaxPosition() {
      int total = this.headerHeight;

      DynamicHeightScrollableWidget.DynamicEntry entry;
      for(Iterator var2 = this.method_25396().iterator(); var2.hasNext(); total += entry.method_25364()) {
         entry = (DynamicHeightScrollableWidget.DynamicEntry)var2.next();
      }

      return total;
   }

   public abstract static class DynamicEntry<E extends class_351<E>> extends class_351<E> {
      private int height;

      public DynamicEntry(int height) {
         this.height = height;
      }

      public void method_25343(@NotNull class_332 context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
      }

      public abstract void render(class_332 var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10);

      public int method_25364() {
         return this.height;
      }
   }
}
