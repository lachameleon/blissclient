package com.dwarslooper.cactus.client.gui.widget;

import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.Iterator;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_4280;
import net.minecraft.class_5481;
import net.minecraft.class_4280.class_4281;
import org.jetbrains.annotations.NotNull;

public class StyledTextListWidget extends class_4280<StyledTextListWidget.Entry> {
   private static final StyledTextListWidget.Entry EMPTY;

   public StyledTextListWidget(int width, int height, int y, int itemHeight) {
      super(CactusConstants.mc, width, height, y, itemHeight);
   }

   public void updateFromText(String text) {
      this.method_25339();
      if (text != null) {
         String[] var2 = text.split("\n");
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String s = var2[var4];
            if (s.isEmpty()) {
               this.addEntry(EMPTY);
            } else {
               Iterator var6 = CactusConstants.mc.field_1772.method_1728(class_2561.method_43470(s), this.method_25322() - 6).iterator();

               while(var6.hasNext()) {
                  class_5481 wrapLine = (class_5481)var6.next();
                  this.addEntry(new StyledTextListWidget.Entry(wrapLine));
               }
            }
         }

      }
   }

   public StyledTextListWidget.Entry getSelected() {
      return null;
   }

   public int method_25322() {
      return this.field_22758 - 10;
   }

   protected int method_65507() {
      return this.method_46426() + this.method_25368() - 6;
   }

   public int addEntry(StyledTextListWidget.Entry entry) {
      return super.method_25321(entry);
   }

   static {
      EMPTY = new StyledTextListWidget.Entry(class_5481.field_26385);
   }

   public static class Entry extends class_4281<StyledTextListWidget.Entry> {
      private final class_5481 text;
      private final int indent;

      public Entry(String text) {
         this(class_2561.method_43470(text).method_30937());
      }

      public Entry(class_5481 text) {
         this(text, 0);
      }

      public Entry(class_5481 text, int indent) {
         this.text = text;
         this.indent = indent;
      }

      @NotNull
      public class_2561 method_37006() {
         return class_2561.method_43473();
      }

      public void method_25343(@NotNull class_332 context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
      }

      public void render(class_332 context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
         context.method_35720(CactusConstants.mc.field_1772, this.text, x + this.indent, y, -1);
      }
   }

   public static class LinkEntry extends StyledTextListWidget.Entry {
      public LinkEntry(class_2561 text, int indent) {
         super(text.method_27661().method_27695(new class_124[]{class_124.field_1073, class_124.field_1078}).method_30937(), indent);
      }
   }
}
