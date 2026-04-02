package com.dwarslooper.cactus.client.event.impl.render;

import com.dwarslooper.cactus.client.CactusClient;
import java.util.function.Function;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_638;

public abstract class Render3DEvent {
   private final Render3DEvent.RenderContext renderContext;

   private Render3DEvent(Render3DEvent.RenderContext renderContext) {
      this.renderContext = renderContext;
   }

   public Render3DEvent.RenderContext context() {
      return this.renderContext;
   }

   public static class RenderContext {
      private final class_4587 matrices;
      private final class_4184 camera;
      private final class_638 world;
      private final float tickDelta;

      public RenderContext(class_4587 matrices, class_4184 camera, class_638 world, float tickDelta) {
         this.matrices = matrices;
         this.camera = camera;
         this.world = world;
         this.tickDelta = tickDelta;
      }

      public class_4587 matrices() {
         return this.matrices;
      }

      public class_4184 camera() {
         return this.camera;
      }

      public class_638 world() {
         return this.world;
      }

      public float tickDelta() {
         return this.tickDelta;
      }
   }

   public static class AfterTranslucent extends Render3DEvent {
      private AfterTranslucent(Render3DEvent.RenderContext renderContext) {
         super(renderContext);
      }
   }

   public static enum Stage {
      AFTER_TRANSLUCENT(Render3DEvent.AfterTranslucent::new);

      private final Function<Render3DEvent.RenderContext, Render3DEvent> factory;

      private Stage(Function<Render3DEvent.RenderContext, Render3DEvent> factory) {
         this.factory = factory;
      }

      public Render3DEvent create(Render3DEvent.RenderContext ctx) {
         return (Render3DEvent)this.factory.apply(ctx);
      }

      public void post(Render3DEvent.RenderContext ctx) {
         CactusClient.EVENT_BUS.post(this.create(ctx));
      }

      // $FF: synthetic method
      private static Render3DEvent.Stage[] $values() {
         return new Render3DEvent.Stage[]{AFTER_TRANSLUCENT};
      }
   }
}
