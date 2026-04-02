package com.dwarslooper.cactus.client.gui.hud.element.impl;

import com.dwarslooper.cactus.client.gui.hud.element.DynamicHudElement;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.ColorSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ItemUtils;
import com.dwarslooper.cactus.client.util.game.ServerUtils;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_239;
import net.minecraft.class_2561;
import net.minecraft.class_2680;
import net.minecraft.class_2769;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_3965;
import net.minecraft.class_5250;
import org.joml.Vector2i;

public class BlockStateElement extends DynamicHudElement<BlockStateElement> {
   private final List<class_2248> INVISIBLE;
   private final Set<class_5250> properties;
   private int maxPropertyWidth;
   public Setting<Boolean> showProperties;
   public Setting<Boolean> hideInvisibleBlocks;

   public BlockStateElement() {
      super("blockState", new Vector2i(80, 24));
      this.INVISIBLE = List.of(class_2246.field_10499, class_2246.field_31037, class_2246.field_10369, class_2246.field_10124, class_2246.field_10008);
      this.properties = new HashSet();
      this.showProperties = this.elementGroup.add(new BooleanSetting("showProperties", true));
      this.hideInvisibleBlocks = this.elementGroup.add(new BooleanSetting("hideInvisible", true));
   }

   public BlockStateElement duplicate() {
      return new BlockStateElement();
   }

   public boolean canResize() {
      return false;
   }

   public void render(class_332 context, int x, int y, int screenWidth, int screenHeight, float delta, boolean inEditor) {
      this.properties.clear();
      if (!inEditor && CactusConstants.mc.field_1687 != null) {
         class_239 var9 = CactusConstants.mc.field_1765;
         if (!(var9 instanceof class_3965)) {
            return;
         }

         class_3965 bhr = (class_3965)var9;
         class_2680 state = CactusConstants.mc.field_1687.method_8320(bhr.method_17777());
         if (state.method_26215() || (Boolean)this.hideInvisibleBlocks.get() && this.INVISIBLE.contains(state.method_26204())) {
            return;
         }

         this.maxPropertyWidth = 28 + CactusConstants.mc.field_1772.method_1727(ServerUtils.getBlockNameStr(state.method_26204()));
         class_5250 text;
         if ((Boolean)this.showProperties.get()) {
            for(Iterator var10 = state.method_11656().entrySet().iterator(); var10.hasNext(); this.maxPropertyWidth = Math.max(this.maxPropertyWidth, CactusConstants.mc.field_1772.method_27525(text) + 8)) {
               Entry<class_2769<?>, Comparable<?>> entry = (Entry)var10.next();
               text = class_2561.method_43470(ItemUtils.propertyToFormattedString((class_2769)entry.getKey(), (Comparable)entry.getValue()));
               this.properties.add(text);
            }
         }
      }

      super.render(context, x, y, screenWidth, screenHeight, delta, inEditor);
   }

   public void renderContent(class_332 context, int x, int y, int width, int height, int screenWidth, int screenHeight, float delta, boolean inEditor) {
      class_2680 state = class_2246.field_10029.method_9564();
      if (!inEditor) {
         class_239 var12 = CactusConstants.mc.field_1765;
         if (var12 instanceof class_3965) {
            class_3965 bhr = (class_3965)var12;
            state = CactusConstants.mc.field_1687.method_8320(bhr.method_17777());
         }
      }

      context.method_51445(this.fromBlock(state).method_7854(), x + 4, y + 4);
      class_327 var10001 = CactusConstants.mc.field_1772;
      String var10002 = ServerUtils.getBlockNameStr(state.method_26204());
      int var10003 = x + 4 + 16 + 4;
      int var10004 = y + 4;
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      context.method_51433(var10001, var10002, var10003, var10004 + (16 - 9) / 2 + 1, ((ColorSetting.ColorValue)this.textColor.get()).color(), this.textShadows());
      int propertyY = 0;

      for(Iterator var15 = this.properties.iterator(); var15.hasNext(); ++propertyY) {
         class_5250 property = (class_5250)var15.next();
         var10001 = CactusConstants.mc.field_1772;
         var10003 = x + 4;
         var10004 = y + 4 + 20;
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         context.method_51439(var10001, property, var10003, var10004 + propertyY * (9 + 2), -1, this.textShadows());
      }

   }

   public void renderBackground(class_332 context, int x, int y, int width, int height, float delta, boolean inEditor) {
      super.renderBackground(context, x, y, width, height, delta, inEditor);
   }

   public Vector2i getEffectiveSize() {
      int var10002 = this.maxPropertyWidth;
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      return new Vector2i(var10002, 24 + (9 + 2) * this.properties.size());
   }

   private class_1792 fromBlock(class_2680 state) {
      class_2248 block = state.method_26204();
      if (block == class_2246.field_10382) {
         return class_1802.field_8705;
      } else if (block == class_2246.field_10164) {
         return class_1802.field_8187;
      } else {
         return block != class_2246.field_10379 && block != class_2246.field_10008 ? block.method_8389() : class_1802.field_8249;
      }
   }
}
