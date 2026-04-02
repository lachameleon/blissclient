package com.dwarslooper.cactus.client.gui.hud.element.impl;

import com.dwarslooper.cactus.client.gui.hud.element.DynamicHudElement;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.ColorSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.EnumSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.InputTimeTracker;
import java.awt.Color;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.class_2561;
import net.minecraft.class_304;
import net.minecraft.class_332;
import org.joml.Vector2i;

public class KeystrokesElement extends DynamicHudElement<KeystrokesElement> {
   private static final int UNIT_SIZE = 22;
   private static final int SPACING = 2;
   public Setting<KeystrokesElement.Layout> layout;
   public Setting<ColorSetting.ColorValue> keyColor;
   public Setting<ColorSetting.ColorValue> keyColorPressed;
   public Setting<Boolean> wasd;
   public Setting<Boolean> space;
   public Setting<Boolean> mouse;
   public Setting<Boolean> cps;

   public KeystrokesElement() {
      super("keystrokes", new Vector2i(60, 40));
      this.layout = this.elementGroup.add((new EnumSetting("layout", KeystrokesElement.Layout.Default)).setCallback(this::update));
      this.keyColor = this.elementGroup.add((new ColorSetting("keyColor", ColorSetting.ColorValue.of(new Color(-1912602624, true), false), true)).setCallback(this::update));
      this.keyColorPressed = this.elementGroup.add((new ColorSetting("keyColorPressed", ColorSetting.ColorValue.of(new Color(-1905891738, true), false), true)).setCallback(this::update));
      this.wasd = this.elementGroup.add((new BooleanSetting("wasd", true)).setCallback(this::update));
      this.space = this.elementGroup.add((new BooleanSetting("space", true)).setCallback(this::update));
      this.mouse = this.elementGroup.add((new BooleanSetting("mouse", true)).setCallback(this::update));
      this.cps = this.elementGroup.add(new BooleanSetting("cps", false));
   }

   private void update(Object o) {
      this.correct();
   }

   public void created() {
      this.correct();
   }

   public void correct() {
      Vector2i baseSize = new Vector2i();
      if ((Boolean)this.wasd.get()) {
         baseSize.add(66, 44);
      }

      if ((Boolean)this.mouse.get() && this.layout.get() != KeystrokesElement.Layout.Compact) {
         baseSize.add(0, 22);
      }

      if ((Boolean)this.space.get()) {
         baseSize.add(0, 10);
      }

      if (this.layout.get() == KeystrokesElement.Layout.Spaced) {
         int spacesX = (int)Math.ceil((double)baseSize.x() / 22.0D) - 1;
         int spacesY = (int)Math.ceil((double)baseSize.y() / 22.0D) - 1;
         baseSize.add(spacesX * 2, spacesY * 2);
      }

      this.resize(baseSize);
      super.correct();
   }

   public boolean canResize() {
      return false;
   }

   public KeystrokesElement duplicate() {
      return new KeystrokesElement();
   }

   public HudElement.Style getDefaultStyle() {
      return HudElement.Style.Transparent;
   }

   public void renderContent(class_332 context, int x, int y, int width, int height, int screenWidth, int screenHeight, float delta, boolean inEditor) {
      int currentY = y;
      int spacing = this.spacing();
      if ((Boolean)this.wasd.get()) {
         int xCenter = x + (width - 22) / 2;
         this.renderNamedKey(context, xCenter, y, 22, 22, CactusConstants.mc.field_1690.field_1894);
         this.renderNamedKey(context, x, y + 22 + spacing, 22, 22, CactusConstants.mc.field_1690.field_1913);
         this.renderNamedKey(context, xCenter, y + 22 + spacing, 22, 22, CactusConstants.mc.field_1690.field_1881);
         this.renderNamedKey(context, x + width - 22, y + 22 + spacing, 22, 22, CactusConstants.mc.field_1690.field_1849);
         currentY = y + (22 + spacing) * 2;
      }

      if ((Boolean)this.mouse.get()) {
         boolean compact = this.layout.get() == KeystrokesElement.Layout.Compact;
         int mouseY = compact ? y : currentY;
         int mouseW = compact ? 22 : (width - spacing) / 2;
         boolean showCps = (Boolean)this.cps.get() && !compact;
         int var10000;
         if (showCps) {
            var10000 = mouseY + 2;
         } else {
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            var10000 = mouseY + (22 - 9) / 2 + 1;
         }

         int textCenter = var10000;
         this.renderNamedKey(context, x, mouseY, mouseW, 22, CactusConstants.mc.field_1690.field_1886, (k) -> {
            return class_2561.method_43470("LMB");
         }, textCenter);
         this.renderNamedKey(context, x + width - mouseW, mouseY, mouseW, 22, CactusConstants.mc.field_1690.field_1904, (k) -> {
            return class_2561.method_43470("RMB");
         }, textCenter);
         if (showCps) {
            var10000 = currentY + 4;
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            int cpsY = var10000 + 9;
            String cpsSuffix = " CPS";
            var10000 = InputTimeTracker.INSTANCE.getInputsThisSecond(0);
            String lb = var10000 + cpsSuffix;
            var10000 = InputTimeTracker.INSTANCE.getInputsThisSecond(1);
            String rb = var10000 + cpsSuffix;
            context.method_51433(CactusConstants.mc.field_1772, lb, x + (mouseW - CactusConstants.mc.field_1772.method_1727(lb)) / 2, cpsY, ((ColorSetting.ColorValue)this.textColor.get()).color(), false);
            context.method_51433(CactusConstants.mc.field_1772, rb, x + width - (mouseW + CactusConstants.mc.field_1772.method_1727(rb)) / 2, cpsY, ((ColorSetting.ColorValue)this.textColor.get()).color(), false);
         }

         if (!compact) {
            currentY += 22 + spacing;
         }
      }

      if ((Boolean)this.space.get()) {
         this.renderKeyBackground(context, x, currentY, width, 10, CactusConstants.mc.field_1690.field_1903);
         context.method_51738(x + 8, x + width - 8, currentY + 5, ((ColorSetting.ColorValue)this.textColor.get()).color());
      }

   }

   private int spacing() {
      return this.layout.get() == KeystrokesElement.Layout.Spaced ? 2 : 0;
   }

   private void renderNamedKey(class_332 context, int x, int y, int width, int height, class_304 key) {
      this.renderNamedKey(context, x, y, width, height, key, class_304::method_16007);
   }

   private void renderNamedKey(class_332 context, int x, int y, int width, int height, class_304 key, Function<class_304, class_2561> textGetter) {
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      this.renderNamedKey(context, x, y, width, height, key, textGetter, y + (height - 9) / 2 + 1);
   }

   private void renderNamedKey(class_332 context, int x, int y, int width, int height, class_304 key, Function<class_304, class_2561> textGetter, int textY) {
      this.renderKeyBackground(context, x, y, width, height, key);
      class_2561 text = (class_2561)textGetter.apply(key);
      int w = CactusConstants.mc.field_1772.method_27525(text);
      context.method_51439(CactusConstants.mc.field_1772, text, x + (width - w) / 2, textY, ((ColorSetting.ColorValue)this.textColor.get()).color(), false);
   }

   private void renderKeyBackground(class_332 context, int x, int y, int width, int height, class_304 key) {
      context.method_25294(x, y, x + width, y + height, key.method_1434() ? ((ColorSetting.ColorValue)this.keyColorPressed.get()).color() : ((ColorSetting.ColorValue)this.keyColor.get()).color());
   }

   public static enum Layout {
      Default,
      Compact,
      Spaced;

      // $FF: synthetic method
      private static KeystrokesElement.Layout[] $values() {
         return new KeystrokesElement.Layout[]{Default, Compact, Spaced};
      }
   }
}
