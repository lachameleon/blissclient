package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.GridScrollableWidget;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1060;
import net.minecraft.class_10799;
import net.minecraft.class_11909;
import net.minecraft.class_156;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_5244;
import org.jetbrains.annotations.NotNull;

public class ScreenshotListScreen extends CScreen {
   private static int ID_COUNT = 0;
   private final File dir;
   private ExecutorService executor;
   private ScreenshotListScreen.Widget screenshotWidget;

   public ScreenshotListScreen() {
      super("screenshots");
      this.dir = new File(CactusConstants.mc.field_1697, "screenshots");
   }

   public ScreenshotListScreen(class_437 parent) {
      this();
      this.parent = parent;
   }

   public void method_25426() {
      super.method_25426();
      this.executor = Executors.newSingleThreadScheduledExecutor();
      if (this.screenshotWidget == null) {
         this.screenshotWidget = new ScreenshotListScreen.Widget(this.field_22789, this.field_22790 - 70, 30, 68, 120);
      }

      if (this.dir.exists()) {
         this.screenshotWidget.loadFiles(this.dir);
      }

      this.method_37063(this.screenshotWidget);
      this.method_37063(new CButtonWidget(this.field_22789 / 2 - 150 - 2, this.field_22790 - 32, 150, 20, class_2561.method_43471("gui.screen.screenshot_view.openFolder"), (button) -> {
         class_156.method_668().method_672(this.dir);
      }));
      this.method_37063(new CButtonWidget(this.field_22789 / 2 + 2, this.field_22790 - 32, 150, 20, class_5244.field_24334, (button) -> {
         this.method_25419();
      }));
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 8, -1);
   }

   public void method_25410(int width, int height) {
      super.method_25410(width, height);
      this.screenshotWidget.method_55445(this.field_22789, this.field_22790 - 70);
   }

   public void method_25432() {
      this.dropTextures();
      this.executor.shutdownNow();
   }

   private void dropTextures() {
      ArrayList<ScreenshotListScreen.Widget.ScreenshotEntry> snapshot = new ArrayList(this.screenshotWidget.method_25396());
      CompletableFuture.runAsync(() -> {
         snapshot.forEach((entry) -> {
            try {
               entry.close();
            } catch (Exception var2) {
            }

         });
      });
   }

   public class Widget extends GridScrollableWidget<ScreenshotListScreen.Widget.ScreenshotEntry> {
      public Widget(int width, int height, int y, int itemHeight, int itemWidth) {
         super(CactusConstants.mc, width, height, y, itemHeight, itemWidth);
      }

      public void loadFiles(File directory) {
         this.method_25339();
         File[] files = directory.listFiles((dir, name) -> {
            return name.endsWith(".png");
         });
         if (files != null) {
            Iterator var3 = Arrays.stream(files).sorted(Comparator.comparingLong(File::lastModified).reversed()).toList().iterator();

            while(var3.hasNext()) {
               File file = (File)var3.next();
               this.addEntry(new ScreenshotListScreen.Widget.ScreenshotEntry(file));
            }

         }
      }

      public int method_25322() {
         return this.field_22758 - 40;
      }

      protected int method_65507() {
         return this.method_55442() - 6;
      }

      public class ScreenshotEntry extends GridScrollableWidget.GridEntry<ScreenshotListScreen.Widget.ScreenshotEntry> implements AutoCloseable {
         private final File file;
         private boolean loaded = false;
         private boolean loadFailed = false;
         private class_1011 image;
         private class_2960 identifier;

         public ScreenshotEntry(File file) {
            this.file = file;
         }

         public void renderGrid(class_332 context, int index, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            if (!this.loaded) {
               this.loaded = true;
               ScreenshotListScreen.this.executor.execute(() -> {
                  try {
                     this.image = class_1011.method_4309(new FileInputStream(this.file));
                     CactusConstants.mc.execute(() -> {
                        if (!ScreenshotListScreen.this.executor.isShutdown() && CactusConstants.mc.field_1755 == ScreenshotListScreen.this) {
                           int var10002 = ++ScreenshotListScreen.ID_COUNT;
                           this.identifier = class_2960.method_60655("cactus", "screenshot_" + var10002);
                           class_1060 var10000 = CactusConstants.mc.method_1531();
                           class_2960 var10001 = this.identifier;
                           class_2960 var10004 = this.identifier;
                           Objects.requireNonNull(var10004);
                           var10000.method_4616(var10001, new class_1043(var10004::toString, this.image));
                        }
                     });
                  } catch (Exception var2) {
                     this.loadFailed = true;
                  }

               });
            } else if (this.identifier != null) {
               context.method_25290(class_10799.field_56883, this.identifier, x + 2, y + 2, 0.0F, 0.0F, entryWidth - 4, entryHeight - 4, entryWidth - 4, entryHeight - 4);
            } else if (!this.loadFailed) {
               context.method_25300(CactusConstants.mc.field_1772, "Loading..", x + entryWidth / 2, y + entryHeight / 2 + 1, -1);
            } else {
               context.method_25300(CactusConstants.mc.field_1772, "Can't load image", x + entryWidth / 2, y + entryHeight / 2 + 1, -1);
            }

         }

         public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
            List<File> entries = ScreenshotListScreen.this.screenshotWidget.method_25396().stream().map(ScreenshotListScreen.Widget.ScreenshotEntry::getFile).toList();
            CactusConstants.mc.method_1507(new ScreenshotViewerScreen(ScreenshotListScreen.this, entries, entries.indexOf(this.getFile())));
            return true;
         }

         public class_2960 getIdentifier() {
            return this.identifier;
         }

         public void close() {
            CactusConstants.mc.execute(() -> {
               CactusConstants.mc.method_1531().method_4615(this.identifier);
            });
         }

         public File getFile() {
            return this.file;
         }
      }
   }
}
