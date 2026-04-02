package com.dwarslooper.cactus.client.render.cosmetics.models;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.render.cosmetics.CosmeticRenderer;
import com.dwarslooper.cactus.client.util.client.CactusModelBakery;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.class_10055;
import net.minecraft.class_1087;
import net.minecraft.class_11659;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_591;
import net.minecraft.class_2350.class_2351;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class JsonCosmeticRenderer implements CosmeticRenderer {
   private final JsonCosmeticRenderer.CosmeticModelJson cosmeticModelJson;
   private final class_1087 model;
   private final class_2960 texture;
   private final List<JsonCosmeticRenderer.TransformConsumer> transformers;
   private final Set<JsonCosmeticRenderer> children;

   public JsonCosmeticRenderer(String json, @Nullable class_2960 texture) {
      this((JsonCosmeticRenderer.CosmeticModelJson)CactusClient.GSON.fromJson(json, JsonCosmeticRenderer.CosmeticModelJson.class), texture);
   }

   public JsonCosmeticRenderer(JsonCosmeticRenderer.CosmeticModelJson cosmeticModel, @Nullable class_2960 texture) {
      this.children = new HashSet();
      if (cosmeticModel != null && cosmeticModel.modelJson != null && cosmeticModel.mountingPoint != null) {
         this.cosmeticModelJson = cosmeticModel;
         this.texture = texture;
         this.model = CactusModelBakery.bakeJson(cosmeticModel.modelJson);
         this.transformers = new ArrayList();
         switch(cosmeticModel.mountingPoint.ordinal()) {
         case 0:
            this.transformers.add((a, b, c, d) -> {
               c.field_3398.method_22703(a);
            });
            break;
         case 1:
            this.transformers.add((a, b, c, d) -> {
               c.field_3391.method_22703(a);
            });
            break;
         case 2:
            this.transformers.add((a, b, c, d) -> {
               c.field_27433.method_22703(a);
            });
            break;
         case 3:
            this.transformers.add((a, b, c, d) -> {
               c.field_3401.method_22703(a);
            });
            break;
         case 4:
            this.transformers.add((a, b, c, d) -> {
               c.field_3397.method_22703(a);
            });
            break;
         case 5:
            this.transformers.add((a, b, c, d) -> {
               c.field_3392.method_22703(a);
            });
         }

         Vector3f scale;
         if (cosmeticModel.translation != null) {
            scale = cosmeticModel.translation;
            this.transformers.add((matrixStack, entity, ctx, tickDelta) -> {
               matrixStack.method_46416(-scale.x, -scale.y, -scale.z);
            });
         }

         if (cosmeticModel.rotation != null) {
            scale = cosmeticModel.rotation;
            this.transformers.add((matrixStack, entity, ctx, tickDelta) -> {
               matrixStack.method_22907((new Quaternionf()).rotateXYZ(scale.x, scale.y, scale.z));
            });
         }

         if (cosmeticModel.scale != null) {
            scale = cosmeticModel.scale;
            this.transformers.add((matrixStack, entity, ctx, tickDelta) -> {
               matrixStack.method_22905(scale.x, scale.y, scale.z);
            });
         }

         if (cosmeticModel.animations != null) {
            Iterator var5 = cosmeticModel.animations.iterator();

            while(var5.hasNext()) {
               JsonCosmeticRenderer.ModelAxisAnimation animation = (JsonCosmeticRenderer.ModelAxisAnimation)var5.next();
               switch(animation.axis) {
               case field_11048:
                  this.transformers.add((matrixStack, entity, ctx, tickDelta) -> {
                     matrixStack.method_22904(animation.getOffset(), 0.0D, 0.0D);
                  });
                  break;
               case field_11052:
                  this.transformers.add((matrixStack, entity, ctx, tickDelta) -> {
                     matrixStack.method_22904(0.0D, animation.getOffset(), 0.0D);
                  });
                  break;
               case field_11051:
                  this.transformers.add((matrixStack, entity, ctx, tickDelta) -> {
                     matrixStack.method_22904(0.0D, 0.0D, animation.getOffset());
                  });
               }
            }
         }

         if (cosmeticModel.children() != null) {
            cosmeticModel.children().forEach((child) -> {
               this.children.add(new JsonCosmeticRenderer(child, texture));
            });
         }

         CactusClient.getLogger().info("Finished parsing json cosmetic with {} transformers {} children", this.transformers.size(), this.children.size());
      } else {
         throw new IllegalArgumentException("Invalid cosmetic model!");
      }
   }

   public JsonCosmeticRenderer recreate() {
      return new JsonCosmeticRenderer(this.cosmeticModelJson, this.texture);
   }

   public void render(class_4587 matrices, class_11659 queue, class_10055 entity, class_591 ctx, float tickDelta, int light) {
      matrices.method_22903();
      Iterator var7 = this.transformers.iterator();

      while(var7.hasNext()) {
         JsonCosmeticRenderer.TransformConsumer transformer = (JsonCosmeticRenderer.TransformConsumer)var7.next();
         transformer.transform(matrices, entity, ctx, tickDelta);
      }

      matrices.method_22907((new Quaternionf()).rotateX(3.1415927F).rotateY(3.1415927F));
      matrices.method_22904(-0.5D, -0.5D, -0.5D);
      CactusModelBakery.renderBakedModel(this.model, matrices, queue, light);
      matrices.method_22909();
      this.children.forEach((renderer) -> {
         renderer.render(matrices, queue, entity, ctx, tickDelta, light);
      });
   }

   public static record CosmeticModelJson(String modelJson, JsonCosmeticRenderer.ModelMountingPoint mountingPoint, @Nullable Vector3f translation, @Nullable Vector3f rotation, @Nullable Vector3f scale, @Nullable List<JsonCosmeticRenderer.ModelAxisAnimation> animations, @Nullable List<JsonCosmeticRenderer.CosmeticModelJson> children) {
      public CosmeticModelJson(String modelJson, JsonCosmeticRenderer.ModelMountingPoint mountingPoint, @Nullable Vector3f translation, @Nullable Vector3f rotation, @Nullable Vector3f scale, @Nullable List<JsonCosmeticRenderer.ModelAxisAnimation> animations, @Nullable List<JsonCosmeticRenderer.CosmeticModelJson> children) {
         this.modelJson = modelJson;
         this.mountingPoint = mountingPoint;
         this.translation = translation;
         this.rotation = rotation;
         this.scale = scale;
         this.animations = animations;
         this.children = children;
      }

      public String modelJson() {
         return this.modelJson;
      }

      public JsonCosmeticRenderer.ModelMountingPoint mountingPoint() {
         return this.mountingPoint;
      }

      @Nullable
      public Vector3f translation() {
         return this.translation;
      }

      @Nullable
      public Vector3f rotation() {
         return this.rotation;
      }

      @Nullable
      public Vector3f scale() {
         return this.scale;
      }

      @Nullable
      public List<JsonCosmeticRenderer.ModelAxisAnimation> animations() {
         return this.animations;
      }

      @Nullable
      public List<JsonCosmeticRenderer.CosmeticModelJson> children() {
         return this.children;
      }
   }

   public static enum ModelMountingPoint {
      HEAD,
      BODY,
      LEFT_ARM,
      RIGHT_ARM,
      LEFT_LEG,
      RIGHT_LEG;

      // $FF: synthetic method
      private static JsonCosmeticRenderer.ModelMountingPoint[] $values() {
         return new JsonCosmeticRenderer.ModelMountingPoint[]{HEAD, BODY, LEFT_ARM, RIGHT_ARM, LEFT_LEG, RIGHT_LEG};
      }
   }

   @FunctionalInterface
   public interface TransformConsumer {
      void transform(class_4587 var1, class_10055 var2, class_591 var3, Float var4);
   }

   public static record ModelAxisAnimation(class_2351 axis, float offset, float speed, float weight) {
      public ModelAxisAnimation(class_2351 axis, float offset, float speed, float weight) {
         this.axis = axis;
         this.offset = offset;
         this.speed = speed;
         this.weight = weight;
      }

      public double getOffset() {
         return Math.sin((double)System.currentTimeMillis() / 1000.0D * (double)this.speed + (double)(this.offset * 0.017453292F)) / 100.0D * (double)this.weight;
      }

      public class_2351 axis() {
         return this.axis;
      }

      public float offset() {
         return this.offset;
      }

      public float speed() {
         return this.speed;
      }

      public float weight() {
         return this.weight;
      }
   }
}
