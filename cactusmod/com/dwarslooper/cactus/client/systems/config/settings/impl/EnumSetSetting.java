package com.dwarslooper.cactus.client.systems.config.settings.impl;

import com.dwarslooper.cactus.client.gui.screen.window.WindowScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.list.CheckboxListWidget;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.class_10799;
import net.minecraft.class_11909;
import net.minecraft.class_2477;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_4185;
import net.minecraft.class_5244;
import org.jetbrains.annotations.NotNull;

public class EnumSetSetting<T extends Enum<T>> extends Setting<Set<T>> {
   private final Class<T> type;
   private final T[] constants;

   @SafeVarargs
   public EnumSetSetting(String id, Class<T> type, T... values) {
      super(id, enumSetWithValues(type, Arrays.asList(values)));
      this.type = type;
      this.constants = (Enum[])type.getEnumConstants();
   }

   public String getText() {
      return class_2477.method_10517().method_48307("setting.enumSet.edit").formatted(new Object[]{((Set)this.get()).size(), this.constants.length});
   }

   public void save(JsonObject object) {
      JsonArray values = new JsonArray();
      ((Set)this.get()).forEach((value) -> {
         values.add(value.name());
      });
      object.add("values", values);
   }

   public Set<T> load(JsonObject object) {
      List<String> values = object.getAsJsonArray("values").asList().stream().map(JsonElement::getAsString).toList();
      this.set(enumSetWithValues(this.type, Arrays.stream(this.getConstants()).filter((anEnum) -> {
         return values.stream().anyMatch((v) -> {
            return v.equalsIgnoreCase(anEnum.name());
         });
      }).toList()));
      return (Set)this.get();
   }

   public boolean trySet(Set<Object> value) {
      try {
         this.set((Set)value);
         return true;
      } catch (Exception var3) {
         return false;
      }
   }

   public boolean preLoad(JsonObject object) {
      return !object.has("value") && super.preLoad(object);
   }

   public Map<T, Boolean> asMap() {
      return (Map)Arrays.stream(this.getConstants()).collect(Collectors.toMap((t) -> {
         return t;
      }, (t) -> {
         return ((Set)this.get()).contains(t);
      }));
   }

   public class_339 buildWidget() {
      return new EnumSetSetting.Widget();
   }

   public T[] getConstants() {
      return this.constants;
   }

   private static <T extends Enum<T>> EnumSet<T> enumSetWithValues(Class<T> type, Collection<T> values) {
      EnumSet<T> ts = EnumSet.noneOf(type);
      ts.addAll(values);
      return ts;
   }

   public class Widget extends Setting<T>.Widget {
      public Widget() {
         super();
      }

      public void wrappedRender(class_332 context, int mouseX, int mouseY, float delta) {
         context.method_52706(class_10799.field_56883, class_4185.field_45339.comp_1604(), this.field_22758 - this.widgetWidth, 0, this.widgetWidth, 20);
         context.method_25300(this.textRenderer, EnumSetSetting.this.getText(), this.field_22758 - this.widgetWidth / 2, this.textYCenter, Color.WHITE.getRGB());
      }

      public void method_25348(@NotNull class_11909 click, boolean doubled) {
         CactusConstants.mc.method_1507(new EnumSetSetting.SelectionScreen(EnumSetSetting.this.asMap(), class_2561.method_43470(EnumSetSetting.this.name()), EnumSetSetting.this::set, (t) -> {
            return EnumSetting.valueToString(EnumSetSetting.this.getNamespace(), t);
         }));
      }
   }

   private static class SelectionScreen<T extends Enum<T>> extends WindowScreen {
      private final Map<T, Boolean> states;
      private final class_2561 windowTitle;
      private final Consumer<Set<T>> callback;
      private final Function<T, String> nameMapper;
      private CheckboxListWidget<T> widget;

      public SelectionScreen(Map<T, Boolean> states, class_2561 windowTitle, Consumer<Set<T>> callback, Function<T, String> nameMapper) {
         super("enumList", 220, 200);
         this.states = new HashMap(states);
         this.windowTitle = windowTitle;
         this.callback = callback;
         this.nameMapper = nameMapper;
      }

      public void method_25426() {
         super.method_25426();
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         int headerHeight = 9 + 2;
         this.widget = new CheckboxListWidget(this.boxWidth() - 8, this.boxHeight() - 12 - 22 - headerHeight, this.y() + 6 + headerHeight);
         this.widget.method_46421(this.x() + 4);
         this.states.forEach((t, selected) -> {
            this.widget.add(t, class_2561.method_43470((String)this.nameMapper.apply(t)), selected);
         });
         this.method_37063(this.widget);
         this.method_37063(new CButtonWidget(this.x() + this.boxWidth() / 2 + 2, this.y() + this.boxHeight() - 24, 100, 20, class_5244.field_24334, (button) -> {
            this.states.clear();
            this.states.putAll(this.widget.getAsMap());
            this.states.entrySet().removeIf((e) -> {
               return !(Boolean)e.getValue();
            });
            if (this.callback != null) {
               this.callback.accept(this.states.keySet());
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
