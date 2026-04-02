package com.dwarslooper.cactus.client.systems.config.settings.impl;

import com.dwarslooper.cactus.client.util.Utils;
import com.dwarslooper.cactus.client.util.generic.TextUtils;
import com.google.gson.JsonObject;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_10799;
import net.minecraft.class_11909;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_4185;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.PointerBuffer;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public class FileSetting extends Setting<File> {
   public FileSetting(String id, File value) {
      super(id, value);
   }

   public void save(JsonObject object) {
      object.addProperty("value", (String)Utils.orElse((File)this.get(), File::getAbsolutePath, ""));
   }

   public File load(JsonObject object) {
      String value = object.get("value").getAsString();
      this.set(!value.isEmpty() ? new File(value) : null);
      return (File)this.get();
   }

   public class_339 buildWidget() {
      return new FileSetting.Widget();
   }

   public class Widget extends Setting<File>.Widget {
      private static final class_2561 UNSET;

      public Widget() {
         super();
      }

      public void wrappedRender(class_332 context, int mouseX, int mouseY, float delta) {
         File value = (File)FileSetting.this.get();
         context.method_52706(class_10799.field_56883, class_4185.field_45339.comp_1604(), this.field_22758 - this.widgetWidth, 0, this.widgetWidth, 20);
         this.drawOverlay(context, this.field_22758 - this.widgetWidth, this.widgetWidth);
         class_2561 fileName = (class_2561)Utils.orElse(value, (f) -> {
            return class_2561.method_43470(TextUtils.trimToWidth(f.getName(), this.widgetWidth - 8, (String)null));
         }, UNSET);
         class_327 var10001 = this.textRenderer;
         int var10003 = this.field_22758 - this.widgetWidth + 4;
         int var10004 = this.method_25364();
         Objects.requireNonNull(this.textRenderer);
         context.method_27535(var10001, fileName, var10003, (var10004 - 9) / 2 + 1, -1);
      }

      public void method_25348(@NotNull class_11909 click, boolean doubled) {
         CompletableFuture.supplyAsync(() -> {
            return TinyFileDialogs.tinyfd_openFileDialog("Select File", (CharSequence)null, (PointerBuffer)null, (CharSequence)null, false);
         }).thenAccept((s) -> {
            if (s != null) {
               FileSetting.this.set(new File(s));
            }

         });
      }

      static {
         UNSET = class_2561.method_43470("Unset").method_27692(class_124.field_1061);
      }
   }
}
