package com.dwarslooper.cactus.client.addon.gui;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_332;
import net.minecraft.class_437;
import org.jetbrains.annotations.NotNull;

public class CactusAddonsScreen extends CScreen {
   private AddonListWidget widget;

   public CactusAddonsScreen(class_437 parent) {
      super("addons");
      this.parent = parent;
   }

   public void method_25426() {
      super.method_25426();
      this.method_37063(this.widget = new AddonListWidget(this));
      List var10000 = CactusClient.getInstance().getAddonHandler().getAddons();
      AddonListWidget var10001 = this.widget;
      Objects.requireNonNull(var10001);
      var10000.forEach(var10001::add);
      this.finishDrawables();
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 8, -1);
   }
}
