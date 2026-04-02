package com.dwarslooper.cactus.client.systems.config.settings.impl;

import com.dwarslooper.cactus.client.util.CactusConstants;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.class_10799;
import net.minecraft.class_11909;
import net.minecraft.class_2477;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_4185;
import org.jetbrains.annotations.NotNull;

public class EnumSetting<T extends Enum<T>> extends Setting<T> {
   private final T[] constants;

   public EnumSetting(String name, T value) {
      super(name, value);
      this.constants = (Enum[])value.getClass().getEnumConstants();
   }

   public String getText() {
      return valueToString(this.getNamespace(), (Enum)this.get());
   }

   public void save(JsonObject object) {
      object.addProperty("value", ((Enum)this.get()).name());
   }

   public T load(JsonObject object) {
      Optional<T> val = Arrays.stream(this.getConstants()).filter((anEnum) -> {
         return anEnum.name().equalsIgnoreCase(object.get("value").getAsString());
      }).findFirst();
      val.ifPresent(this::set);
      return (Enum)this.get();
   }

   public boolean trySet(Object value) {
      try {
         this.set((Enum)value);
         return true;
      } catch (Exception var3) {
         return false;
      }
   }

   public class_339 buildWidget() {
      return new EnumSetting.Widget();
   }

   public T[] getConstants() {
      return this.constants;
   }

   public static <T extends Enum<?>> String valueToString(String namespace, T value) {
      if (value instanceof EnumSetting.INamespaceOverrides) {
         EnumSetting.INamespaceOverrides no = (EnumSetting.INamespaceOverrides)value;
         namespace = no.getNamespace();
      }

      return class_2477.method_10517().method_48307(namespace + "." + Character.toLowerCase(value.name().charAt(0)) + value.name().substring(1));
   }

   public class Widget extends Setting<T>.Widget {
      public Widget() {
         super();
      }

      public int widgetWidth() {
         return Math.max(super.widgetWidth(), (Integer)Arrays.stream(EnumSetting.this.getConstants()).map((cnst) -> {
            return CactusConstants.mc.field_1772.method_1727(cnst.name()) + 4;
         }).max(Integer::compare).orElse(0));
      }

      public void wrappedRender(class_332 context, int mouseX, int mouseY, float delta) {
         context.method_52706(class_10799.field_56883, class_4185.field_45339.comp_1604(), this.field_22758 - this.widgetWidth, 0, this.widgetWidth, 20);
         this.drawOverlay(context, this.field_22758 - this.widgetWidth, this.widgetWidth);
         context.method_25300(this.textRenderer, EnumSetting.this.getText(), this.field_22758 - this.widgetWidth / 2, this.textYCenter, Color.WHITE.getRGB());
      }

      public void method_25348(@NotNull class_11909 click, boolean doubled) {
         Class<T> clazz = ((Enum)EnumSetting.this.get()).getDeclaringClass();
         EnumSetting.this.set(((Enum[])clazz.getEnumConstants())[(((Enum)EnumSetting.this.get()).ordinal() + 1) % ((Enum[])clazz.getEnumConstants()).length]);
      }
   }

   public interface INamespaceOverrides {
      String getNamespace();
   }
}
