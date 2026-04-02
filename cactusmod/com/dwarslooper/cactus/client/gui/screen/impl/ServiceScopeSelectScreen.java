package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.gui.screen.window.WindowScreen;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.Objects;
import net.minecraft.class_11735;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_4185;
import net.minecraft.class_5244;
import net.minecraft.class_5489;
import net.minecraft.class_7077;
import org.jetbrains.annotations.NotNull;

public class ServiceScopeSelectScreen extends WindowScreen {
   private class_5489 text;

   public ServiceScopeSelectScreen() {
      super("selectScope", 200, 200);
   }

   public void method_25426() {
      super.method_25426();
      this.setBoxWidth(this.calculateWidth());
      this.text = class_5489.method_61133(CactusConstants.mc.field_1772, this.calculateWidth() - 8, new class_2561[]{class_2561.method_43470("But wait, there's more! We have created a backend for Cactus Mod which allows you to do some some really cool things like seeing other players special capes, inviting friends to your singleplayer worlds or servers, organizing your own team and much more!"), class_5244.field_41874, class_2561.method_43470("We've created this little popup at the start to give you the option to choose if you want to use those online features (I really recommend :D). You can always change that later in the settings.")});
      this.method_37063(class_4185.method_46430(class_2561.method_43470("Lets go!"), (button) -> {
         this.method_25419();
      }).method_46434(this.x() + this.boxWidth() - 62, this.y() + this.boxHeight() - 22, 60, 20).method_46431());
      int var10003 = this.x() + 4;
      int var10004 = this.y() + this.boxHeight();
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      var10004 = var10004 - 9 - 2;
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      this.method_37063(new class_7077(var10003, var10004, 60, 9, class_2561.method_43470("Maybe later"), (button) -> {
         this.method_25419();
      }, CactusConstants.mc.field_1772));
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      class_5489 var10000 = this.text;
      class_11735 var10001 = class_11735.field_62009;
      int var10002 = this.x() + 4;
      int var10003 = this.y() + 4;
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      var10000.method_75816(var10001, var10002, var10003, 9, context.method_75788());
   }

   private int calculateWidth() {
      return Math.max(this.field_22789 / 2, 200);
   }
}
