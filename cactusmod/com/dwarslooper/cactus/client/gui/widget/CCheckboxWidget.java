package com.dwarslooper.cactus.client.gui.widget;

import java.util.function.BiConsumer;
import net.minecraft.class_10799;
import net.minecraft.class_11907;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_4264;
import net.minecraft.class_6382;
import org.jetbrains.annotations.NotNull;

public class CCheckboxWidget extends class_4264 {
   private static final class_2960 SELECTED_TEXTURE = class_2960.method_60656("widget/checkbox_selected");
   private static final class_2960 TEXTURE = class_2960.method_60656("widget/checkbox");
   private boolean checked;
   private final BiConsumer<CCheckboxWidget, Boolean> callback;

   public CCheckboxWidget(int x, int y, int width, int height, boolean checked, BiConsumer<CCheckboxWidget, Boolean> callback) {
      super(x, y, width, height, class_2561.method_43473());
      this.checked = checked;
      this.callback = callback;
   }

   public void method_75752(class_332 context, int mouseX, int mouseY, float delta) {
      class_2960 identifier = this.checked ? SELECTED_TEXTURE : TEXTURE;
      context.method_52706(class_10799.field_56883, identifier, this.method_46426(), this.method_46427(), this.field_22758, this.field_22759);
   }

   public void method_47399(@NotNull class_6382 builder) {
   }

   public void method_25306(@NotNull class_11907 input) {
      this.checked = !this.checked;
      this.callback.accept(this, this.checked);
   }

   public boolean isChecked() {
      return this.checked;
   }

   public void setChecked(boolean checked) {
      this.checked = checked;
   }
}
