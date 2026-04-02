package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.gui.background.LoadedBackground;
import com.dwarslooper.cactus.client.gui.screen.window.WindowScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.GridScrollableWidget;
import com.dwarslooper.cactus.client.render.RenderHelper;
import com.dwarslooper.cactus.client.systems.ISerializable;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.dwarslooper.cactus.client.systems.config.impl.CactusConfig;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.mixinterface.IBackgroundAccessImpl;
import com.google.gson.JsonObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.class_10799;
import net.minecraft.class_11909;
import net.minecraft.class_124;
import net.minecraft.class_156;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_5244;
import net.minecraft.class_9848;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BackgroundSelectorScreen extends WindowScreen {
   private static final File dir;
   private static final List<File> files;
   private static final Function<Integer, class_2561> LOADING_TEXT;
   private static final List<LoadedBackground> backgrounds;
   private static boolean isReloading;
   private static boolean loadedOnce;
   private final Consumer<LoadedBackground> changeCallback;
   private BackgroundSelectorScreen.Widget widget;
   private static LoadedBackground selected;

   public BackgroundSelectorScreen(class_437 parent, Consumer<LoadedBackground> changeCallback) {
      super("backgroundSelector", parent.field_22789 - parent.field_22789 / 4, parent.field_22790 - parent.field_22790 / 8);
      this.changeCallback = changeCallback;
      if (!dir.exists()) {
         dir.mkdirs();
      }

   }

   public void method_25426() {
      super.method_25426();
      this.method_37063(this.widget = new BackgroundSelectorScreen.Widget(this));
      this.widget.method_46421(this.x() + 4);
      if (!loadedOnce && backgrounds.isEmpty()) {
         this.reload();
         loadedOnce = true;
      } else {
         List var10000 = backgrounds;
         BackgroundSelectorScreen.Widget var10001 = this.widget;
         Objects.requireNonNull(var10001);
         var10000.forEach(var10001::add);
      }

      this.method_37063(new CButtonWidget(this.x() + this.boxWidth() / 2 - 100 - 50 - 4, this.y() + this.boxHeight() - 24, 100, 20, class_2561.method_43470(this.getTranslatableElement("reload", new Object[0])), (button) -> {
         this.reload();
      }));
      this.method_37063(new CButtonWidget(this.x() + this.boxWidth() / 2 - 50, this.y() + this.boxHeight() - 24, 100, 20, class_2561.method_43471("gui.screen.cactus.button.folder"), (button) -> {
         class_156.method_668().method_672(dir);
      }));
      this.method_37063(new CButtonWidget(this.x() + this.boxWidth() / 2 + 50 + 4, this.y() + this.boxHeight() - 24, 100, 20, class_5244.field_24334, (button) -> {
         this.method_25419();
      }));
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_27534(CactusConstants.mc.field_1772, this.field_22785, this.field_22789 / 2, this.y() + 2, -1);
      if (isReloading) {
         context.method_27534(CactusConstants.mc.field_1772, (class_2561)LOADING_TEXT.apply(files.size()), this.field_22789 / 2, this.field_22790 / 2, -1);
         class_327 var10001 = CactusConstants.mc.field_1772;
         String var10002 = RenderHelper.getLoading();
         int var10003 = this.field_22789 / 2;
         int var10004 = this.field_22790 / 2;
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         context.method_25300(var10001, var10002, var10003, var10004 + 9 + 2, -11141291);
      }

      if (backgrounds.size() > this.widget.method_25396().size()) {
         this.method_41843();
      }

   }

   private static CompletableFuture<List<LoadedBackground>> loadBackgrounds() {
      files.clear();
      if (dir.exists() && !isReloading) {
         File[] fa = dir.listFiles();
         if (fa == null) {
            return CompletableFuture.completedFuture(Collections.emptyList());
         } else {
            isReloading = true;
            files.addAll(Arrays.asList(fa));
            return CompletableFuture.supplyAsync(() -> {
               List<LoadedBackground> backgrounds = new ArrayList();
               Iterator var1 = files.iterator();

               while(var1.hasNext()) {
                  File f = (File)var1.next();
                  LoadedBackground lb = LoadedBackground.create(f);
                  if (lb != null) {
                     backgrounds.add(lb);
                  }
               }

               isReloading = false;
               return backgrounds;
            });
         }
      } else {
         return CompletableFuture.completedFuture(Collections.emptyList());
      }
   }

   private void reload() {
      if (!isReloading) {
         this.widget.clear();
         BackgroundSelectorScreen.Widget var10000 = this.widget;
         BackgroundSelectorScreen.Widget var10003 = this.widget;
         Objects.requireNonNull(var10003);
         var10000.addEntry(var10003.new NoneEntry());
         backgrounds.removeIf((background) -> {
            background.close();
            return true;
         });
         loadBackgrounds().thenAccept((list) -> {
            CactusClient.getLogger().info("Loaded {} custom backgrounds", list.size());
            backgrounds.addAll(list);
            CactusConstants.mc.execute(() -> {
               this.method_41843();
            });
         });
      }
   }

   public static boolean isSelected(LoadedBackground background) {
      return selected != null && selected.getFile().equals(background != null ? background.getFile() : null);
   }

   private void select(@Nullable LoadedBackground background) {
      if (background != null) {
         background.select().thenAccept((bg) -> {
            selected = bg;
            this.changeCallback.accept(bg);
            ((IBackgroundAccessImpl)this).cactus$updateBackground();
         });
      } else {
         Consumer var10000 = this.changeCallback;
         selected = null;
         var10000.accept((Object)null);
         ((IBackgroundAccessImpl)this).cactus$updateBackground();
      }

   }

   public static LoadedBackground getSelected() {
      return selected;
   }

   static {
      dir = new File(CactusConstants.DIRECTORY, "backgrounds");
      files = new ArrayList();
      LOADING_TEXT = (i) -> {
         return class_2561.method_43469("gui.screen.backgroundSelector.loading", new Object[]{i});
      };
      backgrounds = new ArrayList();
      isReloading = false;
      loadedOnce = false;
   }

   public class Widget extends GridScrollableWidget<BackgroundSelectorScreen.Widget.Entry> {
      private static final class_2960 CHECKMARK = class_2960.method_60654("pending_invite/accept");

      public Widget(WindowScreen parent) {
         super(CactusConstants.mc, parent.boxWidth() - 8, parent.boxHeight() - 8 - 24 - 10, parent.y() + 4 + 10, 80, 80);
         this.addEntry(new BackgroundSelectorScreen.Widget.NoneEntry());
      }

      public void add(LoadedBackground background) {
         this.addEntry(new BackgroundSelectorScreen.Widget.PanoramaEntry(background));
      }

      public int method_25322() {
         return this.field_22758 - 20;
      }

      public class NoneEntry extends BackgroundSelectorScreen.Widget.Entry {
         public static class_2960 DEFAULT_PANORAMA = class_2960.method_60654("textures/gui/title/background/panorama_0.png");

         public void renderGrid(class_332 context, int index, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int color = class_9848.method_61318(1.0F, 0.5F, 0.5F, 0.5F);
            context.method_25291(class_10799.field_56883, DEFAULT_PANORAMA, x + 2, y + 2, 0.0F, 0.0F, entryWidth - 4, entryHeight - 4, entryWidth - 4, entryHeight - 4, color);
            context.method_27534(CactusConstants.mc.field_1772, class_2561.method_43471("generator.minecraft.normal").method_27692(class_124.field_1067), x + entryWidth / 2, y + entryHeight / 2, -1);
         }

         public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
            BackgroundSelectorScreen.this.select((LoadedBackground)null);
            Widget.this.method_25354(CactusConstants.mc.method_1483());
            return true;
         }
      }

      public class PanoramaEntry extends BackgroundSelectorScreen.Widget.Entry {
         private final LoadedBackground background;

         public PanoramaEntry(LoadedBackground background) {
            this.background = background;
         }

         public void renderGrid(class_332 context, int index, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            boolean selected = BackgroundSelectorScreen.isSelected(this.background);
            int color = class_9848.method_61318(1.0F, 0.5F, 0.5F, 0.5F);
            context.method_25291(class_10799.field_56883, this.background.getPreviewIdentifier(), x + 2, y + 2, 0.0F, 0.0F, entryWidth - 4, entryHeight - 4, entryWidth - 4, entryHeight - 4, selected ? -1 : color);
            if (selected) {
               context.method_52707(class_10799.field_56883, BackgroundSelectorScreen.Widget.CHECKMARK, x + 2, y + 2, 12, 12, -1);
            }

         }

         public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
            BackgroundSelectorScreen.this.select(this.background);
            Widget.this.method_25354(CactusConstants.mc.method_1483());
            return true;
         }
      }

      public abstract static class Entry extends GridScrollableWidget.GridEntry<BackgroundSelectorScreen.Widget.Entry> {
      }
   }

   public static class Configuration implements ISerializable<BackgroundSelectorScreen.Configuration> {
      private int hash;

      public static BackgroundSelectorScreen.Configuration get() {
         return (BackgroundSelectorScreen.Configuration)((CactusConfig)CactusClient.getConfig(CactusConfig.class)).getSubConfig(BackgroundSelectorScreen.Configuration.class);
      }

      public void registerTexturesNow() {
         BackgroundSelectorScreen.loadBackgrounds().thenAccept((list) -> {
            BackgroundSelectorScreen.backgrounds.addAll(list);
            list.stream().filter((background) -> {
               return background.getFile().hashCode() == this.hash;
            }).findFirst().ifPresent((background) -> {
               background.select().thenAccept((b) -> {
                  if (CactusConstants.mc.field_1755 != null) {
                     ((IBackgroundAccessImpl)CactusConstants.mc.field_1755).cactus$updateBackground();
                  }

               });
               BackgroundSelectorScreen.selected = background;
            });
         });
      }

      public JsonObject toJson(TreeSerializerFilter filter) {
         JsonObject object = new JsonObject();
         object.addProperty("selected", BackgroundSelectorScreen.selected != null ? BackgroundSelectorScreen.selected.getFile().hashCode() : -1);
         return object;
      }

      public BackgroundSelectorScreen.Configuration fromJson(JsonObject object) {
         this.hash = object.has("selected") ? object.get("selected").getAsInt() : -1;
         return this;
      }
   }
}
