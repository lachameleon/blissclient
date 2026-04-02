package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.gui.screen.ITranslatable;
import com.dwarslooper.cactus.client.systems.config.CactusSystemConfig;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_5244;
import net.minecraft.class_7065;
import net.minecraft.class_8133;
import net.minecraft.class_8667;
import org.jetbrains.annotations.NotNull;

public class CheatWarningScreen extends class_7065 implements ITranslatable {
   private final class_437 parent;
   private final BooleanConsumer callback;

   public CheatWarningScreen(class_437 parent, class_2561 message, BooleanConsumer callback) {
      super(class_2561.method_43471("gui.screen.unsafeWarning.title").method_27694((style) -> {
         return style.method_10977(class_124.field_1061).method_10982(true);
      }), message, class_2561.method_43471("gui.screen.unsafeWarning.dismiss"), class_2561.method_43473());
      this.parent = parent;
      this.callback = callback;
   }

   public void method_25419() {
      this.field_22787.method_1507(this.parent);
   }

   @NotNull
   protected class_8133 method_57750() {
      class_8667 directionalLayoutWidget = class_8667.method_52741().method_52735(8);
      directionalLayoutWidget.method_52740().method_46467();
      class_8667 directionalLayoutWidget2 = (class_8667)directionalLayoutWidget.method_52736(class_8667.method_52742().method_52735(8));
      directionalLayoutWidget2.method_52736(class_4185.method_46430(class_5244.field_24339, (button) -> {
         this.callback.accept(false);
         this.method_25419();
      }).method_46431());
      directionalLayoutWidget2.method_52736(class_4185.method_46430(class_5244.field_24338, (button) -> {
         if (this.field_37217.method_20372()) {
            CactusSystemConfig.skipModuleCheatWarning = true;
         }

         this.callback.accept(true);
         this.method_25419();
      }).method_46431());
      return directionalLayoutWidget;
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
   }

   public String getKey() {
      return "unsafeWarning";
   }
}
