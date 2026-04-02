package com.dwarslooper.cactus.client.gui.screen;

import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_339;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import org.apache.commons.compress.utils.Lists;

public abstract class CScreen extends class_437 implements ITranslatable {
   public final List<class_339> drawablesBypass = Lists.newArrayList();
   public int posWithDefaultWidth;
   private float scale = 1.0F;
   private final String key;
   public class_437 parent;

   public boolean method_25421() {
      return true;
   }

   public CScreen(String key) {
      super(class_2561.method_43469("gui.screen." + key + ".title", new Object[]{key}));
      this.parent = CactusConstants.mc.field_1755;
      this.key = key;
   }

   public String getKey() {
      return "gui.screen." + this.key;
   }

   public void method_25419() {
      CactusConstants.mc.method_1507(this.parent);
   }

   public void method_25426() {
      this.init(true);
   }

   public void init(boolean backButton) {
      this.posWithDefaultWidth = this.field_22789 / 2 - 60;
      this.drawablesBypass.clear();
      if (backButton) {
         this.method_37063(new CTextureButtonWidget(6, 6, 100, (button) -> {
            this.method_25419();
         }));
      }

   }

   public void scale(class_4587 matrix, float f) {
      if (!(f <= 1.0F)) {
         if (this.scale != 1.0F) {
            this.normalizeScale(matrix);
         }

         this.scale = f;
         matrix.method_22905(f, f, f);
      }
   }

   public void normalizeScale(class_4587 matrix) {
      float unscale = 1.0F / this.scale;
      matrix.method_22905(unscale, unscale, unscale);
      this.scale = 1.0F;
   }

   public void finishDrawables() {
      Iterator var1 = this.drawablesBypass.iterator();

      while(var1.hasNext()) {
         class_339 bypass = (class_339)var1.next();
         this.method_37063(bypass);
      }

   }
}
