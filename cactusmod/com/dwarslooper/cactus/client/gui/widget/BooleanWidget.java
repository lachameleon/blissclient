package com.dwarslooper.cactus.client.gui.widget;

import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import com.dwarslooper.cactus.client.util.generic.TextUtils;
import java.util.function.Consumer;
import net.minecraft.class_10799;
import net.minecraft.class_11907;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_9848;
import net.minecraft.class_332.class_12228;
import org.jetbrains.annotations.NotNull;

public class BooleanWidget extends CButtonWidget {
   private boolean value;
   private final Consumer<Boolean> setter;

   public BooleanWidget(int x, int y, int width, int height, class_2561 message, boolean value, Consumer<Boolean> setter) {
      super(x, y, width, height, message, (button) -> {
      });
      this.value = value;
      this.setter = setter;
   }

   protected void method_75752(class_332 context, int mouseX, int mouseY, float delta) {
      context.method_52706(class_10799.field_56883, field_45339.method_52729(this.field_22763, this.method_25367()), this.method_46426(), this.method_46427(), this.method_25368(), this.method_25364());
      context.method_51448().pushMatrix();
      float fc = 0.33333334F;
      int color = class_9848.method_61318(1.0F, this.value ? fc : 1.0F, this.value ? 1.0F : fc, fc);
      context.method_52707(class_10799.field_56883, RenderUtils.SLIDER_HANDLE_TEXTURES.method_52729(true, this.method_25367() && this.field_22763), this.method_46426() + this.field_22758 - (this.value ? 8 : this.field_22758), this.method_46427(), 8, 20, color);
      context.method_51448().popMatrix();
      this.method_75793(context.method_75787(this, class_12228.field_63850));
   }

   @NotNull
   public class_2561 method_25369() {
      return super.method_25369().method_27661().method_27693(": ").method_10852(TextUtils.boolAsText(this.value));
   }

   public void method_25306(@NotNull class_11907 input) {
      this.value = !this.value;
      this.setter.accept(this.value);
   }

   public boolean getValue() {
      return this.value;
   }

   public void setValue(boolean value) {
      this.value = value;
   }
}
