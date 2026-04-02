package com.dwarslooper.cactus.client.util.generic;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.function.UnaryOperator;
import javax.imageio.ImageIO;

public class ImageUtils {
   public static ByteBuffer writeImageToBuffer(BufferedImage image) {
      int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), (int[])null, 0, image.getWidth());
      ByteBuffer buffer = ByteBuffer.allocate(4 * image.getWidth() * image.getHeight());
      int[] var3 = pixels;
      int var4 = pixels.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         int pixel = var3[var5];
         buffer.putInt(pixel << 8 | pixel >> 24 & 255);
      }

      buffer.flip();
      return buffer;
   }

   public static BufferedImage resize(BufferedImage image, int width, int height) {
      BufferedImage resizedImage = new BufferedImage(width, height, 1);
      Graphics2D graphics2D = resizedImage.createGraphics();
      graphics2D.drawImage(image, 0, 0, width, height, (ImageObserver)null);
      graphics2D.dispose();
      return resizedImage;
   }

   public static BufferedImage modifyCactusColorSpace(BufferedImage image, Color color, int threshold) {
      for(int y = 0; y < image.getHeight(); ++y) {
         for(int x = 0; x < image.getWidth(); ++x) {
            int rgb = image.getRGB(x, y);
            int alpha = rgb >> 24 & 255;
            int red = rgb >> 16 & 255;
            int green = rgb >> 8 & 255;
            int blue = rgb & 255;
            if (green > red + threshold && green > blue + threshold) {
               float intensity = (float)green / 255.0F;
               rgb = alpha << 24 | rgbWithIntensity(intensity, color.getRed()) << 16 | rgbWithIntensity(intensity, color.getGreen()) << 8 | rgbWithIntensity(intensity, color.getBlue());
               image.setRGB(x, y, rgb);
            }
         }
      }

      return image;
   }

   private static int rgbWithIntensity(float intensity, int rgb) {
      return Math.min(255, Math.round(intensity * (float)rgb));
   }

   public static BufferedImage loadImageFromResource(String path) {
      try {
         InputStream inputStream = ImageUtils.class.getResourceAsStream(path);

         BufferedImage var2;
         label48: {
            try {
               if (inputStream != null) {
                  var2 = ImageIO.read(inputStream);
                  break label48;
               }

               var2 = null;
            } catch (Throwable var5) {
               if (inputStream != null) {
                  try {
                     inputStream.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }
               }

               throw var5;
            }

            if (inputStream != null) {
               inputStream.close();
            }

            return var2;
         }

         if (inputStream != null) {
            inputStream.close();
         }

         return var2;
      } catch (IOException var6) {
         return null;
      }
   }

   public static InputStream bufferedImageToInputStream(BufferedImage image, String format) {
      try {
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

         ByteArrayInputStream var3;
         try {
            ImageIO.write(image, format, byteArrayOutputStream);
            byteArrayOutputStream.flush();
            var3 = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
         } catch (Throwable var6) {
            try {
               byteArrayOutputStream.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         byteArrayOutputStream.close();
         return var3;
      } catch (IOException var7) {
         return null;
      }
   }

   public static InputStream modifyInputFromResource(String path, String writeFormat, UnaryOperator<BufferedImage> modifier) {
      BufferedImage image = loadImageFromResource(path);
      return image != null ? bufferedImageToInputStream((BufferedImage)modifier.apply(image), writeFormat) : null;
   }
}
