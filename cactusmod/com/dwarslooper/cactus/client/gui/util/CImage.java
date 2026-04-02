package com.dwarslooper.cactus.client.gui.util;

import com.dwarslooper.cactus.client.util.CactusConstants;
import net.minecraft.class_1043;
import net.minecraft.class_2960;

public class CImage implements AutoCloseable {
   private final class_2960 identifier;
   private final class_1043 texture;

   public CImage(class_2960 identifier, class_1043 texture) {
      this.identifier = identifier;
      this.texture = texture;
   }

   public class_2960 getIdentifier() {
      return this.identifier;
   }

   public class_1043 getTexture() {
      return this.texture;
   }

   public void close() {
      CactusConstants.mc.method_1531().method_4615(this.identifier);
      this.texture.close();
   }
}
