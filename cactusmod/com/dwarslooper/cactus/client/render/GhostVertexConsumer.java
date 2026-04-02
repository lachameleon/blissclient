package com.dwarslooper.cactus.client.render;

import net.minecraft.class_4588;

public class GhostVertexConsumer implements class_4588 {
   private final class_4588 delegate;
   private final int alpha;

   public GhostVertexConsumer(class_4588 delegate, int alpha) {
      this.delegate = delegate;
      this.alpha = alpha;
   }

   public class_4588 method_22912(float x, float y, float z) {
      return this.delegate.method_22912(x, y, z);
   }

   public class_4588 method_1336(int red, int green, int blue, int alpha) {
      return this.delegate.method_1336(red, green, blue, this.alpha);
   }

   public class_4588 method_39415(int argb) {
      int overridden = this.alpha << 24 | argb & 16777215;
      return this.delegate.method_39415(overridden);
   }

   public class_4588 method_22913(float u, float v) {
      return this.delegate.method_22913(u, v);
   }

   public class_4588 method_60796(int u, int v) {
      return this.delegate.method_60796(u, v);
   }

   public class_4588 method_22921(int u, int v) {
      return this.delegate.method_22921(u, v);
   }

   public class_4588 method_22914(float x, float y, float z) {
      return this.delegate.method_22914(x, y, z);
   }

   public class_4588 method_75298(float width) {
      return this.delegate.method_75298(width);
   }
}
