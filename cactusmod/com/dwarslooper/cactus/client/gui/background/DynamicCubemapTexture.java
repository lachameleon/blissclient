package com.dwarslooper.cactus.client.gui.background;

import com.dwarslooper.cactus.client.CactusClient;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import net.minecraft.class_1011;
import net.minecraft.class_10539;
import net.minecraft.class_1084;
import net.minecraft.class_11405;
import net.minecraft.class_12253;
import net.minecraft.class_2960;
import net.minecraft.class_3300;

public class DynamicCubemapTexture extends class_11405 {
   private final List<byte[]> parts;

   public DynamicCubemapTexture(class_2960 identifier, List<byte[]> parts) {
      super(identifier);
      this.parts = parts;
   }

   public class_10539 method_65809(class_3300 resourceManager) throws IOException {
      if (this.parts != null && this.parts.size() == 6) {
         class_1011[] images = new class_1011[6];
         int[] faceOrder = new int[]{1, 3, 5, 4, 0, 2};

         int i;
         try {
            int width;
            for(width = 0; width < 6; ++width) {
               images[width] = class_1011.method_4309(new ByteArrayInputStream((byte[])this.parts.get(faceOrder[width])));
            }

            width = images[0].method_4307();
            int height = images[0].method_4323();
            class_1011 stitched = new class_1011(width, height * 6, false);

            for(i = 0; i < 6; ++i) {
               if (images[i].method_4307() != width || images[i].method_4323() != height) {
                  throw new IOException("Image dimensions mismatch");
               }

               images[i].method_47594(stitched, 0, 0, 0, i * height, width, height, false, true);
               images[i].close();
            }

            return new class_10539(stitched, new class_1084(true, false, class_12253.field_64077, 0.0F));
         } catch (Exception var9) {
            class_1011[] var5 = images;
            int var6 = images.length;

            for(i = 0; i < var6; ++i) {
               class_1011 img = var5[i];
               if (img != null) {
                  img.close();
               }
            }

            CactusClient.getLogger().error("Failed to load dynamic cubemap texture", var9);
            throw new IOException(var9);
         }
      } else {
         return super.method_65809(resourceManager);
      }
   }
}
