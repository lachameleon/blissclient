package com.dwarslooper.cactus.client.gui.overlay;

import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.class_1109;
import net.minecraft.class_124;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_3417;
import net.minecraft.class_5250;
import net.minecraft.class_8002;

public class SelectionPopup {
   private int x;
   private int y;
   private boolean visible;
   private int width;
   private int height;
   private final List<SelectionPopup.Option> options = new ArrayList();
   private final Consumer<SelectionPopup.Option> callback;

   public SelectionPopup(int x, int y, Consumer<SelectionPopup.Option> selectCallback) {
      this.x = x;
      this.y = y;
      this.callback = selectCallback;
   }

   public void render(class_332 context, double mouseX, double mouseY) {
      if (this.isVisible()) {
         class_8002.method_47946(context, this.x, this.y, this.width, this.height, (class_2960)null);
         Iterator var6 = this.options.iterator();

         while(var6.hasNext()) {
            SelectionPopup.Option option = (SelectionPopup.Option)var6.next();
            option.render(context, mouseX, mouseY);
         }
      }

   }

   public boolean mouseClicked(int button, double mouseX, double mouseY) {
      if (!this.isVisible()) {
         return false;
      } else {
         Iterator var6 = this.options.iterator();

         SelectionPopup.Option option;
         do {
            if (!var6.hasNext()) {
               return false;
            }

            option = (SelectionPopup.Option)var6.next();
         } while(!option.mouseClicked(button, mouseX, mouseY));

         return true;
      }
   }

   public SelectionPopup addOption(int id, class_5250 label, Supplier<Boolean> active, Consumer<SelectionPopup.Option> callback) {
      SelectionPopup.Option option = new SelectionPopup.Option(id, label, active, callback);
      this.options.add(option);
      this.width = Math.max(this.width, option.getWidth());
      this.height += option.getHeight();
      return this;
   }

   public SelectionPopup addOption(int id, class_5250 label, boolean active, Consumer<SelectionPopup.Option> callback) {
      return this.addOption(id, label, () -> {
         return active;
      }, callback);
   }

   public SelectionPopup clear() {
      this.options.clear();
      this.width = this.height = 0;
      return this;
   }

   public void selected(SelectionPopup.Option option) {
      this.callback.accept(option);
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setPosition(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public void setPosition(int x, int y, int maxX, int maxY) {
      this.setPosition(x + this.width < maxX ? x : maxX - this.width, y + this.height < maxY ? y : maxY - this.height);
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public class Option {
      private final int id;
      private final Consumer<SelectionPopup.Option> callback;
      private class_5250 label;
      private Supplier<Boolean> active;

      public Option(int id, class_5250 label, Supplier<Boolean> active, Consumer<SelectionPopup.Option> callback) {
         this.id = id;
         this.callback = callback;
         this.label = label;
         this.active = active;
      }

      public int getId() {
         return this.id;
      }

      public class_5250 getLabel() {
         return this.label;
      }

      public boolean isActive() {
         return (Boolean)this.active.get();
      }

      public void setLabel(class_5250 label) {
         this.label = label;
      }

      public void setActive(boolean active) {
         this.active = () -> {
            return active;
         };
      }

      public void render(class_332 context, double mouseX, double mouseY) {
         boolean hovered = this.isHovered(mouseX, mouseY);
         int yPos = SelectionPopup.this.y + SelectionPopup.this.options.indexOf(this) * this.getHeight();
         context.method_25294(SelectionPopup.this.x, yPos, SelectionPopup.this.x + SelectionPopup.this.width, yPos + this.getHeight(), -1442840576);
         class_124 formatting = class_124.field_1068;
         if (!this.isActive()) {
            formatting = class_124.field_1080;
         } else if (hovered) {
            formatting = class_124.field_1054;
         }

         context.method_27535(CactusConstants.mc.field_1772, this.getLabel().method_27661().method_27692(formatting), SelectionPopup.this.x + 1, yPos + 1, -1);
      }

      public boolean mouseClicked(int button, double mouseX, double mouseY) {
         if (this.isHovered(mouseX, mouseY)) {
            if (this.isActive()) {
               SelectionPopup.this.selected(this);
               this.callback.accept(this);
               CactusConstants.mc.method_1483().method_4873(class_1109.method_47978(class_3417.field_15015, 1.0F));
            }

            return true;
         } else {
            return false;
         }
      }

      private boolean isHovered(double mouseX, double mouseY) {
         return mouseX > (double)SelectionPopup.this.x && mouseX < (double)(SelectionPopup.this.x + this.getWidth()) && mouseY > (double)this.getYPos() && mouseY < (double)(this.getYPos() + this.getHeight());
      }

      public int getYPos() {
         return SelectionPopup.this.y + SelectionPopup.this.options.indexOf(this) * this.getHeight();
      }

      public int getWidth() {
         return CactusConstants.mc.field_1772.method_27525(this.getLabel()) + 2;
      }

      public int getHeight() {
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         return 9 + 2;
      }
   }
}
