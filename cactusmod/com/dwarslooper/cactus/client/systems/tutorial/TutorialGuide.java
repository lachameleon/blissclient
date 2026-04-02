package com.dwarslooper.cactus.client.systems.tutorial;

import com.dwarslooper.cactus.client.gui.tutorial.TutorialScreen;
import com.dwarslooper.cactus.client.systems.SingleInstance;
import com.dwarslooper.cactus.client.util.CactusConstants;
import net.minecraft.class_2561;
import net.minecraft.class_437;

public class TutorialGuide {
   private TutorialPoint currentPoint;
   private class_437 startedFrom;
   private TutorialScreen screen;
   public static int pointsSize = -1;

   public static TutorialGuide get() {
      return (TutorialGuide)SingleInstance.of(TutorialGuide.class, TutorialGuide::new);
   }

   public TutorialGuide() {
      pointsSize = ((TutorialPoint[])TutorialPoint.class.getEnumConstants()).length;
   }

   public void start() {
      this.currentPoint = TutorialPoint.values()[0];
      this.startedFrom = CactusConstants.mc.field_1755;
      CactusConstants.mc.method_1507(this.screen = new TutorialScreen());
      this.screen.update(this);
   }

   public void next() {
      int o = this.currentPoint.ordinal() + 1;
      if (o >= ((TutorialPoint[])TutorialPoint.class.getEnumConstants()).length) {
         CactusConstants.mc.method_1507(this.startedFrom);
      } else {
         this.currentPoint = ((TutorialPoint[])TutorialPoint.class.getEnumConstants())[o];
         this.screen.update(this);
      }

   }

   public void previous() {
      int o = this.currentPoint.ordinal() - 1;
      if (this.isOrdinalInRange(o)) {
         this.currentPoint = ((TutorialPoint[])TutorialPoint.class.getEnumConstants())[o];
         this.screen.update(this);
      }

   }

   public boolean isOrdinalInRange(int o) {
      return o < pointsSize && o >= 0;
   }

   public TutorialPoint getCurrentPoint() {
      return this.currentPoint;
   }

   public TutorialScreen getScreen() {
      return this.screen;
   }

   public void update() {
      this.screen.update(this);
   }

   public static record Highlight(int x, int y, int width, int height, class_2561 tileDescription) {
      public Highlight(int x, int y, int width, int height, class_2561 tileDescription) {
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.tileDescription = tileDescription;
      }

      public int x() {
         return this.x;
      }

      public int y() {
         return this.y;
      }

      public int width() {
         return this.width;
      }

      public int height() {
         return this.height;
      }

      public class_2561 tileDescription() {
         return this.tileDescription;
      }
   }
}
