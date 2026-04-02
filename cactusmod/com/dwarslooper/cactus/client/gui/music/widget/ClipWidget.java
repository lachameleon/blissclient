package com.dwarslooper.cactus.client.gui.music.widget;

import com.dwarslooper.cactus.client.gui.music.ClipArrangementScreen;
import com.dwarslooper.cactus.client.gui.music.NoteBlockLineEditorScreen;
import com.dwarslooper.cactus.client.gui.music.data.PlacedClip;
import com.dwarslooper.cactus.client.gui.music.data.SingleClip;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.class_10799;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_6382;
import org.jetbrains.annotations.NotNull;

public class ClipWidget extends class_339 {
   private final ClipArrangementScreen parent;
   private final int backgroundColor;
   private final int borderColor;
   private ClipLineWidget clipLine;
   private PlacedClip clip;
   private long lastClickTime;
   private double lastMouseX;
   private double lastMouseY;
   private int oldX;
   private int oldY;
   private double doublePosX;
   private double doublePosY;
   private ClipArrangementScreen.SnappingResult lastResult;

   public ClipWidget(ClipArrangementScreen parent, PlacedClip clip, int y) {
      super((int)((float)clip.offX() * 2.0F) + 60, y + 32, (int)((float)clip.ref().totalWidth() * 2.0F), 65, class_2561.method_43470(clip.ref().name()));
      this.parent = parent;
      this.backgroundColor = clip.ref().color().getRGB();
      this.borderColor = clip.ref().color().darker().getRGB();
      this.clip = clip;
   }

   protected void method_48579(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      if (this.clip.ref().replacement().get() != null) {
         this.clip = new PlacedClip((SingleClip)this.clip.ref().replacement().get(), this.clip.offX());
      }

      int currentBorderColor = this.parent.isSelected(this) ? -1 : this.borderColor;
      context.method_25294(this.method_46426(), this.method_46427(), this.method_46426() + this.method_25368(), this.method_46427() + this.method_25364(), this.backgroundColor);
      context.method_73198(this.method_46426(), this.method_46427(), this.method_25368(), this.method_25364(), currentBorderColor);
      context.method_73198(this.method_46426(), this.method_46427(), this.method_25368(), 14, currentBorderColor);
      context.method_27534(CactusConstants.mc.field_1772, this.method_25369(), this.method_46426() + this.method_25368() / 2, this.method_46427() + 2, -1);
      context.method_25302(class_10799.field_56883, this.clip.ref().getCompiledImage(), this.method_46426(), this.method_46427() + 14, (float)this.method_25368(), (float)(this.method_25364() - 15), 0, 0, this.clip.ref().totalWidth(), 24, this.clip.ref().totalWidth(), 24);
   }

   public void setParent(ClipLineWidget clipLine) {
      this.clipLine = clipLine;
   }

   public ClipLineWidget getClipLine() {
      return this.clipLine;
   }

   public void startDragging() {
      this.clipLine.removeClip(this);
      this.doublePosX = (double)(this.oldX = this.method_46426());
      this.doublePosY = (double)(this.oldY = this.method_46427());
      this.lastResult = null;
   }

   public boolean onDrag(double deltaX, double deltaY) {
      this.doublePosX += deltaX;
      this.doublePosY += deltaY;
      this.lastResult = this.parent.snap(this.doublePosX, this.doublePosY, this.clip.ref().totalWidth());
      this.method_48229((int)this.doublePosX, (int)this.doublePosY);
      return this.lastResult.snapSuccessful();
   }

   public void onSuccessfulSnap() {
      this.method_48229((int)(60.0F + (float)this.lastResult.snapX() * 2.0F), 32 + this.lastResult.snapY() * 65);
   }

   public void finishDragging() {
      if (this.lastResult != null && this.lastResult.snapSuccessful()) {
         this.parent.addToLine(this.lastResult.snapY(), this);
         this.clip = new PlacedClip(this.clip.ref(), this.lastResult.snapX());
      } else {
         this.clipLine.addClip(this);
         this.method_48229(this.oldX, this.oldY);
      }

   }

   public int getGridY() {
      return (this.method_46427() - 32) / 65;
   }

   public void setGridY(int gridY) {
      this.method_46419(32 + gridY * 65);
   }

   public boolean collides(int x1, int x2) {
      int aMin = Math.min(x1, x2);
      int aMax = Math.max(x1, x2);
      int bMin = Math.min(this.clip.offX(), this.clip.offX() + this.clip.ref().totalWidth());
      int bMax = Math.max(this.clip.offX(), this.clip.offX() + this.clip.ref().totalWidth());
      return aMax >= bMin && bMax >= aMin;
   }

   public boolean method_25402(class_11909 click, boolean doubled) {
      this.lastMouseX = click.comp_4798();
      this.lastMouseY = click.comp_4799();
      return false;
   }

   public boolean method_25406(class_11909 click) {
      double mouseX = click.comp_4798();
      double mouseY = click.comp_4799();
      if (!(mouseX < (double)this.method_46426()) && !(mouseX > (double)(this.method_46426() + this.method_25368())) && !(mouseY < (double)this.method_46427()) && !(mouseY > (double)(this.method_46427() + this.method_25364())) && !(this.lastMouseX - mouseX + this.lastMouseY - mouseY > 5.0D)) {
         if (System.currentTimeMillis() - this.lastClickTime < 300L) {
            class_310 var10000 = CactusConstants.mc;
            SingleClip var10003 = this.clip.ref();
            AtomicReference var10004 = this.clip.ref().replacement();
            Objects.requireNonNull(var10004);
            var10000.method_1507(new NoteBlockLineEditorScreen(var10003, var10004::set));
         } else {
            this.parent.selectClip(this);
         }

         this.lastClickTime = System.currentTimeMillis();
         return true;
      } else {
         return false;
      }
   }

   public PlacedClip getClip() {
      return this.clip;
   }

   protected void method_47399(@NotNull class_6382 builder) {
   }
}
