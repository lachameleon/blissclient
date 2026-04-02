package com.dwarslooper.cactus.client.systems.config.settings.impl;

import com.dwarslooper.cactus.client.gui.screen.window.WindowScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.list.ExpandableStringListWidget;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.class_10799;
import net.minecraft.class_11909;
import net.minecraft.class_2477;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_5244;
import org.jetbrains.annotations.NotNull;

public class StringListSetting extends Setting<List<String>> {
   public StringListSetting(String id, String... values) {
      super(id, List.of(values));
   }

   public String getText() {
      return class_2477.method_10517().method_48307("setting.stringList.edit").formatted(new Object[]{((List)this.get()).size()});
   }

   public void save(JsonObject object) {
      JsonArray entries = new JsonArray();
      List var10000 = (List)this.get();
      Objects.requireNonNull(entries);
      var10000.forEach(entries::add);
      object.add("values", entries);
   }

   public List<String> load(JsonObject object) {
      this.set(object.getAsJsonArray("values").asList().stream().map(JsonElement::getAsString).toList());
      return (List)this.get();
   }

   public class_339 buildWidget() {
      return new StringListSetting.Widget();
   }

   public class Widget extends Setting<List<String>>.Widget {
      private static final class_2960 TEXTURE = class_2960.method_60654("widget/button");

      public Widget() {
         super();
      }

      public void wrappedRender(class_332 context, int mouseX, int mouseY, float delta) {
         context.method_52706(class_10799.field_56883, TEXTURE, this.field_22758 - this.widgetWidth, 0, this.widgetWidth, 20);
         class_327 var10001 = this.textRenderer;
         String var10002 = StringListSetting.this.getText();
         int var10003 = this.field_22758 - this.widgetWidth / 2;
         int var10004 = this.method_25364();
         Objects.requireNonNull(this.textRenderer);
         context.method_25300(var10001, var10002, var10003, (var10004 - 9) / 2 + 1, Color.WHITE.getRGB());
      }

      public void method_25348(@NotNull class_11909 click, boolean doubled) {
         CactusConstants.mc.method_1507(new StringListSetting.SelectionScreen((List)StringListSetting.this.get(), class_2561.method_43470(StringListSetting.this.name()), StringListSetting.this::set));
      }
   }

   public static class SelectionScreen extends WindowScreen {
      private final List<String> list;
      private final class_2561 windowTitle;
      private ExpandableStringListWidget widget;
      private Consumer<List<String>> callback;

      public SelectionScreen(List<String> list, class_2561 windowTitle) {
         super("stringList", 240, 200);
         this.list = list;
         this.windowTitle = windowTitle;
      }

      public SelectionScreen(List<String> list, class_2561 windowTitle, Consumer<List<String>> callback) {
         this(new ArrayList(list), windowTitle);
         this.callback = callback;
      }

      public void method_25426() {
         super.method_25426();
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         int headerHeight = 9 + 2;
         this.widget = new ExpandableStringListWidget(this.boxWidth() - 8, this.boxHeight() - 12 - 22 - headerHeight, this.y() + 6 + headerHeight);
         this.widget.method_46421(this.x() + 4);
         List var10000 = this.list;
         ExpandableStringListWidget var10001 = this.widget;
         Objects.requireNonNull(var10001);
         var10000.forEach(var10001::add);
         this.widget.method_44382(2.147483647E9D);
         this.method_37063(this.widget);
         this.method_37063(new CButtonWidget(this.x() + this.boxWidth() / 2 + 2, this.y() + this.boxHeight() - 24, 100, 20, class_5244.field_24334, (button) -> {
            this.list.clear();
            this.list.addAll(this.widget.getList());
            if (this.callback != null) {
               this.callback.accept(this.list);
            }

            this.method_25419();
         }));
         this.method_37063(new CButtonWidget(this.x() + this.boxWidth() / 2 - 102, this.y() + this.boxHeight() - 24, 100, 20, class_5244.field_24335, (button) -> {
            this.method_25419();
         }));
      }

      public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
         super.method_25394(context, mouseX, mouseY, delta);
         context.method_27534(CactusConstants.mc.field_1772, this.windowTitle, this.x() + this.boxWidth() / 2, this.y() + 4, -1);
      }
   }
}
