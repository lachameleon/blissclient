package com.dwarslooper.cactus.client.gui.widget;

import com.dwarslooper.cactus.client.gui.screen.window.IntegratedWindowScreen;
import com.dwarslooper.cactus.client.gui.toast.internal.GenericToast;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.class_10799;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_11910;
import net.minecraft.class_124;
import net.minecraft.class_2477;
import net.minecraft.class_2479;
import net.minecraft.class_2481;
import net.minecraft.class_2487;
import net.minecraft.class_2489;
import net.minecraft.class_2494;
import net.minecraft.class_2495;
import net.minecraft.class_2497;
import net.minecraft.class_2499;
import net.minecraft.class_2501;
import net.minecraft.class_2503;
import net.minecraft.class_2516;
import net.minecraft.class_2519;
import net.minecraft.class_2520;
import net.minecraft.class_2522;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_342;
import net.minecraft.class_364;
import net.minecraft.class_437;
import net.minecraft.class_5244;
import net.minecraft.class_6382;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class CVisualNbtEditorWidget extends class_339 {
   private static final class_2960 iconSheet = class_2960.method_60655("cactus", "textures/gui/nbt.png");
   private static CVisualNbtEditorWidget.NbtWidget clipboard;
   private static class_437 screenInstance;
   private final CVisualNbtEditorWidget.NbtCompoundWidget nbt = new CVisualNbtEditorWidget.NbtCompoundWidget(this, (CVisualNbtEditorWidget.NbtWidget)null);
   private final Function<class_2487, Throwable> save;
   private List<class_2561> saveTooltipLines;
   private CVisualNbtEditorWidget.InputBasedWidget currentFocused;
   private double verticalScrollAmount = 0.0D;
   private double horizontalScrollAmount = 0.0D;
   private int currentRenderX;
   private int currentRenderY;
   private int globalMX;
   private int globalMY;
   private float globalDelta;
   private int currentMaxScrollY = 0;
   private int currentMaxScrollX = 0;
   private int tempMaxScrollX;
   private boolean clickFocus;

   public CVisualNbtEditorWidget(class_437 parent, int x, int y, int width, int height, class_2487 stack, Function<class_2487, Throwable> saveAcceptor) {
      super(x, y, width, height, class_2561.method_43473());
      this.save = saveAcceptor;
      screenInstance = parent;
      if (stack != null) {
         this.nbt.init("CACTUS__KEY__PARENT", stack);
      }

   }

   protected void method_48579(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      this.verticalScrollAmount = Math.min(0.0D, Math.max((double)(-(this.currentMaxScrollY - this.field_22759)), this.verticalScrollAmount));
      this.horizontalScrollAmount = Math.min(0.0D, Math.max((double)(-(this.currentMaxScrollX - this.field_22758)), this.horizontalScrollAmount));
      int lastX = this.method_46426();
      this.method_46421(lastX + (int)Math.floor(this.horizontalScrollAmount));
      this.currentRenderX = this.method_46426();
      this.currentRenderY = this.method_46427() + (int)Math.floor(this.verticalScrollAmount);
      this.globalMX = mouseX;
      this.globalMY = mouseY;
      this.globalDelta = delta;
      this.tempMaxScrollX = 0;
      this.nbt.render(context);
      this.currentMaxScrollY = this.currentRenderY - this.method_46427() - (int)Math.floor(this.verticalScrollAmount);
      this.currentMaxScrollX = Math.max(this.tempMaxScrollX - this.method_46426(), this.field_22758);
      this.method_46421(lastX);
   }

   public boolean method_25402(class_11909 click, boolean doubled) {
      double mouseX = click.comp_4798();
      double mouseY = click.comp_4799();
      int button = click.comp_4800().comp_4801();
      int lastX = this.method_46426();
      this.method_46421(lastX + (int)Math.floor(this.horizontalScrollAmount));
      this.currentRenderX = this.method_46426();
      this.currentRenderY = this.method_46427() + (int)Math.floor(this.verticalScrollAmount);
      this.clickFocus = false;
      if (this.nbt.click(mouseX, mouseY, button) && !this.clickFocus && this.currentFocused != null) {
         this.currentFocused.inputWidget.method_25365(false);
         this.currentFocused = null;
      }

      this.method_46421(lastX);
      return true;
   }

   public boolean method_25404(@NotNull class_11908 input) {
      if (this.currentFocused != null) {
         this.currentFocused.onKeyPress(input);
         return true;
      } else {
         return super.method_25404(input);
      }
   }

   public boolean method_25400(@NotNull class_11905 input) {
      if (this.currentFocused != null) {
         this.currentFocused.onCharTyped(input);
         return true;
      } else {
         return super.method_25400(input);
      }
   }

   public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (GLFW.glfwGetKey(CactusConstants.mc.method_22683().method_4490(), 342) != 1 && GLFW.glfwGetKey(CactusConstants.mc.method_22683().method_4490(), 346) != 1) {
         this.verticalScrollAmount += verticalAmount * 20.0D;
      } else {
         this.horizontalScrollAmount += verticalAmount * 20.0D;
      }

      return true;
   }

   protected void method_47399(@NotNull class_6382 builder) {
   }

   private class NbtCompoundWidget extends CVisualNbtEditorWidget.ExpandableWidget<CVisualNbtEditorWidget.NbtWidget> {
      public NbtCompoundWidget(final CVisualNbtEditorWidget param1, CVisualNbtEditorWidget.NbtWidget parent) {
         this(var1, parent, CVisualNbtEditorWidget.NbtType.COMPOUND);
      }

      public NbtCompoundWidget(final CVisualNbtEditorWidget param1, CVisualNbtEditorWidget.NbtWidget parent, CVisualNbtEditorWidget.NbtType icon) {
         super(parent, icon, (e, ctx) -> {
            if (e instanceof CVisualNbtEditorWidget.ExpandableWidget) {
               CVisualNbtEditorWidget.ExpandableWidget<?> w = (CVisualNbtEditorWidget.ExpandableWidget)e;
               int xMod = w.expanded && !w.children.isEmpty() ? 40 : 24;
               ctx.method_27535(CactusConstants.mc.field_1772, class_2561.method_43469("gui.screen.vs_nbt_editor.list." + (w.children.size() == 1 ? '1' : 'c'), new Object[]{w.key, w.children.size()}), var1.currentRenderX + xMod, var1.currentRenderY + 9, -1);
            } else {
               ctx.method_25303(CactusConstants.mc.field_1772, e.key, var1.currentRenderX + 24, var1.currentRenderY + 9, -1);
            }

            e.render(ctx);
         }, CVisualNbtEditorWidget.NbtWidget::click, CVisualNbtEditorWidget.NbtType.ALL_CORE);
      }

      public void init(String key, class_2520 element) {
         if (key.equals("CACTUS__KEY__PARENT") && this.parent == null) {
            this.expanded = true;
         }

         this.key = key;
         ((class_2487)element).method_10541().forEach((k) -> {
            class_2520 e = ((class_2487)element).method_10580(k);
            if (e != null) {
               CVisualNbtEditorWidget.NbtWidget w = this.initFromElement(this, k, e);
               if (w != null) {
                  this.children.add(w);
               }
            }
         });
      }

      public class_2520 save() {
         class_2487 comp = new class_2487();
         this.children.forEach((c) -> {
            comp.method_10566(c.key, c.save());
         });
         return comp;
      }

      public String saveJson(boolean stringCompound) {
         StringBuilder b = new StringBuilder("{");
         boolean first = true;

         CVisualNbtEditorWidget.NbtWidget w;
         for(Iterator var4 = this.children.iterator(); var4.hasNext(); b.append('"').append(w.key).append('"').append(":").append(w.saveJson(stringCompound))) {
            w = (CVisualNbtEditorWidget.NbtWidget)var4.next();
            if (first) {
               first = false;
            } else {
               b.append(",");
            }
         }

         return b.append("}").toString();
      }
   }

   private abstract class NbtWidget {
      protected String key;
      protected final CVisualNbtEditorWidget.NbtWidget parent;

      public NbtWidget(CVisualNbtEditorWidget.NbtWidget parent) {
         this.parent = parent;
      }

      public abstract void init(String var1, class_2520 var2);

      public abstract void render(class_332 var1);

      public abstract boolean click(double var1, double var3, int var5);

      public abstract class_2520 save();

      public abstract String saveJson(boolean var1);

      protected boolean handleGenericButtons(class_332 ctx, int x, int y, double mX, double mY, boolean pasteButton) {
         if (this.parent instanceof CVisualNbtEditorWidget.ExpandableWidget) {
            x -= 40;
         }

         CVisualNbtEditorWidget.ExpandableWidget w;
         CVisualNbtEditorWidget.NbtWidget var10;
         if (ctx == null) {
            if (this.clickIcon(x + 20, y, 16, mX, mY)) {
               CVisualNbtEditorWidget.clipboard = this;
               CVisualNbtEditorWidget.this.method_25354(CactusConstants.mc.method_1483());
               return true;
            }

            if (CVisualNbtEditorWidget.clipboard != null && pasteButton && this.clickIcon(x, y, 16, mX, mY)) {
               boolean isCompound = this instanceof CVisualNbtEditorWidget.NbtCompoundWidget;
               CactusConstants.mc.method_1507(new IntegratedWindowScreen(CVisualNbtEditorWidget.screenInstance, "", 200, isCompound ? 100 : 64, (s) -> {
                  String cname = CVisualNbtEditorWidget.clipboard.getClass().getName().split("\\$")[1];
                  s.addTitle(class_2561.method_43469("gui.screen.vs_nbt_editor.paste", new Object[]{cname.substring(0, cname.length() - 6)}));
                  class_342 text = new class_342(CactusConstants.mc.field_1772, s.x() + 4, s.y() + s.boxHeight() - 78, s.boxWidth() - 8, 20, class_2561.method_43471("gui.screen.vs_nbt_editor.key"));
                  text.method_1880(Integer.MAX_VALUE);
                  text.method_1852(CVisualNbtEditorWidget.clipboard.key.equals("CACTUS__KEY__PARENT") ? class_2477.method_10517().method_48307("gui.screen.vs_nbt_editor.key") : CVisualNbtEditorWidget.clipboard.key);
                  if (!isCompound) {
                     text.method_1862(false);
                  }

                  s.method_37063(text);
                  s.method_37063(new CButtonWidget(s.x() + 4, s.y() + s.boxHeight() - 46, s.boxWidth() - 8, 20, class_5244.field_24338, (button) -> {
                     CVisualNbtEditorWidget.NbtWidget w = thisx.initFromElement(this, text.method_1882(), CVisualNbtEditorWidget.clipboard.save());
                     ((CVisualNbtEditorWidget.ExpandableWidget)this).addElement(w);
                     s.method_25419();
                  }));
                  s.method_37063(new CButtonWidget(s.x() + 4, s.y() + s.boxHeight() - 24, s.boxWidth() - 8, 20, class_5244.field_24335, (button) -> {
                     s.method_25419();
                  }));
               }, IntegratedWindowScreen.RENDER_EMPTY, IntegratedWindowScreen.CLICK_EMPTY));
               CVisualNbtEditorWidget.this.method_25354(CactusConstants.mc.method_1483());
               return true;
            }

            var10 = this.parent;
            if (var10 instanceof CVisualNbtEditorWidget.ExpandableWidget) {
               w = (CVisualNbtEditorWidget.ExpandableWidget)var10;
               int cI;
               if (w.children.indexOf(this) != 0 && this.clickIcon(x + 40, y, 16, mX, mY)) {
                  cI = w.children.indexOf(this);
                  w.children.remove(this);
                  w.addElement(cI - 1, this);
                  CVisualNbtEditorWidget.this.method_25354(CactusConstants.mc.method_1483());
                  return true;
               }

               if (w.children.indexOf(this) != w.children.size() - 1 && this.clickIcon(x + 60, y, 16, mX, mY)) {
                  cI = w.children.indexOf(this);
                  w.children.remove(this);
                  w.addElement(cI + 1, this);
                  CVisualNbtEditorWidget.this.method_25354(CactusConstants.mc.method_1483());
                  return true;
               }

               x += 40;
            }

            if (this.clickIcon(x + 40, y, 16, mX, mY)) {
               var10 = this.parent;
               if (var10 instanceof CVisualNbtEditorWidget.ExpandableWidget) {
                  w = (CVisualNbtEditorWidget.ExpandableWidget)var10;
                  w.children.remove(this);
               }

               CVisualNbtEditorWidget.this.method_25354(CactusConstants.mc.method_1483());
               return true;
            }
         } else {
            this.renderIcon(ctx, x + 20, y, mX, mY, 16, true, CVisualNbtEditorWidget.NbtType.COPY);
            if (pasteButton) {
               this.renderIcon(ctx, x, y, mX, mY, 16, CVisualNbtEditorWidget.clipboard != null, CVisualNbtEditorWidget.NbtType.PASTE);
               if (CVisualNbtEditorWidget.clipboard == null) {
                  this.renderIcon(ctx, x, y, mX, mY, 16, false, CVisualNbtEditorWidget.NbtType.DISABLED);
               }
            }

            var10 = this.parent;
            if (var10 instanceof CVisualNbtEditorWidget.ExpandableWidget) {
               w = (CVisualNbtEditorWidget.ExpandableWidget)var10;
               if (w.children.indexOf(this) == 0) {
                  this.renderIcon(ctx, x + 40, y, mX, mY, 16, false, CVisualNbtEditorWidget.NbtType.UP);
                  this.renderIcon(ctx, x + 40, y, mX, mY, 16, false, CVisualNbtEditorWidget.NbtType.DISABLED);
               } else {
                  this.renderIcon(ctx, x + 40, y, mX, mY, 16, true, CVisualNbtEditorWidget.NbtType.UP);
               }

               if (w.children.indexOf(this) == w.children.size() - 1) {
                  this.renderIcon(ctx, x + 60, y, mX, mY, 16, false, CVisualNbtEditorWidget.NbtType.DOWN);
                  this.renderIcon(ctx, x + 60, y, mX, mY, 16, false, CVisualNbtEditorWidget.NbtType.DISABLED);
               } else {
                  this.renderIcon(ctx, x + 60, y, mX, mY, 16, true, CVisualNbtEditorWidget.NbtType.DOWN);
               }

               this.renderIcon(ctx, x + 80, y, mX, mY, 16, true, CVisualNbtEditorWidget.NbtType.DELETE);
            } else if (this.parent != null) {
               this.renderIcon(ctx, x + 40, y, mX, mY, 16, true, CVisualNbtEditorWidget.NbtType.DELETE);
            }
         }

         return false;
      }

      protected void renderIcon(class_332 ctx, int x, int y, double mouseX, double mouseY, int size, boolean canHover, CVisualNbtEditorWidget.NbtType icon) {
         int offX = icon.ordinal() % 4;
         int offY = icon.ordinal() / 4;
         if (icon.equals(CVisualNbtEditorWidget.NbtType.DISABLED)) {
            ctx.method_25290(class_10799.field_56883, CVisualNbtEditorWidget.iconSheet, x, y, (float)(offX * 16), (float)(offY * 16), size, size, 64, 96);
         } else {
            ctx.method_25302(class_10799.field_56883, CVisualNbtEditorWidget.iconSheet, x, y, (float)(offX * 16), (float)(offY * 16), size, size, 16, 16, 64, 96);
            if (mouseX >= (double)x && mouseX < (double)(x + size) && mouseY > (double)y && mouseY < (double)(y + size)) {
               if (canHover) {
                  offX = CVisualNbtEditorWidget.NbtType.SELECTED.ordinal() % 4;
                  offY = CVisualNbtEditorWidget.NbtType.SELECTED.ordinal() / 4;
                  ctx.method_25302(class_10799.field_56883, CVisualNbtEditorWidget.iconSheet, x, y, (float)(offX * 16), (float)(offY * 16), size, size, 16, 16, 64, 96);
               }

               if (Arrays.asList(CVisualNbtEditorWidget.NbtType.ALL_CORE).contains(icon)) {
                  ctx.method_51438(CactusConstants.mc.field_1772, icon.friendlyName(), (int)mouseX, (int)mouseY);
               } else if (icon == CVisualNbtEditorWidget.NbtType.SAVE && CVisualNbtEditorWidget.this.saveTooltipLines != null && !CVisualNbtEditorWidget.this.saveTooltipLines.isEmpty()) {
                  ctx.method_51434(CactusConstants.mc.field_1772, CVisualNbtEditorWidget.this.saveTooltipLines, (int)mouseX, (int)mouseY);
               }
            }

         }
      }

      protected boolean clickIcon(int x, int y, int size, double mouseX, double mouseY) {
         return mouseX >= (double)x && mouseX <= (double)(x + size) && mouseY >= (double)y && mouseY <= (double)(y + size);
      }

      protected CVisualNbtEditorWidget.NbtWidget initFromElement(CVisualNbtEditorWidget.NbtWidget parent, String key, class_2520 el) {
         if (el == null) {
            return null;
         } else {
            CVisualNbtEditorWidget.NbtWidget outx = null;
            switch(el.method_10711()) {
            case 1:
               outx = CVisualNbtEditorWidget.this.new NbtByteWidget(CVisualNbtEditorWidget.this, parent);
               break;
            case 2:
               outx = CVisualNbtEditorWidget.this.new NbtShortWidget(CVisualNbtEditorWidget.this, parent);
               break;
            case 3:
               outx = CVisualNbtEditorWidget.this.new NbtIntWidget(CVisualNbtEditorWidget.this, parent);
               break;
            case 4:
               outx = CVisualNbtEditorWidget.this.new NbtLongWidget(CVisualNbtEditorWidget.this, parent);
               break;
            case 5:
               outx = CVisualNbtEditorWidget.this.new NbtFloatWidget(CVisualNbtEditorWidget.this, parent);
               break;
            case 6:
               outx = CVisualNbtEditorWidget.this.new NbtDoubleWidget(CVisualNbtEditorWidget.this, parent);
               break;
            case 7:
               outx = CVisualNbtEditorWidget.this.new NbtByteArrayWidget(parent);
               break;
            case 8:
               String content = (String)el.method_68658().get();
               if (content.startsWith("{") && content.endsWith("}")) {
                  try {
                     CVisualNbtEditorWidget.NbtWidget out = CVisualNbtEditorWidget.this.new NbtCompoundStringWidget(CVisualNbtEditorWidget.this, parent);
                     out.init(key, class_2522.method_67315(content));
                     return out;
                  } catch (Exception var7) {
                  }
               }

               outx = CVisualNbtEditorWidget.this.new NbtStringWidget(CVisualNbtEditorWidget.this, parent);
               break;
            case 9:
               outx = CVisualNbtEditorWidget.this.new NbtListWidget(CVisualNbtEditorWidget.this, parent);
               break;
            case 10:
               outx = CVisualNbtEditorWidget.this.new NbtCompoundWidget(CVisualNbtEditorWidget.this, parent);
               break;
            case 11:
               outx = CVisualNbtEditorWidget.this.new NbtIntArrayWidget(parent);
               break;
            case 12:
               outx = CVisualNbtEditorWidget.this.new NbtLongArrayWidget(parent);
            }

            if (outx == null) {
               return null;
            } else {
               ((CVisualNbtEditorWidget.NbtWidget)outx).init(key, el);
               return (CVisualNbtEditorWidget.NbtWidget)outx;
            }
         }
      }
   }

   private abstract class InputBasedWidget extends CVisualNbtEditorWidget.NbtWidget {
      private final CVisualNbtEditorWidget.NbtType icon;
      private final Function<String, Boolean> checkTextFunc;
      private final Function<Character, Boolean> validateChar;
      protected final class_342 inputWidget;

      public static boolean isDigit(char c) {
         return c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9';
      }

      public InputBasedWidget(CVisualNbtEditorWidget.NbtWidget parent, CVisualNbtEditorWidget.NbtType parentType, class_2561 placeholder, Function<String, Boolean> textCheck, Function<Character, Boolean> charCheck) {
         super(parent);
         this.icon = parentType;
         this.checkTextFunc = textCheck;
         this.validateChar = charCheck;
         this.inputWidget = new class_342(CactusConstants.mc.field_1772, 200, 20, placeholder);
         this.inputWidget.method_1880(Integer.MAX_VALUE);
      }

      public void render(class_332 ctx) {
         int lineOffset = this.parent instanceof CVisualNbtEditorWidget.NbtCompoundWidget ? CactusConstants.mc.field_1772.method_1727(this.key) + 6 : 0;
         ctx.method_51738(CVisualNbtEditorWidget.this.currentRenderX + 22 + lineOffset, CVisualNbtEditorWidget.this.method_46426() + CVisualNbtEditorWidget.this.currentMaxScrollX - 296, CVisualNbtEditorWidget.this.currentRenderY + 12, CVisualNbtEditorWidget.this.globalMY >= CVisualNbtEditorWidget.this.currentRenderY && CVisualNbtEditorWidget.this.globalMY < CVisualNbtEditorWidget.this.currentRenderY + 24 && CVisualNbtEditorWidget.this.globalMX >= CVisualNbtEditorWidget.this.currentRenderX && CVisualNbtEditorWidget.this.globalMX < CVisualNbtEditorWidget.this.method_46426() - 8 + CVisualNbtEditorWidget.this.currentMaxScrollX ? -1143087651 : 581610154);
         this.renderIcon(ctx, CVisualNbtEditorWidget.this.currentRenderX, CVisualNbtEditorWidget.this.currentRenderY + 4, (double)CVisualNbtEditorWidget.this.globalMX, (double)CVisualNbtEditorWidget.this.globalMY, 16, false, this.icon);
         this.handleGenericButtons(ctx, CVisualNbtEditorWidget.this.method_46426() + CVisualNbtEditorWidget.this.currentMaxScrollX - 270, CVisualNbtEditorWidget.this.currentRenderY + 4, (double)CVisualNbtEditorWidget.this.globalMX, (double)CVisualNbtEditorWidget.this.globalMY, false);
         this.inputWidget.method_48229(CVisualNbtEditorWidget.this.method_46426() + CVisualNbtEditorWidget.this.currentMaxScrollX - 210, CVisualNbtEditorWidget.this.currentRenderY + 2);
         this.inputWidget.method_1868((Boolean)this.checkTextFunc.apply(this.inputWidget.method_1882()) ? -14446493 : -3791065);
         this.inputWidget.method_48579(ctx, CVisualNbtEditorWidget.this.globalMX, CVisualNbtEditorWidget.this.globalMY, CVisualNbtEditorWidget.this.globalDelta);
      }

      public boolean click(double mouseX, double mouseY, int button) {
         if (this.handleGenericButtons((class_332)null, CVisualNbtEditorWidget.this.method_46426() + CVisualNbtEditorWidget.this.currentMaxScrollX - 270, CVisualNbtEditorWidget.this.currentRenderY + 4, mouseX, mouseY, false)) {
            return true;
         } else {
            if (mouseX > (double)(CVisualNbtEditorWidget.this.method_46426() + CVisualNbtEditorWidget.this.currentMaxScrollX - 210) && mouseX < (double)(CVisualNbtEditorWidget.this.method_46426() + CVisualNbtEditorWidget.this.currentMaxScrollX - 10) && mouseY > (double)(CVisualNbtEditorWidget.this.currentRenderY + 2) && mouseY < (double)(CVisualNbtEditorWidget.this.currentRenderY + 22)) {
               if (!this.inputWidget.method_25370()) {
                  if (CVisualNbtEditorWidget.this.currentFocused != null) {
                     CVisualNbtEditorWidget.this.currentFocused.inputWidget.method_25365(false);
                  }

                  this.inputWidget.method_25365(true);
                  CVisualNbtEditorWidget.this.currentFocused = this;
               }

               CVisualNbtEditorWidget.this.clickFocus = true;
            }

            return this.inputWidget.method_25402(new class_11909(mouseX, mouseY, new class_11910(button, 0)), false);
         }
      }

      public void onKeyPress(class_11908 input) {
         this.inputWidget.method_25404(input);
      }

      public void onCharTyped(class_11905 input) {
         if ((Boolean)this.validateChar.apply((char)input.comp_4793())) {
            this.inputWidget.method_25400(input);
         }

      }
   }

   private static enum NbtType {
      COMPOUND,
      BYTE,
      SHORT,
      INT,
      LONG,
      FLOAT,
      DOUBLE,
      LIST,
      STRING,
      BYTE_ARR,
      INT_ARR,
      LONG_ARR,
      STRING_COMPOUND,
      CREATE,
      EXPAND,
      SHRINK,
      DELETE,
      SAVE,
      PASTE,
      COPY,
      UP,
      DOWN,
      SELECTED,
      DISABLED;

      public static final CVisualNbtEditorWidget.NbtType[] ALL_CORE = new CVisualNbtEditorWidget.NbtType[]{SHORT, BYTE, INT, LONG, FLOAT, LIST, BYTE_ARR, INT_ARR, LONG_ARR, DOUBLE, COMPOUND, STRING, STRING_COMPOUND};

      public class_2561 friendlyName() {
         String var10000 = "gui.screen.vs_nbt_editor.type.%s".formatted(new Object[]{this.name().toLowerCase()});
         char var10001 = this.name().charAt(0);
         return class_2561.method_48321(var10000, var10001 + this.name().substring(1).toLowerCase().replace("_arr", " Array").replace('_', ' '));
      }

      // $FF: synthetic method
      private static CVisualNbtEditorWidget.NbtType[] $values() {
         return new CVisualNbtEditorWidget.NbtType[]{COMPOUND, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, LIST, STRING, BYTE_ARR, INT_ARR, LONG_ARR, STRING_COMPOUND, CREATE, EXPAND, SHRINK, DELETE, SAVE, PASTE, COPY, UP, DOWN, SELECTED, DISABLED};
      }
   }

   private class NbtCompoundStringWidget extends CVisualNbtEditorWidget.NbtCompoundWidget {
      public NbtCompoundStringWidget(final CVisualNbtEditorWidget param1, CVisualNbtEditorWidget.NbtWidget parent) {
         super(var1, parent, CVisualNbtEditorWidget.NbtType.STRING_COMPOUND);
      }

      public class_2520 save() {
         return class_2519.method_23256(super.saveJson(true));
      }

      public String saveJson(boolean stringCompound) {
         return "\"" + class_2519.method_10706(super.saveJson(true)) + "\"";
      }
   }

   private class NbtLongArrayWidget extends CVisualNbtEditorWidget.ExpandableWidget<CVisualNbtEditorWidget.NbtLongWidget> {
      public NbtLongArrayWidget(CVisualNbtEditorWidget.NbtWidget parent) {
         super(parent, CVisualNbtEditorWidget.NbtType.LONG_ARR, CVisualNbtEditorWidget.InputBasedWidget::render, CVisualNbtEditorWidget.InputBasedWidget::click, new CVisualNbtEditorWidget.NbtType[]{CVisualNbtEditorWidget.NbtType.LONG});
      }

      public void init(String key, class_2520 element) {
         this.key = key;
         long[] var3 = ((class_2501)element).method_10615();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            long l = var3[var5];
            this.children.add(CVisualNbtEditorWidget.this.new NbtLongWidget(CVisualNbtEditorWidget.this, this, l));
         }

      }

      public class_2520 save() {
         return new class_2501(this.children.stream().map((c) -> {
            return ((class_2503)c.save()).method_10699();
         }).mapToLong(Long::longValue).toArray());
      }

      public String saveJson(boolean stringCompound) {
         List var10001 = this.children.stream().map((w) -> {
            return w.saveJson(stringCompound);
         }).toList();
         return "[" + String.join(",", var10001) + "]";
      }
   }

   private class NbtIntArrayWidget extends CVisualNbtEditorWidget.ExpandableWidget<CVisualNbtEditorWidget.NbtIntWidget> {
      public NbtIntArrayWidget(CVisualNbtEditorWidget.NbtWidget parent) {
         super(parent, CVisualNbtEditorWidget.NbtType.INT_ARR, CVisualNbtEditorWidget.InputBasedWidget::render, CVisualNbtEditorWidget.InputBasedWidget::click, new CVisualNbtEditorWidget.NbtType[]{CVisualNbtEditorWidget.NbtType.INT});
      }

      public void init(String key, class_2520 element) {
         this.key = key;
         int[] var3 = ((class_2495)element).method_10588();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            int i = var3[var5];
            this.children.add(CVisualNbtEditorWidget.this.new NbtIntWidget(CVisualNbtEditorWidget.this, this, i));
         }

      }

      public class_2520 save() {
         return new class_2495(this.children.stream().map((c) -> {
            return ((class_2497)c.save()).method_10701();
         }).mapToInt(Integer::intValue).toArray());
      }

      public String saveJson(boolean stringCompound) {
         List var10001 = this.children.stream().map((w) -> {
            return w.saveJson(stringCompound);
         }).toList();
         return "[" + String.join(",", var10001) + "]";
      }
   }

   private class NbtByteArrayWidget extends CVisualNbtEditorWidget.ExpandableWidget<CVisualNbtEditorWidget.NbtByteWidget> {
      public NbtByteArrayWidget(CVisualNbtEditorWidget.NbtWidget parent) {
         super(parent, CVisualNbtEditorWidget.NbtType.BYTE_ARR, CVisualNbtEditorWidget.InputBasedWidget::render, CVisualNbtEditorWidget.InputBasedWidget::click, new CVisualNbtEditorWidget.NbtType[]{CVisualNbtEditorWidget.NbtType.BYTE});
      }

      public void init(String key, class_2520 element) {
         this.key = key;
         byte[] var3 = ((class_2479)element).method_10521();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            byte b = var3[var5];
            this.children.add(CVisualNbtEditorWidget.this.new NbtByteWidget(CVisualNbtEditorWidget.this, this, b));
         }

      }

      public class_2520 save() {
         List<Byte> byteList = this.children.stream().map((c) -> {
            return ((class_2481)c.save()).method_10698();
         }).toList();
         byte[] bytes = new byte[byteList.size()];

         for(int i = 0; i < byteList.size(); ++i) {
            bytes[i] = (Byte)byteList.get(i);
         }

         return new class_2479(bytes);
      }

      public String saveJson(boolean stringCompound) {
         List var10001 = this.children.stream().map((w) -> {
            return w.saveJson(stringCompound);
         }).toList();
         return "[" + String.join(",", var10001) + "]";
      }
   }

   private class NbtListWidget extends CVisualNbtEditorWidget.ExpandableWidget<CVisualNbtEditorWidget.NbtWidget> {
      public NbtListWidget(final CVisualNbtEditorWidget param1, CVisualNbtEditorWidget.NbtWidget parent) {
         super(parent, CVisualNbtEditorWidget.NbtType.LIST, CVisualNbtEditorWidget.NbtWidget::render, CVisualNbtEditorWidget.NbtWidget::click, CVisualNbtEditorWidget.NbtType.ALL_CORE);
      }

      public void init(String key, class_2520 element) {
         this.key = key;
         int size = ((class_2499)element).size();

         for(int i = 0; i < size; ++i) {
            class_2520 e = ((class_2499)element).method_10534(i);
            if (e == null) {
               return;
            }

            CVisualNbtEditorWidget.NbtWidget w = this.initFromElement(this, "index_" + i, e);
            if (w == null) {
               return;
            }

            this.children.add(w);
         }

      }

      public class_2520 save() {
         class_2499 list = new class_2499();
         this.children.forEach((c) -> {
            list.add(c.save());
         });
         return list;
      }

      public String saveJson(boolean stringCompound) {
         List var10001 = this.children.stream().map((w) -> {
            return w.saveJson(stringCompound);
         }).toList();
         return "[" + String.join(", ", var10001) + "]";
      }
   }

   private class NbtStringWidget extends CVisualNbtEditorWidget.InputBasedWidget {
      public NbtStringWidget(final CVisualNbtEditorWidget param1, CVisualNbtEditorWidget.NbtWidget parent) {
         super(parent, CVisualNbtEditorWidget.NbtType.STRING, class_2561.method_43471("gui.screen.vs_nbt_editor.text"), (s) -> {
            return true;
         }, (c) -> {
            return true;
         });
      }

      public void init(String key, class_2520 element) {
         this.key = key;
         this.inputWidget.method_1852((String)element.method_68658().get());
      }

      public class_2520 save() {
         return class_2519.method_23256(this.inputWidget.method_1882());
      }

      public String saveJson(boolean stringCompound) {
         return class_2519.method_10706(this.inputWidget.method_1882());
      }
   }

   private class NbtDoubleWidget extends CVisualNbtEditorWidget.InputBasedWidget {
      private double initialValue;

      public NbtDoubleWidget(final CVisualNbtEditorWidget param1, CVisualNbtEditorWidget.NbtWidget parent) {
         super(parent, CVisualNbtEditorWidget.NbtType.DOUBLE, class_2561.method_30163("Double"), (s) -> {
            try {
               Double.parseDouble(s);
               return true;
            } catch (NumberFormatException var2) {
               return false;
            }
         }, CVisualNbtEditorWidget.InputBasedWidget::isDigit);
      }

      public void init(String key, class_2520 element) {
         this.key = key;
         this.initialValue = ((class_2489)element).method_10697();
         this.inputWidget.method_1852(String.valueOf(this.initialValue));
      }

      public class_2520 save() {
         try {
            return class_2489.method_23241(Double.parseDouble(this.inputWidget.method_1882()));
         } catch (NumberFormatException var2) {
            return class_2489.method_23241(this.initialValue);
         }
      }

      public String saveJson(boolean stringCompound) {
         return String.valueOf(((class_2489)this.save()).method_10697());
      }
   }

   private class NbtFloatWidget extends CVisualNbtEditorWidget.InputBasedWidget {
      private float initialValue;

      public NbtFloatWidget(final CVisualNbtEditorWidget param1, CVisualNbtEditorWidget.NbtWidget parent) {
         super(parent, CVisualNbtEditorWidget.NbtType.FLOAT, class_2561.method_30163("Float"), (s) -> {
            try {
               Float.parseFloat(s);
               return true;
            } catch (NumberFormatException var2) {
               return false;
            }
         }, CVisualNbtEditorWidget.InputBasedWidget::isDigit);
      }

      public void init(String key, class_2520 element) {
         this.key = key;
         this.initialValue = ((class_2494)element).method_10700();
         this.inputWidget.method_1852(String.valueOf(this.initialValue));
      }

      public class_2520 save() {
         try {
            return class_2494.method_23244(Float.parseFloat(this.inputWidget.method_1882()));
         } catch (NumberFormatException var2) {
            return class_2494.method_23244(this.initialValue);
         }
      }

      public String saveJson(boolean stringCompound) {
         return String.valueOf(((class_2494)this.save()).method_10700());
      }
   }

   private class NbtLongWidget extends CVisualNbtEditorWidget.InputBasedWidget {
      private long initialValue;

      public NbtLongWidget(final CVisualNbtEditorWidget param1, CVisualNbtEditorWidget.NbtWidget parent) {
         super(parent, CVisualNbtEditorWidget.NbtType.LONG, class_2561.method_30163("Long"), (s) -> {
            try {
               Long.parseLong(s);
               return true;
            } catch (NumberFormatException var2) {
               return false;
            }
         }, CVisualNbtEditorWidget.InputBasedWidget::isDigit);
      }

      public NbtLongWidget(final CVisualNbtEditorWidget param1, CVisualNbtEditorWidget.NbtWidget parent, long l) {
         this(var1, parent);
         this.inputWidget.method_1852(String.valueOf(l));
         this.initialValue = l;
      }

      public void init(String key, class_2520 element) {
         this.key = key;
         this.initialValue = ((class_2503)element).method_10699();
         this.inputWidget.method_1852(String.valueOf(this.initialValue));
      }

      public class_2520 save() {
         try {
            return class_2503.method_23251(Long.parseLong(this.inputWidget.method_1882()));
         } catch (NumberFormatException var2) {
            return class_2503.method_23251(this.initialValue);
         }
      }

      public String saveJson(boolean stringCompound) {
         return String.valueOf(((class_2503)this.save()).method_10699());
      }
   }

   private class NbtIntWidget extends CVisualNbtEditorWidget.InputBasedWidget {
      private int initialValue;

      public NbtIntWidget(final CVisualNbtEditorWidget param1, CVisualNbtEditorWidget.NbtWidget parent) {
         super(parent, CVisualNbtEditorWidget.NbtType.INT, class_2561.method_30163("Integer"), (s) -> {
            try {
               Integer.parseInt(s);
               return true;
            } catch (NumberFormatException var2) {
               return false;
            }
         }, CVisualNbtEditorWidget.InputBasedWidget::isDigit);
      }

      public NbtIntWidget(final CVisualNbtEditorWidget param1, CVisualNbtEditorWidget.NbtWidget parent, int i) {
         this(var1, parent);
         this.inputWidget.method_1852(String.valueOf(i));
         this.initialValue = i;
      }

      public void init(String key, class_2520 element) {
         this.key = key;
         this.initialValue = ((class_2497)element).method_10701();
         this.inputWidget.method_1852(String.valueOf(this.initialValue));
      }

      public class_2520 save() {
         try {
            return class_2497.method_23247(Integer.parseInt(this.inputWidget.method_1882()));
         } catch (NumberFormatException var2) {
            return class_2497.method_23247(this.initialValue);
         }
      }

      public String saveJson(boolean stringCompound) {
         return String.valueOf(((class_2497)this.save()).method_10701());
      }
   }

   private class NbtShortWidget extends CVisualNbtEditorWidget.InputBasedWidget {
      private short initialValue;

      public NbtShortWidget(final CVisualNbtEditorWidget param1, CVisualNbtEditorWidget.NbtWidget parent) {
         super(parent, CVisualNbtEditorWidget.NbtType.SHORT, class_2561.method_30163("Short"), (s) -> {
            try {
               Short.parseShort(s);
               return true;
            } catch (NumberFormatException var2) {
               return false;
            }
         }, CVisualNbtEditorWidget.InputBasedWidget::isDigit);
      }

      public void init(String key, class_2520 element) {
         this.key = key;
         this.initialValue = ((class_2516)element).method_10696();
         this.inputWidget.method_1852(String.valueOf(this.initialValue));
      }

      public class_2520 save() {
         try {
            return class_2516.method_23254(Short.parseShort(this.inputWidget.method_1882()));
         } catch (NumberFormatException var2) {
            return class_2516.method_23254(this.initialValue);
         }
      }

      public String saveJson(boolean stringCompound) {
         return String.valueOf(((class_2516)this.save()).method_10696());
      }
   }

   private class NbtByteWidget extends CVisualNbtEditorWidget.InputBasedWidget {
      private byte initialValue;

      public NbtByteWidget(final CVisualNbtEditorWidget param1, CVisualNbtEditorWidget.NbtWidget parent) {
         super(parent, CVisualNbtEditorWidget.NbtType.BYTE, class_2561.method_30163("Byte"), (s) -> {
            try {
               Byte.parseByte(s);
               return true;
            } catch (NumberFormatException var2) {
               return false;
            }
         }, CVisualNbtEditorWidget.InputBasedWidget::isDigit);
      }

      public NbtByteWidget(final CVisualNbtEditorWidget param1, CVisualNbtEditorWidget.NbtWidget parent, byte b) {
         this(var1, parent);
         this.inputWidget.method_1852(String.valueOf(b));
         this.initialValue = b;
      }

      public void init(String key, class_2520 element) {
         this.key = key;
         this.initialValue = ((class_2481)element).method_10698();
         this.inputWidget.method_1852(String.valueOf(this.initialValue));
      }

      public class_2520 save() {
         try {
            return class_2481.method_23233(Byte.parseByte(this.inputWidget.method_1882()));
         } catch (NumberFormatException var2) {
            return class_2481.method_23233(this.initialValue);
         }
      }

      public String saveJson(boolean stringCompound) {
         byte val = ((class_2481)this.save()).method_10698();
         if (stringCompound) {
            return val == 0 ? "false" : (val == 1 ? "true" : String.valueOf(val));
         } else {
            return String.valueOf(val);
         }
      }
   }

   private abstract class ExpandableWidget<T extends CVisualNbtEditorWidget.NbtWidget> extends CVisualNbtEditorWidget.NbtWidget {
      private final CVisualNbtEditorWidget.NbtType icon;
      private final BiConsumer<T, class_332> renderChild;
      private final CVisualNbtEditorWidget.ExpandableWidget.ClickFunction<T> clickChild;
      private final CVisualNbtEditorWidget.NbtType[] createTypes;
      public final List<T> children;
      protected boolean expanded;

      public ExpandableWidget(CVisualNbtEditorWidget.NbtWidget parent, CVisualNbtEditorWidget.NbtType parentType, BiConsumer<T, class_332> renderElement, CVisualNbtEditorWidget.ExpandableWidget.ClickFunction<T> clickElement, CVisualNbtEditorWidget.NbtType[] allowedCreateTypes) {
         super(parent);
         this.icon = parentType;
         this.renderChild = renderElement;
         this.clickChild = clickElement;
         this.children = new ArrayList();
         this.createTypes = allowedCreateTypes;
      }

      public void addElement(CVisualNbtEditorWidget.NbtWidget w) {
         this.addElement(0, w);
      }

      public void addElement(int i, CVisualNbtEditorWidget.NbtWidget w) {
         if (w instanceof CVisualNbtEditorWidget.NbtWidget) {
            this.children.add(i, w);
         }

      }

      public void addElement(String key, CVisualNbtEditorWidget.NbtType type) {
         if (List.of(this.createTypes).contains(type)) {
            switch(type.ordinal()) {
            case 0:
               this.addElement(this.initFromElement(this, key, new class_2487()));
               break;
            case 1:
               this.addElement(this.initFromElement(this, key, class_2481.method_23234(false)));
               break;
            case 2:
               this.addElement(this.initFromElement(this, key, class_2516.method_23254((short)0)));
               break;
            case 3:
               this.addElement(this.initFromElement(this, key, class_2497.method_23247(0)));
               break;
            case 4:
               this.addElement(this.initFromElement(this, key, class_2503.method_23251(0L)));
               break;
            case 5:
               this.addElement(this.initFromElement(this, key, class_2494.method_23244(0.0F)));
               break;
            case 6:
               this.addElement(this.initFromElement(this, key, class_2489.method_23241(0.0D)));
               break;
            case 7:
               this.addElement(this.initFromElement(this, key, new class_2499()));
               break;
            case 8:
               this.addElement(this.initFromElement(this, key, class_2519.method_23256("")));
               break;
            case 9:
               this.addElement(this.initFromElement(this, key, new class_2479(new byte[]{0})));
               break;
            case 10:
               this.addElement(this.initFromElement(this, key, new class_2495(new int[]{0})));
               break;
            case 11:
               this.addElement(this.initFromElement(this, key, new class_2501(new long[]{0L})));
               break;
            case 12:
               this.addElement(this.initFromElement(this, key, class_2519.method_23256("{}")));
            }

         }
      }

      public void render(class_332 ctx) {
         this.renderIcon(ctx, CVisualNbtEditorWidget.this.currentRenderX, CVisualNbtEditorWidget.this.currentRenderY + 4, (double)CVisualNbtEditorWidget.this.globalMX, (double)CVisualNbtEditorWidget.this.globalMY, 16, false, this.icon);
         if (this.createTypes.length != 0) {
            this.renderIcon(ctx, CVisualNbtEditorWidget.this.method_46426() + CVisualNbtEditorWidget.this.currentMaxScrollX - 44, CVisualNbtEditorWidget.this.currentRenderY + 4, (double)CVisualNbtEditorWidget.this.globalMX, (double)CVisualNbtEditorWidget.this.globalMY, 16, true, CVisualNbtEditorWidget.NbtType.CREATE);
         }

         if (this.parent == null) {
            this.renderIcon(ctx, CVisualNbtEditorWidget.this.method_46426() + CVisualNbtEditorWidget.this.currentMaxScrollX - 64, CVisualNbtEditorWidget.this.currentRenderY + 4, (double)CVisualNbtEditorWidget.this.globalMX, (double)CVisualNbtEditorWidget.this.globalMY, 16, true, CVisualNbtEditorWidget.NbtType.SAVE);
         }

         this.handleGenericButtons(ctx, CVisualNbtEditorWidget.this.method_46426() + CVisualNbtEditorWidget.this.currentMaxScrollX - 104, CVisualNbtEditorWidget.this.currentRenderY + 4, (double)CVisualNbtEditorWidget.this.globalMX, (double)CVisualNbtEditorWidget.this.globalMY, true);
         int lineOffset = this.parent instanceof CVisualNbtEditorWidget.NbtCompoundWidget ? CactusConstants.mc.field_1772.method_27525(class_2561.method_43469("gui.screen.vs_nbt_editor.list." + (this.children.size() == 1 ? '1' : 'c'), new Object[]{this.key, this.children.size()})) + 6 : 0;
         if (this.expanded) {
            this.renderIcon(ctx, CVisualNbtEditorWidget.this.method_46426() + CVisualNbtEditorWidget.this.currentMaxScrollX - 24, CVisualNbtEditorWidget.this.currentRenderY + 4, (double)CVisualNbtEditorWidget.this.globalMX, (double)CVisualNbtEditorWidget.this.globalMY, 16, true, CVisualNbtEditorWidget.NbtType.SHRINK);
            ctx.method_51738(CVisualNbtEditorWidget.this.currentRenderX + (this.children.isEmpty() ? 22 : 38) + lineOffset, CVisualNbtEditorWidget.this.method_46426() + CVisualNbtEditorWidget.this.currentMaxScrollX - (this.parent == null ? 110 : 150), CVisualNbtEditorWidget.this.currentRenderY + 12, CVisualNbtEditorWidget.this.globalMY >= CVisualNbtEditorWidget.this.currentRenderY && CVisualNbtEditorWidget.this.globalMY < CVisualNbtEditorWidget.this.currentRenderY + 24 && CVisualNbtEditorWidget.this.globalMX >= CVisualNbtEditorWidget.this.currentRenderX && CVisualNbtEditorWidget.this.globalMX < CVisualNbtEditorWidget.this.method_46426() - 8 + CVisualNbtEditorWidget.this.currentMaxScrollX ? -1143087651 : 581610154);
            if (this.children.isEmpty()) {
               return;
            }

            ctx.method_25294(CVisualNbtEditorWidget.this.currentRenderX + 16, CVisualNbtEditorWidget.this.currentRenderY + 10, CVisualNbtEditorWidget.this.currentRenderX + 34, CVisualNbtEditorWidget.this.currentRenderY + 14, -12244673);
            AtomicInteger lastElementY = new AtomicInteger(CVisualNbtEditorWidget.this.currentRenderY - 10);
            CVisualNbtEditorWidget.this.tempMaxScrollX = Math.max(CVisualNbtEditorWidget.this.currentRenderX + 450, CVisualNbtEditorWidget.this.tempMaxScrollX);
            CVisualNbtEditorWidget var10000 = CVisualNbtEditorWidget.this;
            var10000.currentRenderX += 24;
            var10000 = CVisualNbtEditorWidget.this;
            var10000.currentRenderY += 24;
            this.children.forEach((c) -> {
               ctx.method_25294(CVisualNbtEditorWidget.this.currentRenderX + 6, lastElementY.get() + 20, CVisualNbtEditorWidget.this.currentRenderX + 10, CVisualNbtEditorWidget.this.currentRenderY + 4, -12244673);
               lastElementY.set(CVisualNbtEditorWidget.this.currentRenderY);
               this.renderChild.accept(c, ctx);
               CVisualNbtEditorWidget var10000 = CVisualNbtEditorWidget.this;
               var10000.currentRenderY += 24;
            });
            var10000 = CVisualNbtEditorWidget.this;
            var10000.currentRenderX -= 24;
            var10000 = CVisualNbtEditorWidget.this;
            var10000.currentRenderY -= 24;
         } else {
            ctx.method_51738(CVisualNbtEditorWidget.this.currentRenderX + 22 + lineOffset, CVisualNbtEditorWidget.this.method_46426() + CVisualNbtEditorWidget.this.currentMaxScrollX - (this.parent == null ? 110 : 150), CVisualNbtEditorWidget.this.currentRenderY + 12, CVisualNbtEditorWidget.this.globalMY >= CVisualNbtEditorWidget.this.currentRenderY && CVisualNbtEditorWidget.this.globalMY < CVisualNbtEditorWidget.this.currentRenderY + 24 && CVisualNbtEditorWidget.this.globalMX >= CVisualNbtEditorWidget.this.currentRenderX && CVisualNbtEditorWidget.this.globalMX < CVisualNbtEditorWidget.this.method_46426() - 8 + CVisualNbtEditorWidget.this.currentMaxScrollX ? -1143087651 : 581610154);
            this.renderIcon(ctx, CVisualNbtEditorWidget.this.method_46426() + CVisualNbtEditorWidget.this.currentMaxScrollX - 24, CVisualNbtEditorWidget.this.currentRenderY + 4, (double)CVisualNbtEditorWidget.this.globalMX, (double)CVisualNbtEditorWidget.this.globalMY, 16, true, CVisualNbtEditorWidget.NbtType.EXPAND);
         }

      }

      public boolean click(double mouseX, double mouseY, int button) {
         if (this.createTypes.length != 0 && this.clickIcon(CVisualNbtEditorWidget.this.method_46426() + CVisualNbtEditorWidget.this.currentMaxScrollX - 44, CVisualNbtEditorWidget.this.currentRenderY + 4, 16, mouseX, mouseY)) {
            if (this.createTypes.length == 1) {
               this.addElement("li", this.createTypes[0]);
               CVisualNbtEditorWidget.this.method_25354(CactusConstants.mc.method_1483());
               return true;
            } else {
               boolean isCompound = this instanceof CVisualNbtEditorWidget.NbtCompoundWidget;
               AtomicReference<class_342> textRef = new AtomicReference();
               CactusConstants.mc.method_1507(new IntegratedWindowScreen(CVisualNbtEditorWidget.screenInstance, "", 200, isCompound ? 165 : 138, (s) -> {
                  s.addTitle(class_2561.method_43471("gui.screen.vs_nbt_editor.add"));
                  textRef.set(new class_342(CactusConstants.mc.field_1772, s.x() + 4, s.y() + s.boxHeight() - 144, s.boxWidth() - 8, 20, class_2561.method_43471("gui.screen.vs_nbt_editor.key")));
                  if (!isCompound) {
                     ((class_342)textRef.get()).method_1862(false);
                  }

                  s.method_37063((class_342)textRef.get());
                  s.method_37063(new CButtonWidget(s.x() + 4, s.y() + s.boxHeight() - 24, s.boxWidth() - 8, 20, class_5244.field_24335, (btn) -> {
                     s.method_25419();
                  }));
                  s.method_25395((class_364)textRef.get());
               }, (s, ctx, mX, mY, d) -> {
                  int size = this.createTypes.length;
                  int rows = size / 5;
                  int lastRow = size % 5;

                  int startX;
                  int x;
                  for(startX = 0; startX < rows; ++startX) {
                     for(x = 0; x < 5; ++x) {
                        this.renderIcon(ctx, s.x() + 28 + x * 30, s.y() + s.boxHeight() - 116 + startX * 30, (double)mX, (double)mY, 24, true, this.createTypes[startX * 5 + x]);
                     }
                  }

                  if (lastRow != 0) {
                     startX = 103 - lastRow * 15;

                     for(x = 0; x < lastRow; ++x) {
                        this.renderIcon(ctx, s.x() + startX + x * 30, s.y() + s.boxHeight() - 116 + rows * 30, (double)mX, (double)mY, 24, true, this.createTypes[rows * 5 + x]);
                     }
                  }

               }, (s, mX, mY, btn) -> {
                  int size = this.createTypes.length;
                  int rows = size / 5;
                  int lastRow = size % 5;

                  int startX;
                  int x;
                  for(startX = 0; startX < rows; ++startX) {
                     for(x = 0; x < 5; ++x) {
                        if (this.clickIcon(s.x() + 28 + x * 30, s.y() + s.boxHeight() - 116 + startX * 30, 24, mX, mY)) {
                           thisx.addElement(((class_342)textRef.get()).method_1882(), this.createTypes[startX * 5 + x]);
                           CVisualNbtEditorWidget.this.method_25354(CactusConstants.mc.method_1483());
                           s.method_25419();
                        }
                     }
                  }

                  if (lastRow != 0) {
                     startX = 103 - lastRow * 15;

                     for(x = 0; x < lastRow; ++x) {
                        if (this.clickIcon(s.x() + startX + x * 30, s.y() + s.boxHeight() - 116 + rows * 30, 24, mX, mY)) {
                           thisx.addElement(((class_342)textRef.get()).method_1882(), this.createTypes[rows * 5 + x]);
                           CVisualNbtEditorWidget.this.method_25354(CactusConstants.mc.method_1483());
                           s.method_25419();
                        }
                     }
                  }

                  return false;
               }));
               CVisualNbtEditorWidget.this.method_25354(CactusConstants.mc.method_1483());
               return true;
            }
         } else if (this.parent == null && this.clickIcon(CVisualNbtEditorWidget.this.method_46426() + CVisualNbtEditorWidget.this.currentMaxScrollX - 64, CVisualNbtEditorWidget.this.currentRenderY + 4, 16, mouseX, mouseY)) {
            Throwable t = (Throwable)CVisualNbtEditorWidget.this.save.apply((class_2487)CVisualNbtEditorWidget.this.nbt.save());
            GenericToast toast;
            if (t != null) {
               toast = new GenericToast(class_2561.method_43470("Save failed"), class_2561.method_43470(t.getMessage()).method_27692(class_124.field_1061), 10000L);
               CactusConstants.mc.method_1566().method_1999(toast);
               CVisualNbtEditorWidget.this.saveTooltipLines = List.of(class_2561.method_43470("Save failed"), class_2561.method_43473(), class_2561.method_43470(t.getMessage().replaceAll("(.{80})", "$1\n")).method_27692(class_124.field_1061));
            } else {
               toast = new GenericToast(class_2561.method_43470("Saved!").method_27692(class_124.field_1060), class_2561.method_43473(), 1000L);
               CactusConstants.mc.method_1566().method_1999(toast);
               CVisualNbtEditorWidget.this.saveTooltipLines = List.of(class_2561.method_43470("Saved!").method_27692(class_124.field_1060));
            }

            CVisualNbtEditorWidget.this.method_25354(CactusConstants.mc.method_1483());
            return true;
         } else if (this.handleGenericButtons((class_332)null, CVisualNbtEditorWidget.this.method_46426() + CVisualNbtEditorWidget.this.currentMaxScrollX - 104, CVisualNbtEditorWidget.this.currentRenderY + 4, mouseX, mouseY, true)) {
            return true;
         } else if (this.clickIcon(CVisualNbtEditorWidget.this.method_46426() + CVisualNbtEditorWidget.this.currentMaxScrollX - 24, CVisualNbtEditorWidget.this.currentRenderY + 4, 16, mouseX, mouseY)) {
            this.expanded = !this.expanded;
            CVisualNbtEditorWidget.this.method_25354(CactusConstants.mc.method_1483());
            return true;
         } else if (this.expanded) {
            AtomicBoolean hit = new AtomicBoolean(false);
            CVisualNbtEditorWidget var10000 = CVisualNbtEditorWidget.this;
            var10000.currentRenderX += 24;
            var10000 = CVisualNbtEditorWidget.this;
            var10000.currentRenderY += 24;
            this.children.stream().toList().forEach((c) -> {
               if (this.clickChild.apply(c, mouseX, mouseY, button)) {
                  hit.set(true);
               }

               CVisualNbtEditorWidget var10000 = CVisualNbtEditorWidget.this;
               var10000.currentRenderY += 24;
            });
            var10000 = CVisualNbtEditorWidget.this;
            var10000.currentRenderX -= 24;
            var10000 = CVisualNbtEditorWidget.this;
            var10000.currentRenderY -= 24;
            return hit.get();
         } else {
            return false;
         }
      }

      public interface ClickFunction<T> {
         boolean apply(T var1, double var2, double var4, int var6);
      }
   }
}
