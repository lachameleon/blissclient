package com.dwarslooper.cactus.client.gui.music;

import com.dwarslooper.cactus.client.gui.music.data.PlacedClip;
import com.dwarslooper.cactus.client.gui.music.data.SingleClip;
import com.dwarslooper.cactus.client.gui.music.widget.ClipLineWidget;
import com.dwarslooper.cactus.client.gui.music.widget.ClipWidget;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.CToggleButtonWidget;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_4068;
import net.minecraft.class_5250;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

public class ClipArrangementScreen extends CScreen {
   public static final int CLIP_HEIGHT = 65;
   public static final float PIXELS_PER_TICK = 2.0F;
   private final List<ClipLineWidget> clipLines = new ArrayList();
   private final List<ClipWidget> clips = new ArrayList();
   private final List<ClipWidget> selectedClips = new ArrayList();
   private final Map<Vector2i, PlacedClip> clipboard = new HashMap();
   private int ticksPerBeat = 4;
   private CToggleButtonWidget snapButton;
   private double lastMouseX;
   private double lastMouseY;
   private ClipLineWidget soloLine;

   public ClipArrangementScreen() {
      super("arranger");
      this.clipLines.add(new ClipLineWidget(this, 0));
      this.clipLines.add(new ClipLineWidget(this, 1));
      PlacedClip clip = new PlacedClip(new SingleClip("Test", new AtomicReference(), 48, "pling", 4, 12, new HashMap(), Color.red, new AtomicReference()), 0);
      ClipWidget wid = new ClipWidget(this, clip, 0);
      ((ClipLineWidget)this.clipLines.getFirst()).addClip(wid);
      this.clips.add(wid);
   }

   public void method_25426() {
      super.method_25426();
      Iterator var1 = this.clipLines.iterator();

      while(var1.hasNext()) {
         ClipLineWidget clipLine = (ClipLineWidget)var1.next();
         this.method_25429(clipLine);
      }

      var1 = this.clips.iterator();

      while(var1.hasNext()) {
         ClipWidget clip = (ClipWidget)var1.next();
         this.method_25429(clip);
      }

      this.selectedClips.clear();
      this.snapButton = (CToggleButtonWidget)this.method_37063(new CToggleButtonWidget(this.field_22789 - 53, 3, 50, 26, class_2561.method_43470("Snap"), (state) -> {
      }));
      this.method_37063(new CButtonWidget(this.field_22789 - 66, 3, 13, 13, class_2561.method_43470("+"), (btn) -> {
         this.changeTicks(1);
      }));
      this.method_37063(new CButtonWidget(this.field_22789 - 66, 16, 13, 13, class_2561.method_43470("-"), (btn) -> {
         this.changeTicks(-1);
      }));
   }

   public void method_25394(class_332 ctx, int mouseX, int mouseY, float delta) {
      ctx.method_25294(0, 0, this.field_22789, this.field_22790, -15657705);
      Iterator var5 = this.clipLines.iterator();

      while(var5.hasNext()) {
         ClipLineWidget clipLine = (ClipLineWidget)var5.next();
         clipLine.method_25394(ctx, mouseX, mouseY, delta);
      }

      var5 = this.clips.iterator();

      while(var5.hasNext()) {
         ClipWidget clip = (ClipWidget)var5.next();
         clip.method_25394(ctx, mouseX, mouseY, delta);
      }

      ctx.method_51448().pushMatrix();
      ctx.method_51448().translate(0.0F, 0.0F);
      ctx.method_25294(0, 0, 60, this.field_22790, -13682624);
      ctx.method_25294(0, 0, this.field_22789, 32, -11576729);
      var5 = this.field_33816.iterator();

      while(var5.hasNext()) {
         class_4068 drawable = (class_4068)var5.next();
         drawable.method_25394(ctx, mouseX, mouseY, delta);
      }

      class_327 var10001 = CactusConstants.mc.field_1772;
      class_5250 var10002 = class_2561.method_43470(this.ticksPerBeat + " ticks/beat");
      int var10003 = this.field_22789 - 120;
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      ctx.method_27534(var10001, var10002, var10003, 16 - 9 / 2, -395538);
      ctx.method_51448().popMatrix();
   }

   public boolean method_25404(class_11908 input) {
      int keyCode = input.comp_4795();
      Iterator clipboardPosIterator;
      ClipWidget clip;
      int maxPos;
      Iterator var6;
      ClipLineWidget clipLine;
      int y;
      ClipLineWidget line;
      PlacedClip clip;
      ClipWidget wid;
      int minPosClipboard;
      Entry entry;
      switch(keyCode) {
      case 65:
         if (this.isControlDown()) {
            this.selectedClips.clear();
            this.selectedClips.addAll(this.clips);
            return true;
         }
         break;
      case 67:
         if (this.isControlDown()) {
            this.clipboard.clear();
            clipboardPosIterator = this.selectedClips.iterator();

            while(clipboardPosIterator.hasNext()) {
               clip = (ClipWidget)clipboardPosIterator.next();
               this.clipboard.put(new Vector2i(clip.getClip().offX(), clip.getGridY()), clip.getClip());
            }

            return true;
         }
         break;
      case 68:
         if (this.isControlDown()) {
            this.clipboard.clear();
            clipboardPosIterator = this.selectedClips.iterator();

            while(clipboardPosIterator.hasNext()) {
               clip = (ClipWidget)clipboardPosIterator.next();
               this.clipboard.put(new Vector2i(clip.getClip().offX(), clip.getGridY()), clip.getClip());
            }

            if (this.clipboard.isEmpty()) {
               return true;
            }

            clipboardPosIterator = this.clipboard.keySet().iterator();

            for(minPosClipboard = ((Vector2i)clipboardPosIterator.next()).x; clipboardPosIterator.hasNext(); minPosClipboard = Math.min(minPosClipboard, ((Vector2i)clipboardPosIterator.next()).x)) {
            }

            maxPos = 0;

            for(var6 = this.clipLines.iterator(); var6.hasNext(); maxPos = Math.max(maxPos, clipLine.getMaxPos())) {
               clipLine = (ClipLineWidget)var6.next();
            }

            var6 = this.clipboard.entrySet().iterator();

            while(var6.hasNext()) {
               entry = (Entry)var6.next();
               y = ((Vector2i)entry.getKey()).y;
               if (y >= 0 && y < this.clipLines.size()) {
                  line = (ClipLineWidget)this.clipLines.get(y);
                  clip = new PlacedClip(((PlacedClip)entry.getValue()).ref(), maxPos + ((Vector2i)entry.getKey()).x - minPosClipboard);
                  wid = new ClipWidget(this, clip, y);
                  this.clips.add(wid);
                  this.method_37063(wid);
                  line.addClip(wid);
               }
            }

            return true;
         }
         break;
      case 86:
         if (this.isControlDown()) {
            if (this.clipboard.isEmpty()) {
               return true;
            }

            clipboardPosIterator = this.clipboard.keySet().iterator();

            for(minPosClipboard = ((Vector2i)clipboardPosIterator.next()).x; clipboardPosIterator.hasNext(); minPosClipboard = Math.min(minPosClipboard, ((Vector2i)clipboardPosIterator.next()).x)) {
            }

            maxPos = 0;

            for(var6 = this.clipLines.iterator(); var6.hasNext(); maxPos = Math.max(maxPos, clipLine.getMaxPos())) {
               clipLine = (ClipLineWidget)var6.next();
            }

            var6 = this.clipboard.entrySet().iterator();

            while(var6.hasNext()) {
               entry = (Entry)var6.next();
               y = ((Vector2i)entry.getKey()).y;
               if (y >= 0 && y < this.clipLines.size()) {
                  line = (ClipLineWidget)this.clipLines.get(y);
                  clip = new PlacedClip(((PlacedClip)entry.getValue()).ref(), maxPos + ((Vector2i)entry.getKey()).x - minPosClipboard);
                  wid = new ClipWidget(this, clip, y);
                  this.clips.add(wid);
                  this.method_37063(wid);
                  line.addClip(wid);
               }
            }

            return true;
         }
         break;
      case 261:
         clipboardPosIterator = this.selectedClips.iterator();

         while(clipboardPosIterator.hasNext()) {
            clip = (ClipWidget)clipboardPosIterator.next();
            this.clips.remove(clip);
            clip.getClipLine().removeClip(clip);
            this.method_37066(clip);
         }

         this.selectedClips.clear();
         return true;
      }

      return super.method_25404(input);
   }

   public boolean method_25402(class_11909 click, boolean doubled) {
      this.lastMouseX = click.comp_4798();
      this.lastMouseY = click.comp_4799();
      return super.method_25402(click, doubled);
   }

   public boolean method_25403(class_11909 click, double deltaX, double deltaY) {
      boolean firstFrame = this.lastMouseX != -1.0D;
      this.lastMouseX = -1.0D;
      boolean snapSuccessful = true;

      Iterator var8;
      ClipWidget clip;
      for(var8 = this.selectedClips.iterator(); var8.hasNext(); snapSuccessful = snapSuccessful && clip.onDrag(deltaX, deltaY)) {
         clip = (ClipWidget)var8.next();
         if (firstFrame) {
            clip.startDragging();
         }
      }

      if (snapSuccessful) {
         var8 = this.selectedClips.iterator();

         while(var8.hasNext()) {
            clip = (ClipWidget)var8.next();
            clip.onSuccessfulSnap();
         }
      }

      return true;
   }

   public boolean method_25406(class_11909 click) {
      double mouseX = click.comp_4798();
      double mouseY = click.comp_4799();
      if (this.lastMouseX != -1.0D) {
         if (super.method_25406(click)) {
            return true;
         } else if (this.lastMouseX - mouseX + this.lastMouseY - mouseY > 5.0D) {
            return false;
         } else {
            this.selectedClips.clear();
            return true;
         }
      } else {
         Iterator var6 = this.selectedClips.iterator();

         while(var6.hasNext()) {
            ClipWidget clip = (ClipWidget)var6.next();
            clip.finishDragging();
         }

         return true;
      }
   }

   public void changeTicks(int diff) {
      this.ticksPerBeat += diff;
      if (this.ticksPerBeat < 1) {
         this.ticksPerBeat = 1;
      }

   }

   public void solo(ClipLineWidget line) {
      this.soloLine = line;
      Iterator var2 = this.clipLines.iterator();

      while(var2.hasNext()) {
         ClipLineWidget clipLine = (ClipLineWidget)var2.next();
         clipLine.setSolo(line);
      }

   }

   public void selectClip(ClipWidget clipWidget) {
      if (this.isControlDown()) {
         if (this.selectedClips.contains(clipWidget)) {
            this.selectedClips.remove(clipWidget);
            return;
         }
      } else {
         this.selectedClips.clear();
      }

      this.selectedClips.add(clipWidget);
   }

   private boolean isControlDown() {
      long handle = CactusConstants.mc.method_22683().method_4490();
      return GLFW.glfwGetKey(handle, 341) == 1 || GLFW.glfwGetKey(handle, 345) == 1;
   }

   public void deleteLine(ClipLineWidget clipLineWidget) {
      this.clipLines.remove(clipLineWidget);
      this.method_37066(clipLineWidget);
      Iterator var2 = clipLineWidget.getClips().iterator();

      while(var2.hasNext()) {
         ClipWidget clip = (ClipWidget)var2.next();
         this.clips.remove(clip);
         this.method_37066(clip);
      }

      for(int i = 0; i < this.clipLines.size(); ++i) {
         ((ClipLineWidget)this.clipLines.get(i)).setGridY(i);
      }

   }

   public boolean isSelected(ClipWidget clipWidget) {
      return this.selectedClips.contains(clipWidget);
   }

   public boolean isSoloed(ClipLineWidget line) {
      return this.soloLine == line;
   }

   public void addToLine(int i, ClipWidget clip) {
      ((ClipLineWidget)this.clipLines.get(i)).addClip(clip);
   }

   public ClipArrangementScreen.SnappingResult snap(double doublePosX, double doublePosY, int totalWidth) {
      int x = (int)Math.round((doublePosX - 60.0D) / 2.0D);
      int y = (int)Math.round((doublePosY - 32.0D) / 65.0D);
      if (this.snapButton.isToggled) {
         x -= x % this.ticksPerBeat;
      }

      if (y >= 0 && y < this.clipLines.size() && x >= 0) {
         ClipLineWidget line = (ClipLineWidget)this.clipLines.get(y);
         return line.collides(x, x + totalWidth) ? new ClipArrangementScreen.SnappingResult(0, 0, false) : new ClipArrangementScreen.SnappingResult(x, y, true);
      } else {
         return new ClipArrangementScreen.SnappingResult(0, 0, false);
      }
   }

   public static record SnappingResult(int snapX, int snapY, boolean snapSuccessful) {
      public SnappingResult(int snapX, int snapY, boolean snapSuccessful) {
         this.snapX = snapX;
         this.snapY = snapY;
         this.snapSuccessful = snapSuccessful;
      }

      public int snapX() {
         return this.snapX;
      }

      public int snapY() {
         return this.snapY;
      }

      public boolean snapSuccessful() {
         return this.snapSuccessful;
      }
   }
}
