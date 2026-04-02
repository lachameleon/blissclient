package com.dwarslooper.cactus.client.gui.widget.list;

import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.util.CSimpleListEntry;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.List;
import net.minecraft.class_11909;
import net.minecraft.class_332;
import net.minecraft.class_4280;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FixedStringListWidget extends class_4280<FixedStringListWidget.StringListEntry> {
   public final CScreen parent;
   public String selectedString;
   private final int width;

   public FixedStringListWidget(CScreen parent, int width, int height, int y, int itemHeight) {
      super(CactusConstants.mc, width, height, y, itemHeight);
      this.parent = parent;
      this.width = width;
   }

   public void setSelected(@Nullable FixedStringListWidget.StringListEntry entry) {
      super.method_25313(entry);
      this.selectedString = entry != null ? entry.key : "none";
   }

   public int method_25322() {
      return this.width - 9;
   }

   public void addEntries(List<String> strings) {
      strings.forEach(this::add);
      this.method_44382(0.0D);
   }

   public void add(String key) {
      this.method_25321(new FixedStringListWidget.StringListEntry(this, key));
   }

   protected int method_65507() {
      return (this.width + this.parent.field_22789) / 2 - 4;
   }

   public void clear() {
      this.method_25339();
   }

   public static class StringListEntry extends CSimpleListEntry<FixedStringListWidget.StringListEntry> {
      FixedStringListWidget parent;
      public final String key;

      public StringListEntry(class_4280<FixedStringListWidget.StringListEntry> owner, String key) {
         super(owner);
         this.key = key;
         this.parent = (FixedStringListWidget)owner;
      }

      public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
         this.parent.setSelected(this);
         return true;
      }

      public void render(class_332 context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
         context.method_25303(CactusConstants.mc.field_1772, this.parent.parent.getTranslatableElement(this.key, new Object[0]), x + 16, y + 5, -1);
         super.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
      }
   }
}
