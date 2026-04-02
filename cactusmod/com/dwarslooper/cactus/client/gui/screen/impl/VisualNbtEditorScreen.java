package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.widget.CVisualNbtEditorWidget;
import com.dwarslooper.cactus.client.util.CactusConstants;
import net.minecraft.class_1799;
import net.minecraft.class_2487;
import net.minecraft.class_2509;
import net.minecraft.class_332;
import org.jetbrains.annotations.NotNull;

public class VisualNbtEditorScreen extends CScreen {
   private final class_1799 itemStack;
   private final int inventorySlot;
   private CVisualNbtEditorWidget instance;

   public VisualNbtEditorScreen(class_1799 stack, int slot) {
      super("vs_nbt_editor");
      this.itemStack = stack;
      this.inventorySlot = slot;
   }

   public void method_25426() {
      if (this.instance == null) {
         assert CactusConstants.mc.field_1724 != null;

         class_2487 tag = (class_2487)class_1799.field_24671.encodeStart(CactusConstants.mc.field_1724.method_56673().method_57093(class_2509.field_11560), this.itemStack).getOrThrow();
         this.method_37063(this.instance = new CVisualNbtEditorWidget(this, 50, 50, this.field_22789 - 100, this.field_22790 - 100, tag, (nbt) -> {
            try {
               assert CactusConstants.mc.field_1724 != null && CactusConstants.mc.field_1687 != null;

               class_1799 result = (class_1799)class_1799.field_24671.parse(CactusConstants.mc.field_1687.method_30349().method_57093(class_2509.field_11560), nbt).getOrThrow();
               CactusConstants.mc.field_1724.method_31548().method_5447(this.inventorySlot, result);
               return null;
            } catch (Exception var3) {
               CactusClient.getLogger().error("NBT save failed", var3);
               return var3;
            }
         }));
      } else {
         this.instance.method_25358(this.field_22789 - 100);
         this.instance.method_53533(this.field_22790 - 100);
         this.method_37063(this.instance);
      }

   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_51448().scale(2.0F, 2.0F);
      context.method_27534(CactusConstants.mc.field_1772, this.field_22785, this.field_22789 / 4, 7, -1);
      context.method_51448().scale(0.5F, 0.5F);
   }
}
