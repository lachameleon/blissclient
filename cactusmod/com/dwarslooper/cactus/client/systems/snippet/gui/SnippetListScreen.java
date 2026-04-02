package com.dwarslooper.cactus.client.systems.snippet.gui;

import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.mixinterface.IChatScreenSaveStateImpl;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;

public class SnippetListScreen extends CScreen {
   private final IChatScreenSaveStateImpl screen;

   public SnippetListScreen(IChatScreenSaveStateImpl pasteTo) {
      super("snippets");
      this.screen = pasteTo;
   }

   public void pasteToChat(String text) {
      this.screen.cactus$setSaveState(text);
      this.method_25419();
   }

   public void method_25426() {
      super.method_25426();
      SnippetListWidget widget = new SnippetListWidget(this);
      class_342 searchWidget = new class_342(CactusConstants.mc.field_1772, this.field_22789 / 2 - 100, (this.field_22790 - 200) / 2 - 24, 200, 20, class_2561.method_43473());
      searchWidget.method_1863((s) -> {
         searchWidget.method_1887(s.isEmpty() ? this.getTranslatableElement("search", new Object[0]) : "");
         widget.updateFilter(s);
      });
      searchWidget.method_1852("");
      this.method_37063(searchWidget);
      this.method_37063(widget);
      this.method_48265(searchWidget);
   }

   public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 8, -1);
   }
}
