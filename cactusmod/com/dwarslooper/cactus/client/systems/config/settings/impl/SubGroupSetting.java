package com.dwarslooper.cactus.client.systems.config.settings.impl;

import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.dwarslooper.cactus.client.systems.config.settings.gui.SettingContainer;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.class_10799;
import net.minecraft.class_11909;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_4264;
import net.minecraft.class_437;
import net.minecraft.class_5244;
import net.minecraft.class_5250;
import org.jetbrains.annotations.NotNull;

public class SubGroupSetting extends Setting<SettingContainer> {
   public final Supplier<CScreen> screenSupplier;

   public SubGroupSetting(String id, SettingContainer value, Supplier<CScreen> screenSupplier) {
      super(id, value);
      this.screenSupplier = screenSupplier;
   }

   public void save(JsonObject object) {
      object.add("settings", ((SettingContainer)this.get()).toJson(TreeSerializerFilter.ALL));
   }

   public SettingContainer load(JsonObject object) {
      return ((SettingContainer)this.get()).fromJson(object.getAsJsonObject("settings"));
   }

   public class_339 buildWidget() {
      return new SubGroupSetting.Widget();
   }

   public class Widget extends Setting<SettingContainer>.Widget {
      public Widget() {
         super();
      }

      public void drawName(class_332 context) {
         class_327 var10001 = this.textRenderer;
         class_5250 var10002 = class_2561.method_43470(SubGroupSetting.this.name()).method_10852(class_5244.field_39678).method_27692(class_124.field_1073);
         int var10003 = this.field_22758 / 2;
         int var10004 = this.field_22759;
         Objects.requireNonNull(this.textRenderer);
         context.method_27534(var10001, var10002, var10003, (var10004 - 9) / 2 + 1, Color.WHITE.getRGB());
      }

      public void wrappedRender(class_332 context, int mouseX, int mouseY, float delta) {
      }

      public void drawBackground(class_332 context) {
         context.method_52706(class_10799.field_56883, class_4264.field_45339.comp_1605(), 0, 0, this.method_25368(), this.method_25364());
      }

      public void method_25348(@NotNull class_11909 click, boolean doubled) {
         CactusConstants.mc.method_1507((class_437)SubGroupSetting.this.screenSupplier.get());
      }
   }
}
