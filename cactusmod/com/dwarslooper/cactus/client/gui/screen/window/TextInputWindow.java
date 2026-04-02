package com.dwarslooper.cactus.client.gui.screen.window;

import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.awt.Color;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.class_11908;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_5244;
import org.jetbrains.annotations.NotNull;

public class TextInputWindow extends WindowScreen {
   private final String title;
   private class_342 textFieldWidget;
   private CButtonWidget submitButton;
   private String placeholder;
   private String initialText;
   private Consumer<String> onChange;
   private Predicate<String> textPredicate;
   private Consumer<String> submitConsumer;
   private int maxLength = Integer.MAX_VALUE;
   private int minLength = 0;
   private boolean allowEmptyText = true;

   public TextInputWindow(String key, String text) {
      super(key, 200, 64);
      this.title = text;
   }

   public void method_25426() {
      super.method_25426();
      this.method_37063(this.textFieldWidget = new class_342(CactusConstants.mc.field_1772, this.x() + 4, this.y() + 16, this.boxWidth() - 8, 16, class_2561.method_43470(this.title)));
      this.textFieldWidget.method_1863((s) -> {
         this.textFieldWidget.method_1887(s.isEmpty() && this.placeholder != null ? this.placeholder : "");
         this.submitButton.field_22763 = this.buttonActive();
         if (this.onChange != null) {
            this.onChange.accept(s);
         }

      });
      if (this.textPredicate != null) {
         this.textFieldWidget.method_1890(this.textPredicate);
      }

      this.textFieldWidget.method_1880(this.maxLength);
      this.method_37063(this.submitButton = new CButtonWidget(this.x() + this.boxWidth() - 64, this.y() + 40, 60, 20, class_5244.field_44914, (button) -> {
         if (this.submitConsumer != null) {
            this.submitConsumer.accept(this.textFieldWidget.method_1882());
         }

         if (CactusConstants.mc.field_1755 == this) {
            this.method_25419();
         }

      }, this.buttonActive()));
      this.method_37063(new CButtonWidget(this.x() + 4, this.y() + 40, 60, 20, class_5244.field_24335, (button) -> {
         this.method_25419();
      }));
      this.textFieldWidget.method_1852(this.initialText != null ? this.initialText : "");
      this.method_48265(this.textFieldWidget);
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_25300(CactusConstants.mc.field_1772, this.title, this.field_22789 / 2, this.y() + 4, Color.WHITE.getRGB());
   }

   public boolean method_25404(@NotNull class_11908 input) {
      if (this.method_25399() != this.textFieldWidget || !this.buttonActive() || input.comp_4795() != 257 && input.comp_4795() != 335) {
         return super.method_25404(input);
      } else {
         this.submitButton.method_25306(input);
         return true;
      }
   }

   private boolean buttonActive() {
      return this.allowEmptyText || !this.textFieldWidget.method_1882().isEmpty() && this.textFieldWidget.method_1882().length() >= this.minLength;
   }

   public TextInputWindow setPlaceholder(String placeholder) {
      this.placeholder = placeholder;
      return this;
   }

   public TextInputWindow setInitialText(String text) {
      this.initialText = text;
      return this;
   }

   public TextInputWindow setChangeListener(Consumer<String> listener) {
      this.onChange = listener;
      return this;
   }

   public TextInputWindow onSubmit(Consumer<String> submitConsumer) {
      this.submitConsumer = submitConsumer;
      return this;
   }

   public TextInputWindow allowEmptyText(boolean allow) {
      this.allowEmptyText = allow;
      return this;
   }

   public TextInputWindow setPredicate(Predicate<String> textPredicate) {
      this.textPredicate = textPredicate;
      return this;
   }

   public TextInputWindow range(int min, int max) {
      this.minLength = min;
      this.maxLength = max;
      return this;
   }
}
