package com.dwarslooper.cactus.client.systems.config.settings.impl;

import com.dwarslooper.cactus.client.gui.screen.impl.GraphicDrawingScreen;
import com.dwarslooper.cactus.client.systems.config.settings.ICopyable;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1060;
import net.minecraft.class_10799;
import net.minecraft.class_11909;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_4185;
import org.jetbrains.annotations.NotNull;

public class Graphic2DSetting extends Setting<Graphic2DSetting.Graphic2D> {
   public Graphic2DSetting(String id, Graphic2DSetting.Graphic2D value) {
      super(id, value);
   }

   public void save(JsonObject object) {
      object.addProperty("width", ((Graphic2DSetting.Graphic2D)this.get()).getWidth());
      object.addProperty("height", ((Graphic2DSetting.Graphic2D)this.get()).getHeight());

      try {
         String encodedPixels = Base64.getEncoder().encodeToString(RenderUtils.asByteArray(((Graphic2DSetting.Graphic2D)this.get()).getImage()));
         object.addProperty("data", encodedPixels);
      } catch (IOException var3) {
         throw new IllegalStateException();
      }
   }

   public Graphic2DSetting.Graphic2D load(JsonObject object) {
      int width = object.get("width").getAsInt();
      int height = object.get("height").getAsInt();
      String encodedPixels = object.get("data").getAsString();
      byte[] pixelBytes = Base64.getDecoder().decode(encodedPixels);
      this.set(width == ((Graphic2DSetting.Graphic2D)this.getDefaultValue()).getWidth() && height == ((Graphic2DSetting.Graphic2D)this.getDefaultValue()).getHeight() ? Graphic2DSetting.Graphic2D.createFromBytes(pixelBytes) : (Graphic2DSetting.Graphic2D)this.getDefaultValue());
      return (Graphic2DSetting.Graphic2D)this.get();
   }

   public void reset() {
      super.reset();
      ((Graphic2DSetting.Graphic2D)this.get()).rebuildTexture();
   }

   public class_339 buildWidget() {
      return new Graphic2DSetting.Widget();
   }

   public static class Graphic2D implements ICopyable<Graphic2DSetting.Graphic2D> {
      private final class_2960 id;
      private final class_1011 image;

      public static Graphic2DSetting.Graphic2D createEmpty(int width, int height) {
         return new Graphic2DSetting.Graphic2D(new class_1011(width, height, true));
      }

      public static Graphic2DSetting.Graphic2D createFromBytes(byte[] bytes) {
         try {
            return new Graphic2DSetting.Graphic2D(class_1011.method_49277(bytes));
         } catch (IOException var2) {
            throw new IllegalArgumentException(var2);
         }
      }

      public Graphic2D(class_1011 image) {
         this(image, class_2960.method_60655("cactus", "editable-graphic." + String.valueOf(UUID.randomUUID())));
      }

      public Graphic2D(class_1011 image, class_2960 id) {
         this.image = image;
         this.id = id;
      }

      public class_2960 getId() {
         return this.id;
      }

      public class_1011 getImage() {
         return this.image;
      }

      public int getWidth() {
         return this.image.method_4307();
      }

      public int getHeight() {
         return this.image.method_4323();
      }

      public void paint(int x, int y, int color) {
         this.checkBounds(x, y);
         this.image.method_61941(x, y, color);
      }

      public int[] getPixels() {
         return this.image.method_61942();
      }

      public int get(int x, int y) {
         this.checkBounds(x, y);
         return this.image.method_61940(x, y);
      }

      public boolean hasBuiltTexture() {
         return CactusConstants.mc.method_1531().field_5286.containsKey(this.id);
      }

      public class_2960 rebuildTexture() {
         class_1011 image = new class_1011(this.getWidth(), this.getHeight(), true);
         image.method_4317(this.image);
         class_1060 var10000 = CactusConstants.mc.method_1531();
         class_2960 var10001 = this.id;
         class_2960 var10004 = this.id;
         Objects.requireNonNull(var10004);
         var10000.method_4616(var10001, new class_1043(var10004::toString, image));
         return this.id;
      }

      private void checkBounds(int x, int y) {
         if (x < 0 || x >= this.getWidth() || y < 0 || y >= this.getHeight()) {
            throw new IndexOutOfBoundsException("Coordinates out of bounds");
         }
      }

      public Graphic2DSetting.Graphic2D copy() {
         class_1011 ni = new class_1011(this.getWidth(), this.getHeight(), true);
         ni.method_4317(this.getImage());
         return new Graphic2DSetting.Graphic2D(ni, this.id);
      }

      public boolean equals(Object obj) {
         boolean var10000;
         if (obj instanceof Graphic2DSetting.Graphic2D) {
            Graphic2DSetting.Graphic2D graphic2D = (Graphic2DSetting.Graphic2D)obj;
            if (Arrays.equals(graphic2D.getPixels(), this.getPixels())) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public class Widget extends Setting<Graphic2DSetting.Graphic2D>.Widget {
      public final int posX;

      public Widget() {
         super();
         this.posX = this.field_22758 - this.widgetWidth;
      }

      public void wrappedRender(class_332 context, int mouseX, int mouseY, float delta) {
         Graphic2DSetting.Graphic2D value = (Graphic2DSetting.Graphic2D)Graphic2DSetting.this.get();
         if (!value.hasBuiltTexture()) {
            value.rebuildTexture();
         }

         context.method_52706(class_10799.field_56883, class_4185.field_45339.comp_1604(), this.field_22758 - this.widgetWidth, 0, this.widgetWidth, 20);
         this.drawOverlay(context, this.field_22758 - this.widgetWidth, this.widgetWidth);
         class_327 var10001 = this.textRenderer;
         String var10002 = "%sx%s".formatted(new Object[]{value.getWidth(), value.getHeight()});
         int var10003 = this.field_22758 - this.widgetWidth + 4;
         int var10004 = this.method_25364();
         Objects.requireNonNull(this.textRenderer);
         context.method_25303(var10001, var10002, var10003, (var10004 - 9) / 2 + 1, -1);
         context.method_25290(class_10799.field_56883, value.getId(), this.field_22758 - 1 - value.getWidth(), (this.field_22759 - value.getHeight()) / 2 + 1, 0.0F, 0.0F, 16, 16, 16, 16);
      }

      public void method_25348(@NotNull class_11909 click, boolean doubled) {
         CactusConstants.mc.method_1507(new GraphicDrawingScreen((Graphic2DSetting.Graphic2D)Graphic2DSetting.this.get(), Graphic2DSetting.this::set));
      }
   }
}
