package com.dwarslooper.cactus.client.gui.tutorial;

import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.systems.tutorial.TutorialGuide;
import com.dwarslooper.cactus.client.systems.tutorial.TutorialPoint;
import com.dwarslooper.cactus.client.util.CactusConstants;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_10799;
import net.minecraft.class_11908;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_437;
import net.minecraft.class_5244;
import net.minecraft.class_5481;
import net.minecraft.class_8002;
import net.minecraft.class_9848;
import org.jetbrains.annotations.NotNull;

public class TutorialScreen extends CScreen {
   private static final class_2960 descriptionBackground = class_2960.method_60654("popup/background");
   private TutorialPoint point;
   private class_339 buttonPrev;
   private class_339 buttonNext;
   private class_437 showScreen;
   private List<TutorialGuide.Highlight> highlights;
   private final IntObjectMap<List<class_5481>> descriptionsWrapped = new IntObjectHashMap();

   public void update(TutorialGuide guide) {
      TutorialPoint p = guide.getCurrentPoint();
      this.point = p;
      this.buttonPrev.field_22763 = guide.isOrdinalInRange(p.ordinal() - 1);
      this.buttonNext.method_25355((class_2561)(guide.isOrdinalInRange(p.ordinal() + 1) ? class_5244.field_41873 : class_2561.method_43470("Finish")));
      this.showScreen = p.getView();
      this.descriptionsWrapped.clear();
      this.descriptionsWrapped.put(-1, CactusConstants.mc.field_1772.method_1728(p.description, this.field_22789 - 16));
      this.highlights = this.point.getHighlights(this.showScreen);
      this.highlights.forEach((highlight) -> {
         this.descriptionsWrapped.put(this.highlights.indexOf(highlight), CactusConstants.mc.field_1772.method_1728(highlight.tileDescription(), this.field_22789 - 16));
      });
   }

   public TutorialScreen() {
      super("tutorial");
   }

   public void method_25426() {
      super.init(false);
      this.method_37063(this.buttonPrev = new CButtonWidget(0, 0, 60, 20, class_5244.field_24339, (button) -> {
         TutorialGuide.get().previous();
      }));
      this.method_37063(this.buttonNext = new CButtonWidget(this.field_22789 - 60, 0, 60, 20, class_5244.field_41873, (button) -> {
         TutorialGuide.get().next();
      }));
   }

   public void method_25420(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
   }

   public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
      List<class_5481> description = (List)this.descriptionsWrapped.get(-1);
      context.method_51448().pushMatrix();
      context.method_51448().translate(0.0F, 0.0F);
      this.showScreen.method_25394(context, -1, -1, delta);
      context.method_51448().popMatrix();
      context.method_25294(0, 0, this.field_22789, this.field_22790, 1140850688);
      Iterator var6 = this.highlights.iterator();

      while(var6.hasNext()) {
         TutorialGuide.Highlight h = (TutorialGuide.Highlight)var6.next();
         context.method_73198(h.x(), h.y(), h.width(), h.height(), -16711936);
         if (!this.buttonPrev.method_49606() && !this.buttonNext.method_49606() && mouseX > h.x() && mouseX < h.x() + h.width() && mouseY > h.y() && mouseY < h.y() + h.height()) {
            description = (List)this.descriptionsWrapped.get(this.highlights.indexOf(h));
         }
      }

      super.method_25394(context, mouseX, mouseY, delta);
      class_2561 title = this.point.title.method_27661().method_27693(" (").method_27693(Integer.toString(this.point.ordinal() + 1)).method_27693("/").method_27693(Integer.toString(TutorialGuide.pointsSize)).method_27693(")");
      int w = CactusConstants.mc.field_1772.method_27525(title);
      int var10001 = (this.field_22789 - (w + 8)) / 2;
      int var10003 = w + 8;
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      class_8002.method_47946(context, var10001, 3, var10003, 9 + 4, (class_2960)null);
      context.method_27534(CactusConstants.mc.field_1772, title, this.field_22789 / 2, 6, -1);
      int var10000 = description.size();
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      int j = var10000 * (9 + 2) + 2;
      int top = 12 + j;
      context.method_51448().pushMatrix();
      int color = class_9848.method_61318(0.6F, 1.0F, 1.0F, 1.0F);
      context.method_52707(class_10799.field_56883, descriptionBackground, 0, this.field_22790 - top, this.field_22789, top, color);
      context.method_51448().popMatrix();
      int yI = 0;

      for(Iterator var12 = description.iterator(); var12.hasNext(); yI += 9 + 2) {
         class_5481 orderedText = (class_5481)var12.next();
         context.method_35719(CactusConstants.mc.field_1772, orderedText, this.field_22789 / 2, this.field_22790 - 4 - j + yI, -1);
         Objects.requireNonNull(CactusConstants.mc.field_1772);
      }

      this.buttonNext.method_46419(this.field_22790 - 20 - top);
      this.buttonPrev.method_46419(this.field_22790 - 20 - top);
   }

   public boolean method_25404(class_11908 input) {
      if (input.comp_4795() == 262) {
         TutorialGuide.get().next();
      } else {
         if (input.comp_4795() != 263) {
            return super.method_25404(input);
         }

         TutorialGuide.get().previous();
      }

      return true;
   }

   public void method_25410(int width, int height) {
      this.update(TutorialGuide.get());
      super.method_25410(width, height);
   }
}
