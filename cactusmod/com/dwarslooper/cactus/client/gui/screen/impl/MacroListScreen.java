package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.list.MacroListWidget;
import com.dwarslooper.cactus.client.util.CactusConstants;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_5244;
import org.jetbrains.annotations.NotNull;

public class MacroListScreen extends CScreen {
   private MacroListWidget macroList;

   public MacroListScreen(class_437 parent) {
      super("macros");
      this.parent = parent;
   }

   public void method_25426() {
      super.method_25426();
      this.macroList = new MacroListWidget(this);
      this.method_37063(this.macroList);
      this.method_37063(new CButtonWidget(this.field_22789 / 2 - 150 - 2, this.field_22790 - 32, 150, 20, class_2561.method_30163(this.getTranslatableElement("create", new Object[0])), (button) -> {
         CactusConstants.mc.method_1507(new MacroEditScreen(CactusConstants.mc.field_1755));
      }));
      this.method_37063(new CButtonWidget(this.field_22789 / 2 + 2, this.field_22790 - 32, 150, 20, class_5244.field_24334, (button) -> {
         this.method_25419();
      }));
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 8, -1);
   }
}
