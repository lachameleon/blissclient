package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.gui.screen.CScreen;
import java.util.Iterator;
import net.minecraft.class_11909;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_3675;
import net.minecraft.class_4068;

public class CMCStackerScreen extends CScreen {
   private boolean initialized;
   private float zoomValue = 0.7F;

   public CMCStackerScreen() {
      super("stacker");
   }

   public void method_25426() {
      super.method_25426();
      this.initialized = true;
   }

   public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (class_3675.method_15987(this.field_22787.method_22683(), 341) || class_3675.method_15987(this.field_22787.method_22683(), 345)) {
         this.zoomValue += (float)(verticalAmount / 10.0D);
      }

      return true;
   }

   public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
      context.method_51448().scale(this.zoomValue, this.zoomValue);
      Iterator var5 = this.field_33816.iterator();

      while(var5.hasNext()) {
         class_4068 drawable = (class_4068)var5.next();
         drawable.method_25394(context, (int)((float)mouseX / this.zoomValue), (int)((float)mouseY / this.zoomValue), delta);
      }

      context.method_51448().scale(1.0F / this.zoomValue, 1.0F / this.zoomValue);
   }

   public boolean method_25402(class_11909 click, boolean doubled) {
      double mouseX = (double)((int)(click.comp_4798() / (double)this.zoomValue));
      double mouseY = (double)((int)(click.comp_4799() / (double)this.zoomValue));
      class_11909 adjustedClick = new class_11909(mouseX, mouseY, click.comp_4800());
      return super.method_25402(adjustedClick, doubled);
   }

   public class_339 addWidget(class_339 widget) {
      if (this.initialized) {
         return (class_339)this.method_37063(widget);
      } else {
         this.field_33816.addFirst(widget);
         return this.addWidget(widget);
      }
   }
}
