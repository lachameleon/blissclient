package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.gui.screen.ITranslatable;
import com.dwarslooper.cactus.client.util.CactusConstants;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_5244;
import net.minecraft.class_7065;
import net.minecraft.class_8133;
import net.minecraft.class_8667;
import org.jetbrains.annotations.Nullable;

public class ChoiceWarningScreen extends class_7065 implements ITranslatable {
   private final class_437 parent;
   private final BooleanConsumer callback;
   private final BooleanConsumer checked;

   public ChoiceWarningScreen(class_437 parent, class_2561 title, class_2561 message, @Nullable class_2561 checkMessage, @Nullable BooleanConsumer skip, BooleanConsumer callback) {
      super(title, message, checkMessage, class_2561.method_43473());
      this.parent = parent;
      this.checked = skip;
      this.callback = callback;
   }

   public ChoiceWarningScreen(class_437 parent, class_2561 message, @Nullable BooleanConsumer skip, BooleanConsumer callback) {
      this(parent, class_2561.method_43471("gui.screen.unsafeWarning.title").method_27695(new class_124[]{class_124.field_1061, class_124.field_1067}), message, skip != null ? class_2561.method_43471("gui.screen.unsafeWarning.dismiss") : null, skip, callback);
   }

   public ChoiceWarningScreen(class_437 parent, class_2561 message, BooleanConsumer callback) {
      this(parent, message, (BooleanConsumer)null, callback);
   }

   public void method_25419() {
      CactusConstants.mc.method_1507(this.parent);
   }

   private void accept(boolean accepted) {
      this.callback.accept(accepted);
      if (CactusConstants.mc.field_1755 == this) {
         this.method_25419();
      }

   }

   public class_8133 method_57750() {
      class_8667 directionalLayoutWidget = class_8667.method_52741().method_52735(8);
      directionalLayoutWidget.method_52740().method_46467();
      class_8667 directionalLayoutWidget2 = (class_8667)directionalLayoutWidget.method_52736(class_8667.method_52742().method_52735(8));
      directionalLayoutWidget2.method_52736(class_4185.method_46430(class_5244.field_24339, (button) -> {
         this.accept(false);
      }).method_46431());
      directionalLayoutWidget2.method_52736(class_4185.method_46430(class_5244.field_24338, (button) -> {
         if (this.field_37217 != null && this.field_37217.method_20372()) {
            this.checked.accept(true);
         }

         this.accept(true);
      }).method_46431());
      return directionalLayoutWidget;
   }

   public String getKey() {
      return "unsafeWarning";
   }
}
