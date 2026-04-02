package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.list.LocalServerListWidget;
import com.dwarslooper.cactus.client.systems.localserver.LocalServerManager;
import com.dwarslooper.cactus.client.util.CactusConstants;
import net.minecraft.class_11908;
import net.minecraft.class_156;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_5244;
import org.jetbrains.annotations.NotNull;

public class LocalServerListScreen extends CScreen {
   private LocalServerListWidget serverListWidget;

   public LocalServerListScreen(class_437 parent) {
      super("local_servers");
      this.parent = parent;
   }

   public void method_25426() {
      super.method_25426();
      this.method_37063(this.serverListWidget = new LocalServerListWidget(this));
      this.method_37063(new CButtonWidget(this.field_22789 / 2 - 120 - 60 - 4, this.field_22790 - 32, 120, 20, class_2561.method_43471("gui.screen.cactus.button.folder"), (button) -> {
         class_156.method_668().method_672(LocalServerManager.get().getDirectory());
      }));
      this.method_37063(new CButtonWidget(this.field_22789 / 2 - 60, this.field_22790 - 32, 120, 20, class_2561.method_43470(this.getTranslatableElement("createServer", new Object[0])), (button) -> {
         CactusConstants.mc.method_1507(new CreateLocalServerScreen(this));
      }));
      this.method_37063(new CButtonWidget(this.field_22789 / 2 + 60 + 4, this.field_22790 - 32, 120, 20, class_5244.field_24334, (button) -> {
         this.method_25419();
      }));
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 8, -1);
   }

   public boolean method_25404(@NotNull class_11908 input) {
      return super.method_25404(input) || this.serverListWidget.method_25404(input);
   }
}
