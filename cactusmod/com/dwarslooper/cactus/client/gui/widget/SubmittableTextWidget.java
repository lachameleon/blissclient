package com.dwarslooper.cactus.client.gui.widget;

import java.util.function.Consumer;
import net.minecraft.class_11908;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_342;

public class SubmittableTextWidget extends class_342 {
   private final Consumer<String> submitConsumer;

   public SubmittableTextWidget(class_327 textRenderer, int x, int y, int width, int height, class_2561 text, Consumer<String> onSubmit) {
      super(textRenderer, x, y, width, height, text);
      this.submitConsumer = onSubmit;
   }

   public boolean method_25404(class_11908 input) {
      int keyCode = input.comp_4795();
      if (keyCode != 257 && keyCode != 335) {
         return super.method_25404(input);
      } else {
         this.submitConsumer.accept(this.method_1882());
         this.method_1852("");
         return true;
      }
   }

   public Consumer<String> getSubmitConsumer() {
      return this.submitConsumer;
   }
}
