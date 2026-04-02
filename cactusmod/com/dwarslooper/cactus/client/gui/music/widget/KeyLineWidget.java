package com.dwarslooper.cactus.client.gui.music.widget;

import com.dwarslooper.cactus.client.gui.music.NoteBlockLineEditorScreen;
import com.dwarslooper.cactus.client.gui.music.data.PlacedNote;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_6382;
import org.jetbrains.annotations.NotNull;

public class KeyLineWidget extends class_339 {
   public static final int COLOR_WHITE = -395538;
   public static final int COLOR_WHITE_DARKER = -7040630;
   public static final int COLOR_BLACK = -15003118;
   private static final int LIGHTER_BG = -15065052;
   private static final int DARKER_BG = -15327452;
   public static final int KEY_WIDTH = 60;
   private double doubleY;
   private final int whiteY;
   private final int pitch;
   private final boolean black;
   private final NoteBlockLineEditorScreen parent;
   private final List<PlacedNote> notes = new ArrayList();

   public KeyLineWidget(int pitch, String key, int y, boolean black, NoteBlockLineEditorScreen parent) {
      super(0, y * (NoteBlockLineEditorScreen.zoomedY + 1), parent.field_22789, NoteBlockLineEditorScreen.zoomedY, class_2561.method_43470(key));
      this.whiteY = y;
      this.pitch = pitch;
      this.black = black;
      this.parent = parent;
      this.doubleY = (double)this.method_46427();
   }

   public KeyLineWidget(int y, NoteBlockLineEditorScreen parent) {
      super(0, y * (NoteBlockLineEditorScreen.zoomedY + 1), parent.field_22789, NoteBlockLineEditorScreen.zoomedY, class_2561.method_43473());
      this.whiteY = y;
      this.pitch = -1;
      this.black = false;
      this.parent = parent;
      this.doubleY = (double)this.method_46427();
   }

   protected void method_48579(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      if (this.pitch == -1) {
         context.method_25294(0, this.method_46427(), 60, this.method_46427() + NoteBlockLineEditorScreen.zoomedY, -7040630);
      } else {
         if (!this.black) {
            if (this.whiteY % 2 == 0) {
               context.method_25294(0, this.method_46427(), this.field_22758, this.method_46427() + this.field_22759, -15065052);
            } else {
               context.method_25294(0, this.method_46427(), this.field_22758, this.method_46427() + this.field_22759, -15327452);
            }
         }

         this.renderNotes(context);
         class_327 var10001;
         class_2561 var10002;
         int var10004;
         if (this.black) {
            int blackOff = NoteBlockLineEditorScreen.zoomedY / 3;
            context.method_25294(30, this.method_46427() - blackOff - 1, 60, this.method_46427() + blackOff, -15003118);
            var10001 = CactusConstants.mc.field_1772;
            var10002 = this.method_25369();
            var10004 = this.method_46427();
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            context.method_51439(var10001, var10002, 42, var10004 - 9 / 2, -395538, false);
         } else {
            context.method_25294(0, this.method_46427(), 60, this.method_46427() + NoteBlockLineEditorScreen.zoomedY, -395538);
            var10001 = CactusConstants.mc.field_1772;
            var10002 = this.method_25369();
            var10004 = this.method_46427() + NoteBlockLineEditorScreen.zoomedY / 2;
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            context.method_51439(var10001, var10002, 13, var10004 - 9 / 2, -15003118, false);
         }

      }
   }

   private void renderNotes(class_332 ctx) {
      int y1 = this.method_46427();
      int y2 = this.method_46427() + NoteBlockLineEditorScreen.zoomedY;
      if (this.black) {
         y1 -= NoteBlockLineEditorScreen.zoomedY / 3 - 1;
         y2 = this.method_46427() + NoteBlockLineEditorScreen.zoomedY / 3;
      }

      int bg = this.black ? this.parent.doubleDarkColor : this.parent.darkColor;
      int fg = this.black ? this.parent.darkColor : this.parent.normalColor;
      Iterator var6 = this.notes.iterator();

      while(var6.hasNext()) {
         PlacedNote note = (PlacedNote)var6.next();
         int x1 = 60 + note.timestamp() * NoteBlockLineEditorScreen.zoomedX - (int)this.parent.scrollDistanceX;
         int x2 = x1 + NoteBlockLineEditorScreen.zoomedX;
         if (x2 > 60) {
            ctx.method_25294(x1, y1, x2, y2, bg);
            ctx.method_25294(x1 + 1, y1 + 1, x2 - 1, y2 - 1, fg);
         }
      }

   }

   public boolean method_25402(class_11909 click, boolean doubled) {
      double mouseX = click.comp_4798();
      double mouseY = click.comp_4799();
      int button = click.comp_4800().comp_4801();
      if (this.pitch == -1) {
         return false;
      } else {
         int blackOff = NoteBlockLineEditorScreen.zoomedY / 3;
         if (mouseX >= (double)(this.black ? 30 : 0) && mouseX < 60.0D && mouseY > (double)(this.black ? this.method_46427() - blackOff : this.method_46427()) && mouseY < (double)(this.method_46427() + (this.black ? blackOff : NoteBlockLineEditorScreen.zoomedY))) {
            this.parent.playSound(this.pitch);
            return true;
         } else {
            int y1 = this.method_46427();
            int y2 = this.method_46427() + NoteBlockLineEditorScreen.zoomedY;
            if (this.black) {
               y1 -= NoteBlockLineEditorScreen.zoomedY / (button == 1 ? 3 : 4) - 1;
               y2 = this.method_46427() + NoteBlockLineEditorScreen.zoomedY / (button == 1 ? 3 : 4);
            }

            if (!(mouseY < (double)y1) && !(mouseY >= (double)y2)) {
               mouseX += (double)((int)this.parent.scrollDistanceX);
               PlacedNote atPos = null;
               Iterator var12 = this.notes.iterator();

               while(var12.hasNext()) {
                  PlacedNote note = (PlacedNote)var12.next();
                  int x1 = 60 + note.timestamp() * NoteBlockLineEditorScreen.zoomedX;
                  int x2 = x1 + NoteBlockLineEditorScreen.zoomedX;
                  if (mouseX > (double)x1 && mouseX < (double)x2) {
                     atPos = note;
                     break;
                  }
               }

               switch(button) {
               case 0:
                  if (atPos != null) {
                     return false;
                  } else {
                     int timestamp = (int)((mouseX - 60.0D) / (double)NoteBlockLineEditorScreen.zoomedX);
                     if (timestamp >= this.parent.getClipLength()) {
                        return false;
                     }

                     this.notes.add(new PlacedNote(timestamp));
                     this.parent.playSound(this.pitch);
                     return true;
                  }
               case 1:
                  if (atPos != null) {
                     this.notes.remove(atPos);
                     return true;
                  }

                  return true;
               default:
                  return false;
               }
            } else {
               return false;
            }
         }
      }
   }

   protected void method_47399(@NotNull class_6382 builder) {
   }

   public void placeAllNotes(Collection<PlacedNote> notes) {
      this.notes.addAll(notes);
   }

   public void playAt(int pos) {
      Iterator var2 = this.notes.iterator();

      PlacedNote note;
      do {
         if (!var2.hasNext()) {
            return;
         }

         note = (PlacedNote)var2.next();
      } while(note.timestamp() != pos);

      this.parent.playSound(this.pitch);
   }

   public void cutAt(int pos) {
      Iterator var2 = this.notes.stream().toList().iterator();

      while(var2.hasNext()) {
         PlacedNote note = (PlacedNote)var2.next();
         if (note.timestamp() >= pos) {
            this.notes.remove(note);
         }
      }

   }

   public boolean isBlack() {
      return this.black;
   }

   public int getPitch() {
      return this.pitch;
   }

   public List<PlacedNote> getNotes() {
      return this.notes;
   }

   public void moveY(double amount) {
      this.doubleY += amount;
      this.method_46419((int)this.doubleY);
   }
}
