package com.dwarslooper.cactus.client.gui.widget;

import com.dwarslooper.cactus.client.render.RenderHelper;
import com.dwarslooper.cactus.client.util.generic.QuadConsumer;
import java.util.function.Supplier;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_6382;
import net.minecraft.class_7528;
import org.jetbrains.annotations.NotNull;

public class CScrollableWidget extends class_7528 {
   private final int scrollSpeed;
   private final Supplier<Integer> contentHeight;
   private final QuadConsumer<class_332, Integer, Integer, Float> contentRenderer;

   public CScrollableWidget(int x, int y, int width, int height, int scrollSpeed, class_2561 message, Supplier<Integer> contentHeight, QuadConsumer<class_332, Integer, Integer, Float> contentRenderer) {
      super(x, y, width, height, message);
      this.contentHeight = contentHeight;
      this.contentRenderer = contentRenderer;
      this.scrollSpeed = scrollSpeed;
   }

   protected int method_44395() {
      return (Integer)this.contentHeight.get() + 4;
   }

   protected double method_44393() {
      return (double)this.scrollSpeed;
   }

   protected void method_48579(class_332 context, int mouseX, int mouseY, float deltaTicks) {
      context.method_51448().pushMatrix();
      context.method_25294(this.method_46426(), this.method_46427(), this.method_46426() + this.method_25368(), this.method_46427() + this.method_25364(), -16777216);
      RenderHelper.drawBorder(context, this.method_46426(), this.method_46427(), this.method_25368() - 6, this.method_25364(), -8421504);
      this.method_44396(context, mouseX, mouseY);
      context.method_44379(this.method_46426() + 1, this.method_46427() + 1, this.method_46426() + this.method_25368() - 1, this.method_46427() + this.method_25364() - 1);
      context.method_51448().translate(1.0F, 1.0F);
      context.method_51448().translate(0.0F, (float)(-this.method_44387()));
      this.contentRenderer.accept(context, mouseX, mouseY, deltaTicks);
      context.method_44380();
      context.method_51448().popMatrix();
   }

   public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
      boolean bl = this.method_65505(click);
      return super.method_25402(click, doubled) || bl;
   }

   protected void method_47399(@NotNull class_6382 builder) {
   }

   public void method_44382(double scrollY) {
      super.method_44382(scrollY);
   }
}
