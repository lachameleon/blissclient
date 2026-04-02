package com.dwarslooper.cactus.client.gui.toast;

import com.dwarslooper.cactus.client.gui.toast.internal.GenericToast;
import com.dwarslooper.cactus.client.util.CactusConstants;
import net.minecraft.class_1792;
import net.minecraft.class_2960;
import net.minecraft.class_374;
import org.apache.commons.lang3.Validate;

public class ToastSystem {
   public static void displayMessage(String titleText, String messageText, class_1792 icon, long duration) {
      Validate.notNull(titleText, "Title cannot be null", new Object[0]);
      Validate.notNull(messageText, "Message Text cannot be null", new Object[0]);
      class_374 manager = CactusConstants.mc.method_1566();
      GenericToast toast = (new GenericToast(titleText, messageText)).setIcon(icon);
      if (duration != -1L) {
         toast.setDuration(duration);
      }

      manager.method_1999(toast);
   }

   public static void displayMessage(String titleText, String messageText, class_1792 icon) {
      displayMessage(titleText, messageText, icon, -1L);
   }

   public static void displayMessage(String titleText, String messageText, class_2960 icon, long duration) {
      Validate.notNull(titleText, "Title cannot be null", new Object[0]);
      Validate.notNull(messageText, "Message Text cannot be null", new Object[0]);
      class_374 manager = CactusConstants.mc.method_1566();
      GenericToast toast = (new GenericToast(titleText, messageText)).setIcon(icon);
      if (duration != -1L) {
         toast.setDuration(duration);
      }

      manager.method_1999(toast);
   }

   public static void displayMessage(String titleText, String messageText, class_2960 icon) {
      displayMessage(titleText, messageText, icon, -1L);
   }

   public static void displayMessage(String titleText, String messageText) {
      displayMessage(titleText, messageText, (class_2960)null, -1L);
   }

   public static void displayMessage(String titleText, String messageText, long duration) {
      displayMessage(titleText, messageText, (class_2960)null, duration);
   }
}
