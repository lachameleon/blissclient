package com.dwarslooper.cactus.client.gui.widget;

import com.dwarslooper.cactus.client.util.game.render.RenderFunction;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_6382;
import org.jetbrains.annotations.NotNull;

public class CCustomRenderWidget extends class_339 {
   private final RenderFunction renderMethod;

   public CCustomRenderWidget(RenderFunction render) {
      super(0, 0, 0, 0, class_2561.method_43473());
      this.renderMethod = render;
      this.field_22763 = false;
   }

   protected void method_48579(@NotNull class_332 context, int mouseX, int mouseY, float deltaTicks) {
      this.renderMethod.render(context, mouseX, mouseY, deltaTicks);
   }

   protected void method_47399(@NotNull class_6382 builder) {
   }
}
