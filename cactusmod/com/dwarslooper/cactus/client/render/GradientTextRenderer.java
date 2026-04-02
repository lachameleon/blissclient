package com.dwarslooper.cactus.client.render;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.StreamerMode;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.ColorUtils;
import java.awt.Color;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.class_332;

public class GradientTextRenderer {
   public static boolean dontLoopback = false;
   private static final List<Character> whitespaces = List.of(' ');

   public static int renderGradientText(class_332 context, String text, int x, int y, List<Color> colors, long deltaTime, float animationSpeed, GradientTextRenderer.Animation type, boolean skipSpaces, boolean shadow) {
      if (!text.isEmpty() && !colors.isEmpty()) {
         text = ((StreamerMode)ModuleManager.get().get(StreamerMode.class)).hideName(text);
         long speed = (long)((int)(animationSpeed * 1000.0F));
         float offset = speed == 0L ? 0.0F : type.transformOffset((float)(deltaTime % (speed * (long)colors.size())) / (float)speed);
         int currentX = x;

         for(int i = 0; i < text.length(); ++i) {
            char character = text.charAt(i);
            String charStr = String.valueOf(character);
            if (skipSpaces || !whitespaces.contains(character)) {
               float gradientPos = type.transformPosition(Math.max(1, text.length() - 1), i) + offset;
               if (animationSpeed > 0.0F) {
                  gradientPos %= 1.0F;
               }

               Color color = ColorUtils.interpolateGradient(colors, gradientPos, speed == 0L);
               dontLoopback = true;
               context.method_51433(CactusConstants.mc.field_1772, charStr, currentX, y, color.getRGB(), shadow);
               dontLoopback = false;
            }

            currentX += CactusConstants.mc.field_1772.method_1727(charStr);
         }

         return currentX;
      } else {
         return 0;
      }
   }

   public static void renderGradientText(class_332 context, String text, int x, int y, List<Color> colors, boolean skipSpaces, boolean shadow) {
      renderGradientText(context, text, x, y, colors, 0L, 0.0F, GradientTextRenderer.Animation.LINEAR_LOOPBACK_BACKWARD, skipSpaces, shadow);
   }

   public static enum Animation {
      LINEAR_LOOPBACK_FORWARD((f) -> {
         return f;
      }, (len, i) -> {
         return 1.0F - (float)i / (float)len;
      }),
      LINEAR_LOOPBACK_BACKWARD((f) -> {
         return f;
      }, (len, i) -> {
         return (float)i / (float)len;
      }),
      SINEWAVE((f) -> {
         return (float)(Math.sin((double)(f * 2.0F) * 3.141592653589793D) * 0.5D + 0.5D);
      }, (len, i) -> {
         return 1.0F - (float)i / (float)len;
      });

      private final Function<Float, Float> offsetTransformer;
      private final BiFunction<Integer, Integer, Float> positionTransformer;

      private Animation(Function<Float, Float> offsetTransformer, BiFunction<Integer, Integer, Float> positionTransformer) {
         this.offsetTransformer = offsetTransformer;
         this.positionTransformer = positionTransformer;
      }

      public float transformOffset(float val) {
         return (Float)this.offsetTransformer.apply(val);
      }

      public float transformPosition(int len, int index) {
         return (Float)this.positionTransformer.apply(len, index);
      }

      // $FF: synthetic method
      private static GradientTextRenderer.Animation[] $values() {
         return new GradientTextRenderer.Animation[]{LINEAR_LOOPBACK_FORWARD, LINEAR_LOOPBACK_BACKWARD, SINEWAVE};
      }
   }
}
