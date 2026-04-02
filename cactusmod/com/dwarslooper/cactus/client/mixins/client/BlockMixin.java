package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.util.CopyBlockState;
import com.dwarslooper.cactus.client.util.game.ItemUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_156;
import net.minecraft.class_1799;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2561;
import net.minecraft.class_2680;
import net.minecraft.class_2769;
import net.minecraft.class_310;
import net.minecraft.class_4538;
import net.minecraft.class_9275;
import net.minecraft.class_9290;
import net.minecraft.class_9334;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin({class_2248.class})
public abstract class BlockMixin {
   public void copyBlockState(class_4538 world, class_2338 pos, class_2680 state, CallbackInfoReturnable<class_1799> cir) {
      CopyBlockState module = (CopyBlockState)ModuleManager.get().get(CopyBlockState.class);
      class_310 mc = class_310.method_1551();
      boolean controlDown = GLFW.glfwGetKey(mc.method_22683().method_4490(), 341) == 1 || GLFW.glfwGetKey(mc.method_22683().method_4490(), 345) == 1;
      if (module.active() && controlDown) {
         class_1799 stack = new class_1799((class_2248)this);
         Map<String, String> properties = new HashMap();
         List<class_2561> lines = new ArrayList();
         Iterator var11 = state.method_28501().iterator();

         while(var11.hasNext()) {
            class_2769<?> property = (class_2769)var11.next();
            String name = property.method_11899();
            if (!(((List)module.properties.get()).contains(name) ^ module.listType.get() == CopyBlockState.Mode.Whitelist)) {
               Comparable<?> value = state.method_11654(property);
               String formattedProperty = ItemUtils.propertyToFormattedString(property, value);
               String valueStr = class_156.method_650(property, value);
               properties.put(name, valueStr);
               lines.add(class_2561.method_43470(formattedProperty));
            }
         }

         if (!properties.isEmpty()) {
            if ((Boolean)module.appendToLore.get()) {
               stack.method_57379(class_9334.field_49632, new class_9290(lines));
            }

            stack.method_57379(class_9334.field_49623, new class_9275(properties));
            cir.setReturnValue(stack);
         }
      }

   }
}
