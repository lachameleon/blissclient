package com.dwarslooper.cactus.client.addon.gui;

import com.dwarslooper.cactus.client.addon.v2.Addon;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.util.CactusConstants;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_4280;
import org.jetbrains.annotations.NotNull;

public class AddonListWidget extends class_4280<AddonEntry> {
   public CScreen parent;

   public AddonListWidget(CScreen parent) {
      super(CactusConstants.mc, parent.field_22789, parent.field_22790 - 60, 40, 40);
      this.parent = parent;
   }

   public void add(Addon addon) {
      this.method_25321(new AddonEntry(this, addon));
   }

   public int method_65507() {
      return super.method_65507() + 30;
   }

   public int method_25322() {
      return super.method_25322() + 85;
   }

   public void method_48579(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_48579(context, mouseX, mouseY, delta);
      if (this.method_25396().isEmpty()) {
         context.method_27534(CactusConstants.mc.field_1772, class_2561.method_43470(this.parent.getTranslatableElement("text.noAddons", new Object[0])), this.field_22758 / 2, this.field_22759 / 2, -7829368);
      }

   }

   public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
      return super.method_25402(click, doubled);
   }
}
