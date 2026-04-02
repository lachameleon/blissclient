package com.dwarslooper.cactus.client.gui.widget;

import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import java.awt.Color;
import java.util.Objects;
import net.minecraft.class_10799;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_4185;
import net.minecraft.class_7919;
import net.minecraft.class_4185.class_4241;
import org.jetbrains.annotations.NotNull;

public class CTextureButtonWidget extends class_4185 {
   public static final class_2960 WIDGETS_TEXTURE = class_2960.method_60655("cactus", "textures/gui/widgets.png");
   private final boolean transparent;
   private final int u;
   private final int v;
   private final int hoveredVOffset;
   private final int textureWidth;
   private final int textureHeight;
   private final class_2960 texture;
   private int textureTileWidth;
   private int textureTileHeight;

   public CTextureButtonWidget(int x, int y, int u, class_4241 pressAction) {
      this(x, y, 20, 20, u, 0, 20, WIDGETS_TEXTURE, 640, 64, pressAction, class_2561.method_43473(), Color.WHITE, false);
   }

   public CTextureButtonWidget(int x, int y, int u, class_2561 message, class_4241 pressAction) {
      this(x, y, 20, 20, u, 0, 20, WIDGETS_TEXTURE, 640, 64, pressAction, message, Color.WHITE, false);
   }

   public CTextureButtonWidget(int x, int y, int width, int height, int u, int v, int hoveredVOffset, class_2960 identifier, int textureWidth, int textureHeight, class_4241 pressAction, class_2561 message, Color color, boolean transparent) {
      super(x, y, width, height, (class_2561)(message != null ? message : class_2561.method_43473()), pressAction, (e) -> {
         return class_2561.method_43473();
      });
      this.textureTileWidth = 20;
      this.textureTileHeight = 20;
      this.u = u;
      this.v = v;
      this.hoveredVOffset = hoveredVOffset;
      this.textureWidth = textureWidth;
      this.textureHeight = textureHeight;
      this.transparent = transparent;
      this.texture = identifier;
   }

   public CTextureButtonWidget(int x, int y, int width, int height, int u, int v, int hoveredVOffset, class_2960 identifier, int textureWidth, int textureHeight, int textureTileWidth, int textureTileHeight, class_4241 pressAction, class_2561 message, Color color, boolean transparent) {
      this(x, y, width, height, u, v, hoveredVOffset, identifier, textureWidth, textureHeight, pressAction, message, color, transparent);
      this.textureTileWidth = textureTileWidth;
      this.textureTileHeight = textureTileHeight;
   }

   protected void method_75752(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      if (!this.transparent) {
         this.method_75794(context);
      }

      if (this.transparent && RenderUtils.darkMode) {
         RenderUtils.applyDarkmode(context, this);
      }

      int a = class_3532.method_15386(this.field_22765 * 255.0F) << 24;
      context.method_25291(class_10799.field_56883, this.texture, this.method_46426(), this.method_46427(), (float)this.u, (float)this.getTextureY(), this.textureTileWidth, this.textureTileHeight, this.textureWidth, this.textureHeight, 16777215 | a);
      int i = this.field_22763 ? 16777215 : 10526880;
      if (this.method_25369() != null && !this.method_25369().equals(class_2561.method_43473()) && this.field_22758 > 20) {
         class_327 var10001 = CactusConstants.mc.field_1772;
         class_2561 var10002 = this.method_25369();
         int var10003 = this.method_46426() + 20 + 3;
         int var10004 = this.method_46427();
         int var10005 = this.field_22759;
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         context.method_27535(var10001, var10002, var10003, var10004 + (var10005 - 9) / 2 + 1, i | a);
      }

   }

   public int getTextureY() {
      int i = 0;
      if (!this.field_22763) {
         i = 2;
      } else if (this.method_49606() || this.method_25367()) {
         i = 1;
      }

      return this.v + i * 20;
   }

   public static class Builder {
      public final class_4241 pressAction;
      public int x;
      public int y;
      public int width = 100;
      public int height = 20;
      public int u;
      public int v;
      public int hoveredVOffset = 20;
      public int textureWidth = 640;
      public int textureHeight = 64;
      public int textureTileWidth = 20;
      public int textureTileHeight = 20;
      public class_2960 texture;
      public class_2561 message;
      public Color color;
      public boolean transparent;
      public class_7919 tooltip;

      public Builder(class_4241 pressAction) {
         this.texture = CTextureButtonWidget.WIDGETS_TEXTURE;
         this.color = Color.GRAY;
         this.pressAction = pressAction;
      }

      public CTextureButtonWidget.Builder positioned(int x, int y) {
         this.x = x;
         this.y = y;
         return this;
      }

      public CTextureButtonWidget.Builder dimensions(int width, int height) {
         this.width = width;
         this.height = height;
         return this;
      }

      public CTextureButtonWidget.Builder uv(int u, int v) {
         this.u = u;
         this.v = v;
         return this;
      }

      public CTextureButtonWidget.Builder hoverOffset(int offset) {
         this.hoveredVOffset = offset;
         return this;
      }

      public CTextureButtonWidget.Builder noUvo() {
         this.u = 0;
         this.v = 0;
         this.hoveredVOffset = 0;
         return this;
      }

      public CTextureButtonWidget.Builder textureDimensions(int textureWidth, int textureHeight) {
         this.textureWidth = textureWidth;
         this.textureHeight = textureHeight;
         return this;
      }

      public CTextureButtonWidget.Builder textureTileDimensions(int width, int height) {
         this.textureTileWidth = width;
         this.textureTileHeight = height;
         return this;
      }

      public CTextureButtonWidget.Builder fitTextWidth() {
         this.width = CactusConstants.mc.field_1772.method_27525(this.message) + 20 + 2 + 4;
         return this;
      }

      public CTextureButtonWidget.Builder texture(class_2960 texture) {
         this.texture = texture;
         return this;
      }

      public CTextureButtonWidget.Builder message(class_2561 message) {
         this.message = message;
         return this;
      }

      public CTextureButtonWidget.Builder color(Color color) {
         this.color = color;
         return this;
      }

      public CTextureButtonWidget.Builder transparent(boolean transparent) {
         this.transparent = transparent;
         return this;
      }

      public CTextureButtonWidget.Builder simpleCactus() {
         this.texture = CTextureButtonWidget.WIDGETS_TEXTURE;
         this.dimensions(20, 20);
         this.textureDimensions(640, 64);
         this.hoverOffset(20);
         return this;
      }

      public CTextureButtonWidget.Builder simpleSingle() {
         this.textureDimensions(16, 16);
         this.noUvo();
         return this;
      }

      public CTextureButtonWidget.Builder tooltip(class_7919 tooltip) {
         this.tooltip = tooltip;
         return this;
      }

      public CTextureButtonWidget build() {
         CTextureButtonWidget widget = new CTextureButtonWidget(this.x, this.y, this.width, this.height, this.u, this.v, this.hoveredVOffset, this.texture, this.textureWidth, this.textureHeight, this.textureTileWidth, this.textureTileHeight, this.pressAction, this.message, this.color, this.transparent);
         widget.method_47400(this.tooltip);
         return widget;
      }
   }
}
