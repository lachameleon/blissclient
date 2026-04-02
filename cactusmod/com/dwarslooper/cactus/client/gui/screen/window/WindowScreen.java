package com.dwarslooper.cactus.client.gui.screen.window;

import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.util.generic.ColorUtils;
import java.awt.Color;
import net.minecraft.class_10799;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import org.jetbrains.annotations.NotNull;

public class WindowScreen extends CScreen {
   private int boxWidth;
   private int boxHeight;
   public static final class_2960 BACKGROUND = class_2960.method_60655("cactus", "textures/gui/light_dirt_background.png");

   public WindowScreen(String key, int width, int height) {
      super(key);
      this.boxWidth = width;
      this.boxHeight = height;
   }

   public void method_25426() {
      super.init(false);
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      if (this.parent != null) {
         context.method_51448().pushMatrix();
         context.method_51448().translate(0.0F, 0.0F);
         this.parent.method_47413(context, this.field_22789, this.field_22790, delta);
         context.method_51448().popMatrix();
      }

      context.method_25294(0, 0, this.field_22789, this.field_22790, ColorUtils.withAlpha(Color.BLACK, 0.5F).getRGB());
      context.method_25290(class_10799.field_56883, BACKGROUND, this.x(), this.y(), 0.0F, 0.0F, this.boxWidth(), this.boxHeight(), 32, 32);
      context.method_73198(this.x(), this.y(), this.boxWidth, this.boxHeight, Color.GRAY.brighter().getRGB());
      super.method_25394(context, mouseX, mouseY, delta);
   }

   public void method_25410(int width, int height) {
      super.method_25410(width, height);
      if (this.parent != null) {
         this.parent.method_25410(width, height);
      }

   }

   public void method_25420(class_332 context, int mouseX, int mouseY, float delta) {
      context.method_44379(this.x() + 1, this.y() + 1, this.x() + this.boxWidth() - 1, this.y() + this.boxHeight() - 1);
      this.method_57735(context);
      context.method_44380();
   }

   public int x() {
      return (this.field_22789 - this.boxWidth) / 2;
   }

   public int y() {
      return (this.field_22790 - this.boxHeight) / 2;
   }

   public int boxWidth() {
      return this.boxWidth;
   }

   public int boxHeight() {
      return this.boxHeight;
   }

   public void setBoxWidth(int boxWidth) {
      this.boxWidth = boxWidth;
   }

   public void setBoxHeight(int boxHeight) {
      this.boxHeight = boxHeight;
   }
}
