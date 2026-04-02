package com.dwarslooper.cactus.client.feature.modules.util;

import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.ClientTickEvent;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.class_1703;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_8786;

public class AutoCrafter extends Module {
   public static AutoCrafter.AutoCrafterPath currentPath;
   int tick = 0;

   public AutoCrafter() {
      super("auto_craft", ModuleManager.CATEGORY_UTILITY, (new Module.Options()).set(Module.Flag.SERVER_UNSAFE, true).set(Module.Flag.EXPERIMENTAL, true));
   }

   @EventHandler
   public void onTick(ClientTickEvent event) {
      if (currentPath != null) {
         ++this.tick;
         if (this.tick >= 40) {
            currentPath.recheck();
            this.tick = 0;
         }

      }
   }

   public static class AutoCrafterPath {
      private class_8786<?> recipe;
      private List<AutoCrafter.AutoCrafterPath> children;
      private class_1792 type;
      private int count;
      private int available;

      public AutoCrafterPath(class_1792 item, int amount) {
         this.setChildren(new ArrayList());
         this.setType(item);
         this.setCount(amount);
      }

      public void recheck() {
         AutoCrafter.AutoCrafterPath rechecked = generatePath(AutoCrafter.currentPath.type, AutoCrafter.currentPath.count);
         this.recipe = rechecked.recipe;
         this.children = rechecked.children;
         this.type = rechecked.type;
         this.count = rechecked.count;
         this.available = rechecked.available;
      }

      public void craft(class_634 network, class_1703 handler) {
         assert class_310.method_1551().field_1724 != null;

         if (this.recipe == null) {
            ChatUtils.error("Recipe is null (why tho)");
         } else {
            Iterator var3 = this.getChildren().iterator();

            while(var3.hasNext()) {
               AutoCrafter.AutoCrafterPath path = (AutoCrafter.AutoCrafterPath)var3.next();
               if (path.available < path.getCount()) {
                  path.craft(network, handler);
               }
            }

            for(int i = 0; i < this.getCount() - this.getAvailable(); ++i) {
               int slot = class_310.method_1551().field_1724.method_31548().method_7376() + 10;
               if (slot == 9) {
                  ChatUtils.warning("No inventory space left.");
                  return;
               }
            }

         }
      }

      public static AutoCrafter.AutoCrafterPath generatePath(class_1792 type, Integer amount) {
         class_310 mc = class_310.method_1551();

         assert mc.field_1724 != null;

         Map<class_1792, Integer> totalItemsList = new HashMap();

         for(int i = 0; i < 36; ++i) {
            class_1799 stack = mc.field_1724.method_31548().method_5438(i);
            if (totalItemsList.containsKey(stack.method_7909())) {
               totalItemsList.put(stack.method_7909(), (Integer)totalItemsList.get(stack.method_7909()) + stack.method_7947());
            } else {
               totalItemsList.put(stack.method_7909(), stack.method_7947());
            }
         }

         totalItemsList.remove(type);
         return generatePath(mc, type, amount, new ArrayList(), totalItemsList);
      }

      public static AutoCrafter.AutoCrafterPath generatePath(class_310 mc, class_1792 type, Integer amount, List<class_1792> itemStack, Map<class_1792, Integer> inventory) {
         assert mc.field_1724 != null;

         AutoCrafter.AutoCrafterPath path = new AutoCrafter.AutoCrafterPath(type, amount);
         if (itemStack.contains(type)) {
            return path;
         } else {
            if (path.getCount() > 0 && inventory.containsKey(type)) {
               if ((Integer)inventory.get(type) >= path.getCount()) {
                  path.setAvailable(path.getCount());
               } else {
                  path.setAvailable((Integer)inventory.get(type));
               }

               inventory.put(type, (Integer)inventory.get(type) - path.getAvailable());
            }

            new AtomicBoolean(false);
            mc.field_1724.method_3130().method_1393().forEach((col) -> {
               col.method_2650().forEach((recipe) -> {
               });
            });
            return path;
         }
      }

      public List<AutoCrafter.AutoCrafterPath> getChildren() {
         return this.children;
      }

      public void setChildren(List<AutoCrafter.AutoCrafterPath> children) {
         this.children = children;
      }

      public int getCount() {
         return this.count;
      }

      public void setCount(int count) {
         this.count = count;
      }

      public class_1792 getType() {
         return this.type;
      }

      public void setType(class_1792 type) {
         this.type = type;
      }

      public int getAvailable() {
         return this.available;
      }

      public void setAvailable(int available) {
         this.available = available;
      }
   }
}
