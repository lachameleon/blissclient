package com.dwarslooper.cactus.client.systems.config.settings.impl;

import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import com.dwarslooper.cactus.client.util.generic.TextUtils;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.util.Objects;
import net.minecraft.class_10799;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_4264;
import net.minecraft.class_9848;
import org.jetbrains.annotations.NotNull;

public class BooleanSetting extends Setting<Boolean> {
   public BooleanSetting(String name, Boolean value) {
      super(name, value);
   }

   public Boolean load(JsonObject object) {
      this.set(object.get("value").getAsBoolean());
      return (Boolean)this.get();
   }

   public void save(JsonObject object) {
      object.addProperty("value", (Boolean)this.get());
   }

   public class_339 buildWidget() {
      return new BooleanSetting.Widget();
   }

   public class Widget extends Setting<Boolean>.Widget {
      public Widget() {
         super();
      }

      public int widgetWidth() {
         return Math.min(this.field_22758 / 4, 70);
      }

      public void wrappedRender(class_332 context, int mouseX, int mouseY, float delta) {
         context.method_52706(class_10799.field_56883, class_4264.field_45339.comp_1604(), this.field_22758 - this.widgetWidth, 0, this.widgetWidth, 20);
         this.drawOverlay(context, this.field_22758 - this.widgetWidth, this.widgetWidth);
         boolean t = (Boolean)BooleanSetting.this.get();
         int color = class_9848.method_61324(255, t ? 85 : 255, t ? 255 : 85, 85);
         context.method_52707(class_10799.field_56883, RenderUtils.SLIDER_HANDLE_TEXTURES.comp_1604(), this.field_22758 - (t ? 8 : this.widgetWidth), 0, 8, 20, color);
         class_327 var10001 = this.textRenderer;
         class_2561 var10002 = TextUtils.boolAsText((Boolean)BooleanSetting.this.get());
         int var10003 = this.field_22758 - this.widgetWidth / 2;
         int var10004 = this.method_25364();
         Objects.requireNonNull(this.textRenderer);
         context.method_27534(var10001, var10002, var10003, (var10004 - 9) / 2 + 1, Color.WHITE.getRGB());
      }

      public void method_25348(@NotNull class_11909 click, boolean doubled) {
         BooleanSetting.this.set(!(Boolean)BooleanSetting.this.get());
      }
   }
}
