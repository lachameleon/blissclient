package com.dwarslooper.cactus.client.feature.modules.render;

import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.ClientTickEvent;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.IntegerSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.stream.Stream;
import net.minecraft.class_1304;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_9276;
import net.minecraft.class_9288;
import net.minecraft.class_9334;

public class ItemCount extends Module {
   private class_1799 lastStack;
   private int lastScanResult;
   public Setting<Boolean> scanContainers;
   public Setting<Integer> nestedScanMaxDepth;
   public Setting<Boolean> strictCompare;
   public Setting<Boolean> onlySelectedSlot;
   public Setting<Boolean> customFontSize;
   public Setting<Integer> fontSize;
   public Setting<Boolean> numberAbbreviation;
   public Setting<Integer> offsetX;
   public Setting<Integer> offsetY;

   public ItemCount() {
      super("itemCount");
      this.scanContainers = this.mainGroup.add(new BooleanSetting("scanContainers", true));
      this.nestedScanMaxDepth = this.mainGroup.add((new IntegerSetting("maxScanDepth", 4, IntegerSetting.EditorStyle.Input)).min(1).visibleIf(() -> {
         return (Boolean)this.scanContainers.get();
      }));
      this.strictCompare = this.mainGroup.add(new BooleanSetting("strictCompare", true));
      this.onlySelectedSlot = this.mainGroup.add(new BooleanSetting("onlySelectedSlot", false));
      this.customFontSize = this.mainGroup.add(new BooleanSetting("customFontSize", false));
      this.fontSize = this.mainGroup.add((new IntegerSetting("fontSize", 1)).min(1).max(20).visibleIf(() -> {
         return (Boolean)this.customFontSize.get();
      }));
      this.numberAbbreviation = this.mainGroup.add(new BooleanSetting("numberAbbreviation", false));
      this.offsetX = this.mainGroup.add((new IntegerSetting("offsetX", 1)).min(-10).max(10));
      this.offsetY = this.mainGroup.add((new IntegerSetting("offsetY", 1)).min(-10).max(10));
   }

   public void onDisable() {
      this.lastStack = null;
   }

   @EventHandler
   public void onTick(ClientTickEvent event) {
      this.lastStack = null;
   }

   public int scanCountsFor(class_1799 stack) {
      if (this.lastStack != null && this.lastStack.equals(stack)) {
         return this.lastScanResult;
      } else {
         class_1661 inventory = CactusConstants.mc.field_1724.method_31548();
         int totalSlots = inventory.method_67533().size() + class_1304.values().length;
         int total = 0;

         for(int i = 0; i < totalSlots; ++i) {
            class_1799 scan = inventory.method_5438(i);
            total += this.sum(stack, scan, 0);
         }

         this.lastStack = stack;
         return this.lastScanResult = total;
      }
   }

   private int sum(class_1799 stack, class_1799 scan, int treeDepth) {
      int total = 0;
      if ((Boolean)this.scanContainers.get() && treeDepth <= (Integer)this.nestedScanMaxDepth.get()) {
         if (scan.method_57826(class_9334.field_49650)) {
            total += this.sumFromStackStream(stack, scan, ((class_9276)scan.method_58695(class_9334.field_49650, class_9276.field_49289)).method_59707(), treeDepth);
         }

         if (scan.method_57826(class_9334.field_49622)) {
            total += this.sumFromStackStream(stack, scan, ((class_9288)scan.method_58695(class_9334.field_49622, class_9288.field_49334)).method_59712(), treeDepth);
         }
      }

      if (this.areSimilar(stack, scan)) {
         total += scan.method_7947();
      }

      return total;
   }

   private int sumFromStackStream(class_1799 stack, class_1799 scan, Stream<class_1799> stream, int treeDepth) {
      return stream.mapToInt((inContainer) -> {
         return this.sum(stack, inContainer, treeDepth + 1);
      }).sum() * scan.method_7947();
   }

   private boolean areSimilar(class_1799 stack, class_1799 other) {
      return (Boolean)this.strictCompare.get() ? class_1799.method_31577(stack, other) : stack.method_31574(other.method_7909());
   }
}
