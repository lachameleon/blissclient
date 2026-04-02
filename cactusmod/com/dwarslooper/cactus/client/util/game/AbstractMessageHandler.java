package com.dwarslooper.cactus.client.util.game;

import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.function.Consumer;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_5244;
import net.minecraft.class_5250;
import net.minecraft.class_746;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMessageHandler {
   public abstract class_2561 getPrefix();

   public abstract class_2561 formatSubPrefix(String var1);

   public abstract class_2561 arrangeMessage(class_2561 var1, @Nullable class_2561 var2, class_2561 var3);

   public class_2561 arrangeMessage(class_2561 prefix, class_2561 message) {
      return this.arrangeMessage(prefix, (class_2561)null, message);
   }

   public void info(String message) {
      this.info((class_2561)class_2561.method_43470(message));
   }

   public void info(class_2561 text) {
      whenAvailable((p) -> {
         p.method_7353(this.arrangeMessage(this.getPrefix(), coloredIfAbsent(text.method_27661(), class_124.field_1080)), false);
      });
   }

   public void infoPrefix(String prefix, String message) {
      this.infoPrefix(prefix, (class_2561)class_2561.method_43470(message));
   }

   public void infoPrefix(String prefix, class_2561 text) {
      whenAvailable((p) -> {
         p.method_7353(this.arrangeMessage(this.getPrefix(), this.formatSubPrefix(prefix), coloredIfAbsent(text.method_27661(), class_124.field_1080)), false);
      });
   }

   public void warning(String message) {
      this.warning((class_2561)class_2561.method_43470(message));
   }

   public void warning(class_2561 text) {
      whenAvailable((p) -> {
         p.method_7353(this.arrangeMessage(this.getPrefix(), coloredIfAbsent(text.method_27661(), class_124.field_1054)), false);
      });
   }

   public void warningPrefix(String prefix, String message) {
      this.warningPrefix(prefix, (class_2561)class_2561.method_43470(message));
   }

   public void warningPrefix(String prefix, class_2561 text) {
      whenAvailable((p) -> {
         p.method_7353(this.arrangeMessage(this.getPrefix(), this.formatSubPrefix(prefix), coloredIfAbsent(text.method_27661(), class_124.field_1054)), false);
      });
   }

   public void error(String message) {
      this.error((class_2561)class_2561.method_43470(message));
   }

   public void error(class_2561 text) {
      whenAvailable((p) -> {
         p.method_7353(this.arrangeMessage(this.getPrefix(), coloredIfAbsent(text.method_27661(), class_124.field_1061)), false);
      });
   }

   public void errorPrefix(String prefix, String message) {
      this.errorPrefix(prefix, (class_2561)class_2561.method_43470(message));
   }

   public void errorPrefix(String prefix, class_2561 text) {
      whenAvailable((p) -> {
         p.method_7353(this.arrangeMessage(this.getPrefix(), this.formatSubPrefix(prefix), coloredIfAbsent(text.method_27661(), class_124.field_1061)), false);
      });
   }

   public void actionbar(String message) {
      this.actionbar((class_2561)class_2561.method_43470(message));
   }

   public void actionbar(class_2561 text) {
      whenAvailable((p) -> {
         p.method_7353(text, true);
      });
   }

   private class_5250 prefix() {
      return this.getPrefix().method_27661().method_10852(class_5244.field_41874);
   }

   private class_5250 subPrefix(String prefix) {
      return this.formatSubPrefix(prefix).method_27661().method_10852(class_5244.field_41874);
   }

   private static class_5250 coloredIfAbsent(class_5250 text, class_124 formatting) {
      return text.method_27694((style) -> {
         return style.method_10973() == null ? style.method_10977(formatting) : style;
      });
   }

   private static void whenAvailable(Consumer<class_746> func) {
      if (CactusConstants.mc.field_1724 != null) {
         CactusConstants.mc.execute(() -> {
            func.accept(CactusConstants.mc.field_1724);
         });
      }

   }
}
