package com.dwarslooper.cactus.client.gui.screen.window;

import java.awt.Color;
import java.util.function.Consumer;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_364;
import net.minecraft.class_4068;
import net.minecraft.class_437;
import net.minecraft.class_6379;
import org.jetbrains.annotations.NotNull;

public class IntegratedWindowScreen extends WindowScreen {
   private final Consumer<IntegratedWindowScreen> initConsumer;
   private final IntegratedWindowScreen.RenderConsumer renderConsumer;
   private final IntegratedWindowScreen.ClickConsumer clickConsumer;
   private class_2561 title;
   public static final Consumer<IntegratedWindowScreen> INIT_EMPTY = (s) -> {
   };
   public static final IntegratedWindowScreen.RenderConsumer RENDER_EMPTY = (s, c, x, y, d) -> {
   };
   public static final IntegratedWindowScreen.ClickConsumer CLICK_EMPTY = (s, x, y, b) -> {
      return false;
   };

   public IntegratedWindowScreen(class_437 parent, String key, int width, int height, Consumer<IntegratedWindowScreen> init, IntegratedWindowScreen.RenderConsumer render, IntegratedWindowScreen.ClickConsumer click) {
      super(key, width, height);
      this.parent = parent;
      this.initConsumer = init;
      this.renderConsumer = render;
      this.clickConsumer = click;
   }

   public void method_25426() {
      super.method_25426();
      this.initConsumer.accept(this);
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_27534(this.field_22793, this.title, this.x() + this.boxWidth() / 2, this.y() + 4, Color.WHITE.getRGB());
      this.renderConsumer.render(this, context, mouseX, mouseY, delta);
   }

   public boolean method_25402(class_11909 click, boolean doubled) {
      return this.clickConsumer.click(this, click.comp_4798(), click.comp_4799(), click.method_74245()) || super.method_25402(click, doubled);
   }

   public void addTitle(class_2561 title) {
      this.title = title;
   }

   public <T extends class_4068> T method_37060(T drawable) {
      return super.method_37060(drawable);
   }

   public <T extends class_364 & class_4068 & class_6379> T method_37063(T drawableElement) {
      return super.method_37063(drawableElement);
   }

   public <T extends class_364 & class_6379> T method_25429(T child) {
      return super.method_25429(child);
   }

   public void method_25419() {
      class_310.method_1551().method_1507(this.parent);
   }

   @FunctionalInterface
   public interface RenderConsumer {
      void render(IntegratedWindowScreen var1, class_332 var2, int var3, int var4, float var5);
   }

   @FunctionalInterface
   public interface ClickConsumer {
      boolean click(IntegratedWindowScreen var1, double var2, double var4, int var6);
   }
}
