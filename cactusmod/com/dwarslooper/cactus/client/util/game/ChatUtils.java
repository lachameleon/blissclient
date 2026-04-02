package com.dwarslooper.cactus.client.util.game;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.systems.config.settings.impl.ColorSetting;
import java.util.function.Supplier;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_5244;
import net.minecraft.class_5250;
import org.jetbrains.annotations.Nullable;

public class ChatUtils {
   private static final ChatUtils.Handler handler = new ChatUtils.Handler();

   public static void info(String message) {
      info((class_2561)class_2561.method_43470(message));
   }

   public static void info(class_2561 message) {
      handler.info(message);
   }

   public static void infoPrefix(String prefix, String message) {
      handler.infoPrefix(prefix, message);
   }

   public static void infoPrefix(String prefix, class_2561 message) {
      handler.infoPrefix(prefix, message);
   }

   public static void warning(String message) {
      handler.warning(message);
   }

   public static void warning(class_2561 message) {
      handler.warning(message);
   }

   public static void warningPrefix(String prefix, String message) {
      handler.warningPrefix(prefix, message);
   }

   public static void warningPrefix(String prefix, class_2561 message) {
      handler.warningPrefix(prefix, message);
   }

   public static void error(String message) {
      handler.error(message);
   }

   public static void error(class_2561 message) {
      handler.error(message);
   }

   public static void errorPrefix(String prefix, String message) {
      handler.errorPrefix(prefix, message);
   }

   public static void errorPrefix(String prefix, class_2561 message) {
      handler.errorPrefix(prefix, message);
   }

   public static void actionbar(String message) {
      handler.actionbar(message);
   }

   public static void actionbar(class_2561 message) {
      handler.actionbar(message);
   }

   private static class Handler extends AbstractMessageHandler {
      private static final Supplier<class_5250> chatPrefix = () -> {
         return class_2561.method_43470(CactusClient.getPrefix()).method_27694((style) -> {
            return style.method_36139(((ColorSetting.ColorValue)CactusSettings.get().accentColor.get()).color());
         });
      };

      public class_2561 getPrefix() {
         return (class_2561)chatPrefix.get();
      }

      public class_2561 formatSubPrefix(String prefix) {
         return class_2561.method_43470("[").method_10852(class_2561.method_43470(prefix)).method_27693("]").method_27692(class_124.field_1062);
      }

      public class_2561 arrangeMessage(class_2561 prefix, @Nullable class_2561 subPrefix, class_2561 message) {
         class_5250 text = prefix.method_27661().method_10852(class_5244.field_41874);
         if (subPrefix != null) {
            text.method_10852(subPrefix).method_10852(class_5244.field_41874);
         }

         return text.method_10852(message);
      }
   }
}
