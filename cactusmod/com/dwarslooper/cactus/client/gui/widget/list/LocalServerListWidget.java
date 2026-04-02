package com.dwarslooper.cactus.client.gui.widget.list;

import com.dwarslooper.cactus.client.gui.screen.impl.ConsoleScreen;
import com.dwarslooper.cactus.client.gui.screen.impl.LocalServerListScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.systems.localserver.LocalServerManager;
import com.dwarslooper.cactus.client.systems.localserver.instance.LocalServer;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.TextUtils;
import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_364;
import net.minecraft.class_4265;
import net.minecraft.class_6379;
import net.minecraft.class_4265.class_4266;
import org.jetbrains.annotations.NotNull;

public class LocalServerListWidget extends class_4265<LocalServerListWidget.LocalServerEntry> {
   public LocalServerManager manager = LocalServerManager.get();
   public LocalServerListScreen parent;

   public LocalServerListWidget(LocalServerListScreen parent) {
      super(CactusConstants.mc, parent.field_22789, parent.field_22790 - 80, 40, 22);
      this.parent = parent;
      this.manager.getServers().forEach((server) -> {
         this.method_25321(new LocalServerListWidget.LocalServerEntry(server));
      });
   }

   protected boolean method_73379() {
      return true;
   }

   public int method_25322() {
      return 120 + LocalServerListWidget.LocalServerEntry.widthOffset;
   }

   protected int method_65507() {
      return super.method_65507() + 10;
   }

   public class LocalServerEntry extends class_4266<LocalServerListWidget.LocalServerEntry> {
      private final LocalServer server;
      public static int widthOffset = 148;
      private final class_339 startStopWidget;
      private final class_339 consoleWidget;
      private final class_339 editWidget;

      public LocalServerEntry(LocalServer server) {
         this.server = server;
         this.startStopWidget = new CButtonWidget(0, 0, 60, 20, this::getButtonText, (button) -> {
            if (server.isOnline()) {
               server.scheduleStop().whenComplete((unused, throwable) -> {
                  button.method_25355(this.getButtonText());
               });
            } else {
               server.run();
            }

            button.method_25355(this.getButtonText());
         });
         this.consoleWidget = new CButtonWidget(0, 0, 60, 20, class_2561.method_43470("Console"), (button) -> {
            CactusConstants.mc.method_1507(new ConsoleScreen(server));
         });
         this.editWidget = new CTextureButtonWidget(0, 0, 220, (button) -> {
         });
      }

      public void method_25343(class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
         int x = this.method_73380();
         int y = this.method_73382();
         int entryWidth = LocalServerListWidget.this.method_25322();
         this.consoleWidget.field_22763 = this.server.isOnline();
         this.startStopWidget.method_48229(x + entryWidth - 23 - 124, y - 1);
         this.consoleWidget.method_48229(x + entryWidth - 23 - 62, y - 1);
         this.editWidget.method_48229(x + entryWidth - 23, y - 1);
         this.method_25396().forEach((e) -> {
            if (e instanceof class_339) {
               class_339 w = (class_339)e;
               w.method_25394(context, mouseX, mouseY, tickDelta);
            }

         });
         String text = TextUtils.trimToWidth(this.server.getName(), LocalServerListWidget.this.method_25322() - widthOffset - 2, "...");
         context.method_25303(CactusConstants.mc.field_1772, text, x, y + 5, -1);
      }

      public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
         super.method_25402(click, doubled);
         return true;
      }

      @NotNull
      public List<? extends class_364> method_25396() {
         return ImmutableList.of(this.startStopWidget, this.consoleWidget, this.editWidget);
      }

      @NotNull
      public List<? extends class_6379> method_37025() {
         return ImmutableList.of(this.startStopWidget, this.consoleWidget, this.editWidget);
      }

      private class_2561 getButtonText() {
         return class_2561.method_43470(this.server.isOnline() ? "Stop" : "Start");
      }
   }
}
