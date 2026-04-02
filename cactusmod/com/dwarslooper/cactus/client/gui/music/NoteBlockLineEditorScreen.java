package com.dwarslooper.cactus.client.gui.music;

import com.dwarslooper.cactus.client.gui.music.data.PlacedNote;
import com.dwarslooper.cactus.client.gui.music.data.SingleClip;
import com.dwarslooper.cactus.client.gui.music.widget.KeyLineWidget;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_3414;
import net.minecraft.class_4068;
import net.minecraft.class_5250;
import org.lwjgl.glfw.GLFW;

public class NoteBlockLineEditorScreen extends CScreen {
   protected static final int BAR_COLOR = -11576729;
   protected static final int KEYBOARD_BG = -13682624;
   protected static final int DARKER_BG = -15657705;
   protected static final int COLOR_GRAY = -10593194;
   public static int zoomedX = 10;
   public static int zoomedY = 20;
   private final List<KeyLineWidget> keyLines;
   private final String instrument;
   private final class_3414 soundEvent;
   private final SingleClip clipToLoad;
   private Consumer<SingleClip> callback;
   private String clipName;
   private int ticksPerBeat;
   private int totalBeats;
   private int clipProgress;
   private float clipDisplayProgress;
   public double scrollDistanceX;
   public int normalColor;
   public int darkColor;
   public int doubleDarkColor;

   private NoteBlockLineEditorScreen(String instrument, Color color, SingleClip clipToLoad) {
      super("music");
      this.keyLines = new ArrayList();
      this.ticksPerBeat = 8;
      this.totalBeats = 8;
      this.instrument = instrument;
      this.normalColor = color.getRGB();
      this.darkColor = color.darker().getRGB();
      this.doubleDarkColor = color.darker().darker().getRGB();
      this.soundEvent = class_3414.method_47908(class_2960.method_60656("block.note_block." + instrument));
      this.clipToLoad = clipToLoad;
   }

   public NoteBlockLineEditorScreen(String instrument, Color color) {
      this(instrument, color, (SingleClip)null);
   }

   public NoteBlockLineEditorScreen(SingleClip clip, Consumer<SingleClip> callback) {
      this(clip.instrument(), clip.color(), clip);
      this.callback = callback;
      this.ticksPerBeat = clip.ticksPerBeat();
      this.totalBeats = clip.totalBeats();
      this.clipName = clip.name();
   }

   public void method_25419() {
      if (this.callback != null) {
         this.callback.accept(this.createClipData());
      }

      super.method_25419();
   }

   public void method_25426() {
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(24, "F#", 1, true, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(21, "D#", 3, true, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(19, "C#", 4, true, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(16, "A#", 6, true, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(14, "G#", 7, true, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(12, "F#", 8, true, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(9, "D#", 10, true, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(7, "C#", 11, true, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(4, "A#", 13, true, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(2, "G#", 14, true, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(0, "F#", 15, true, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(0, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(23, "F", 1, false, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(22, "E", 2, false, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(20, "D", 3, false, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(18, "C", 4, false, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(17, "B", 5, false, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(15, "A", 6, false, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(13, "G", 7, false, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(11, "F", 8, false, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(10, "E", 9, false, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(8, "D", 10, false, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(6, "C", 11, false, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(5, "B", 12, false, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(3, "A", 13, false, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(1, "G", 14, false, this)));
      this.keyLines.add((KeyLineWidget)this.method_25429(new KeyLineWidget(15, this)));
      if (this.clipToLoad != null) {
         Iterator var1 = this.keyLines.iterator();

         while(var1.hasNext()) {
            KeyLineWidget widget = (KeyLineWidget)var1.next();
            if (this.clipToLoad.lines().containsKey(widget.getPitch())) {
               widget.placeAllNotes((Collection)this.clipToLoad.lines().get(widget.getPitch()));
            }
         }
      }

      this.method_37063(new CButtonWidget(this.field_22789 - 16, 3, 13, 13, class_2561.method_43470("+"), (btn) -> {
         this.changeSize(1);
      }));
      this.method_37063(new CButtonWidget(this.field_22789 - 16, 16, 13, 13, class_2561.method_43470("-"), (btn) -> {
         this.changeSize(-1);
      }));
      this.method_37063(new CButtonWidget(this.field_22789 - 116, 3, 13, 13, class_2561.method_43470("+"), (btn) -> {
         this.changeTicks(1);
      }));
      this.method_37063(new CButtonWidget(this.field_22789 - 116, 16, 13, 13, class_2561.method_43470("-"), (btn) -> {
         this.changeTicks(-1);
      }));
      super.method_25426();
      int amount = 72 - ((KeyLineWidget)this.keyLines.getFirst()).method_46427();
      this.keyLines.forEach((keyLine) -> {
         keyLine.moveY((double)amount);
      });
   }

   public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
      context.method_25294(0, 0, this.field_22789, this.field_22790, -15657705);
      context.method_25294(0, 0, 60, this.field_22790, -13682624);
      context.method_51448().pushMatrix();
      Iterator var5 = this.keyLines.reversed().iterator();

      while(var5.hasNext()) {
         KeyLineWidget keyLine = (KeyLineWidget)var5.next();
         keyLine.method_25394(context, mouseX, mouseY, delta);
      }

      context.method_51448().popMatrix();
      context.method_25294(60 + this.totalBeats * this.ticksPerBeat * zoomedX - (int)this.scrollDistanceX, 0, this.field_22789, this.field_22790, 1711276032);

      int trueX;
      for(trueX = 1; trueX <= this.totalBeats; ++trueX) {
         int trueX = 60 + trueX * this.ticksPerBeat * zoomedX - (int)this.scrollDistanceX;
         if (trueX >= 60) {
            context.method_51742(trueX, 0, this.field_22790, -10593194);
         }
      }

      this.clipDisplayProgress += delta;
      trueX = (int)(60.0F + this.clipDisplayProgress / 2.0F * (float)zoomedX) - (int)this.scrollDistanceX;
      if (trueX >= 60) {
         context.method_51742(trueX, 0, this.field_22790, -395538);
      }

      context.method_25294(0, 0, this.field_22789, 32, -11576729);
      Iterator var10 = this.field_33816.iterator();

      while(var10.hasNext()) {
         class_4068 drawable = (class_4068)var10.next();
         drawable.method_25394(context, mouseX, mouseY, delta);
      }

      class_327 var10001 = CactusConstants.mc.field_1772;
      class_5250 var10002 = class_2561.method_43470(this.totalBeats + " beats");
      int var10003 = this.field_22789 - 80;
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      context.method_51439(var10001, var10002, var10003, 16 - 9 / 2, -395538, true);
      var10001 = CactusConstants.mc.field_1772;
      var10002 = class_2561.method_43470(this.ticksPerBeat + " ticks/beat");
      var10003 = this.field_22789 - 200;
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      context.method_51439(var10001, var10002, var10003, 16 - 9 / 2, -395538, true);
   }

   public void method_25393() {
      ++this.clipProgress;
      if (this.clipProgress >= this.getClipLength() * 2) {
         this.clipProgress = 0;
      }

      if (Math.abs(this.clipDisplayProgress - (float)this.clipProgress) > 0.5F) {
         this.clipDisplayProgress = (float)this.clipProgress;
      }

      if (this.clipProgress % 2 == 0) {
         Iterator var1 = this.keyLines.iterator();

         while(var1.hasNext()) {
            KeyLineWidget keyLine = (KeyLineWidget)var1.next();
            keyLine.playAt(this.clipProgress / 2);
         }
      }

   }

   public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (this.isControlDown() && horizontalAmount == 0.0D) {
         horizontalAmount = verticalAmount;
         verticalAmount = 0.0D;
      }

      double amount;
      if (verticalAmount > 0.0D) {
         if (((KeyLineWidget)this.keyLines.getFirst()).method_46427() >= 72) {
            return false;
         } else {
            amount = Math.min(verticalAmount, (double)(72 - ((KeyLineWidget)this.keyLines.getFirst()).method_46427()));
            this.keyLines.forEach((keyLine) -> {
               keyLine.moveY(amount);
            });
            return true;
         }
      } else if (verticalAmount < 0.0D) {
         if (((KeyLineWidget)this.keyLines.getLast()).method_46427() <= this.field_22790 - 40) {
            return false;
         } else {
            amount = Math.max(verticalAmount, (double)(this.field_22790 - 40 - ((KeyLineWidget)this.keyLines.getLast()).method_46427()));
            this.keyLines.forEach((keyLine) -> {
               keyLine.moveY(amount);
            });
            return true;
         }
      } else if (horizontalAmount != 0.0D) {
         this.scrollDistanceX += horizontalAmount;
         if (this.scrollDistanceX < 0.0D) {
            this.scrollDistanceX = 0.0D;
         }

         int maxScrollX = (this.totalBeats * this.ticksPerBeat + 3) * zoomedX + 60 - this.field_22789;
         if (maxScrollX < 0) {
            maxScrollX = 0;
         }

         if (this.scrollDistanceX > (double)maxScrollX) {
            this.scrollDistanceX = (double)maxScrollX;
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean isControlDown() {
      long handle = CactusConstants.mc.method_22683().method_4490();
      return GLFW.glfwGetKey(handle, 341) == 1 || GLFW.glfwGetKey(handle, 345) == 1;
   }

   public SingleClip createClipData() {
      Map<Integer, List<PlacedNote>> notes = new HashMap();
      Iterator var2 = this.keyLines.iterator();

      while(var2.hasNext()) {
         KeyLineWidget widget = (KeyLineWidget)var2.next();
         if (!widget.getNotes().isEmpty()) {
            notes.put(widget.getPitch(), widget.getNotes());
         }
      }

      return new SingleClip(this.clipName, new AtomicReference(), this.totalBeats * this.ticksPerBeat, this.instrument, this.ticksPerBeat, this.totalBeats, notes, new Color(this.normalColor), new AtomicReference());
   }

   public void changeSize(int diff) {
      this.totalBeats += diff;
      if (diff < 0) {
         Iterator var2 = this.keyLines.iterator();

         while(var2.hasNext()) {
            KeyLineWidget keyLine = (KeyLineWidget)var2.next();
            keyLine.cutAt(this.getClipLength());
         }
      }

   }

   public void changeTicks(int diff) {
      this.ticksPerBeat += diff;
      if (diff < 0) {
         Iterator var2 = this.keyLines.iterator();

         while(var2.hasNext()) {
            KeyLineWidget keyLine = (KeyLineWidget)var2.next();
            keyLine.cutAt(this.getClipLength());
         }
      }

   }

   public void playSound(int pitch) {
      float truePitch = (float)Math.pow(2.0D, (double)(pitch - 12) / 12.0D);
      class_1113 soundInstance = class_1109.method_4757(this.soundEvent, truePitch, 2.0F);
      CactusConstants.mc.method_1483().method_4873(soundInstance);
   }

   public int getClipLength() {
      return this.totalBeats * this.ticksPerBeat;
   }
}
