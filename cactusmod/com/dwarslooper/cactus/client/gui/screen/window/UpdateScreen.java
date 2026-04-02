package com.dwarslooper.cactus.client.gui.screen.window;

import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import java.awt.Color;
import java.net.URI;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.class_124;
import net.minecraft.class_156;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5244;
import net.minecraft.class_5250;
import org.jetbrains.annotations.NotNull;

public class UpdateScreen extends WindowScreen {
   private Consumer<UpdateScreen.UserResponse> submitConsumer;
   private final String version;
   private final URI uri;

   public UpdateScreen(String ver, URI uri) {
      super("update", 256, 64);
      this.version = ver;
      this.uri = uri;
   }

   public void method_25426() {
      super.method_25426();
      this.method_37063(new CButtonWidget(this.x() + 4, this.y() + 40, 80, 20, class_2561.method_43470(this.getTranslatableElement("download", new Object[0])), (button) -> {
         this.submitConsumer.accept(UpdateScreen.UserResponse.DOWNLOAD);
      }));
      this.method_37063(new CButtonWidget(this.x() + 4 + 84, this.y() + 40, 80, 20, class_2561.method_43470(this.getTranslatableElement("openUrl", new Object[0])), (button) -> {
         this.submitConsumer.accept(UpdateScreen.UserResponse.OPEN_URL);
         class_156.method_668().method_673(this.uri);
         this.method_25419();
      }));
      this.method_37063(new CButtonWidget(this.x() + 4 + 168, this.y() + 40, 80, 20, class_2561.method_43470(this.getTranslatableElement("skip", new Object[0])), (button) -> {
         this.submitConsumer.accept(UpdateScreen.UserResponse.SKIP);
         this.method_25419();
      }));
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_27534(this.field_22793, this.field_22785, this.x() + this.boxWidth() / 2, this.y() + 10, Color.WHITE.getRGB());
      class_327 var10001 = this.field_22793;
      class_5250 var10002 = class_2561.method_43471("gui.screen.cactus.title").method_10852(class_5244.field_41874).method_10852(class_2561.method_43470(this.version).method_27692(class_124.field_1073));
      int var10003 = this.x() + this.boxWidth() / 2;
      int var10004 = this.y() + 10;
      Objects.requireNonNull(this.field_22793);
      context.method_27534(var10001, var10002, var10003, var10004 + 9, Color.WHITE.getRGB());
   }

   public void method_25410(int width, int height) {
      super.method_25410(width, height);
      this.parent.method_25410(width, height);
   }

   public UpdateScreen onSubmit(Consumer<UpdateScreen.UserResponse> consumer) {
      this.submitConsumer = consumer;
      return this;
   }

   public static enum UserResponse {
      DOWNLOAD,
      OPEN_URL,
      SKIP;

      // $FF: synthetic method
      private static UpdateScreen.UserResponse[] $values() {
         return new UpdateScreen.UserResponse[]{DOWNLOAD, OPEN_URL, SKIP};
      }
   }
}
