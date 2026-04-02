package com.dwarslooper.cactus.client.util;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.mixins.accessor.MinecraftServerAccessor;
import com.dwarslooper.cactus.client.util.generic.CallerSensitive;
import com.dwarslooper.cactus.client.util.generic.MathUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Iterator;
import java.util.function.Function;
import java.util.regex.Pattern;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.class_1799;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2588;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_5250;
import net.minecraft.class_7417;
import net.minecraft.class_32.class_5143;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;

public class Utils {
   private static final Pattern FILE_NAME_INVALID_CHARS_PATTERN = Pattern.compile("[\\s\\\\/:*?\"<>|]");
   private static final byte[] HEX_ARRAY;
   @Internal
   public static final Matrix4f lastProjMat;
   @Internal
   public static final Matrix4f lastPosMat;

   public static String serializeWorldName(String name) {
      return FILE_NAME_INVALID_CHARS_PATTERN.matcher(name).replaceAll("_");
   }

   public static String getWorldIdentification() {
      if (CactusConstants.mc.field_1687 != null) {
         class_2960 wId = CactusConstants.mc.field_1687.method_27983().method_29177();
         String var10000 = wId.method_12836();
         String dimId = "_" + var10000 + "_" + wId.method_12832();
         if (CactusConstants.mc.method_1542()) {
            class_5143 session = ((MinecraftServerAccessor)CactusConstants.mc.method_1576()).getStorageSource();
            File dir = session.method_27424(CactusConstants.mc.field_1687.method_27983()).toFile();
            if (dir.toPath().relativize(CactusConstants.mc.method_1586().method_19636().getParent()).getNameCount() != 2) {
               dir = dir.getParentFile();
            }

            var10000 = dir.getName();
            return var10000 + dimId;
         }

         if (CactusConstants.mc.method_1558() != null) {
            var10000 = CactusConstants.mc.method_1558().method_2994() ? "local" : CactusConstants.mc.method_1558().field_3761;
            return var10000 + dimId;
         }
      }

      return "fallback";
   }

   public static File getLastModifiedFile(File directory) {
      File[] files = directory.listFiles(File::isFile);
      long lastModifiedTime = Long.MIN_VALUE;
      File chosenFile = null;
      if (files != null) {
         File[] var5 = files;
         int var6 = files.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            File file = var5[var7];
            if (file.lastModified() > lastModifiedTime) {
               chosenFile = file;
               lastModifiedTime = file.lastModified();
            }
         }
      }

      return chosenFile;
   }

   public static FileTime fileCreationTime(File file) {
      try {
         BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
         return attr.creationTime();
      } catch (IOException var2) {
         CactusClient.getLogger().error("Failed to read file metadata", var2);
         return null;
      }
   }

   public static void writeFile(File file, String content) throws IOException {
      FileWriter writer = new FileWriter(file);
      writer.append(content);
      writer.close();
   }

   public static PointerBuffer createFileTypeFilter(String... types) {
      PointerBuffer filters = BufferUtils.createPointerBuffer(types.length);
      String[] var2 = types;
      int var3 = types.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String type = var2[var4];
         ByteBuffer filter = MemoryUtil.memASCII(type);
         filters.put(filter);
      }

      filters.rewind();
      return filters;
   }

   public static void unsafeDelayed(Runnable runnable, long delay) {
      (new Thread(() -> {
         try {
            Thread.sleep(delay);
            CactusConstants.mc.execute(runnable);
         } catch (InterruptedException var4) {
            throw new RuntimeException(var4);
         }
      })).start();
   }

   public static boolean containsIgnoreCase(String s, String term) {
      return s.toLowerCase().contains(term.toLowerCase());
   }

   public static boolean matchesSearch(String s, @Nullable String term) {
      return term == null || containsIgnoreCase(trimSpaces(s), trimSpaces(term));
   }

   public static String trimSpaces(String s) {
      return s.replaceAll("\\s+", "");
   }

   public static boolean isValidURL(String url) {
      try {
         (new URI(url)).toURL();
         return true;
      } catch (URISyntaxException | MalformedURLException var2) {
         return false;
      }
   }

   public static double getMouseX() {
      return CactusConstants.mc.field_1755 == null ? -1.0D : CactusConstants.mc.field_1729.method_1603() * (double)CactusConstants.mc.method_22683().method_4486() / (double)CactusConstants.mc.method_22683().method_4480();
   }

   public static double getMouseY() {
      return CactusConstants.mc.field_1755 == null ? -1.0D : CactusConstants.mc.field_1729.method_1604() * (double)CactusConstants.mc.method_22683().method_4502() / (double)CactusConstants.mc.method_22683().method_4507();
   }

   public static <T> T orElse(@Nullable T value, @Nullable T other) {
      return value != null ? value : other;
   }

   public static <T, E> E orElse(@Nullable T instance, Function<T, E> mapper, @Nullable E other) {
      return instance != null ? mapper.apply(instance) : other;
   }

   public static boolean translatableTextsMatch(class_2561 text, class_2561 other) {
      boolean var10000;
      if (text instanceof class_5250) {
         class_5250 mt1 = (class_5250)text;
         if (other instanceof class_5250) {
            class_5250 mt2 = (class_5250)other;
            class_7417 var6 = mt1.method_10851();
            if (var6 instanceof class_2588) {
               class_2588 ttc1 = (class_2588)var6;
               var6 = mt2.method_10851();
               if (var6 instanceof class_2588) {
                  class_2588 ttc2 = (class_2588)var6;
                  if (ttc1.method_11022().equals(ttc2.method_11022())) {
                     var10000 = true;
                     return var10000;
                  }
               }
            }
         }
      }

      var10000 = false;
      return var10000;
   }

   public static String bytesToHex(byte[] bytes) {
      byte[] hexChars = new byte[bytes.length * 2];

      for(int j = 0; j < bytes.length; ++j) {
         int v = bytes[j] & 255;
         hexChars[j * 2] = HEX_ARRAY[v >>> 4];
         hexChars[j * 2 + 1] = HEX_ARRAY[v & 15];
      }

      return new String(hexChars, StandardCharsets.UTF_8);
   }

   public static String computeSecureEncoded(int shaSize, byte[]... data) {
      try {
         MessageDigest md = MessageDigest.getInstance("SHA-" + shaSize);
         md.reset();
         byte[][] var3 = data;
         int var4 = data.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            byte[] datum = var3[var5];
            md.update(datum);
         }

         return new String(Base64.getEncoder().encode(md.digest()));
      } catch (NoSuchAlgorithmException var7) {
         throw new IllegalStateException();
      }
   }

   public static boolean hasInitialized() {
      return CactusConstants.mc != null && CactusConstants.mc.method_53466();
   }

   public static boolean isInWorld() {
      return hasInitialized() && CactusConstants.mc.field_1724 != null && CactusConstants.mc.field_1687 != null;
   }

   public static boolean isNetworkingAvailable() {
      return isInWorld() && CactusConstants.mc.method_1562() != null;
   }

   public static Utils.ScreenPositionResult worldToScreen(class_243 vector) {
      class_243 camPos = vector.method_1020(CactusConstants.mc.field_1773.method_19418().method_71156());
      int screenWidth = CactusConstants.mc.method_22683().method_4486();
      int screenHeight = CactusConstants.mc.method_22683().method_4502();
      Vector4f screenPos = new Vector4f((float)camPos.field_1352, (float)camPos.field_1351, (float)camPos.field_1350, 1.0F);
      screenPos.mul(lastPosMat).mul(lastProjMat);
      double w = 1.0D / (double)screenPos.w() * 0.5D;
      Vector3d position = new Vector3d(((double)screenPos.x() * w + 0.5D) * (double)screenWidth, (1.0D - ((double)screenPos.y() * w + 0.5D)) * (double)screenHeight, (double)screenPos.z() * w + 0.5D);
      boolean behind = screenPos.w() <= 0.0F;
      if (behind) {
         position = new Vector3d((double)screenWidth - position.x, (double)screenHeight - position.y, (double)screenPos.z());
      }

      return new Utils.ScreenPositionResult(position, behind, MathUtils.inBounds(0.0D, (double)screenWidth, position.x()) && MathUtils.inBounds(0.0D, (double)screenHeight, position.y()));
   }

   public static URI asURI(String uri) {
      try {
         return new URI(uri);
      } catch (URISyntaxException var2) {
         return null;
      }
   }

   @CallerSensitive
   public static Class<?> getDirectCallerClass() {
      try {
         return Class.forName(Thread.currentThread().getStackTrace()[3].getClassName());
      } catch (IndexOutOfBoundsException | ClassNotFoundException var1) {
         return null;
      }
   }

   public static int getItemBarColor(class_1799 stack) {
      int i = stack.method_7936();
      float f = Math.max(0.0F, ((float)i - (float)stack.method_7919()) / (float)i);
      return class_3532.method_15369(f / 3.0F, 1.0F, 1.0F);
   }

   public static boolean isModInstalled(String modId) {
      Iterator var1 = FabricLoader.getInstance().getAllMods().iterator();

      ModContainer modContainer;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         modContainer = (ModContainer)var1.next();
      } while(!modContainer.getMetadata().getId().equals(modId));

      return true;
   }

   public static void ensureDirectory(File file) {
      if (!file.isDirectory()) {
         throw new IllegalStateException("Not a directory: " + file.getPath());
      }
   }

   public static void ensureFile(File file) {
      if (!file.isFile()) {
         throw new IllegalStateException("Not a file: " + file.getPath());
      }
   }

   public static String clearFormattingChars(String input) {
      return input.replaceAll("§.", "");
   }

   public static String replaceTypeableFormattingChars(String input) {
      return input.replaceAll("&([a-f0-9])", "§$1");
   }

   public static Vector3d toJomlVector(class_243 vec3d) {
      return new Vector3d(vec3d.method_10216(), vec3d.method_10214(), vec3d.method_10215());
   }

   public static class_243 toMcVector(Vector3d vec3d) {
      return new class_243(vec3d.x(), vec3d.y(), vec3d.z());
   }

   public static Utils.TimeUnits getTimeUnits(long ticks) {
      long totalSeconds = ticks / 20L;
      long seconds = totalSeconds % 60L;
      long minutes = totalSeconds / 60L % 60L;
      long hours = totalSeconds / 3600L % 24L;
      long days = totalSeconds / 86400L;
      long weeks = days / 7L;
      days %= 7L;
      return new Utils.TimeUnits(weeks, days, hours, minutes, seconds);
   }

   static {
      HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
      lastProjMat = new Matrix4f();
      lastPosMat = new Matrix4f();
   }

   public static record ScreenPositionResult(Vector3d pos, boolean behind, boolean visible) {
      public ScreenPositionResult(Vector3d pos, boolean behind, boolean visible) {
         this.pos = pos;
         this.behind = behind;
         this.visible = visible;
      }

      public Vector3d pos() {
         return this.pos;
      }

      public boolean behind() {
         return this.behind;
      }

      public boolean visible() {
         return this.visible;
      }
   }

   public static record TimeUnits(long weeks, long days, long hours, long minutes, long seconds) {
      public TimeUnits(long weeks, long days, long hours, long minutes, long seconds) {
         this.weeks = weeks;
         this.days = days;
         this.hours = hours;
         this.minutes = minutes;
         this.seconds = seconds;
      }

      public long[] toArray() {
         return new long[]{this.weeks, this.days, this.hours, this.minutes, this.seconds};
      }

      public long weeks() {
         return this.weeks;
      }

      public long days() {
         return this.days;
      }

      public long hours() {
         return this.hours;
      }

      public long minutes() {
         return this.minutes;
      }

      public long seconds() {
         return this.seconds;
      }
   }
}
