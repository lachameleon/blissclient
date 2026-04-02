package com.dwarslooper.cactus.client.gui.widget.list;

import com.dwarslooper.cactus.client.gui.screen.impl.NotificationsScreen;
import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.systems.NotificationManager;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_10799;
import net.minecraft.class_11909;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_4280;
import net.minecraft.class_5244;
import net.minecraft.class_5481;
import net.minecraft.class_4280.class_4281;
import org.jetbrains.annotations.NotNull;

public class MessageListWidget extends class_4280<MessageListWidget.Entry> {
   public List<NotificationManager.NotificationMessage> messages = new ArrayList();

   public MessageListWidget(int width, int height) {
      super(CactusConstants.mc, width, height, 40, 44);
      this.update();
   }

   public MessageListWidget add(NotificationManager.NotificationMessage message) {
      this.method_44399(new MessageListWidget.Entry(this, message));
      return this;
   }

   public void update() {
      this.method_25339();
      this.messages.clear();
      List<NotificationManager.NotificationMessage> messages = NotificationManager.get().messages;
      this.messages.addAll(messages);
      messages.forEach(this::add);
      this.method_44382(0.0D);
   }

   protected int method_65507() {
      return this.method_55442() - 6;
   }

   public int method_25322() {
      return 320;
   }

   public void method_48579(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_48579(context, mouseX, mouseY, delta);
      if (this.messages.isEmpty()) {
         context.method_27534(CactusConstants.mc.field_1772, class_2561.method_43471("gui.screen.notifications.noMessages").method_27692(class_124.field_1080), this.method_46426() + this.field_22758 / 2, this.method_46427() + this.field_22759 / 2, Color.WHITE.getRGB());
      }

   }

   public static class Entry extends class_4281<MessageListWidget.Entry> {
      private final MessageListWidget owner;
      private final NotificationManager.NotificationMessage message;
      private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");

      public Entry(MessageListWidget owner, NotificationManager.NotificationMessage message) {
         this.owner = owner;
         this.message = message;
      }

      @NotNull
      public class_2561 method_37006() {
         return class_2561.method_43473();
      }

      public void method_25343(class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
         context.method_27535(CactusConstants.mc.field_1772, class_5244.field_39003.method_27661().method_10852(class_2561.method_43470(this.message.getTitle()).method_27695(new class_124[]{class_124.field_1067, this.message.isRead() ? class_124.field_1080 : class_124.field_1068})).method_10852(class_5244.field_41874).method_10852(class_2561.method_43470("(%s)".formatted(new Object[]{this.dateFormat.format(new Date(this.message.getTimestamp()))})).method_27695(new class_124[]{class_124.field_1063, class_124.field_1056})).method_10852(class_5244.field_41874).method_10852(class_2561.method_43469("gui.screen.notifications.byAuthor", new Object[]{this.message.getAuthor()}).method_27692(class_124.field_1063)), this.method_73380() + 2, this.method_73382(), Color.WHITE.getRGB());
         List<class_5481> list = CactusConstants.mc.field_1772.method_1728(class_2561.method_43470(this.message.getContent()).method_27692(this.message.isRead() ? class_124.field_1080 : class_124.field_1068), this.method_73387() - 4);

         for(int i = 0; i < Math.min(list.size(), 3); ++i) {
            class_327 var10001 = CactusConstants.mc.field_1772;
            class_5481 var10002 = (class_5481)list.get(i);
            int var10003 = this.method_73380() + 2;
            int var10004 = this.method_73382() + 10;
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            context.method_51430(var10001, var10002, var10003, var10004 + 9 * i, Color.WHITE.getRGB(), false);
         }

         context.method_51448().pushMatrix();
         context.method_51448().scale(0.5F, 0.5F);
         context.method_25290(class_10799.field_56883, CTextureButtonWidget.WIDGETS_TEXTURE, (int)((float)(this.method_73380() + this.method_73387() - 10) / 0.5F), (int)((float)this.method_73382() / 0.5F), 200.0F, 0.0F, 20, 20, 640, 64);
         context.method_51448().popMatrix();
      }

      public boolean method_25402(class_11909 click, boolean doubled) {
         double mouseX = click.comp_4798();
         double mouseY = click.comp_4799();
         double x = mouseX - (double)this.owner.method_25342();
         double y = mouseY - (double)this.owner.method_25337(this.owner.method_25396().indexOf(this));
         if (x > (double)(this.owner.method_25322() - 16) && y < 10.0D) {
            NotificationManager.get().messages.remove(this.message);
            this.owner.update();
            return true;
         } else {
            if (!doubled) {
               CactusConstants.mc.method_1507(new NotificationsScreen.ViewMessageScreen(this.message));
               this.message.setRead(true);
            }

            return true;
         }
      }
   }
}
