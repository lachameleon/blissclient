package com.dwarslooper.cactus.client.systems;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.io.File;
import net.minecraft.class_2487;
import net.minecraft.class_2507;

public enum CactusSaves {
   ARMOR_STAND("armorstand"),
   COMMAND_CHAIN("cmdchain");

   public final String dir;

   private CactusSaves(String dirName) {
      this.dir = dirName;
   }

   public void save(String fileName, class_2487 compound) {
      try {
         class_2507.method_10630(compound, this.file(fileName).toPath());
      } catch (Exception var4) {
         CactusClient.getLogger().error("Failed to save NBT", var4);
      }

   }

   public class_2487 load(String fileName) {
      try {
         return class_2507.method_10633(this.file(fileName).toPath());
      } catch (Exception var3) {
         CactusClient.getLogger().error("Failed to load NBT", var3);
         return null;
      }
   }

   public class_2487 load(File file) {
      try {
         return class_2507.method_10633(file.toPath());
      } catch (Exception var3) {
         CactusClient.getLogger().error("Failed to load NBT", var3);
         return null;
      }
   }

   public File dir() {
      File file = new File(CactusConstants.DIRECTORY, "save/%s".formatted(new Object[]{this.dir}));
      file.mkdirs();
      return file;
   }

   public File file(String name) {
      return new File(this.dir(), "%s.nbt".formatted(new Object[]{name}));
   }

   // $FF: synthetic method
   private static CactusSaves[] $values() {
      return new CactusSaves[]{ARMOR_STAND, COMMAND_CHAIN};
   }
}
