package com.dwarslooper.cactus.client.gui.widget;

import com.dwarslooper.cactus.client.util.CactusConstants;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_350;
import net.minecraft.class_6382;
import net.minecraft.class_350.class_351;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GridScrollableWidget<E extends GridScrollableWidget.GridEntry<E>> extends class_350<E> {
   private final int itemWidth;
   private int itemRow;
   private int itemCol;
   @Nullable
   private E hoveredEntry;
   public boolean debug = false;
   protected int headerHeight = 0;

   public GridScrollableWidget(class_310 client, int width, int height, int y, int itemHeight, int itemWidth) {
      super(client, width, height, y, itemHeight);
      this.itemWidth = itemWidth;
   }

   public int addEntry(E entry) {
      return super.method_25321(entry);
   }

   public void clear() {
      this.method_25339();
   }

   public void method_25311(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      if (this.debug) {
         context.method_51742(this.method_25342() - 10, this.method_46427(), this.method_46427() + this.method_44390(), -256);
         context.method_51738(this.method_46426(), this.method_46426() + this.method_25368(), this.method_46427() + 2, -65536);
         context.method_51738(this.method_25342(), this.method_31383(), this.method_46427() + 1, -16711936);
         context.method_51742(this.method_25342(), 0, 2000, -16711936);
         context.method_51742(this.method_31383(), 0, 2000, -16711936);
      }

      int p = this.getItemsPerRow();
      if (this.debug) {
         context.method_27535(CactusConstants.mc.field_1772, class_2561.method_43470("IPR: %s | SNAP 3: %s | COL: %s | ROW: %s".formatted(new Object[]{p, 3 / p, this.itemCol, this.itemRow})), mouseX, mouseY, -1);
      }

      this.hoveredEntry = this.method_25405((double)mouseX, (double)mouseY) ? (GridScrollableWidget.GridEntry)this.method_25308((double)mouseX, (double)mouseY) : null;

      for(int m = 0; m < this.method_25340(); ++m) {
         E entry = (GridScrollableWidget.GridEntry)this.method_25396().get(m);
         int top = this.method_25337(m);
         if (this.method_25319(m) >= this.method_46427() && top <= this.method_55443()) {
            entry.renderGrid(context, m, entry.method_46426(), entry.method_46427(), this.itemWidth, this.field_62109, mouseX, mouseY, this.hoveredEntry == entry, delta);
         }
      }

   }

   protected void method_47399(@NotNull class_6382 builder) {
   }

   public int getItemLeft(int row) {
      return this.method_25342() + (this.method_25322() - row * this.itemWidth) / 2;
   }

   public int getItemsPerRow() {
      return this.method_25322() / this.itemWidth;
   }

   protected int method_44395() {
      int h = (this.method_25340() + this.getItemsPerRow() - 1) / this.getItemsPerRow();
      return h * this.field_62109 + this.headerHeight + 4;
   }

   protected int addEntry(E entry, int height) {
      int index = super.method_73370(entry, height);
      this.repositionGrid();
      return index;
   }

   public void repositionGrid() {
      int itemsPerRow = this.getItemsPerRow();

      for(int index = 0; index < this.method_25340(); ++index) {
         int indexX = index % itemsPerRow;
         int indexY = index / itemsPerRow;
         E entry = (GridScrollableWidget.GridEntry)this.method_25396().get(index);
         entry.method_46421(this.getItemLeft(itemsPerRow) + indexX * this.itemWidth);
         entry.method_46419((int)((double)(this.method_46427() + 2 + indexY * this.field_62109) - this.method_44387()));
         entry.method_73381(this.itemWidth);
         entry.method_73383(this.field_62109);
      }

   }

   public int getRowLeft(int index) {
      int p = this.getItemsPerRow();
      return this.getItemLeft(p) + index % p * this.itemWidth;
   }

   protected int method_65507() {
      return this.method_46426() + this.method_25368() - 8;
   }

   @Nullable
   public E getHovered() {
      return this.hoveredEntry;
   }

   public abstract static class GridEntry<E extends class_351<E>> extends class_351<E> {
      public final void method_25343(@NotNull class_332 context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
      }

      public abstract void renderGrid(class_332 var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10);
   }
}
