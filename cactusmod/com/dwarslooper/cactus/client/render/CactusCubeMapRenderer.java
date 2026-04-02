package com.dwarslooper.cactus.client.render;

import com.dwarslooper.cactus.client.util.CactusConstants;
import java.io.File;
import java.io.FileInputStream;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1060;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_751;

public class CactusCubeMapRenderer extends class_751 {
   private static final class_2960 faces = class_2960.method_60655("cactus", "panorama");
   private boolean canDraw = false;

   public CactusCubeMapRenderer(File directory) {
      super(faces);
      if (directory.exists() && directory.listFiles().length != 0) {
         try {
            for(int i = 0; i < 6; ++i) {
               String name = "panorama_%s.png".formatted(new Object[]{i});
               class_1043 texture = new class_1043(() -> {
                  return name;
               }, class_1011.method_4309(new FileInputStream(new File(directory, name))));
               CactusConstants.mc.execute(() -> {
                  class_1060 var10000 = CactusConstants.mc.method_1531();
                  class_2960 var10001 = faces;
                  String var10002 = faces.method_12832();
                  var10000.method_4616(var10001.method_45136(var10002 + "_" + i + ".png"), texture);
               });
            }

            this.canDraw = true;
            System.out.println("Can draw!");
         } catch (Exception var6) {
            var6.printStackTrace();
         }

      }
   }

   public void method_3156(class_310 client, float x, float y) {
      if (this.canDraw) {
         super.method_3156(client, x, y);
      }
   }
}
