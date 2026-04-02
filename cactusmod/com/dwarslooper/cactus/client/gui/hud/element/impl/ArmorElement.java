package com.dwarslooper.cactus.client.gui.hud.element.impl;

import com.dwarslooper.cactus.client.gui.hud.element.DynamicHudElement;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.EnumSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ItemUtils;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_332;
import org.joml.Vector2i;

public class ArmorElement extends DynamicHudElement<ArmorElement> {
   private static final List<class_1799> PLACEHOLDERS;
   private static final List<class_1799> DEBUG;
   public Setting<ArmorElement.Direction> direction;
   public Setting<Boolean> renderSlot;

   public ArmorElement() {
      super("armor", ArmorElement.Direction.Vertical.size);
      this.direction = this.elementGroup.add((new EnumSetting("direction", ArmorElement.Direction.Vertical)).setCallback((dir) -> {
         this.resize(dir.size);
         this.correct();
      }));
      this.renderSlot = this.elementGroup.add(new BooleanSetting("slot", true));
   }

   public boolean canResize() {
      return false;
   }

   public ArmorElement duplicate() {
      return new ArmorElement();
   }

   public void renderContent(class_332 context, int x, int y, int width, int height, int screenWidth, int screenHeight, float delta, boolean inEditor) {
      int drawY = y + height - 19;
      int drawX = this.direction.get() == ArmorElement.Direction.Vertical ? x + 3 : x + width - 19;
      Iterator var12 = this.getArmorItems(inEditor).iterator();

      while(var12.hasNext()) {
         class_1799 stack = (class_1799)var12.next();
         context.method_51445(stack, drawX, drawY);
         if ((Boolean)this.renderSlot.get()) {
            context.method_51427(stack, drawX, drawY);
         }

         if (this.direction.get() == ArmorElement.Direction.Vertical) {
            drawY -= 18;
         } else {
            drawX -= 18;
         }
      }

   }

   private List<class_1799> getArmorItems(boolean placeholder) {
      return placeholder ? (CactusConstants.mc.method_74189() ? DEBUG : PLACEHOLDERS) : ItemUtils.getPlayerArmor();
   }

   static {
      PLACEHOLDERS = Stream.of(class_1802.field_8370, class_1802.field_8570, class_1802.field_8577, class_1802.field_8267).map(class_1792::method_7854).toList();
      DEBUG = Stream.of(class_1802.field_8879, class_1802.field_8879, class_1802.field_8879, class_1802.field_8879).map((i) -> {
         return new class_1799(i, 8);
      }).toList();
   }

   public static enum Direction {
      Vertical(new Vector2i(22, 76)),
      Horizontal(new Vector2i(76, 22));

      private final Vector2i size;

      private Direction(Vector2i size) {
         this.size = size;
      }

      // $FF: synthetic method
      private static ArmorElement.Direction[] $values() {
         return new ArmorElement.Direction[]{Vertical, Horizontal};
      }
   }
}
