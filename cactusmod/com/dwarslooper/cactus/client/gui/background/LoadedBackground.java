package com.dwarslooper.cactus.client.gui.background;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntUnaryOperator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1060;
import net.minecraft.class_2960;
import org.jetbrains.annotations.NotNull;

public abstract class LoadedBackground implements AutoCloseable {
   private static int ID_COUNT = 0;
   private final File file;
   private final class_1011 previewImage;
   private final class_2960 previewIdentifier;

   private LoadedBackground(File file, class_1011 image) {
      class_1011 icon = new class_1011(256, 256, false);
      int wh = Math.min(image.method_4307(), image.method_4323());
      image.method_4300(0, 0, wh, wh, icon);
      this.file = file;
      this.previewImage = icon;
      int var10002 = ++ID_COUNT;
      this.previewIdentifier = class_2960.method_60655("cactus", "preview-" + var10002);
      CactusConstants.mc.execute(() -> {
         class_1060 var10000 = CactusConstants.mc.method_1531();
         class_2960 var10001 = this.previewIdentifier;
         class_2960 var10004 = this.previewIdentifier;
         Objects.requireNonNull(var10004);
         var10000.method_4616(var10001, new class_1043(var10004::toString, icon));
      });
   }

   public static LoadedBackground create(File file) {
      boolean isStatic = !file.getName().endsWith(".zip");
      LoadedBackground background = isStatic ? LoadedBackground.Static.create(file) : LoadedBackground.Rotating.create(file);
      CactusClient.getLogger().info("Loaded custom background '{}' (static={})", file.getName(), isStatic);
      return background;
   }

   public CompletableFuture<LoadedBackground> select() {
      return this.loadTextures();
   }

   public CompletableFuture<LoadedBackground> loadTextures() {
      CompletableFuture<LoadedBackground> future = new CompletableFuture();
      CactusConstants.mc.execute(() -> {
         this.wrappedLoadTextures(future);
      });
      return future;
   }

   protected abstract void wrappedLoadTextures(CompletableFuture<LoadedBackground> var1);

   public abstract class_2960 getTextureIdentifier();

   @NotNull
   public File getFile() {
      return this.file;
   }

   public class_2960 getPreviewIdentifier() {
      return this.previewIdentifier;
   }

   protected class_1011 getPreviewImage() {
      return this.previewImage;
   }

   public boolean isStatic() {
      return this instanceof LoadedBackground.Static;
   }

   public void close() {
      CactusConstants.mc.execute(() -> {
         CactusConstants.mc.method_1531().method_4615(this.previewIdentifier);
      });
   }

   public static class Static extends LoadedBackground {
      private static final class_2960 ID = class_2960.method_60655("cactus", "dynamic/panorama/static");
      private final class_1011 image;

      public Static(File file, class_1011 image) {
         super(file, image);
         this.image = image.method_48462(IntUnaryOperator.identity());
      }

      public static LoadedBackground create(File file) {
         try {
            FileInputStream stream = new FileInputStream(file);

            LoadedBackground.Static var3;
            try {
               class_1011 image = class_1011.method_4309(stream);

               try {
                  var3 = new LoadedBackground.Static(file, image);
               } catch (Throwable var7) {
                  if (image != null) {
                     try {
                        image.close();
                     } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                     }
                  }

                  throw var7;
               }

               if (image != null) {
                  image.close();
               }
            } catch (Throwable var8) {
               try {
                  stream.close();
               } catch (Throwable var5) {
                  var8.addSuppressed(var5);
               }

               throw var8;
            }

            stream.close();
            return var3;
         } catch (Exception var9) {
            CactusClient.getLogger().error("Can't load custom (static) background");
            return null;
         }
      }

      protected void wrappedLoadTextures(CompletableFuture<LoadedBackground> future) {
         try {
            class_1060 var10000 = CactusConstants.mc.method_1531();
            class_2960 var10001 = ID;
            class_2960 var10004 = ID;
            Objects.requireNonNull(var10004);
            var10000.method_4616(var10001, new class_1043(var10004::toString, this.image.method_48462(IntUnaryOperator.identity())));
            future.complete(this);
         } catch (Exception var3) {
            CactusClient.getLogger().error("Can't register static texture", var3);
         }

      }

      public class_2960 getTextureIdentifier() {
         return ID;
      }
   }

   public static class Rotating extends LoadedBackground {
      private final class_2960 id;
      private final byte[][] parts;

      public Rotating(File file, class_1011 icon, byte[][] parts) {
         super(file, icon);
         this.parts = parts;
         this.id = class_2960.method_60655("cactus", "dynamic/panorama/cube/" + UUID.randomUUID().toString());
      }

      public static LoadedBackground create(File file) {
         class_1011 preview = null;

         Enumeration entries;
         try {
            ZipFile zip = new ZipFile(file);

            LoadedBackground.Rotating var19;
            try {
               entries = zip.entries();
               Map<String, ZipEntry> unsortedEntries = new HashMap();
               entries.asIterator().forEachRemaining((z) -> {
                  unsortedEntries.put(z.getName(), z);
               });
               byte[][] images = new byte[6][];
               int i = 0;

               while(true) {
                  if (i >= 6) {
                     var19 = new LoadedBackground.Rotating(file, preview, images);
                     break;
                  }

                  ZipEntry e = (ZipEntry)unsortedEntries.get("panorama_%s.png".formatted(new Object[]{i}));
                  InputStream is = zip.getInputStream(e);
                  byte[] o = is.readAllBytes();
                  images[i] = o;
                  if (i == 0) {
                     preview = class_1011.method_4309(new ByteArrayInputStream(o));
                  }

                  ++i;
               }
            } catch (Throwable var16) {
               try {
                  zip.close();
               } catch (Throwable var15) {
                  var16.addSuppressed(var15);
               }

               throw var16;
            }

            zip.close();
            return var19;
         } catch (Exception var17) {
            CactusClient.getLogger().error("Can't load custom (cube map) background!", var17);
            entries = null;
         } finally {
            if (preview != null) {
               preview.close();
            }

         }

         return entries;
      }

      protected void wrappedLoadTextures(CompletableFuture<LoadedBackground> future) {
         for(int i = 0; i < 6; ++i) {
            try {
               class_1011 image = class_1011.method_4309(new ByteArrayInputStream(this.parts[i]));
               class_2960 id = this.id.method_48331("_" + i + ".png");
               class_1060 var10000 = CactusConstants.mc.method_1531();
               Objects.requireNonNull(id);
               var10000.method_4616(id, new class_1043(id::toString, image));
            } catch (Exception var5) {
               CactusClient.getLogger().error("Failed to assign textures to panorama", var5);
            }
         }

         future.complete(this);
      }

      public class_2960 getTextureIdentifier() {
         return this.id;
      }

      @NotNull
      public List<byte[]> getParts() {
         return Arrays.asList(this.parts);
      }
   }
}
