package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.render.RenderHelper;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.networking.ImgurHelper;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1060;
import net.minecraft.class_10799;
import net.minecraft.class_124;
import net.minecraft.class_156;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_3674;
import net.minecraft.class_410;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_4185.class_7840;
import org.jetbrains.annotations.NotNull;

public class ScreenshotViewerScreen extends CScreen {
   private final List<File> entries;
   private final int index;
   private final File screenshotFile;
   private class_1011 image;
   private class_2960 screenshot;

   public ScreenshotViewerScreen(class_437 parent, File file) {
      super("screenshot_view");
      this.entries = Collections.emptyList();
      this.parent = parent;
      this.screenshotFile = file;
      this.index = -1;
   }

   public ScreenshotViewerScreen(class_437 parent, List<File> entries, int index) {
      super("screenshot_view");
      this.parent = parent;
      this.entries = entries;
      this.index = index;
      this.screenshotFile = (File)entries.get(index);
   }

   public void method_25426() {
      super.method_25426();
      if (this.index >= 0) {
         class_4185 previous = (new class_7840(class_2561.method_43470("<-"), (button) -> {
            CactusConstants.mc.method_1507(new ScreenshotViewerScreen(this.parent, this.entries, Math.max(0, this.index - 1)));
         })).method_46434(20, (this.field_22790 - 20) / 2, 20, 20).method_46431();
         previous.field_22763 = this.index > 0;
         this.method_37063(previous);
         int maxIndex = this.entries.size() - 1;
         class_4185 next = (new class_7840(class_2561.method_43470("->"), (button) -> {
            CactusConstants.mc.method_1507(new ScreenshotViewerScreen(this.parent, this.entries, Math.min(maxIndex, this.index + 1)));
         })).method_46434(this.field_22789 - 40, (this.field_22790 - 20) / 2, 20, 20).method_46431();
         next.field_22763 = this.index < maxIndex;
         this.method_37063(next);
      }

      this.method_37063(new CButtonWidget(this.field_22789 / 2 - 240 - 6, this.field_22790 - 40, 120, 20, class_2561.method_43470(this.getTranslatableElement("openScreenshot", new Object[0])), (button) -> {
         class_156.method_668().method_672(this.screenshotFile);
      }));
      this.method_37063(new CButtonWidget(this.field_22789 / 2 - 120 - 2, this.field_22790 - 40, 120, 20, class_2561.method_43470(this.getTranslatableElement("openFolder", new Object[0])), (button) -> {
         class_156.method_668().method_672(this.screenshotFile.getParentFile());
      }));
      this.method_37063(new CButtonWidget(this.field_22789 / 2 + 2, this.field_22790 - 40, 120, 20, class_2561.method_43470(this.getTranslatableElement("upload", new Object[0])), (button) -> {
         button.method_25355(class_2561.method_43470(this.getTranslatableElement("uploading", new Object[0])));
         button.field_22763 = false;
         ImgurHelper.uploadImage(this.screenshotFile).whenComplete((obj, error) -> {
            if (error == null) {
               String link = obj.get("link").getAsString();
               CactusClient.getLogger().info("Screenshot uploaded to {} (id={},deletehash={})", new Object[]{link, obj.get("id"), obj.get("deletehash")});
               button.method_25355(class_2561.method_43470(this.getTranslatableElement("uploadSuccess", new Object[0])).method_27692(class_124.field_1060));
               (new class_3674()).method_15979(CactusConstants.mc.method_22683(), link);
            } else {
               button.method_25355(class_2561.method_43470(this.getTranslatableElement("uploadFailed", new Object[0])).method_27692(class_124.field_1061));
               CactusClient.getLogger().error("Failed to upload image", error);
            }

         });
      }));
      this.method_37063(new CButtonWidget(this.field_22789 / 2 + 2 + 120 + 4, this.field_22790 - 40, 120, 20, class_2561.method_43470(this.getTranslatableElement("delete", new Object[0])), (button) -> {
         CactusConstants.mc.method_1507(new class_410((b) -> {
            if (b) {
               this.screenshotFile.delete();
               this.method_25419();
            } else {
               CactusConstants.mc.method_1507(this);
            }

         }, class_2561.method_43470(this.getTranslatableElement("delete.header", new Object[0])), class_2561.method_43470(this.getTranslatableElement("delete.warning", new Object[]{this.screenshotFile.getName()})), class_2561.method_43470(this.getTranslatableElement("delete", new Object[0])), class_2561.method_43471("gui.cancel")));
      }));

      try {
         if (this.screenshot == null) {
            FileInputStream stream = new FileInputStream(this.screenshotFile);
            this.image = class_1011.method_4309(stream);
            class_2960 i = class_2960.method_60655("cactus", "dynamic/screenshot/view");
            class_1060 var10000 = CactusConstants.mc.method_1531();
            Objects.requireNonNull(i);
            var10000.method_4616(i, new class_1043(i::toString, this.image));
            this.screenshot = i;
         }
      } catch (Exception var4) {
         CactusClient.getLogger().error("Failed to read image data", var4);
      }

   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      int spacing = true;
      float imgRatio = (float)this.image.method_4307() / (float)this.image.method_4323();
      int texHeight = this.field_22790 - 24 - 20;
      int texWidth = (int)((float)texHeight * imgRatio);
      RenderHelper.drawBorder(context, (this.field_22789 - texWidth) / 2 - 1, 7, texWidth + 2, texHeight + 2, -11184811);
      if (this.screenshot != null) {
         context.method_25302(class_10799.field_56883, this.screenshot, (this.field_22789 - texWidth) / 2, 8, 0.0F, 0.0F, texWidth, texHeight, texWidth, texHeight, texWidth, texHeight);
      }

      context.method_25300(this.field_22793, this.getTranslatableElement("title", new Object[]{this.screenshotFile.getName()}), this.field_22789 / 2, 10, -1);
      super.method_25394(context, mouseX, mouseY, delta);
   }

   public void method_25410(int width, int height) {
      super.method_25410(width, height);
      if (this.parent != null) {
         this.parent.method_25410(width, height);
      }

   }

   public void method_25432() {
      CactusConstants.mc.method_1531().method_4615(this.screenshot);
      this.screenshot = null;
   }
}
