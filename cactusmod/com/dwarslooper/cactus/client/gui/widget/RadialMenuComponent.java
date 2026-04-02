package com.dwarslooper.cactus.client.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_3532;

public class RadialMenuComponent<T> {
   private final List<RadialMenuComponent.Entry<T>> entries = new ArrayList();
   private float[] hoverAnimation = new float[0];
   private int selectedIndex = -1;

   public void setEntries(List<RadialMenuComponent.Entry<T>> newEntries) {
      this.entries.clear();
      this.entries.addAll(newEntries);
      if (this.hoverAnimation.length != this.entries.size()) {
         this.hoverAnimation = new float[this.entries.size()];
      }

      if (this.selectedIndex >= this.entries.size()) {
         this.selectedIndex = -1;
      }

   }

   public List<RadialMenuComponent.Entry<T>> getEntries() {
      return this.entries;
   }

   public int getSelectedIndex() {
      return this.selectedIndex;
   }

   public void clearSelection() {
      this.selectedIndex = -1;
   }

   public RadialMenuComponent.Entry<T> getSelectedEntry() {
      return this.selectedIndex >= 0 && this.selectedIndex < this.entries.size() ? (RadialMenuComponent.Entry)this.entries.get(this.selectedIndex) : null;
   }

   public void updateSelection(double mouseX, double mouseY, double centerX, double centerY, float deadzonePx, float snappingBias) {
      if (this.entries.isEmpty()) {
         this.selectedIndex = -1;
      } else {
         double dx = mouseX - centerX;
         double dy = mouseY - centerY;
         double distance = Math.sqrt(dx * dx + dy * dy);
         if (distance < (double)deadzonePx) {
            this.selectedIndex = -1;
         } else {
            double angle = Math.atan2(dy, dx) + 1.5707963267948966D;
            if (angle < 0.0D) {
               angle += 6.283185307179586D;
            }

            double perEntry = 6.283185307179586D / (double)this.entries.size();
            int nearestIndex = Math.floorMod((int)Math.floor((angle + perEntry / 2.0D) / perEntry), this.entries.size());
            if (this.selectedIndex >= 0 && this.selectedIndex < this.entries.size()) {
               double selectedCenterAngle = (double)this.selectedIndex * perEntry;
               double delta = wrapAngle(angle - selectedCenterAngle);
               double hysteresis = perEntry * 0.5D + perEntry * (double)class_3532.method_15363(snappingBias, 0.0F, 0.48F);
               if (Math.abs(delta) > hysteresis) {
                  this.selectedIndex = nearestIndex;
               }

            } else {
               this.selectedIndex = nearestIndex;
            }
         }
      }
   }

   public void updateAnimation(float deltaSeconds, float animationSpeed) {
      if (this.hoverAnimation.length != this.entries.size()) {
         this.hoverAnimation = new float[this.entries.size()];
      }

      float alpha = 1.0F - (float)Math.exp((double)(-Math.max(1.0F, animationSpeed) * Math.max(1.0E-4F, deltaSeconds)));

      for(int i = 0; i < this.hoverAnimation.length; ++i) {
         float target = i == this.selectedIndex ? 1.0F : 0.0F;
         float[] var10000 = this.hoverAnimation;
         var10000[i] += (target - this.hoverAnimation[i]) * alpha;
      }

   }

   public void render(class_332 context, int centerX, int centerY, int radius, int slotWidth, int slotHeight, int selectedFill, int normalFill, int selectedOutline, int normalOutline) {
      if (!this.entries.isEmpty()) {
         class_310 mc = class_310.method_1551();

         for(int i = 0; i < this.entries.size(); ++i) {
            RadialMenuComponent.Entry<T> entry = (RadialMenuComponent.Entry)this.entries.get(i);
            float hover = i < this.hoverAnimation.length ? this.hoverAnimation[i] : 0.0F;
            double angle = -1.5707963267948966D + (double)i * (6.283185307179586D / (double)this.entries.size());
            int renderRadius = (int)((float)radius + hover * 6.0F);
            int x = (int)((double)centerX + Math.cos(angle) * (double)renderRadius) - slotWidth / 2;
            int y = (int)((double)centerY + Math.sin(angle) * (double)renderRadius) - slotHeight / 2;
            context.method_51448().pushMatrix();
            float scale = 1.0F + hover * 0.06F;
            float pivotX = (float)x + (float)slotWidth / 2.0F;
            float pivotY = (float)y + (float)slotHeight / 2.0F;
            context.method_51448().translate(pivotX, pivotY);
            context.method_51448().scale(scale, scale);
            context.method_51448().translate(-pivotX, -pivotY);
            boolean selected = i == this.selectedIndex;
            context.method_25294(x, y, x + slotWidth, y + slotHeight, selected ? selectedFill : normalFill);
            context.method_73198(x, y, slotWidth, slotHeight, selected ? selectedOutline : normalOutline);
            class_327 var10001 = mc.field_1772;
            String var10002 = entry.label();
            int var10003 = x + slotWidth / 2;
            Objects.requireNonNull(mc.field_1772);
            context.method_25300(var10001, var10002, var10003, y + (slotHeight - 9) / 2 + 1, -1);
            context.method_51448().popMatrix();
         }

      }
   }

   private static double wrapAngle(double angle) {
      while(angle <= -3.141592653589793D) {
         angle += 6.283185307179586D;
      }

      while(angle > 3.141592653589793D) {
         angle -= 6.283185307179586D;
      }

      return angle;
   }

   public static record Entry<T>(String label, T value) {
      public Entry(String label, T value) {
         this.label = label;
         this.value = value;
      }

      public String label() {
         return this.label;
      }

      public T value() {
         return this.value;
      }
   }
}
