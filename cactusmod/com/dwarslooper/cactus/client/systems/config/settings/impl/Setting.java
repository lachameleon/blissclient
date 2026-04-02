package com.dwarslooper.cactus.client.systems.config.settings.impl;

import com.dwarslooper.cactus.client.systems.ISerializable;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.dwarslooper.cactus.client.systems.config.settings.ICopyable;
import com.dwarslooper.cactus.client.systems.config.settings.IMutableVisibility;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import com.dwarslooper.cactus.client.util.generic.INamespaced;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.class_10799;
import net.minecraft.class_124;
import net.minecraft.class_2477;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_4185;
import net.minecraft.class_6382;
import net.minecraft.class_9848;
import org.jetbrains.annotations.NotNull;

public abstract class Setting<T> implements ISerializable<T>, IMutableVisibility<Setting<T>>, INamespaced {
   private final String id;
   private String namespace;
   private boolean experimental;
   private final T defaultValue;
   private T value;
   private Function<T, String> textGetter;
   private Consumer<T> callback;
   private BooleanSupplier visible;

   public Setting(String id, T value) {
      this.experimental = false;
      this.id = id;
      this.defaultValue = value;
      this.value = this.copyIfSupported(value);
   }

   public Setting(String id, T value, Function<T, String> textGetter) {
      this(id, value);
      this.textGetter = textGetter;
   }

   public String name() {
      return class_2477.method_10517().method_48307(this.getNamespace() + ".name");
   }

   public String description() {
      return class_2477.method_10517().method_4679(this.getNamespace() + ".description", "");
   }

   public void set(T value) {
      this.value = value;
      if (this.callback != null) {
         this.callback.accept(value);
      }

   }

   public T get() {
      return this.value;
   }

   public T getDefaultValue() {
      return this.defaultValue;
   }

   public void reset() {
      this.set(this.copyIfSupported(this.defaultValue));
   }

   public String getText() {
      return this.textGetter != null ? (String)this.textGetter.apply(this.value) : this.value.toString();
   }

   public Setting<T> setExperimental(boolean experimental) {
      this.experimental = experimental;
      return this;
   }

   public Setting<T> setCallback(Consumer<T> callback) {
      this.callback = callback;
      return this;
   }

   public Setting<T> visibleIf(BooleanSupplier supplier) {
      this.visible = supplier;
      return this;
   }

   public boolean visible() {
      return this.visible == null || this.visible.getAsBoolean();
   }

   public boolean isExperimental() {
      return this.experimental;
   }

   public String id() {
      return this.id;
   }

   public void setNamespace(String namespace) {
      if (namespace != null) {
         this.namespace = namespace;
      }

   }

   public String getNamespace() {
      String var10000 = this.namespace != null ? this.namespace + ".settings." : "settings.";
      return var10000 + this.id;
   }

   public abstract void save(JsonObject var1);

   public abstract T load(JsonObject var1);

   public abstract class_339 buildWidget();

   public boolean preLoad(JsonObject object) {
      return true;
   }

   public Setting.SyncData getSyncData() {
      return null;
   }

   public boolean isSynced() {
      return this.getSyncData() != null;
   }

   public boolean isDefault() {
      return Objects.equals(this.get(), this.getDefaultValue());
   }

   private T copyIfSupported(T value) {
      Object var10000;
      if (value instanceof ICopyable) {
         ICopyable<?> c = (ICopyable)value;
         var10000 = c.copy();
      } else {
         var10000 = value;
      }

      return var10000;
   }

   public final JsonObject toJson(TreeSerializerFilter filter) {
      JsonObject object = new JsonObject();
      this.save(object);
      return object;
   }

   public final T fromJson(JsonObject object) {
      return this.load(object);
   }

   public static record SyncData(String id, JsonObject data) implements ISerializable<Setting.SyncData> {
      public SyncData(String id, JsonObject data) {
         this.id = id;
         this.data = data;
      }

      public JsonObject toJson(TreeSerializerFilter filter) {
         JsonObject object = new JsonObject();
         object.addProperty("id", this.id);
         object.add("data", this.data);
         return object;
      }

      public Setting.SyncData fromJson(JsonObject object) {
         throw new UnsupportedOperationException("Can't create SyncData from json");
      }

      public String toString() {
         return this.toJson(TreeSerializerFilter.ALL).toString();
      }

      public String id() {
         return this.id;
      }

      public JsonObject data() {
         return this.data;
      }
   }

   public abstract class Widget extends class_339 {
      public final class_327 textRenderer;
      public final String name;
      public final int textYCenter;
      public final int nameWidth;
      public int widgetWidth;

      public Widget(final Setting this$0) {
         super(0, 0, 280, 20, class_2561.method_43473());
         this.textRenderer = CactusConstants.mc.field_1772;
         this.name = this$0.name();
         int var10001 = this.method_25364();
         Objects.requireNonNull(this.textRenderer);
         this.textYCenter = (var10001 - 9) / 2 + 1;
         this.widgetWidth = this.widgetWidth();
         this.nameWidth = this.textRenderer.method_1727(this.name);
      }

      public int widgetWidth() {
         return Math.min(this.field_22758 / 3, 100);
      }

      public int widgetPosX() {
         return this.field_22758 - this.widgetWidth;
      }

      public abstract void wrappedRender(class_332 var1, int var2, int var3, float var4);

      public void tick() {
      }

      public void rebuild() {
      }

      public void drawBackground(class_332 context) {
         int color = class_9848.method_61324(255, 204, 204, 204);
         context.method_52707(class_10799.field_56883, class_4185.field_45339.comp_1605(), 0, 0, this.method_25368(), this.method_25364(), color);
      }

      public void drawName(class_332 context) {
         class_327 var10001 = this.textRenderer;
         String var10002 = this.name.formatted(new Object[]{class_124.field_1080});
         int var10004 = this.method_25364();
         Objects.requireNonNull(this.textRenderer);
         context.method_51433(var10001, var10002, 4, (var10004 - 9) / 2 + 1, Color.WHITE.getRGB(), false);
      }

      public void drawDecoration(class_332 context) {
      }

      public void drawOverlay(class_332 context, int x, int width) {
         if (RenderUtils.darkMode) {
            context.method_25294(x, 0, x + width, this.method_25364(), -2013265920);
         }

      }

      public void method_48579(class_332 context, int mouseX, int mouseY, float delta) {
         this.widgetWidth = this.widgetWidth();
         context.method_51448().pushMatrix();
         context.method_51448().translate((float)this.method_46426(), (float)this.method_46427());
         this.drawBackground(context);
         this.drawName(context);
         this.drawDecoration(context);
         this.wrappedRender(context, mouseX, mouseY, delta);
         context.method_51448().popMatrix();
      }

      public void method_47399(@NotNull class_6382 builder) {
      }

      public void update() {
      }
   }
}
