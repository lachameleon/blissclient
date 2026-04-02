package com.dwarslooper.cactus.client.util.client;

import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_1011;
import net.minecraft.class_1047;
import net.minecraft.class_1058;
import net.minecraft.class_1059;
import net.minecraft.class_10799;
import net.minecraft.class_12246;
import net.minecraft.class_12247;
import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_4730;
import net.minecraft.class_7764;
import net.minecraft.class_7771;
import net.minecraft.class_7766.class_7767;

public class CactusModelLayerRegistry {
   public static final class_1921 LAYER;
   private static final Map<class_2960, class_1921> layers;
   private static final Map<class_2960, class_1058> sprites;

   public static void registerSprite(class_2960 texId, class_1011 content) {
      class_1059 tex = new class_1059(texId);
      class_7764 cont = new class_7764(texId, new class_7771(content.method_4307(), content.method_4323()), content);
      class_1058 sprite = new CactusModelLayerRegistry.SimpleSprite(texId, cont, cont.method_45807(), cont.method_45815(), 0, 0, 0);
      tex.method_45848(new class_7767(content.method_4307(), content.method_4323(), 0, sprite, Map.of(texId, sprite, class_1047.method_4539(), sprite), CompletableFuture.completedFuture((Object)null)));
      CactusConstants.mc.method_1531().method_4616(texId, tex);
      layers.put(texId, class_12249.method_75980(texId, true));
      sprites.put(texId, sprite);
   }

   public static class_1921 getLayer(class_2960 id) {
      return (class_1921)layers.getOrDefault(id, class_12249.method_75977());
   }

   public static class_1058 getSprite(class_4730 id) {
      class_1058 direct = (class_1058)sprites.get(id.method_24147());
      if (direct != null) {
         return direct;
      } else {
         class_4730 mapped = id;
         class_2960 atlasId = id.method_24144();
         if (class_2960.method_60656("block_or_item").equals(atlasId)) {
            class_2960 texId = id.method_24147();
            String path = texId.method_12832();
            boolean isItemTexture = path.startsWith("item/") || path.startsWith("items/") || path.startsWith("textures/item/") || path.startsWith("textures/items/");
            mapped = new class_4730(isItemTexture ? class_1059.field_64467 : class_1059.field_5275, texId);
         }

         try {
            return CactusConstants.mc.method_72703().method_73030(mapped);
         } catch (IllegalArgumentException var7) {
            return CactusConstants.mc.method_72703().method_73030(new class_4730(class_1059.field_5275, class_1047.method_4539()));
         }
      }
   }

   public static void drop(class_2960 id) {
      CactusConstants.mc.method_1531().method_4615(id);
      layers.remove(id);
      sprites.remove(id);
   }

   static {
      LAYER = class_1921.method_75940("cactus_cosmetic_translucent", class_12247.method_75927(class_10799.field_56906).method_76560("Sampler0", class_1059.field_5275, class_12249.field_64462).method_75928().method_75935().method_75931(class_12246.field_63983).method_75938());
      layers = new HashMap();
      sprites = new HashMap();
   }

   private static final class SimpleSprite extends class_1058 {
      private SimpleSprite(class_2960 atlasId, class_7764 contents, int atlasWidth, int atlasHeight, int x, int y, int padding) {
         super(atlasId, contents, atlasWidth, atlasHeight, x, y, padding);
      }
   }
}
