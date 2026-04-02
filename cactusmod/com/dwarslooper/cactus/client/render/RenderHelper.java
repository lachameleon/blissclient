package com.dwarslooper.cactus.client.render;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.waypoints.WaypointManager;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.ArrayList;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import net.minecraft.class_4597.class_4598;

public class RenderHelper {
   public static ArrayList<RenderableObject> hitBoxes = new ArrayList();
   public static final String[] LOADING = new String[]{"▂ ▃ ▅ ▆ ▇ ▌ ▇ ▆ ▅ ▃ ▂", "▁ ▂ ▃ ▅ ▆ ▇ ▌ ▇ ▆ ▅ ▃", "▁ ▁ ▂ ▃ ▅ ▆ ▇ ▌ ▇ ▆ ▅", "▁ ▁ ▁ ▂ ▃ ▅ ▆ ▇ ▌ ▇ ▆", "▁ ▁ ▁ ▁ ▂ ▃ ▅ ▆ ▇ ▌ ▇", "▁ ▁ ▁ ▁ ▁ ▂ ▃ ▅ ▆ ▇ ▌", "▁ ▁ ▁ ▁ ▂ ▃ ▅ ▆ ▇ ▌ ▇", "▁ ▁ ▁ ▂ ▃ ▅ ▆ ▇ ▌ ▇ ▆", "▁ ▁ ▂ ▃ ▅ ▆ ▇ ▌ ▇ ▆ ▅", "▁ ▂ ▃ ▅ ▆ ▇ ▌ ▇ ▆ ▅ ▃", "▂ ▃ ▅ ▆ ▇ ▌ ▇ ▆ ▅ ▃ ▂", "▃ ▅ ▆ ▇ ▌ ▇ ▆ ▅ ▃ ▂ ▁", "▅ ▆ ▇ ▌ ▇ ▆ ▅ ▃ ▂ ▁ ▁", "▆ ▇ ▌ ▇ ▆ ▅ ▃ ▂ ▁ ▁ ▁", "▇ ▌ ▇ ▆ ▅ ▃ ▂ ▁ ▁ ▁ ▁", "▌ ▇ ▆ ▅ ▃ ▂ ▁ ▁ ▁ ▁ ▁", "▇ ▌ ▇ ▆ ▅ ▃ ▂ ▁ ▁ ▁ ▁", "▆ ▇ ▌ ▇ ▆ ▅ ▃ ▂ ▁ ▁ ▁", "▅ ▆ ▇ ▌ ▇ ▆ ▅ ▃ ▂ ▁ ▁", "▃ ▅ ▆ ▇ ▌ ▇ ▆ ▅ ▃ ▂ ▁"};

   public static void draw(class_4598 immediate, class_4587 matrices, float tickDelta) {
      if (CactusConstants.mc.field_1687 != null) {
         ModuleManager.get().getModules().values().stream().filter((m) -> {
            return m instanceof WorldRendering && m.active();
         }).forEach((m) -> {
            ((WorldRendering)m).onRender(immediate, matrices, tickDelta);
         });
         class_4587 waypointMatrices = new class_4587();
         WaypointManager.get().getInstance().getAllAvailable().forEach((w) -> {
            w.render(waypointMatrices, tickDelta);
         });
         long currentTime = System.currentTimeMillis();
         hitBoxes.forEach((obj) -> {
            if (obj.timed() >= currentTime || obj.timed() == 0L) {
               obj.draw(immediate, matrices, tickDelta);
            }

         });
         hitBoxes.removeIf((obj) -> {
            return obj.getRenderMode() == RenderableObject.RenderMode.Instant && obj.timed() <= currentTime;
         });
      }
   }

   public static String getLoading() {
      return LOADING[(int)(System.currentTimeMillis() / 50L % (long)LOADING.length)];
   }

   public static void drawBorder(class_332 context, int x, int y, int width, int height, int color) {
      context.method_25294(x, y, x + width, y + 1, color);
      context.method_25294(x, y + height - 1, x + width, y + height, color);
      context.method_25294(x, y + 1, x + 1, y + height - 1, color);
      context.method_25294(x + width - 1, y + 1, x + width, y + height - 1, color);
   }
}
