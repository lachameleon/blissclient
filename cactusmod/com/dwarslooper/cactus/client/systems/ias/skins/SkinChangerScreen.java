package com.dwarslooper.cactus.client.systems.ias.skins;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.screen.impl.AccountListScreen;
import com.dwarslooper.cactus.client.gui.screen.window.TextInputWindow;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.imageio.ImageIO;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1060;
import net.minecraft.class_156;
import net.minecraft.class_2477;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_364;
import net.minecraft.class_437;
import net.minecraft.class_8685;
import net.minecraft.class_8765;
import net.minecraft.class_12079.class_10726;
import net.minecraft.class_12079.class_12081;
import org.jetbrains.annotations.NotNull;

public class SkinChangerScreen extends CScreen {
   private SkinListWidget skinListWidget;
   private class_8765 skinPreview;

   public SkinChangerScreen() {
      super("skinChanger");
      this.parent = CactusConstants.mc.field_1755;
   }

   public void method_25426() {
      super.method_25426();
      this.skinPreview = (class_8765)this.method_25429(new class_8765(127, 180, CactusConstants.mc.method_31974(), this::getPreviewTextures));
      this.skinPreview.method_48229(this.field_22789 - this.field_22789 / 2 - 63, (this.field_22790 - 180) / 2);
      File skinDir = new File(CactusConstants.DIRECTORY, "skins");
      if (!skinDir.exists()) {
         skinDir.mkdirs();
      }

      int listHeight;
      if (this.skinListWidget == null) {
         this.skinListWidget = new SkinListWidget(this, 0, 0);
         if (skinDir.isDirectory()) {
            File[] var2 = (File[])Objects.requireNonNull(skinDir.listFiles());
            listHeight = var2.length;

            for(int var4 = 0; var4 < listHeight; ++var4) {
               File file = var2[var4];
               this.skinListWidget.add(file);
            }
         }
      }

      int listWidth = Math.max(this.field_22789 / 3, 124);
      listHeight = this.field_22790 - 80;
      this.skinListWidget.method_73369(listWidth, listHeight, 4, 40);
      this.method_37063(this.skinListWidget);
      this.method_37063(new CButtonWidget(4, this.field_22790 - 24, 100, 20, class_2561.method_43471("gui.screen.cactus.button.folder"), (button) -> {
         class_156.method_668().method_672(skinDir);
         this.method_25395((class_364)null);
      }));
      this.method_37063(new CButtonWidget(108, this.field_22790 - 24, 100, 20, class_2561.method_43470(this.getTranslatableElement("download", new Object[0])), (button) -> {
         CactusConstants.mc.method_1507((new TextInputWindow("none", this.getTranslatableElement("download", new Object[0]))).setPlaceholder(class_2477.method_10517().method_48307("gui.screen.account_switcher.offlineSession.text")).allowEmptyText(false).range(3, 16).setPredicate((s) -> {
            return s.matches("^[a-zA-Z0-9_]*$");
         }).onSubmit((s) -> {
            try {
               BufferedImage image = ImageIO.read((new URI("https://mc-heads.net/skin/" + s)).toURL());
               File file = new File(skinDir, s + ".png");
               ImageIO.write(image, "png", file);
               this.method_41843();
               this.skinListWidget.method_25396().forEach((skinEntry) -> {
                  if (skinEntry.getData().file.equals(file)) {
                     this.skinListWidget.method_25395(skinEntry);
                  }

               });
            } catch (URISyntaxException | IOException var5) {
               CactusClient.getLogger().error("Failed to download skin for %s".formatted(new Object[]{s}), var5);
            }

         }));
      }));
      this.method_37063(new CButtonWidget(30, 6, 100, 20, class_2561.method_43470(this.getTranslatableElement("switchAccount", new Object[0])), (button) -> {
         class_310 var10000 = CactusConstants.mc;
         AccountListScreen var10001 = new AccountListScreen;
         class_437 patt0$temp = this.parent;
         class_437 var10003;
         if (patt0$temp instanceof AccountListScreen) {
            AccountListScreen als = (AccountListScreen)patt0$temp;
            var10003 = als.parent;
         } else {
            var10003 = this.parent;
         }

         var10001.<init>(var10003);
         var10000.method_1507(var10001);
      }));
   }

   public void method_25419() {
      class_437 var2 = this.parent;
      if (var2 instanceof AccountListScreen) {
         AccountListScreen als = (AccountListScreen)var2;
         CactusConstants.mc.method_1507(als.parent);
      } else {
         super.method_25419();
      }
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      if (this.skinListWidget.method_25334() != null) {
         this.skinPreview.method_25394(context, mouseX, mouseY, delta);
      }

      context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 8, -1);
   }

   protected void method_41843() {
      this.skinListWidget = null;
      super.method_41843();
   }

   private class_8685 getPreviewTextures() {
      SkinListWidget.SkinEntry entry = this.skinListWidget != null ? (SkinListWidget.SkinEntry)this.skinListWidget.method_25334() : null;
      return entry != null && entry.getData().getTextures() != null ? entry.getData().getTextures() : SkinHelper.getCachedSkinOrFetch(CactusConstants.mc.method_1548().method_44717());
   }

   public static class SkinTextureData {
      private static final Map<Path, UUID> skinToId = new HashMap();
      private final File file;
      private class_8685 textures;

      public SkinTextureData(File file) {
         this.file = file;

         try {
            Map var10001 = skinToId;
            Path var10002 = file.toPath();
            class_2960 id = class_2960.method_60655("cactus", "skin." + String.valueOf(var10001.computeIfAbsent(var10002, (path) -> {
               return UUID.randomUUID();
            })));
            class_1011 image = class_1011.method_4309(new FileInputStream(file));
            class_1060 var10000 = class_310.method_1551().method_1531();
            Objects.requireNonNull(id);
            var10000.method_4616(id, new class_1043(id::toString, image));
            class_12081 body = new class_10726(id, id);
            this.textures = new class_8685(body, (class_12081)null, (class_12081)null, SkinHelper.skinType(image), false);
         } catch (Exception var5) {
            CactusClient.getLogger().error("Cannot create texture from file '{}'! File corrupted? ({})", file.getName(), var5.getMessage());
         }

      }

      public File getFile() {
         return this.file;
      }

      public class_8685 getTextures() {
         return this.textures;
      }
   }
}
