package com.dwarslooper.cactus.client.userscript.impl;

import com.dwarslooper.cactus.client.gui.screen.ITranslatable;
import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.userscript.Userscript;
import com.dwarslooper.cactus.client.util.generic.ScreenUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.class_2588;
import net.minecraft.class_339;
import net.minecraft.class_437;
import net.minecraft.class_442;
import net.minecraft.class_7417;

public class ButtonModifier extends Userscript {
   public ButtonModifier(JsonObject json) {
      super(json);
   }

   public void load() {
   }

   public void unload() {
   }

   public void invoke(class_437 screen) {
      this.data().keySet().forEach((s) -> {
         String screenKey;
         if (screen instanceof ITranslatable) {
            ITranslatable tc = (ITranslatable)screen;
            screenKey = tc.getKey();
         } else {
            class_7417 patt0$temp = screen.method_25440().method_10851();
            if (patt0$temp instanceof class_2588) {
               class_2588 ttc = (class_2588)patt0$temp;
               screenKey = ttc.method_11022();
            } else {
               screenKey = screen.method_25440().getString();
            }
         }

         if (s.equalsIgnoreCase(screenKey)) {
            this.data().getAsJsonObject(s).getAsJsonArray("modify").asList().stream().map(JsonElement::getAsJsonObject).forEach((object) -> {
               String key = object.get("key").getAsString();
               class_339 widget = ScreenUtils.getButton(screen, key);
               if (screen instanceof class_442) {
                  class_442 ts = (class_442)screen;
                  if (key.equalsIgnoreCase("cactus")) {
                     widget = (class_339)ts.method_25396().stream().filter((element) -> {
                        boolean var10000;
                        if (element instanceof CTextureButtonWidget) {
                           CTextureButtonWidget ctbw = (CTextureButtonWidget)element;
                           if (ctbw.getTextureY() == 0) {
                              var10000 = true;
                              return var10000;
                           }
                        }

                        var10000 = false;
                        return var10000;
                     }).map((e) -> {
                        return (class_339)e;
                     }).findFirst().orElse(widget);
                  }
               }

               if (widget != null) {
                  widget.method_48229(object.get("x").getAsInt(), object.get("y").getAsInt());
               }

            });
         }

      });
   }
}
