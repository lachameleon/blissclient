package com.dwarslooper.cactus.client.gui.widget.list;

import com.dwarslooper.cactus.client.gui.widget.CCheckboxWidget;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_364;
import net.minecraft.class_4265;
import net.minecraft.class_6379;
import net.minecraft.class_4265.class_4266;
import org.jetbrains.annotations.NotNull;

public class CheckboxListWidget<T> extends class_4265<CheckboxListWidget.Entry<T>> {
   public CheckboxListWidget(int width, int height, int y) {
      super(CactusConstants.mc, width, height, y, 24);
   }

   public Map<T, Boolean> getAsMap() {
      return (Map)this.method_25396().stream().collect(Collectors.toMap(CheckboxListWidget.Entry::getObject, CheckboxListWidget.Entry::isSelected));
   }

   public void add(T object, class_2561 name, boolean value) {
      this.method_25321(new CheckboxListWidget.Entry(object, name, value));
   }

   protected int method_65507() {
      return this.method_55442() - 6;
   }

   public int method_25322() {
      return this.field_22758 - 12;
   }

   public int method_25342() {
      return this.method_46426() + 4;
   }

   protected static class Entry<T> extends class_4266<CheckboxListWidget.Entry<T>> {
      private final T object;
      private final class_2561 name;
      private final CCheckboxWidget checkbox;

      public Entry(T object, class_2561 name, boolean value) {
         this.object = object;
         this.name = name;
         this.checkbox = new CCheckboxWidget(0, 0, 20, 20, value, (w, b) -> {
         });
      }

      @NotNull
      public List<? extends class_6379> method_37025() {
         return ImmutableList.of(this.checkbox);
      }

      @NotNull
      public List<? extends class_364> method_25396() {
         return ImmutableList.of(this.checkbox);
      }

      public void method_25343(class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
         int x = this.method_73380();
         int y = this.method_73382();
         class_327 var10001 = CactusConstants.mc.field_1772;
         class_2561 var10002 = this.name;
         int var10005 = this.method_73384();
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         context.method_51439(var10001, var10002, x, y + (var10005 - 9) / 2 + 1, -1, false);
         this.checkbox.method_48229(x + this.method_73387() - 4 - this.checkbox.method_25368(), y);
         this.checkbox.method_25394(context, mouseX, mouseY, tickDelta);
      }

      public T getObject() {
         return this.object;
      }

      public boolean isSelected() {
         return this.checkbox.isChecked();
      }
   }
}
