package com.dwarslooper.cactus.client.util.client;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_10419;
import net.minecraft.class_1047;
import net.minecraft.class_1058;
import net.minecraft.class_1059;
import net.minecraft.class_10813;
import net.minecraft.class_10817;
import net.minecraft.class_10819;
import net.minecraft.class_10820;
import net.minecraft.class_1086;
import net.minecraft.class_1087;
import net.minecraft.class_10889;
import net.minecraft.class_10893;
import net.minecraft.class_1100;
import net.minecraft.class_11659;
import net.minecraft.class_1921;
import net.minecraft.class_2350;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3665;
import net.minecraft.class_4587;
import net.minecraft.class_4608;
import net.minecraft.class_4730;
import net.minecraft.class_5819;
import net.minecraft.class_777;
import net.minecraft.class_7775;
import net.minecraft.class_793;
import net.minecraft.class_9826;
import net.minecraft.class_10419.class_10423;
import net.minecraft.class_7775.class_10897;
import net.minecraft.class_7775.class_12356;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CactusModelBakery {
   private static final class_5819 random = class_5819.method_43053();

   public static class_1087 bakeJson(String json) {
      return bakeJson((Reader)(new StringReader(json)));
   }

   public static class_1087 bakeJson(Reader reader) {
      return bake(class_793.method_3437(reader));
   }

   public static class_1087 bake(class_1100 unbakedModel) {
      return bake(unbakedModel, class_1086.field_63619);
   }

   public static class_1087 bake(class_1100 unbakedModel, class_3665 settings) {
      class_10813 simpleModel = () -> {
         return "cactus-model";
      };
      class_10419 textures = (new class_10423()).method_65554(unbakedModel.comp_3743()).method_65551(simpleModel);
      final class_2960[] ids = new class_2960[]{null};
      class_10817 geo = ((class_10820)Objects.requireNonNull(unbakedModel.comp_3739())).bake(textures, new CactusModelBakery.BakerImpl(new class_9826() {
         public class_1058 method_65739(class_4730 id, class_10813 model) {
            ids[0] = id.method_24147();
            return CactusModelLayerRegistry.getSprite(id);
         }

         public class_1058 method_65740(String name, class_10813 model) {
            ids[0] = class_1047.method_4539();
            return class_310.method_1551().method_72703().method_73030(new class_4730(class_1059.field_5275, class_1047.method_4539()));
         }
      }), settings, simpleModel);
      return new CactusModelBakery.CactusCustomBlockModel(ids[0], geo);
   }

   public static void renderBakedModel(class_1087 model, class_4587 matrices, class_11659 queue, int light) {
      class_1921 layer = CactusModelLayerRegistry.LAYER;
      if (model instanceof CactusModelBakery.CactusCustomBlockModel) {
         CactusModelBakery.CactusCustomBlockModel m = (CactusModelBakery.CactusCustomBlockModel)model;
         if (m.texture.method_12836().equals("cactus")) {
            layer = CactusModelLayerRegistry.getLayer(m.texture);
         }
      }

      matrices.method_22903();
      queue.method_73484(matrices, layer, model, 0.0F, 0.0F, 0.0F, light, class_4608.field_21444, 0);
      matrices.method_22909();
   }

   public static record BakerImpl(class_9826 spriteGetter) implements class_7775 {
      private static final class_12356 DUMMY_INTERNER = (vec) -> {
         return vec;
      };
      private static final class_10889 DUMMY_BLOCK_PART = new class_10889() {
         @NotNull
         public List<class_777> method_68509(@Nullable class_2350 side) {
            return Collections.emptyList();
         }

         public boolean comp_3751() {
            return false;
         }

         @NotNull
         public class_1058 comp_3752() {
            return class_310.method_1551().method_72703().method_73030(new class_4730(class_1059.field_5275, class_1047.method_4539()));
         }
      };

      public BakerImpl(class_9826 spriteGetter) {
         this.spriteGetter = spriteGetter;
      }

      @NotNull
      public class_9826 method_65732() {
         return this.spriteGetter;
      }

      @NotNull
      public class_12356 method_76674() {
         return DUMMY_INTERNER;
      }

      @NotNull
      public class_10889 method_76673() {
         return DUMMY_BLOCK_PART;
      }

      @NotNull
      public class_10819 method_45872(@NotNull class_2960 id) {
         throw new UnsupportedOperationException();
      }

      @NotNull
      public <T> T method_68549(@NotNull class_10897<T> key) {
         throw new UnsupportedOperationException();
      }

      public class_9826 spriteGetter() {
         return this.spriteGetter;
      }
   }

   public static class CactusCustomBlockModel extends class_10893 {
      private final class_2960 texture;

      public CactusCustomBlockModel(class_2960 texture, class_10817 geo) {
         super(new class_10889() {
            // $FF: synthetic field
            final class_10817 val$geo;

            {
               this.val$geo = var1;
            }

            public List<class_777> method_68509(@Nullable class_2350 side) {
               return this.val$geo.method_68049(side);
            }

            public boolean comp_3751() {
               return true;
            }

            public class_1058 comp_3752() {
               return class_310.method_1551().method_72703().method_73030(new class_4730(class_1059.field_5275, class_1047.method_4539()));
            }
         });
         this.texture = texture;
      }

      public class_2960 getTexture() {
         return this.texture;
      }
   }
}
