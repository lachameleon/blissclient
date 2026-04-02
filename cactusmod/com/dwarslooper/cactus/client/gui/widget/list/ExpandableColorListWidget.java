package com.dwarslooper.cactus.client.gui.widget.list;

import com.dwarslooper.cactus.client.gui.screen.impl.ColorPickerScreen;
import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.render.RenderHelper;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.ColorUtils;
import com.dwarslooper.cactus.client.util.generic.WidgetIcons;
import com.google.common.collect.ImmutableList;
import java.awt.Color;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_4265;
import net.minecraft.class_6379;
import net.minecraft.class_4265.class_4266;
import org.jetbrains.annotations.NotNull;

public class ExpandableColorListWidget extends class_4265<ExpandableColorListWidget.Entry> {
   private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("#?\\p{XDigit}{0,8}");
   private String placeholderText;
   private final ExpandableColorListWidget.ColorScreenOpener screenOpener;
   private final boolean includePlaceholder;
   private Consumer<List<String>> changeListener;

   public ExpandableColorListWidget(int width, int height, int y, ExpandableColorListWidget.ColorScreenOpener screenOpener) {
      this(width, height, y, screenOpener, true);
   }

   public ExpandableColorListWidget(int width, int height, int y, ExpandableColorListWidget.ColorScreenOpener screenOpener, boolean includePlaceholder) {
      super(CactusConstants.mc, width, height, y, 24);
      this.placeholderText = "Add ..";
      this.screenOpener = screenOpener;
      this.includePlaceholder = includePlaceholder;
      if (includePlaceholder) {
         this.method_25321(new ExpandableColorListWidget.PlaceholderEntry(this.field_22740, this));
      }

   }

   public void setChangeListener(Consumer<List<String>> changeListener) {
      this.changeListener = changeListener;
   }

   private void notifyChange() {
      if (this.changeListener != null) {
         this.changeListener.accept(this.getList());
      }

   }

   public ExpandableColorListWidget.Entry add(String value) {
      ExpandableColorListWidget.Entry entry = new ExpandableColorListWidget.Entry(this.field_22740, this, normalizeHex(value));
      ExpandableColorListWidget.PlaceholderEntry placeholder = this.includePlaceholder ? (ExpandableColorListWidget.PlaceholderEntry)this.method_25396().getLast() : null;
      if (placeholder != null) {
         this.method_25330(placeholder);
      }

      this.method_25321(entry);
      if (placeholder != null) {
         this.method_25321(placeholder);
      }

      this.notifyChange();
      return entry;
   }

   protected void addValueFromPlaceHolder(String value, ExpandableColorListWidget.PlaceholderEntry placeHolder) {
      ExpandableColorListWidget.Entry entry = this.add(value);
      placeHolder.method_25395((class_364)null);
      entry.hexWidget.method_46421(placeHolder.hexWidget.method_46426());
      entry.hexWidget.method_46419(placeHolder.hexWidget.method_46427());
      entry.method_25395(entry.hexWidget);
      this.method_25395(entry);
   }

   protected void remove(ExpandableColorListWidget.Entry entry) {
      this.method_25330(entry);
      this.method_65506();
      this.notifyChange();
   }

   protected int method_65507() {
      return this.method_55442() - 6;
   }

   public List<String> getList() {
      return this.method_25396().stream().filter((entry) -> {
         return !(entry instanceof ExpandableColorListWidget.PlaceholderEntry);
      }).map(ExpandableColorListWidget.Entry::getHex).toList();
   }

   public void setPlaceholderText(String text) {
      this.placeholderText = text;
   }

   public int method_25322() {
      return this.field_22758 - 12;
   }

   public int method_25342() {
      return this.method_46426() + 4;
   }

   private static String normalizeHex(String raw) {
      if (raw == null) {
         return "#FFFFFF";
      } else {
         String s = raw.trim();
         if (s.isEmpty()) {
            return "#FFFFFF";
         } else {
            if (!s.startsWith("#")) {
               s = "#" + s;
            }

            return s;
         }
      }
   }

   private static boolean isCompleteHexColor(String raw) {
      if (raw == null) {
         return false;
      } else {
         String s = raw.trim();
         if (s.isEmpty()) {
            return false;
         } else {
            if (!s.startsWith("#")) {
               s = "#" + s;
            }

            return s.length() != 7 && s.length() != 9 ? false : HEX_COLOR_PATTERN.matcher(s).matches();
         }
      }
   }

   private static Color parseColor(String raw) {
      try {
         return Color.decode(normalizeHex(raw));
      } catch (Exception var2) {
         return Color.RED;
      }
   }

   public interface ColorScreenOpener {
      void open(ColorPickerScreen var1);
   }

   public class PlaceholderEntry extends ExpandableColorListWidget.Entry {
      public PlaceholderEntry(class_310 client, ExpandableColorListWidget owner) {
         super(client, owner, "");
         super.editButton.field_22764 = false;
         super.deleteButton.field_22764 = false;
      }

      public void method_25343(@NotNull class_332 context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
         this.hexWidget.method_1887(ExpandableColorListWidget.this.placeholderText);
         String text = this.hexWidget.method_1882();
         if (ExpandableColorListWidget.isCompleteHexColor(text)) {
            ExpandableColorListWidget.this.addValueFromPlaceHolder(text, this);
            this.hexWidget.method_1852("");
         }

         super.method_25343(context, mouseX, mouseY, hovered, deltaTicks);
      }
   }

   public class Entry extends class_4266<ExpandableColorListWidget.Entry> {
      public final ExpandableColorListWidget owner;
      public final class_342 hexWidget;
      public final class_4185 editButton;
      public final class_4185 deleteButton;

      public Entry(class_310 client, ExpandableColorListWidget owner, String text) {
         this.owner = owner;
         this.hexWidget = new class_342(client.field_1772, 0, 0, ExpandableColorListWidget.this.method_25322() - 20 - 20 - 4 - 24, 20, class_2561.method_30163(""));
         this.hexWidget.method_1880(9);
         this.hexWidget.method_1852(text);
         this.hexWidget.method_1890((string) -> {
            return ExpandableColorListWidget.HEX_COLOR_PATTERN.matcher(string).matches();
         });
         this.hexWidget.method_1863((s) -> {
            owner.notifyChange();
         });
         this.editButton = new CTextureButtonWidget(0, 0, WidgetIcons.EDIT.offsetX(), (button) -> {
            Color current = ExpandableColorListWidget.parseColor(this.hexWidget.method_1882());
            ColorPickerScreen screen = new ColorPickerScreen(current, false, (c) -> {
               class_342 var10000 = this.hexWidget;
               int var10001 = c.getRGB();
               var10000.method_1852("#" + ColorUtils.getHex(var10001, true));
               owner.notifyChange();
            });
            if (ExpandableColorListWidget.this.screenOpener != null) {
               ExpandableColorListWidget.this.screenOpener.open(screen);
            } else {
               CactusConstants.mc.method_1507(screen);
            }

         });
         this.deleteButton = new CTextureButtonWidget(0, 0, WidgetIcons.DELETE.offsetX(), (button) -> {
            this.owner.remove(this);
         });
      }

      protected String getHex() {
         return ExpandableColorListWidget.normalizeHex(this.hexWidget.method_1882());
      }

      public void method_25343(@NotNull class_332 context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
         int x = this.method_46426();
         int y = this.method_46427();
         int entryWidth = this.method_25368();
         int previewSize = 18;
         int previewY = y + 1;
         Color c = ExpandableColorListWidget.parseColor(this.hexWidget.method_1882());
         RenderHelper.drawBorder(context, x, previewY, previewSize, previewSize, -8355712);
         context.method_25294(x + 1, previewY + 1, x + previewSize - 1, previewY + previewSize - 1, c.getRGB());
         int contentX = x + previewSize + 6;
         int rightButtons = 48;
         this.hexWidget.method_48229(contentX, y);
         this.hexWidget.method_25358(entryWidth - (contentX - x) - rightButtons);
         this.hexWidget.method_25394(context, mouseX, mouseY, deltaTicks);
         this.editButton.method_48229(contentX + this.hexWidget.method_25368() + 4, y);
         this.editButton.method_25394(context, mouseX, mouseY, deltaTicks);
         this.deleteButton.method_48229(this.editButton.method_46426() + 24, y);
         this.deleteButton.method_25394(context, mouseX, mouseY, deltaTicks);
      }

      @NotNull
      public List<? extends class_6379> method_37025() {
         return ImmutableList.of(this.hexWidget, this.editButton, this.deleteButton);
      }

      @NotNull
      public List<? extends class_364> method_25396() {
         return ImmutableList.of(this.hexWidget, this.editButton, this.deleteButton);
      }
   }
}
