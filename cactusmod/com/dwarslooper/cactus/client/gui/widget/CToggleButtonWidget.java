package com.dwarslooper.cactus.client.gui.widget;

import java.util.function.Consumer;
import net.minecraft.class_11907;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_4185;
import net.minecraft.class_332.class_12228;
import org.jetbrains.annotations.NotNull;

public class CToggleButtonWidget extends class_4185 {
   public boolean isToggled;
   private final Consumer<Boolean> onChange;

   public CToggleButtonWidget(int x, int y, int width, int height, class_2561 message, Consumer<Boolean> onChange) {
      super(x, y, width, height, message, (button) -> {
      }, class_4185.field_40754);
      this.onChange = onChange;
   }

   public void method_25306(@NotNull class_11907 input) {
      this.isToggled = !this.isToggled;
      this.onChange.accept(this.isToggled);
   }

   protected void method_75752(@NotNull class_332 context, int mouseX, int mouseY, float deltaTicks) {
      this.method_75794(context);
      this.method_75793(context.method_75787(this, class_12228.field_63850));
      if (this.isToggled) {
         context.method_73198(this.method_46426(), this.method_46427(), this.method_25368(), this.method_25364(), -1);
      }

   }
}
