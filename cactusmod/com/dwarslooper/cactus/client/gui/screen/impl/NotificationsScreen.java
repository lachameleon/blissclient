package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.widget.CScrollableWidget;
import com.dwarslooper.cactus.client.gui.widget.list.MessageListWidget;
import com.dwarslooper.cactus.client.systems.NotificationManager;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.ScreenUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_5481;
import org.jetbrains.annotations.NotNull;

public class NotificationsScreen extends CScreen {
   public MessageListWidget messageListWidget;
   public long unread;

   public NotificationsScreen(class_437 parent) {
      super("notifications");
      this.parent = parent;
   }

   public void method_25426() {
      super.method_25426();
      if (this.messageListWidget == null) {
         this.messageListWidget = new MessageListWidget(this.field_22789, this.field_22790 - 40 - 20);
      } else {
         this.messageListWidget.method_55444(this.field_22789, this.field_22790 - 40 - 20, 0, 40);
      }

      this.unread = this.messageListWidget.messages.stream().filter((msg) -> {
         return !msg.isRead();
      }).count();
      this.method_25429(this.messageListWidget);
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      this.messageListWidget.method_25394(context, mouseX, mouseY, delta);
      context.method_27534(this.field_22793, this.field_22785.method_27661().method_27693(this.unread > 0L ? " (%s)".formatted(new Object[]{this.getTranslatableElement("unread", new Object[]{this.unread})}) : ""), this.field_22789 / 2, 8, -1);
   }

   public static class ViewMessageScreen extends CScreen {
      private final NotificationManager.NotificationMessage message;
      private final List<class_5481> linesWrapped;

      public ViewMessageScreen(NotificationManager.NotificationMessage message) {
         super("view_message");
         this.message = message;
         this.linesWrapped = CactusConstants.mc.field_1772.method_1728(class_2561.method_43470(message.getContent()), 384);
      }

      public void method_25426() {
         super.method_25426();
         this.method_37063(new CScrollableWidget(this.field_22789 / 2 - 200, ScreenUtils.baseY + 10, 400, this.field_22790 - (ScreenUtils.baseY + 10) * 2 - 10, 10, class_2561.method_43470("Sieht man dass ühaupt?"), () -> {
            Objects.requireNonNull(this.field_22793);
            int h = 9 + 2;
            return h * this.linesWrapped.size() + 20;
         }, (context, mx, my, delta) -> {
            int y = ScreenUtils.baseY + 20;

            for(Iterator var6 = this.linesWrapped.iterator(); var6.hasNext(); y += 9 + 2) {
               class_5481 patchNote = (class_5481)var6.next();
               context.method_35720(this.field_22793, patchNote, this.field_22789 / 2 - 200 + 4, y, -1);
               Objects.requireNonNull(CactusConstants.mc.field_1772);
            }

         }));
      }

      public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
         super.method_25394(context, mouseX, mouseY, delta);
         context.method_27534(this.field_22793, class_2561.method_43470(this.message.getTitle()).method_27692(class_124.field_1067), this.field_22789 / 2, 8, -1);
      }
   }
}
