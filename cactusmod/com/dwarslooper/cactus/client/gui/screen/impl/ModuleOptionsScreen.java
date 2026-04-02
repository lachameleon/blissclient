package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.gui.widget.CCheckboxWidget;
import com.dwarslooper.cactus.client.gui.widget.KeyBindWidget;
import com.dwarslooper.cactus.client.systems.config.settings.gui.AbstractSettingScreen;
import com.dwarslooper.cactus.client.systems.key.KeyBind;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.Objects;
import net.minecraft.class_11908;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_437;
import org.jetbrains.annotations.NotNull;

public class ModuleOptionsScreen extends AbstractSettingScreen {
   private static final class_2561 toggleReleaseInfo = class_2561.method_43471("module.toggleRelease");
   private final Module module;
   private KeyBindWidget keyBindWidget;

   public ModuleOptionsScreen(Module module, class_437 parent) {
      super("module_options", module.settings);
      this.module = module;
      this.parent = parent;
   }

   public boolean method_25421() {
      return false;
   }

   public void method_25426() {
      super.method_25426();
      int var10005 = this.field_22790 - 26;
      KeyBind var10008 = this.module.getBind();
      Module var10009 = this.module;
      Objects.requireNonNull(var10009);
      this.method_37063(this.keyBindWidget = new KeyBindWidget(6, var10005, 120, 20, var10008, var10009::setBind));
      this.method_37063(new CCheckboxWidget(130, this.field_22790 - 26, 20, 20, this.module.shouldToggleOnBindRelease(), (w, b) -> {
         this.module.setToggleOnRelease(b);
      }));
      this.addCopyPaste();
   }

   public boolean method_25404(@NotNull class_11908 input) {
      return this.keyBindWidget.method_25370() ? this.keyBindWidget.onKey(input.comp_4795()) : super.method_25404(input);
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_51448().pushMatrix();
      context.method_51448().scale(2.0F, 2.0F);
      String title = this.module.getDisplayName();
      context.method_25303(this.field_22793, title, (int)((float)this.field_22789 / 2.0F / 2.0F - (float)this.field_22793.method_1727(this.module.getDisplayName()) / 2.0F), 2, CactusClient.getInstance().getRGB());
      context.method_51448().popMatrix();
      context.method_25300(this.field_22793, this.module.getDescription(), this.field_22789 / 2, 24, -1);
      class_327 var10001 = this.field_22793;
      class_2561 var10002 = toggleReleaseInfo;
      int var10004 = this.field_22790 - 6;
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      context.method_27535(var10001, var10002, 154, var10004 - (9 + 20) / 2, -1);
   }
}
