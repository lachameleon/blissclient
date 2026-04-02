package com.dwarslooper.cactus.client.gui.hud.element.impl;

import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.ClientTickEvent;
import com.dwarslooper.cactus.client.gui.hud.element.DynamicHudElement;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.ColorSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.IntegerSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.StringSetting;
import com.dwarslooper.cactus.client.systems.params.PlaceholderHandler;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.ColorUtils;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import org.joml.Vector2i;

public class GraphElement extends DynamicHudElement<GraphElement> {
   public Setting<String> title;
   public Setting<String> sourcePlaceholder;
   public Setting<Integer> refreshRate;
   public Setting<Integer> segmentSize;
   public Setting<ColorSetting.ColorValue> minColor;
   public Setting<ColorSetting.ColorValue> maxColor;
   public Setting<Boolean> fixedMin;
   public Setting<Integer> minValue;
   public Setting<Boolean> fixedMax;
   public Setting<Integer> maxValue;
   private final Queue<Float> values;
   private int tickCounter;
   private float min;
   private float max;

   public GraphElement() {
      super("graph", new Vector2i(60, 40), new Vector2i(20, 10));
      this.title = this.elementGroup.add(new StringSetting("title", "New Graph"));
      this.sourcePlaceholder = this.elementGroup.add(new StringSetting("source", ""));
      this.refreshRate = this.elementGroup.add((new IntegerSetting("refreshRate", 10)).min(1).max(80));
      this.segmentSize = this.elementGroup.add((new IntegerSetting("segmentSize", 1)).min(1).max(4));
      this.minColor = this.elementGroup.add(new ColorSetting("minColor", ColorSetting.ColorValue.of(new Color(-16711936), false), true));
      this.maxColor = this.elementGroup.add(new ColorSetting("maxColor", ColorSetting.ColorValue.of(new Color(-16711936), false), true));
      this.fixedMin = this.elementGroup.add(new BooleanSetting("fixedMin", false));
      this.minValue = this.elementGroup.add((new IntegerSetting("minValue", 60)).min(Integer.MIN_VALUE).max(Integer.MAX_VALUE)).visibleIf(() -> {
         return (Boolean)this.fixedMin.get();
      });
      this.fixedMax = this.elementGroup.add(new BooleanSetting("fixedMax", false));
      this.maxValue = this.elementGroup.add((new IntegerSetting("maxValue", 200)).min(Integer.MIN_VALUE).max(Integer.MAX_VALUE)).visibleIf(() -> {
         return (Boolean)this.fixedMax.get();
      });
      this.values = new ArrayDeque();
      this.tickCounter = 0;
   }

   public GraphElement duplicate() {
      return new GraphElement();
   }

   public void renderContent(class_332 context, int x, int y, int width, int height, int screenWidth, int screenHeight, float delta, boolean inEditor) {
      float range = this.max - this.min;
      boolean hasTitle = !((String)this.title.get()).isEmpty();
      byte var10000;
      if (hasTitle) {
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         var10000 = 9;
      } else {
         var10000 = 0;
      }

      int titleOffset = var10000;
      if (hasTitle) {
         context.method_25303(CactusConstants.mc.field_1772, PlaceholderHandler.get().replacePlaceholders((String)this.title.get()), x, y, -1);
      }

      int segmentX = x;
      int lastSegmentY = -1;
      int lastSegmentColor = -1;

      int segmentEnd;
      for(Iterator var16 = this.values.iterator(); var16.hasNext(); segmentX = segmentEnd) {
         Float value = (Float)var16.next();
         float normalized;
         if (range == 0.0F) {
            normalized = 0.5F;
         } else {
            normalized = class_3532.method_15363((value - this.min) / range, 0.0F, 1.0F);
         }

         int color = this.getColor(normalized) | -16777216;
         int drawY = (int)Math.floor((double)((float)(y + height) - normalized * (float)(height - titleOffset)));
         boolean bridgeLast = lastSegmentY != -1 && Math.abs(drawY - lastSegmentY) >= 1;
         boolean down = lastSegmentY < drawY;
         int segmentDrawX = segmentX;
         segmentEnd = segmentX + (Integer)this.segmentSize.get();
         if (bridgeLast) {
            int y1 = Math.min(lastSegmentY, drawY);
            int y2 = Math.max(lastSegmentY, drawY);
            if (down) {
               context.method_25296(segmentX, y1, segmentX + 1, y2, lastSegmentColor, color);
            } else {
               context.method_25296(segmentX, y1, segmentX + 1, y2, color, lastSegmentColor);
            }

            segmentDrawX = segmentX + 1;
         }

         if (segmentEnd > segmentDrawX) {
            context.method_25294(segmentDrawX, drawY - 1, segmentEnd, drawY, color);
         }

         lastSegmentY = drawY;
         lastSegmentColor = color;
      }

   }

   private void pushRefresh() {
      String placeholder = PlaceholderHandler.get().getPlaceholder((String)this.sourcePlaceholder.get());
      if (placeholder != null) {
         try {
            float value = Float.parseFloat(placeholder);
            this.values.offer(value);
         } catch (NumberFormatException var4) {
         }
      }

      while(this.values.size() * (Integer)this.segmentSize.get() > this.getSize().x()) {
         this.values.remove();
      }

      if (!(Boolean)this.fixedMin.get() || !(Boolean)this.fixedMax.get()) {
         this.min = Float.MAX_VALUE;
         this.max = -3.4028235E38F;
         Iterator var5 = this.values.iterator();

         while(var5.hasNext()) {
            Float value = (Float)var5.next();
            if (!(Boolean)this.fixedMin.get() && value < this.min) {
               this.min = value;
            }

            if (!(Boolean)this.fixedMax.get() && value > this.max) {
               this.max = value;
            }
         }
      }

      if ((Boolean)this.fixedMin.get()) {
         this.min = (float)(Integer)this.minValue.get();
      }

      if ((Boolean)this.fixedMax.get()) {
         this.max = (float)(Integer)this.maxValue.get();
      }

   }

   private int getColor(float phase) {
      int minColorValue = ((ColorSetting.ColorValue)this.minColor.get()).color();
      int maxColorValue = ((ColorSetting.ColorValue)this.maxColor.get()).color();
      return minColorValue == maxColorValue ? minColorValue : ColorUtils.interpolateColor(new Color(minColorValue), new Color(maxColorValue), phase).getRGB();
   }

   @EventHandler
   public void onTick(ClientTickEvent event) {
      int rr = (Integer)this.refreshRate.get();
      ++this.tickCounter;
      if (this.tickCounter >= rr) {
         this.pushRefresh();
         this.tickCounter = 0;
      }

   }
}
