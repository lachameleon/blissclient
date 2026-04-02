package com.dwarslooper.cactus.client.util.game.render;

import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.MathUtils;
import com.dwarslooper.cactus.client.util.mixinterface.IRotatableColoredQuadGuiElementRenderState;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.minecraft.class_10017;
import net.minecraft.class_1011;
import net.minecraft.class_10799;
import net.minecraft.class_11231;
import net.minecraft.class_11242;
import net.minecraft.class_1309;
import net.minecraft.class_156;
import net.minecraft.class_1664;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_3532;
import net.minecraft.class_8666;
import net.minecraft.class_897;
import net.minecraft.class_898;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.stb.STBImage;

public class RenderUtils {
   public static final class_2960 TAGS = class_2960.method_60655("cactus", "textures/gui/tags.png");
   public static boolean darkMode;
   public static class_8666 SLIDER_TEXTURES = new class_8666(class_2960.method_60656("widget/slider"), class_2960.method_60656("widget/slider_highlighted"));
   public static class_8666 SLIDER_HANDLE_TEXTURES = new class_8666(class_2960.method_60656("widget/slider_handle"), class_2960.method_60656("widget/slider_handle_highlighted"));

   public static void applyDarkmode(class_332 context, class_339 widget) {
   }

   public static void drawItemWithMeta(class_332 context, class_1799 itemStack, int x, int y) {
      context.method_51427(itemStack, x, y);
      context.method_51431(CactusConstants.mc.field_1772, itemStack, x, y);
   }

   public static void drawTextAlignedRight(class_332 context, class_2561 text, int x, int y, int color, boolean shadow) {
      context.method_51439(CactusConstants.mc.field_1772, text, x - CactusConstants.mc.field_1772.method_27525(text), y, color, shadow);
   }

   public static void drawTextAlignedRight(class_332 context, String text, int x, int y, int color, boolean shadow) {
      drawTextAlignedRight(context, (class_2561)class_2561.method_43470(text), x, y, color, shadow);
   }

   public static void drawImportantNotificationIcon(class_332 context, int x, int y, float scale) {
      if ((class_156.method_658() / 400L & 1L) == 1L) {
         y += (int)(2.0F * scale);
      }

      context.method_51448().pushMatrix();
      context.method_51448().scale(scale, scale);
      context.method_25290(class_10799.field_56883, TAGS, (int)((float)x / scale), (int)((float)y / scale), 0.0F, 0.0F, 9, 8, 32, 32);
      context.method_51448().popMatrix();
   }

   public static void drawImportantNotificationIconWithTooltip(class_332 context, int x, int y, int mouseX, int mouseY, float scale, class_2561 tootip) {
      drawImportantNotificationIcon(context, x, y, scale);
      if ((float)mouseX > (float)x / scale && (float)mouseX <= (float)x / scale + 9.0F && (float)mouseY > (float)y / scale && (float)mouseY <= (float)y / scale + 8.0F) {
         context.method_51438(CactusConstants.mc.field_1772, tootip, mouseX, mouseY);
      }

   }

   public static void drawEntityAligned(class_332 context, int x, int y, int width, int height, int scale, class_1309 entity, Quaternionf rotation) {
      drawEntityAligned(context, x, y, width, height, scale, entity, rotation, (Quaternionf)null);
   }

   public static void drawEntityAligned(class_332 context, int x, int y, int width, int height, int scale, class_1309 entity, Quaternionf rotation, @Nullable Quaternionf cameraAngle) {
      drawEntityAligned(context, x, y, width, height, scale, entity, rotation, cameraAngle, (Consumer)null);
   }

   public static void drawEntityAligned(class_332 context, int x, int y, int width, int height, int scale, class_1309 entity, Quaternionf rotation, @Nullable Quaternionf cameraAngle, @Nullable Consumer<class_10017> stateEditor) {
      float entityScale = entity.method_55693();
      Vector3f translation = new Vector3f(0.0F, entity.method_17682() / 2.0F, 0.0F);
      drawEntity(context, x, y, x + width, y + height, (float)scale / entityScale, translation, rotation, cameraAngle, entity, stateEditor);
   }

   public static void drawEntity(class_332 drawer, int x1, int y1, int x2, int y2, float scale, Vector3f translation, Quaternionf rotation, @Nullable Quaternionf overrideCameraAngle, class_1309 entity, @Nullable Consumer<class_10017> stateEditor) {
      class_898 entityRenderManager = class_310.method_1551().method_1561();
      class_897<? super class_1309, ?> entityRenderer = entityRenderManager.method_3953(entity);
      class_10017 entityRenderState = entityRenderer.method_62425(entity, 1.0F);
      entityRenderState.field_61820 = 15728880;
      entityRenderState.field_61823.clear();
      entityRenderState.field_61821 = 0;
      if (stateEditor != null) {
         stateEditor.accept(entityRenderState);
      }

      drawer.method_70856(entityRenderState, scale, translation, rotation, overrideCameraAngle, x1, y1, x2, y2);
   }

   public static void drawSkin(class_332 context, double x, double y, class_2960 skinTexture, boolean slim, int scale) {
      context.method_51448().pushMatrix();
      context.method_51448().scale((float)scale, (float)scale);
      context.method_51448().translate((float)x, (float)y);
      context.method_25290(class_10799.field_56883, skinTexture, 4, 0, 8.0F, 8.0F, 8, 8, 64, 64);
      context.method_25290(class_10799.field_56883, skinTexture, 4, 8, 20.0F, 20.0F, 8, 12, 64, 64);
      context.method_25290(class_10799.field_56883, skinTexture, slim ? 1 : 0, 8, 44.0F, 20.0F, slim ? 3 : 4, 12, 64, 64);
      context.method_25290(class_10799.field_56883, skinTexture, 12, 8, 36.0F, 52.0F, slim ? 3 : 4, 12, 64, 64);
      context.method_25290(class_10799.field_56883, skinTexture, 4, 20, 4.0F, 20.0F, 4, 12, 64, 64);
      context.method_25290(class_10799.field_56883, skinTexture, 8, 20, 20.0F, 52.0F, 4, 12, 64, 64);
      if (CactusConstants.mc.field_1690.method_32594(class_1664.field_7563)) {
         context.method_25290(class_10799.field_56883, skinTexture, 4, 0, 40.0F, 8.0F, 8, 8, 64, 64);
      }

      if (CactusConstants.mc.field_1690.method_32594(class_1664.field_7570)) {
         context.method_25290(class_10799.field_56883, skinTexture, slim ? 1 : 0, 8, 44.0F, 36.0F, slim ? 3 : 4, 12, 64, 64);
      }

      if (CactusConstants.mc.field_1690.method_32594(class_1664.field_7568)) {
         context.method_25290(class_10799.field_56883, skinTexture, 12, 8, 52.0F, 52.0F, slim ? 3 : 4, 12, 64, 64);
      }

      if (CactusConstants.mc.field_1690.method_32594(class_1664.field_7565)) {
         context.method_25290(class_10799.field_56883, skinTexture, 4, 20, 4.0F, 36.0F, 4, 12, 64, 64);
      }

      if (CactusConstants.mc.field_1690.method_32594(class_1664.field_7566)) {
         context.method_25290(class_10799.field_56883, skinTexture, 8, 20, 4.0F, 52.0F, 4, 12, 64, 64);
      }

      if (CactusConstants.mc.field_1690.method_32594(class_1664.field_7564)) {
         context.method_25290(class_10799.field_56883, skinTexture, 4, 8, 20.0F, 36.0F, 8, 12, 64, 64);
      }

      context.method_51448().popMatrix();
   }

   public static void renderProgressBar(class_332 drawContext, int minX, int minY, int maxX, int maxY, float progress, int borderColor, int barColor) {
      int i = class_3532.method_15386((float)(maxX - minX - 4) * progress);
      drawContext.method_25294(minX + 2, minY + 2, minX + i, maxY - 2, barColor);
      drawContext.method_25294(minX + 1, minY, maxX - 1, minY + 1, borderColor);
      drawContext.method_25294(minX + 1, maxY, maxX - 1, maxY - 1, borderColor);
      drawContext.method_25294(minX, minY, minX + 1, maxY, borderColor);
      drawContext.method_25294(maxX, minY, maxX - 1, maxY, borderColor);
   }

   public static void renderProgressBar(class_332 drawContext, int minX, int minY, int maxX, int maxY, float progress) {
      renderProgressBar(drawContext, minX, minY, maxX, maxY, progress, -1, -1);
   }

   public static void renderLabeledProgressBar(class_332 context, int minX, int minY, int maxX, int maxY, float progress, long startTime, boolean textShadow) {
      long eta = MathUtils.calculateETA(progress, startTime);
      String etaHms = progress == 0.0F ? "N/A" : String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta), TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1L), TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1L));
      renderProgressBar(context, minX, minY, maxX, maxY, progress, -5592406, -11141291);
      context.method_51433(CactusConstants.mc.field_1772, (int)(progress * 100.0F) + "%", minX, maxY + 2, -1, textShadow);
      drawTextAlignedRight(context, (String)("ETA: " + etaHms), maxX, maxY + 2, -1, textShadow);
   }

   public static void drawCactusTexture(class_332 context, int x, int y, int id) {
      context.method_25290(class_10799.field_56883, CTextureButtonWidget.WIDGETS_TEXTURE, x, y, (float)(id * 20), 0.0F, 20, 20, 640, 64);
   }

   public static void drawCactusTexture(class_332 context, int x, int y, int id, int offsetV) {
      context.method_25290(class_10799.field_56883, CTextureButtonWidget.WIDGETS_TEXTURE, x, y, (float)(id * 20), (float)(offsetV * 20), 20, 20, 640, 64);
   }

   public static void drawText(class_332 context, class_2561 text, float x, float y, int color) {
      withOffset(context, x, y, (ctx) -> {
         ctx.method_27535(CactusConstants.mc.field_1772, text, 0, 0, color);
      });
   }

   public static void drawText(class_332 context, String text, float x, float y, int color) {
      withOffset(context, x, y, (ctx) -> {
         ctx.method_25303(CactusConstants.mc.field_1772, text, 0, 0, color);
      });
   }

   private static void withOffset(class_332 context, float x, float y, Consumer<class_332> renderer) {
      context.method_51448().pushMatrix();
      context.method_51448().translate(x, y);
      renderer.accept(context);
      context.method_51448().popMatrix();
   }

   public static void fillGradientHorizontal(class_332 context, int x1, int y1, int x2, int y2, int startColor, int endColor) {
      class_11242 state = new class_11242(class_10799.field_56879, class_11231.method_70899(), new Matrix3x2f(context.method_51448()), x1, y1, x2, y2, startColor, endColor, context.field_44659.method_70863());
      ((IRotatableColoredQuadGuiElementRenderState)state).cactus$setSideways(true);
      context.field_59826.method_70919(state);
   }

   public static byte[] asByteArray(class_1011 nativeImage) throws IOException {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

      byte[] array;
      try {
         WritableByteChannel writableByteChannel = Channels.newChannel(byteArrayOutputStream);

         try {
            if (!nativeImage.method_24032(writableByteChannel)) {
               throw new IOException("Could not write image to byte array: " + STBImage.stbi_failure_reason());
            }

            array = byteArrayOutputStream.toByteArray();
         } catch (Throwable var8) {
            if (writableByteChannel != null) {
               try {
                  writableByteChannel.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (writableByteChannel != null) {
            writableByteChannel.close();
         }
      } catch (Throwable var9) {
         try {
            byteArrayOutputStream.close();
         } catch (Throwable var6) {
            var9.addSuppressed(var6);
         }

         throw var9;
      }

      byteArrayOutputStream.close();
      return array;
   }

   public static void fillRainbow(class_332 context, int x1, int y1, int x2, int y2) {
      int[] colors = new int[]{Color.red.getRGB(), Color.yellow.getRGB(), Color.green.getRGB(), Color.cyan.getRGB(), Color.blue.getRGB(), Color.magenta.getRGB(), Color.red.getRGB()};
      int width = x2 - x1;
      int segments = colors.length - 1;
      int segmentWidth = width / segments;

      for(int i = 0; i < segments; ++i) {
         int startX = x1 + i * segmentWidth;
         int endX = i == segments - 1 ? x2 : startX + segmentWidth;
         fillGradientHorizontal(context, startX, y1, endX, y2, colors[i], colors[i + 1]);
      }

   }

   public static void beginPixelAligned(class_332 context) {
      context.method_51448().pushMatrix();
      context.method_51448().scale(1.0F / (float)(Integer)CactusConstants.mc.field_1690.method_42474().method_41753());
   }

   public static void endPixelAligned(class_332 context) {
      context.method_51448().popMatrix();
   }

   public static int multiplyUI(int i) {
      return i * (Integer)CactusConstants.mc.field_1690.method_42474().method_41753();
   }

   public static double multiplyUI(double i) {
      return i * (double)(Integer)CactusConstants.mc.field_1690.method_42474().method_41753();
   }
}
