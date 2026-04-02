package com.dwarslooper.cactus.client.util.generic;

import com.dwarslooper.cactus.client.mixins.accessor.ScreenWidgetsAccessor;
import java.util.Iterator;
import net.minecraft.class_2561;
import net.minecraft.class_2588;
import net.minecraft.class_339;
import net.minecraft.class_364;
import net.minecraft.class_437;
import net.minecraft.class_7417;
import net.minecraft.class_7919;

public class ScreenUtils {
   public static int baseX;
   public static int baseY = 36;

   public static boolean deleteButton(class_437 screen, String key) {
      class_339 widget = getButton(screen, key);
      ((ScreenWidgetsAccessor)screen).removeChild(widget);
      return widget != null;
   }

   public static class_339 getButton(class_437 screen, String key) {
      class_339 widget = null;
      Iterator var3 = screen.method_25396().iterator();

      while(true) {
         class_339 cw;
         class_7417 content;
         do {
            class_364 child;
            do {
               if (!var3.hasNext()) {
                  return widget;
               }

               child = (class_364)var3.next();
            } while(!(child instanceof class_339));

            cw = (class_339)child;
            content = cw.method_25369().method_10851();
         } while((!(content instanceof class_2588) || !((class_2588)content).method_11022().equals(key)) && !cw.method_25369().getString().equals(key));

         widget = cw;
      }
   }

   public static class_7919 tooltipLiteral(String text) {
      return class_7919.method_47407(class_2561.method_43470(text));
   }
}
