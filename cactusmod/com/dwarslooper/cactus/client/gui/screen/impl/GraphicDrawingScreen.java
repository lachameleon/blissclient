package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.gui.screen.window.WindowScreen;
import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Graphic2DSetting;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.WidgetIcons;
import java.awt.Color;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.class_11909;
import net.minecraft.class_332;
import net.minecraft.class_4185;
import net.minecraft.class_5244;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class GraphicDrawingScreen extends WindowScreen {
   private static final int pixelSize = 8;
   private static final int gridColorLight = -1429418804;
   private static final int gridColorDark = -1432774247;
   private final Graphic2DSetting.Graphic2D graphic;
   private final Consumer<Graphic2DSetting.Graphic2D> callback;
   private int drawColor = -1;
   private boolean isDrawing = false;

   public GraphicDrawingScreen(Graphic2DSetting.Graphic2D value, Consumer<Graphic2DSetting.Graphic2D> callback) {
      super("graphicDrawer", value.getWidth() * 8 + 26, value.getWidth() * 8 + 26);
      this.graphic = value;
      this.callback = callback;
   }

   public void method_25426() {
      super.method_25426();
      this.method_37063(new CTextureButtonWidget(this.x() + 2, this.y() + 2, WidgetIcons.EDIT.offsetX(), (button) -> {
         CactusConstants.mc.method_1507(new ColorPickerScreen(new Color(this.drawColor), (color) -> {
            this.drawColor = color.getRGB();
         }));
      }));
      this.method_37063(class_4185.method_46430(class_5244.field_24334, (button) -> {
         this.graphic.rebuildTexture();
         this.callback.accept(this.graphic);
         this.method_25419();
      }).method_46433(this.x() + this.boxWidth() - 62, this.y() + this.boxHeight() - 22).method_46432(60).method_46431());
      this.method_37063(class_4185.method_46430(class_5244.field_24335, (button) -> {
         this.method_25419();
      }).method_46433(this.x() + 2, this.y() + this.boxHeight() - 22).method_46432(60).method_46431());
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      int gridWidth = this.graphic.getWidth() * 8;
      int startX = this.x() + this.boxWidth() - (gridWidth + 3);
      int startY = this.y() + 3;

      int y;
      int x;
      for(y = 0; y < this.graphic.getHeight(); ++y) {
         for(x = 0; x < this.graphic.getWidth(); ++x) {
            int color = (x + y) % 2 == 0 ? -1429418804 : -1432774247;
            this.fillRect(context, startX + x * 8, startY + y * 8, 8, 8, color);
         }
      }

      for(y = 0; y < this.graphic.getHeight(); ++y) {
         for(x = 0; x < this.graphic.getWidth(); ++x) {
            if (this.graphic.get(x, y) != 0) {
               this.fillRect(context, startX + x * 8, startY + y * 8, 8, 8, this.graphic.get(x, y));
            }
         }
      }

   }

   public boolean method_25402(class_11909 click, boolean doubled) {
      double mouseX = click.comp_4798();
      double mouseY = click.comp_4799();
      int button = click.comp_4800().comp_4801();
      if (!super.method_25402(click, doubled)) {
         if (this.isControlDown()) {
            this.pickColor(mouseX, mouseY);
            return true;
         }

         this.isDrawing = true;
         if (button == 0 || button == 1) {
            this.handleDraw(mouseX, mouseY, button == 1);
            return true;
         }
      }

      return false;
   }

   public boolean method_25406(@NotNull class_11909 click) {
      this.isDrawing = false;
      return super.method_25406(click);
   }

   public boolean method_25403(class_11909 click, double deltaX, double deltaY) {
      double mouseX = click.comp_4798();
      double mouseY = click.comp_4799();
      int button = click.comp_4800().comp_4801();
      if (!super.method_25403(click, deltaX, deltaY) && this.isDrawing && (button == 0 || button == 1)) {
         this.handleDraw(mouseX, mouseY, button == 1);
         return true;
      } else {
         return false;
      }
   }

   private void onPixelAt(double mouseX, double mouseY, BiConsumer<Integer, Integer> callback) {
      int gridWidth = this.graphic.getWidth() * 8;
      int startX = this.x() + this.boxWidth() - (gridWidth + 3);
      int startY = this.y() + 3;
      int x = (int)((mouseX - (double)startX) / 8.0D);
      int y = (int)((mouseY - (double)startY) / 8.0D);
      if (x >= 0 && x < this.graphic.getWidth() && y >= 0 && y < this.graphic.getHeight()) {
         callback.accept(x, y);
      }

   }

   private void handleDraw(double mouseX, double mouseY, boolean erase) {
      this.onPixelAt(mouseX, mouseY, (x, y) -> {
         this.graphic.paint(x, y, erase ? 0 : this.drawColor);
      });
   }

   private void pickColor(double mouseX, double mouseY) {
      this.onPixelAt(mouseX, mouseY, (x, y) -> {
         this.drawColor = this.graphic.get(x, y);
      });
   }

   private void fillRect(class_332 context, int x, int y, int width, int height, int color) {
      context.method_25294(x, y, x + width, y + height, color);
   }

   public void method_25419() {
      this.graphic.rebuildTexture();
      this.callback.accept(this.graphic);
      super.method_25419();
   }

   private boolean isControlDown() {
      long handle = CactusConstants.mc.method_22683().method_4490();
      return GLFW.glfwGetKey(handle, 341) == 1 || GLFW.glfwGetKey(handle, 345) == 1;
   }
}
