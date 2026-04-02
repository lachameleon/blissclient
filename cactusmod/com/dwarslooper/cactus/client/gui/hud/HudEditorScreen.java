package com.dwarslooper.cactus.client.gui.hud;

import com.dwarslooper.cactus.client.gui.hud.element.DynamicHudElement;
import com.dwarslooper.cactus.client.gui.hud.element.impl.HudElement;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import com.dwarslooper.cactus.client.util.generic.WidgetIcons;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_10799;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_3532;
import net.minecraft.class_4068;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector2i;

public class HudEditorScreen extends CScreen {
   private static final class_2960 HOTBAR_TEXTURE = class_2960.method_60656("hud/hotbar");
   private static final int SNAP_INDICATOR_COLOR = -2011521996;
   private static final int SELECTION_COLOR = -1;
   private static final class_2960 ANCHOR = class_2960.method_60655("cactus", "textures/gui/hud/anchor.png");
   private final HudManager hudManager = HudManager.getInstance();
   private final HudManager.Settings settings;
   private class_339 removeButton;
   private HudElement<?> selected;
   private Vector2d movementDelta = new Vector2d();
   private boolean unlockDrag;
   private boolean isScaling;
   private double closestSnapX;
   private double closestSnapY;
   private Integer snapLineX;
   private Integer snapLineY;
   private HudElement<?> snapElementX;
   private HudElement<?> snapElementY;

   public HudEditorScreen() {
      super("hudEditor");
      this.settings = this.hudManager.getSettings();
   }

   public void method_25426() {
      super.method_25426();
      this.selected = null;
      this.method_37063(new CTextureButtonWidget(30, 6, WidgetIcons.SETTINGS.offsetX(), (button) -> {
         CactusConstants.mc.method_1507(new HudSettingsScreen(this.hudManager));
      }));
      this.method_37063(new CButtonWidget(54, 6, 20, 20, class_2561.method_43470("+").method_27695(new class_124[]{class_124.field_1067, class_124.field_1060}), (button) -> {
         CactusConstants.mc.method_1507(new AddHudElementScreen());
      }));
      this.method_37063(this.removeButton = new CTextureButtonWidget(78, 6, WidgetIcons.DELETE.offsetX(), (button) -> {
         if (this.selected != null) {
            this.hudManager.remove(this.selected);
            this.selected = null;
         }

         this.update();
      }));
      this.update();
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      if (CactusConstants.mc.field_1687 == null) {
         context.method_52706(class_10799.field_56883, HOTBAR_TEXTURE, this.field_22789 / 2 - 91, this.field_22790 - 22, 182, 22);
      }

      RenderUtils.drawTextAlignedRight(context, (String)this.getTranslatableElement("settingsUsage", new Object[0]), this.field_22789 - 4, 4, -1, true);
      Iterator var5 = this.field_33816.iterator();

      while(var5.hasNext()) {
         class_4068 drawable = (class_4068)var5.next();
         drawable.method_25394(context, mouseX, mouseY, delta);
      }

      if (this.selected != null && (Boolean)this.settings.highlightAnchorRegion.get()) {
         Vector2i absolute = this.selected.getAbsolutePosition(this.field_22789, this.field_22790);
         float wUnit = (float)this.field_22789 / 3.0F;
         float hUnit = (float)this.field_22790 / 3.0F;
         int screenX = (int)((float)((int)Math.min(((float)absolute.x() + (float)this.selected.getSize().x() / 2.0F) / wUnit, 2.0F)) * wUnit);
         int screenY = (int)((float)((int)Math.min(((float)absolute.y() + (float)this.selected.getSize().y() / 2.0F) / hUnit, 2.0F)) * hUnit);
         context.method_25294(screenX, screenY, (int)((double)screenX + Math.ceil((double)((float)this.field_22789 / 3.0F))), (int)((double)screenY + Math.ceil((double)((float)this.field_22790 / 3.0F))), 1140850688);
      }

      this.hudManager.forEach((ahe) -> {
         Vector2i absolute = ahe.getAbsolutePosition(CactusConstants.mc.method_22683().method_4486(), CactusConstants.mc.method_22683().method_4502());
         Vector2i size = ahe.getSize();
         int x = absolute.x();
         int y = absolute.y();
         int width = size.x();
         int height = size.y();
         ahe.render(context, x, y, this.field_22789, this.field_22790, delta, true);
         if (this.selected == ahe) {
            context.method_73198(x, y, width, height, -1);
            if (ahe.canResize()) {
               context.method_73198(x + width - 8, y + height - 8, 8, 8, -1);
            }

            if (!(Boolean)ahe.autoAnchor.get()) {
               Anchor[] var10 = Anchor.values();
               int var11 = var10.length;

               for(int var12 = 0; var12 < var11; ++var12) {
                  Anchor value = var10[var12];
                  Vector2i anchorPos = value.getElementPosition(absolute, size);
                  context.method_25290(class_10799.field_56883, ANCHOR, anchorPos.x(), anchorPos.y(), (float)(value == ahe.getAnchor() ? 0 : 10), 0.0F, 10, 10, 20, 10);
               }
            }
         }

      });
      if (this.selected != null && (this.snapLineX != null || this.snapLineY != null)) {
         if (this.snapLineX != null) {
            context.method_51742(this.snapLineX, 0, this.field_22790, -2011521996);
         }

         if (this.snapLineY != null) {
            context.method_51738(0, this.field_22789, this.snapLineY, -2011521996);
         }
      }

      this.update();
   }

   public boolean method_25402(class_11909 click, boolean doubled) {
      double mouseX = click.comp_4798();
      double mouseY = click.comp_4799();
      int button = click.method_74245();
      boolean b = super.method_25402(click, doubled);
      this.unlockDrag = false;
      if (!b) {
         this.resetSnap();
         Iterator var9 = this.hudManager.iterator();

         while(var9.hasNext()) {
            HudElement<?> e = (HudElement)var9.next();
            Vector2i ePos = e.getAbsolutePosition(CactusConstants.mc.method_22683().method_4486(), CactusConstants.mc.method_22683().method_4502());
            Vector2i eSize = e.getSize();
            if (e == this.selected && !(Boolean)e.autoAnchor.get()) {
               Anchor[] var13 = Anchor.values();
               int var14 = var13.length;

               for(int var15 = 0; var15 < var14; ++var15) {
                  Anchor value = var13[var15];
                  Vector2i anchorPos = value.getElementPosition(ePos, eSize);
                  if (mouseX >= (double)anchorPos.x() && mouseX < (double)(anchorPos.x() + 10) && mouseY >= (double)anchorPos.y() && mouseY < (double)(anchorPos.y() + 10)) {
                     e.setAnchor(value);
                     e.fromAbsolute(ePos, this.field_22789, this.field_22790);
                     return true;
                  }
               }
            }

            if (mouseX >= (double)ePos.x && mouseX < (double)(ePos.x + eSize.x) && mouseY >= (double)ePos.y && mouseY < (double)(ePos.y + eSize.y)) {
               this.unlockDrag = true;
               this.selected = e;
               this.update();
               if (button == 0) {
                  if (e.canResize() && mouseX > (double)(ePos.x + eSize.x - 10) && mouseY > (double)(ePos.y + eSize.y - 10)) {
                     this.isScaling = true;
                     this.movementDelta = new Vector2d((double)eSize.x, (double)eSize.y);
                  } else {
                     this.movementDelta = new Vector2d((double)ePos.x, (double)ePos.y);
                  }
               } else if (button == 1) {
                  CactusConstants.mc.method_1507(new ElementSettingsScreen(e));
               }

               return true;
            }
         }
      }

      this.selected = null;
      return b;
   }

   public boolean method_25406(@NotNull class_11909 click) {
      this.unlockDrag = false;
      this.isScaling = false;
      this.resetSnap();
      boolean b = super.method_25406(click);
      if (!b) {
         this.movementDelta = new Vector2d();
      }

      return b;
   }

   public boolean method_25403(@NotNull class_11909 click, double deltaX, double deltaY) {
      boolean b = super.method_25403(click, deltaX, deltaY);
      if (!b && this.unlockDrag && this.selected != null) {
         Vector2i absolute = this.selected.getAbsolutePosition(this.field_22789, this.field_22790);
         Vector2i size = this.selected.getSize();
         this.movementDelta.add(deltaX, deltaY);
         Vector2i mtbDelta = (new Vector2i()).set(this.movementDelta);
         this.resetSnap();
         boolean snap = (Boolean)this.settings.snap.get() && !click.method_74240();
         this.closestSnapX = this.closestSnapY = (double)(this.getSnapThreshold() + 1);
         if (this.isScaling) {
            mtbDelta.max(this.selected.getMinSize().min(new Vector2i(this.field_22789, this.field_22790)));
            this.correctElementOoB(absolute, size, mtbDelta, true);
            if (snap) {
               this.findResizeSnapPositions(absolute, mtbDelta);
            }

            this.moveAndUpdate(absolute, this.selected.getSize());
            this.selected.resize(mtbDelta);
         } else {
            this.correctElementOoB(absolute, size, mtbDelta, false);
            if (snap) {
               this.findSnapPositions(mtbDelta, size);
            }

            this.moveAndUpdate(mtbDelta, size);
         }
      }

      return b;
   }

   public boolean method_25404(class_11908 input) {
      int keyCode = input.comp_4795();
      if (this.selected != null && (Boolean)this.settings.keyMove.get()) {
         boolean move = !input.method_74239();
         Vector2i delta = new Vector2i();
         switch(keyCode) {
         case 261:
            this.hudManager.remove(this.selected);
            this.selected = null;
            break;
         case 262:
            delta.add(1, 0);
            break;
         case 263:
            delta.sub(1, 0);
            break;
         case 264:
            delta.add(0, 1);
            break;
         case 265:
            delta.sub(0, 1);
         }

         if (delta.lengthSquared() != 0L && (move || this.selected.canResize())) {
            Vector2i absolute = this.selected.getAbsolutePosition(this.field_22789, this.field_22790);
            delta.add(move ? absolute : this.selected.getSize());
            this.correctElementOoB(absolute, this.selected.getSize(), delta, !move);
            if (move) {
               this.moveAndUpdate(delta, this.selected.getSize());
            } else {
               this.moveAndUpdate(absolute, delta);
               this.selected.resize(delta);
            }

            return true;
         }
      }

      return super.method_25404(input);
   }

   private void moveAndUpdate(Vector2i pos, Vector2i size) {
      if (this.selected != null) {
         if ((Boolean)this.selected.autoAnchor.get()) {
            try {
               Anchor anchor = this.findAnchor(pos, size);
               if (this.selected.getAnchor() != anchor) {
                  this.selected.setAnchor(anchor);
               }
            } catch (IllegalArgumentException var4) {
               size.set(this.field_22789 - pos.x(), this.field_22790 - pos.y());
            }
         }

         this.selected.fromAbsolute(pos, this.field_22789, this.field_22790);
      }

   }

   private void update() {
      this.removeButton.field_22763 = this.selected != null;
   }

   private int getSnapThreshold() {
      return (Integer)this.settings.snapDistance.get();
   }

   private void resetSnap() {
      this.snapLineX = null;
      this.snapLineY = null;
      this.snapElementX = null;
      this.snapElementY = null;
   }

   private void correctElementOoB(Vector2i position, Vector2i size, Vector2i delta, boolean isScaling) {
      delta.set(class_3532.method_15340(delta.x(), 0, isScaling ? this.field_22789 - position.x() : this.field_22789 - size.x()), class_3532.method_15340(delta.y(), 0, isScaling ? this.field_22790 - position.y() : this.field_22790 - size.y()));
   }

   private void findResizeSnapPositions(Vector2i position, Vector2i size) {
      if (this.selected != null) {
         int selectedLeft = position.x();
         int selectedRight = selectedLeft + size.x();
         int selectedTop = position.y();
         int selectedBottom = selectedTop + size.y();
         Iterator var7 = this.getSortedElements().iterator();

         while(var7.hasNext()) {
            HudElement<?> other = (HudElement)var7.next();
            if (other != this.selected) {
               Vector2i otherPos = other.getAbsolutePosition(this.field_22789, this.field_22790);
               Vector2i otherSize = other.getSize();
               int otherLeft = otherPos.x();
               int otherRight = otherLeft + otherSize.x();
               int otherTop = otherPos.y();
               int otherBottom = otherTop + otherSize.y();
               this.checkAndUpdateEdgeSnap(selectedRight, otherLeft, size, true, selectedLeft, other);
               this.checkAndUpdateEdgeSnap(selectedRight, otherRight, size, true, selectedLeft, other);
               this.checkAndUpdateEdgeSnap(selectedBottom, otherTop, size, false, selectedTop, other);
               this.checkAndUpdateEdgeSnap(selectedBottom, otherBottom, size, false, selectedTop, other);
            }
         }

         this.snapResizeToWindowEdges(position, size);
      }
   }

   private void snapResizeToWindowEdges(Vector2i position, Vector2i size) {
      int rightEdgeDist = Math.abs(position.x() + size.x() - this.field_22789);
      if (rightEdgeDist <= this.getSnapThreshold()) {
         size.x = this.field_22789 - position.x();
         this.snapLineX = this.field_22789;
      }

      int bottomEdgeDist = Math.abs(position.y() + size.y() - this.field_22790);
      if (bottomEdgeDist <= this.getSnapThreshold()) {
         size.y = this.field_22790 - position.y();
         this.snapLineY = this.field_22790;
      }

   }

   private void findSnapPositions(Vector2i pos, Vector2i size) {
      if (this.selected != null) {
         this.closestSnapX = (double)(this.getSnapThreshold() + 1);
         this.closestSnapY = (double)(this.getSnapThreshold() + 1);
         int selectedLeft = pos.x();
         int selectedRight = selectedLeft + size.x();
         int selectedTop = pos.y();
         int selectedBottom = selectedTop + size.y();
         int selectedCenterX = selectedLeft + size.x() / 2;
         int selectedCenterY = selectedTop + size.y() / 2;
         Iterator var9 = this.getSortedElements().iterator();

         while(var9.hasNext()) {
            HudElement<?> other = (HudElement)var9.next();
            if (other != this.selected) {
               Vector2i otherPos = other.getAbsolutePosition(this.field_22789, this.field_22790);
               Vector2i otherSize = other.getSize();
               int otherLeft = otherPos.x();
               int otherRight = otherLeft + otherSize.x();
               int otherTop = otherPos.y();
               int otherBottom = otherTop + otherSize.y();
               int otherCenterX = otherLeft + otherSize.x() / 2;
               int otherCenterY = otherTop + otherSize.y() / 2;
               this.checkAndUpdateEdgeSnap(selectedRight, otherRight, pos, true, size.x(), other);
               this.checkAndUpdateEdgeSnap(selectedRight, otherLeft, pos, true, size.x(), other);
               this.checkAndUpdateEdgeSnap(selectedCenterX, otherCenterX, pos, true, size.x() / 2, other);
               this.checkAndUpdateEdgeSnap(selectedLeft, otherLeft, pos, true, 0, other);
               this.checkAndUpdateEdgeSnap(selectedLeft, otherRight, pos, true, 0, other);
               this.checkAndUpdateEdgeSnap(selectedBottom, otherBottom, pos, false, size.y(), other);
               this.checkAndUpdateEdgeSnap(selectedBottom, otherTop, pos, false, size.y(), other);
               this.checkAndUpdateEdgeSnap(selectedCenterY, otherCenterY, pos, false, size.y() / 2, other);
               this.checkAndUpdateEdgeSnap(selectedTop, otherTop, pos, false, 0, other);
               this.checkAndUpdateEdgeSnap(selectedTop, otherBottom, pos, false, 0, other);
            }
         }

         this.snapToWindowEdges(pos, size);
      }
   }

   private List<HudElement<?>> getSortedElements() {
      List<HudElement<?>> elementList = new ArrayList(this.hudManager.getElements());
      Vector2i selPos = this.selected.getAbsolutePosition(this.field_22789, this.field_22790);
      int x3 = selPos.x + this.selected.getSize().x / 2;
      int y3 = selPos.y + this.selected.getSize().y / 2;
      elementList.sort((el1, el2) -> {
         Vector2i el1Pos = el1.getAbsolutePosition(this.field_22789, this.field_22790);
         Vector2i el2Pos = el2.getAbsolutePosition(this.field_22789, this.field_22790);
         int x1 = el1Pos.x + el1.getSize().x / 2;
         int y1 = el1Pos.y + el1.getSize().y / 2;
         int x2 = el2Pos.x + el2.getSize().x / 2;
         int y2 = el2Pos.y + el2.getSize().y / 2;
         double val1 = Math.pow((double)Math.abs(x1 - x3), 2.0D) + Math.pow((double)Math.abs(y1 - y3), 2.0D);
         double val2 = Math.pow((double)Math.abs(x2 - x3), 2.0D) + Math.pow((double)Math.abs(y2 - y3), 2.0D);
         return Double.compare(val1, val2);
      });
      elementList.add(createSnapElement(new Vector2i(5, 5), new Vector2i(this.field_22789 - 10, this.field_22790 - 10)));
      elementList.add(createSnapElement(new Vector2i(this.field_22789 / 2 - 91, this.field_22790 - 22), new Vector2i(182, 22)));
      return elementList;
   }

   private void checkAndUpdateEdgeSnap(int selectedEdge, int otherEdge, Vector2i pos, boolean isXAxis, int offset, HudElement<?> other) {
      int dist = Math.abs(selectedEdge - otherEdge);
      if (dist <= this.getSnapThreshold()) {
         if (isXAxis) {
            if ((double)dist < this.closestSnapX) {
               this.closestSnapX = (double)dist;
               pos.x = otherEdge - offset;
               this.snapLineX = otherEdge;
               this.snapElementX = other;
            }
         } else if ((double)dist < this.closestSnapY) {
            this.closestSnapY = (double)dist;
            pos.y = otherEdge - offset;
            this.snapLineY = otherEdge;
            this.snapElementY = other;
         }

      }
   }

   private void snapToWindowEdges(Vector2i pos, Vector2i size) {
      int snapDistance = this.getSnapThreshold();
      double minX = Math.min(this.closestSnapX, (double)snapDistance);
      double minY = Math.min(this.closestSnapY, (double)snapDistance);
      if ((double)Math.abs(pos.x()) < minX) {
         pos.x = 0;
         this.snapLineX = 0;
      }

      int rightEdge = this.field_22789 - size.x();
      if ((double)Math.abs(pos.x() - rightEdge) < minX) {
         pos.x = rightEdge;
         this.snapLineX = this.field_22789;
      }

      if ((double)Math.abs(pos.y()) < minY) {
         pos.y = 0;
         this.snapLineY = 0;
      }

      int bottomEdge = this.field_22790 - size.y();
      if ((double)Math.abs(pos.y() - bottomEdge) < minY) {
         pos.y = bottomEdge;
         this.snapLineY = this.field_22790;
      }

      int centerX = this.field_22789 / 2 - size.x() / 2;
      if ((double)Math.abs(pos.x() - centerX) < minX) {
         pos.x = centerX;
         this.snapLineX = this.field_22789 / 2;
      }

      int centerY = this.field_22790 / 2 - size.y() / 2;
      if ((double)Math.abs(pos.y() - centerY) < minY) {
         pos.y = centerY;
         this.snapLineY = this.field_22790 / 2;
      }

   }

   public Anchor findAnchor(Vector2i pos, Vector2i size) {
      return this.findAnchor((new Vector2i(pos)).add((new Vector2i(size)).div(2)), this.field_22789, this.field_22790);
   }

   public Anchor findAnchor(Vector2i absolutePos, int width, int height) {
      double cellWidth = (double)width / 3.0D;
      double cellHeight = (double)height / 3.0D;
      int row = (int)Math.min((double)absolutePos.y() / cellHeight, 2.0D);
      int column = (int)Math.min((double)absolutePos.x() / cellWidth, 2.0D);
      Anchor anchor = Anchor.find(row, column);
      if (anchor == null) {
         throw new IllegalArgumentException("Unexpected position for anchor calculation (row=%s column=%s)".formatted(new Object[]{row, column}));
      } else {
         return anchor;
      }
   }

   private static HudElement<?> createSnapElement(Vector2i pos, Vector2i size) {
      return new HudEditorScreen.SnapElement(pos, size);
   }

   private static class SnapElement extends DynamicHudElement<HudEditorScreen.SnapElement> {
      public SnapElement(Vector2i pos, Vector2i size) {
         super("", size);
         this.setAnchor(Anchor.LEFT_UP);
         this.move(pos.x(), pos.y());
      }

      public HudEditorScreen.SnapElement duplicate() {
         return null;
      }

      public void renderContent(class_332 context, int x, int y, int width, int height, int screenWidth, int screenHeight, float delta, boolean inEditor) {
      }
   }
}
