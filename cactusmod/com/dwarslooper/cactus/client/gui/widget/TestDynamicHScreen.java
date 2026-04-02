package com.dwarslooper.cactus.client.gui.widget;

import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.ColorUtils;
import java.awt.Color;
import java.util.Random;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;

public class TestDynamicHScreen extends class_437 {
   private static final Random random = new Random();

   public TestDynamicHScreen() {
      super(class_2561.method_43470("wtf"));
   }

   protected void method_25426() {
      super.method_25426();
      TestDynamicHScreen.Widget w;
      this.method_37063(w = new TestDynamicHScreen.Widget(this.field_22789, this.field_22790 - 80, 60));

      for(int i = 0; i < 20; ++i) {
         int r = 20 + random.nextInt(41);
         w.addEntry(new TestDynamicHScreen.Widget.Entry(r));
      }

   }

   public static class Widget extends DynamicHeightScrollableWidget<TestDynamicHScreen.Widget.Entry> {
      public Widget(int width, int height, int y) {
         super(CactusConstants.mc, width, height, y);
      }

      protected int addEntry(TestDynamicHScreen.Widget.Entry entry) {
         return super.method_25321(entry);
      }

      public static class Entry extends DynamicHeightScrollableWidget.DynamicEntry<TestDynamicHScreen.Widget.Entry> {
         private final Color color = ColorUtils.randomColor();

         public Entry(int i) {
            super(i);
         }

         public void render(class_332 context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.method_27535(CactusConstants.mc.field_1772, class_2561.method_43470("Height: " + this.method_25364()), x, y, -1);
            context.method_25294(x, y, x + entryWidth, y + entryHeight, (hovered ? this.color.brighter().brighter() : this.color).getRGB());
         }
      }
   }
}
