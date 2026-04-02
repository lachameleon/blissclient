package com.dwarslooper.cactus.client.gui.widget;

import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.class_2561;
import net.minecraft.class_2564;
import net.minecraft.class_2583;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_4185;
import net.minecraft.class_4185.class_4241;

public class SupplyingClickableTextWidget extends class_4185 {
   private final Supplier<class_2561> text;
   private final Function<class_2561, class_2561> hoverTextTransformer;

   public SupplyingClickableTextWidget(int x, int y, int width, int height, Supplier<class_2561> textSupplier, class_4241 onPress) {
      this(x, y, width, height, textSupplier, (t) -> {
         return class_2564.method_10889(t.method_27661(), class_2583.field_24360.method_30938(true));
      }, onPress);
   }

   public SupplyingClickableTextWidget(int x, int y, int width, int height, Supplier<class_2561> textSupplier, Function<class_2561, class_2561> hoverTextTransformer, class_4241 onPress) {
      super(x, y, width, height, (class_2561)textSupplier.get(), onPress, field_40754);
      this.text = textSupplier;
      this.hoverTextTransformer = hoverTextTransformer;
   }

   protected void method_75752(class_332 context, int mouseX, int mouseY, float delta) {
      class_2561 text = this.method_25367() ? (class_2561)this.hoverTextTransformer.apply((class_2561)this.text.get()) : (class_2561)this.text.get();
      context.method_27535(CactusConstants.mc.field_1772, text, this.method_46426(), this.method_46427(), -1 | class_3532.method_15386(this.field_22765 * 255.0F) << 24);
   }
}
