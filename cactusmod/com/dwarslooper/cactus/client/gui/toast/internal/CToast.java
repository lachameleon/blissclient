package com.dwarslooper.cactus.client.gui.toast.internal;

import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_368;
import net.minecraft.class_374;
import net.minecraft.class_368.class_369;
import org.jetbrains.annotations.NotNull;

public abstract class CToast implements class_368 {
   public int posY;
   public long duration;
   public int width;
   private boolean shouldClose;
   protected class_369 visibility;

   public void method_1986(@NotNull class_332 context, @NotNull class_327 textRenderer, long startTime) {
      double mouseX = Utils.getMouseX();
      double mouseY = Utils.getMouseY();
      int x = CactusConstants.mc.method_22683().method_4486() - this.method_29049();
      this.drawToast(context, textRenderer, startTime, x, this.posY, mouseX, mouseY);
   }

   public void method_61989(@NotNull class_374 manager, long time) {
      if (this.updateTick(manager, time)) {
         this.close();
      }

      this.visibility = this.shouldClose() ? class_369.field_2209 : class_369.field_2210;
   }

   @NotNull
   public class_369 method_61988() {
      return this.visibility;
   }

   public void close() {
      this.shouldClose = true;
   }

   public boolean shouldClose() {
      return this.shouldClose;
   }

   public boolean updateTick(class_374 manager, long time) {
      return time >= this.duration;
   }

   public abstract void drawToast(class_332 var1, class_327 var2, long var3, int var5, int var6, double var7, double var9);

   public abstract boolean mouseClicked(double var1, double var3, int var5);
}
