package com.dwarslooper.cactus.client.gui.music.widget;

import com.dwarslooper.cactus.client.gui.music.ClipArrangementScreen;
import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.CToggleButtonWidget;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_6382;
import org.jetbrains.annotations.NotNull;

public class ClipLineWidget extends class_339 {
   private static final int LIGHTER_BG = -15065052;
   private static final int DARKER_BG = -15327452;
   private final ClipArrangementScreen parent;
   private int gridY;
   private final CTextureButtonWidget deleteButton;
   private final CToggleButtonWidget soloButton;
   private final CToggleButtonWidget muteButton;
   private final List<ClipWidget> clips = new ArrayList();

   public ClipLineWidget(ClipArrangementScreen parent, int gridY) {
      super(0, 32 + gridY * 65, parent.field_22789, 65, class_2561.method_43473());
      this.parent = parent;
      this.gridY = gridY;
      this.deleteButton = new CTextureButtonWidget(3, 42, 200, (button) -> {
         parent.deleteLine(this);
      });
      this.soloButton = new CToggleButtonWidget(27, 47, 15, 15, class_2561.method_43470("S"), (soloed) -> {
         parent.solo(soloed ? this : null);
      });
      this.muteButton = new CToggleButtonWidget(42, 47, 15, 15, class_2561.method_43470("M"), (muted) -> {
      });
   }

   protected void method_48579(class_332 ctx, int mouseX, int mouseY, float delta) {
      ctx.method_25294(60, this.method_46427(), this.parent.field_22789, this.method_46427() + 64, this.gridY % 2 == 0 ? -15327452 : -15065052);
      this.deleteButton.method_48229(this.method_46426() + 3, this.method_46427() + 42);
      this.soloButton.method_48229(this.method_46426() + 27, this.method_46427() + 47);
      this.muteButton.method_48229(this.method_46426() + 42, this.method_46427() + 47);
      this.deleteButton.method_25394(ctx, mouseX, mouseY, delta);
      this.soloButton.method_25394(ctx, mouseX, mouseY, delta);
      this.muteButton.method_25394(ctx, mouseX, mouseY, delta);
   }

   public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
      boolean handled = this.deleteButton.method_25402(click, doubled) || this.soloButton.method_25402(click, doubled) || this.muteButton.method_25402(click, doubled);
      return handled || super.method_25402(click, doubled);
   }

   public void addClip(ClipWidget clip) {
      clip.setParent(this);
      this.clips.add(clip);
      clip.method_46419(32 + this.gridY * 65);
   }

   public void removeClip(ClipWidget clipWidget) {
      this.clips.remove(clipWidget);
   }

   public List<ClipWidget> getClips() {
      return this.clips;
   }

   public boolean collides(int x1, int x2) {
      Iterator var3 = this.clips.iterator();

      ClipWidget clip;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         clip = (ClipWidget)var3.next();
      } while(!clip.collides(x1, x2));

      return true;
   }

   public int getGridY() {
      return this.gridY;
   }

   public void setGridY(int gridY) {
      this.gridY = gridY;
      this.method_46419(32 + gridY * 65);
   }

   public int getMaxPos() {
      int maxPos = 0;

      ClipWidget clip;
      for(Iterator var2 = this.clips.iterator(); var2.hasNext(); maxPos = Math.max(maxPos, clip.getClip().offX() + clip.getClip().ref().totalWidth())) {
         clip = (ClipWidget)var2.next();
      }

      return maxPos;
   }

   public void setSolo(ClipLineWidget line) {
      this.soloButton.isToggled = line == this;
   }

   protected void method_47399(@NotNull class_6382 builder) {
   }
}
