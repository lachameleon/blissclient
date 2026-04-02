package com.dwarslooper.cactus.client.render.tooltip.element;

import net.minecraft.class_10377;
import net.minecraft.class_11256;
import net.minecraft.class_1767;
import net.minecraft.class_8030;
import net.minecraft.class_9307;
import org.jetbrains.annotations.Nullable;

public record BannerFlagGuiElementRenderState(class_10377 flag, class_1767 baseColor, class_9307 bannerPatterns, int x0, int y0, int x1, int y1, @Nullable class_8030 scissorArea, @Nullable class_8030 bounds, float scale) implements class_11256 {
   public BannerFlagGuiElementRenderState(class_10377 flag, class_1767 baseColor, class_9307 bannerPatterns, int x1, int y1, int x2, int y2, @Nullable class_8030 scissorArea, float scale) {
      this(flag, baseColor, bannerPatterns, x1, y1, x2, y2, scissorArea, class_11256.method_71535(x1, y1, x2, y2, scissorArea), scale);
   }

   public BannerFlagGuiElementRenderState(class_10377 flag, class_1767 baseColor, class_9307 bannerPatterns, int x0, int y0, int x1, int y1, @Nullable class_8030 scissorArea, @Nullable class_8030 bounds, float scale) {
      this.flag = flag;
      this.baseColor = baseColor;
      this.bannerPatterns = bannerPatterns;
      this.x0 = x0;
      this.y0 = y0;
      this.x1 = x1;
      this.y1 = y1;
      this.scissorArea = scissorArea;
      this.bounds = bounds;
      this.scale = scale;
   }

   public class_10377 flag() {
      return this.flag;
   }

   public class_1767 baseColor() {
      return this.baseColor;
   }

   public class_9307 bannerPatterns() {
      return this.bannerPatterns;
   }

   public int comp_4122() {
      return this.x0;
   }

   public int comp_4123() {
      return this.y0;
   }

   public int comp_4124() {
      return this.x1;
   }

   public int comp_4125() {
      return this.y1;
   }

   @Nullable
   public class_8030 comp_4128() {
      return this.scissorArea;
   }

   @Nullable
   public class_8030 comp_4274() {
      return this.bounds;
   }

   public float comp_4133() {
      return this.scale;
   }
}
