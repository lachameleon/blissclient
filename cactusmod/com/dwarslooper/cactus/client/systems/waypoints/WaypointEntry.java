package com.dwarslooper.cactus.client.systems.waypoints;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.Waypoints;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import com.dwarslooper.cactus.client.util.game.PositionUtils;
import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import java.awt.Color;
import java.util.Objects;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_5250;
import net.minecraft.class_822;
import net.minecraft.class_9848;
import net.minecraft.class_2568.class_10613;
import org.joml.Vector3d;

public class WaypointEntry {
   private String name;
   private String dimension;
   private class_2338 position;
   private int color;
   private boolean visible;
   private boolean isDeath;

   public static WaypointEntry createDefault(String name, class_2338 position) {
      String world = "cactus:unknown";
      if (CactusConstants.mc.field_1687 != null) {
         world = CactusConstants.mc.field_1687.method_27983().method_29177().toString();
      }

      return new WaypointEntry(name, position, world, Color.WHITE.getRGB(), true, false);
   }

   public WaypointEntry(String name, class_2338 position, String world, int color, boolean visible, boolean isDeath) {
      this.name = name;
      this.position = position;
      this.dimension = world;
      this.color = color;
      this.visible = visible;
      this.isDeath = isDeath;
   }

   public class_5250 createFormattedName() {
      return class_2561.method_43470(this.getName()).method_27694((style) -> {
         class_2583 var10000 = style.method_36139(this.getColor()).method_36140(!this.isVisible());
         String var10003 = this.isDeath() ? "§c☠ " : "";
         return var10000.method_10949(new class_10613(class_2561.method_43470(var10003 + "§a" + PositionUtils.toString(this.getPosition()) + "\n§9" + this.getDimension())));
      });
   }

   public void render(class_4587 matrices, float tickDelta) {
      Waypoints module = (Waypoints)ModuleManager.get().get(Waypoints.class);
      renderBeacon(this, tickDelta, matrices);
   }

   public void render(class_332 context) {
      String name = this.getName();
      if (name != null) {
         Waypoints module = (Waypoints)ModuleManager.get().get(Waypoints.class);
         Waypoints.DistanceVisibility distanceVisibility = (Waypoints.DistanceVisibility)module.distanceVisibility.get();
         Utils.ScreenPositionResult result = Utils.worldToScreen(this.getPosition().method_46558());
         class_2561 text = class_2561.method_43470(name).method_54663(this.getColor());
         double distance = this.getDistanceToCamera();
         String distanceText = (int)distance + "m";
         if (!result.behind()) {
            Vector3d v2i = result.pos();
            int centerOffset = (int)((double)((float)context.method_51421() / 2.0F) - v2i.x() + ((double)((float)context.method_51443() / 2.0F) - v2i.y()));
            int distanceWidth = CactusConstants.mc.field_1772.method_1727(distanceText);
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            int height = 9;
            context.method_51448().pushMatrix();
            float alpha = class_3532.method_15363(((float)distance - 0.5F) / 4.0F, 0.0F, 1.0F);
            int color = class_9848.method_61318(alpha, 1.0F, 1.0F, 1.0F);
            int nameWidth = CactusConstants.mc.field_1772.method_27525(text);
            RenderUtils.drawText(context, (class_2561)text, (float)v2i.x() - (float)nameWidth / 2.0F, (float)v2i.y(), color);
            if (distanceVisibility.shouldRender(centerOffset)) {
               context.method_51448().scale(0.5F, 0.5F);
               RenderUtils.drawText(context, distanceText, (float)v2i.x() / 0.5F - (float)distanceWidth / 2.0F, (float)v2i.y() / 0.5F - (float)height, color);
            }

            context.method_51448().popMatrix();
         }

      }
   }

   public static void renderBeacon(WaypointEntry waypoint, float tickDelta, class_4587 matrixStack) {
      if (CactusConstants.mc.field_1687 != null && CactusConstants.mc.field_1724 != null) {
         class_243 playerVec = CactusConstants.mc.field_1724.method_73189();
         class_243 waypointVec = new class_243((double)waypoint.getPosition().method_10263(), playerVec.field_1351, (double)waypoint.getPosition().method_10260());
         class_4184 camera = CactusConstants.mc.field_1773.method_19418();
         if (camera != null) {
            double viewX = camera.method_71156().method_10216();
            double viewZ = camera.method_71156().method_10215();
            double x = waypointVec.field_1352 - viewX;
            double z = waypointVec.field_1350 - viewZ;
            double y = -100.0D;
            long time = CactusConstants.mc.field_1687.method_75260();
            matrixStack.method_22903();
            matrixStack.method_22904(x, -100.0D, z);
            float scale = 1.0F;
            float rotation = 1.0F;
            class_822.method_3545(matrixStack, CactusConstants.mc.field_1773.method_72910(), class_822.field_4338, 1.0F, rotation, 0, 355, waypoint.getColor(), 0.2F * scale, 0.25F * scale);
            matrixStack.method_22909();
         }
      }
   }

   public double getDistanceToCamera() {
      class_243 camPos = CactusConstants.mc.field_1773.method_19418().method_71156();
      return Math.sqrt(this.getPosition().method_10268(camPos.field_1352, camPos.field_1351, camPos.field_1350));
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public class_2338 getPosition() {
      return this.position;
   }

   public void setPosition(class_2338 position) {
      this.position = position;
   }

   public int getColor() {
      return this.color;
   }

   public void setColor(int color) {
      this.color = color;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   public boolean isDeath() {
      return this.isDeath;
   }

   public String getDimension() {
      return this.dimension;
   }

   public void setDimension(String dimension) {
      this.dimension = dimension;
   }

   public void setDeath(boolean death) {
      this.isDeath = death;
   }
}
