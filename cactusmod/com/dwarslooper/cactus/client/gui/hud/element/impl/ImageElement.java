package com.dwarslooper.cactus.client.gui.hud.element.impl;

import com.dwarslooper.cactus.client.gui.hud.ElementSettingsScreen;
import com.dwarslooper.cactus.client.gui.hud.element.DynamicHudElement;
import com.dwarslooper.cactus.client.systems.config.settings.impl.FileSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1060;
import net.minecraft.class_10799;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import org.joml.Vector2i;

public class ImageElement extends DynamicHudElement<ImageElement> {
   private static final ExecutorService executor = Executors.newSingleThreadExecutor();
   private final class_2960 texture = class_2960.method_60655("cactus", "hud/image/" + String.valueOf(UUID.randomUUID()));
   private String filePath;
   private ImageElement.Status status;
   public Setting<File> displayFile;

   public ImageElement() {
      super("image", new Vector2i(64, 64), new Vector2i(20, 20));
      this.status = ImageElement.Status.EMPTY;
      this.displayFile = this.elementGroup.add(new FileSetting("file", (File)null));
   }

   public ImageElement duplicate() {
      return new ImageElement();
   }

   public void removed() {
      this.release();
   }

   public void renderContent(class_332 context, int x, int y, int width, int height, int screenWidth, int screenHeight, float delta, boolean inEditor) {
      if (!(CactusConstants.mc.field_1755 instanceof ElementSettingsScreen)) {
         File file = (File)this.displayFile.get();
         if (file != null && !file.getAbsolutePath().equals(this.filePath)) {
            this.status = ImageElement.Status.LOADING;
            this.filePath = file.getAbsolutePath();
            executor.execute(() -> {
               try {
                  class_1011 image = class_1011.method_4309(new FileInputStream(file));
                  CactusConstants.mc.execute(() -> {
                     class_1060 var10000 = CactusConstants.mc.method_1531();
                     class_2960 var10001 = this.texture;
                     class_2960 var10004 = this.texture;
                     Objects.requireNonNull(var10004);
                     var10000.method_4616(var10001, new class_1043(var10004::toString, image));
                  });
                  this.status = ImageElement.Status.SUCCESS;
               } catch (IOException var3) {
                  this.status = ImageElement.Status.FAIL;
                  this.release();
               }

            });
         }
      }

      if (this.getSize().x() >= 64) {
         String var10000;
         switch(this.status.ordinal()) {
         case 0:
            var10000 = "§7No Selection";
            break;
         case 1:
            var10000 = "§7Loading..";
            break;
         case 2:
            var10000 = "§cInvalid PNG";
            break;
         default:
            var10000 = null;
         }

         String text = var10000;
         if (text != null) {
            class_327 var10001 = CactusConstants.mc.field_1772;
            int var10003 = x + width / 2;
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            context.method_25300(var10001, text, var10003, y + (height - 9) / 2 + 1, -1);
         }
      }

      if (this.status == ImageElement.Status.SUCCESS) {
         context.method_25290(class_10799.field_56883, this.texture, x, y, 0.0F, 0.0F, width, height, width, height);
      }

   }

   private void release() {
      CactusConstants.mc.method_1531().method_4615(this.texture);
   }

   public static enum Status {
      EMPTY,
      LOADING,
      FAIL,
      SUCCESS;

      // $FF: synthetic method
      private static ImageElement.Status[] $values() {
         return new ImageElement.Status[]{EMPTY, LOADING, FAIL, SUCCESS};
      }
   }
}
