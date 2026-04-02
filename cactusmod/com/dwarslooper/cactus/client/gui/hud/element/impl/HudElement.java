package com.dwarslooper.cactus.client.gui.hud.element.impl;

import com.dwarslooper.cactus.client.gui.hud.Anchor;
import com.dwarslooper.cactus.client.gui.hud.ElementBackedSettingGroup;
import com.dwarslooper.cactus.client.gui.hud.HudManager;
import com.dwarslooper.cactus.client.gui.hud.PresetConfig;
import com.dwarslooper.cactus.client.systems.ISerializable;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.dwarslooper.cactus.client.systems.config.settings.group.SettingGroup;
import com.dwarslooper.cactus.client.systems.config.settings.gui.SettingContainer;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.ColorSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.EnumSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.generic.INamespaced;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import net.minecraft.class_10799;
import net.minecraft.class_2350;
import net.minecraft.class_2477;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_8002;
import net.minecraft.class_9848;
import org.joml.Vector2i;

public abstract class HudElement<T extends HudElement<T>> implements ISerializable<HudElement<T>>, INamespaced {
   public static final Vector2i ZERO = new Vector2i(0, 0);
   public static final class_2960 BACKGROUND = class_2960.method_60655("cactus", "hud_element");
   private final String id;
   private final Vector2i position;
   private final Vector2i minSize;
   private final Vector2i size;
   private Anchor anchor;
   public final SettingContainer settings;
   public final SettingGroup elementGroup;
   private boolean calculateBorders;
   private boolean correctNextDraw;
   private final Map<class_2350, Boolean> adjacent;
   public Setting<HudElement.Style> style;
   public Setting<ColorSetting.ColorValue> backgroundColor;
   public Setting<ColorSetting.ColorValue> textColor;
   public Setting<Boolean> autoAnchor;

   protected HudElement(String id, Vector2i size, Vector2i minSize) {
      this.position = new Vector2i();
      this.minSize = new Vector2i();
      this.size = new Vector2i();
      this.anchor = Anchor.LEFT_UP;
      this.calculateBorders = true;
      this.correctNextDraw = false;
      this.adjacent = new EnumMap(class_2350.class);
      this.id = id;
      this.size.set(size);
      this.minSize.set(minSize);
      this.settings = new SettingContainer(this.getNamespace());
      SettingGroup defaultGroup = this.settings.getDefault();
      defaultGroup.setNamespace("hud.element");
      this.elementGroup = this.settings.appendGroup(new ElementBackedSettingGroup(this, "element"));
      this.elementGroup.setNamespace(this.getNamespace());
      this.style = defaultGroup.add(new EnumSetting("style", this.getDefaultStyle()));
      this.backgroundColor = defaultGroup.add((new ColorSetting("backgroundColor", ColorSetting.ColorValue.of(new Color(-1442840576, true), false), true)).visibleIf(() -> {
         return this.style.get() == HudElement.Style.Color;
      }));
      this.textColor = defaultGroup.add(new ColorSetting("textColor", ColorSetting.ColorValue.of(new Color(-1, false), false), true));
      this.autoAnchor = defaultGroup.add(new BooleanSetting("autoAnchor", true));
   }

   protected HudElement(String id, Vector2i size) {
      this(id, size, new Vector2i(8, 8));
   }

   protected HudElement(String id) {
      this(id, new Vector2i(80, 40));
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return class_2477.method_10517().method_48307(this.getNamespace() + ".name");
   }

   public void resize(Vector2i size) {
      this.size.set(size.max(this.getMinSize()));
   }

   public void resize(int x, int y) {
      this.resize(new Vector2i(x, y));
   }

   public void resizeRelative(int x, int y) {
      this.resize(this.size.x() + x, this.size.y() + y);
   }

   public void update() {
      this.resize(this.size.x(), this.size.y());
      this.move(this.position.x(), this.position.y());
   }

   public void move(int x, int y) {
      this.position.set(x, y);
      this.calculateBorders = true;
   }

   public void moveRelative(int x, int y) {
      this.move(this.position.x() + x, this.position.y() + y);
   }

   public Anchor getAnchor() {
      return this.anchor;
   }

   public void setAnchor(Anchor anchor) {
      this.anchor = anchor;
   }

   public Vector2i getRelativePosition() {
      return this.position;
   }

   public Vector2i getAbsolutePosition(int width, int height) {
      Vector2i anchorPos = this.anchor.getAbsolutePosition(width, height);
      return new Vector2i(this.position.x + anchorPos.x(), this.position.y + anchorPos.y());
   }

   public void fromAbsolute(Vector2i absolutePos, int width, int height) {
      Vector2i anchorPos = this.anchor.getAbsolutePosition(width, height);
      this.move(absolutePos.x - anchorPos.x(), absolutePos.y - anchorPos.y());
   }

   public Vector2i getSize() {
      return this.size;
   }

   public Vector2i getEffectiveOrigin(int x, int y, int screenWidth, int screenHeight) {
      Vector2i effectiveSize = this.getEffectiveSize();
      Vector2i origin = new Vector2i((int)((double)x + (double)(this.size.x() - effectiveSize.x()) * this.anchor.getHorizontalFactor()), (int)((double)y + (double)(this.size.y() - effectiveSize.y()) * this.anchor.getVerticalFactor()));
      origin.max(ZERO);
      origin.min(new Vector2i(screenWidth - effectiveSize.x(), screenHeight - effectiveSize.y()));
      return origin;
   }

   public Vector2i getEffectiveSize() {
      return new Vector2i(this.getSize());
   }

   public void correct() {
      this.correctNextDraw = true;
   }

   public void correct(int width, int height) {
      Vector2i pos = this.getAbsolutePosition(width, height);
      Vector2i size = this.getSize();
      Vector2i outer = (new Vector2i(pos)).add(size);
      if (pos.x() < 0 || pos.y() < 0 || outer.x() > width || outer.y() > height) {
         Vector2i corrected = (new Vector2i(pos)).max(ZERO).min(new Vector2i(width - size.x(), height - size.y()));
         this.fromAbsolute(corrected, width, height);
      }

   }

   public void render(class_332 context, int screenWidth, int screenHeight, float delta) {
      Vector2i absolute = this.getAbsolutePosition(screenWidth, screenHeight);
      this.render(context, absolute.x(), absolute.y(), screenWidth, screenHeight, delta, false);
   }

   public void render(class_332 context, int x, int y, int screenWidth, int screenHeight, float delta, boolean inEditor) {
      if (this.correctNextDraw) {
         this.correct(screenWidth, screenHeight);
         this.correctNextDraw = false;
      }

      if (inEditor || this.calculateBorders) {
         this.updateBorders(x, y, screenWidth, screenHeight);
      }

      Vector2i size = inEditor ? this.getSize() : this.getEffectiveSize();
      if (!inEditor && !this.getSize().equals(size)) {
         Vector2i effectiveOrigin = this.getEffectiveOrigin(x, y, screenWidth, screenHeight);
         x = effectiveOrigin.x();
         y = effectiveOrigin.y();
         size = this.getEffectiveSize();
      }

      this.renderBackground(context, x, y, size.x(), size.y(), delta, inEditor);
      if (inEditor) {
         this.renderPlaceholder(context, x, y, size.x(), size.y(), screenWidth, screenHeight, delta);
      } else {
         this.renderContent(context, x, y, size.x(), size.y(), screenWidth, screenHeight, delta, false);
      }

      this.calculateBorders = false;
   }

   public void renderBackground(class_332 context, int x, int y, int width, int height, float delta, boolean inEditor) {
      context.method_44379(x, y, x + width, y + height);
      int w = this.borderOffset(class_2350.field_11039);
      int n = this.borderOffset(class_2350.field_11043);
      switch(((HudElement.Style)this.style.get()).ordinal()) {
      case 0:
         context.method_52707(class_10799.field_56883, BACKGROUND, x - w, y - n, width + w + this.borderOffset(class_2350.field_11034), height + n + this.borderOffset(class_2350.field_11035), class_9848.method_61324(204, 1, 1, 1));
         break;
      case 1:
         context.method_25294(x, y, x + width, y + height, ((ColorSetting.ColorValue)this.backgroundColor.get()).color());
         break;
      case 2:
         class_8002.method_47946(context, x + 4 - w, y + 4 - n, width - 8 + w + this.borderOffset(class_2350.field_11034), height - 8 + n + this.borderOffset(class_2350.field_11035), (class_2960)null);
      case 3:
      }

      context.method_44380();
   }

   public abstract void renderContent(class_332 var1, int var2, int var3, int var4, int var5, int var6, int var7, float var8, boolean var9);

   public void renderPlaceholder(class_332 context, int x, int y, int width, int height, int screenWidth, int screenHeight, float delta) {
      this.renderContent(context, x, y, width, height, screenWidth, screenHeight, delta, true);
   }

   private int borderOffset(class_2350 direction) {
      return (Boolean)this.adjacent.getOrDefault(direction, false) ? 4 : 0;
   }

   private void updateBorders(int x, int y, int screenWidth, int screenHeight) {
      List<HudElement<?>> es = HudManager.getInstance().getElements();
      this.adjacent.put(class_2350.field_11043, this.matchesAnyElement(es, (ePos, eSize) -> {
         return ePos.x == x && eSize.x == this.size.x && ePos.y + eSize.y == y;
      }, screenWidth, screenHeight));
      this.adjacent.put(class_2350.field_11035, this.matchesAnyElement(es, (ePos, eSize) -> {
         return ePos.x == x && eSize.x == this.size.x && ePos.y == y + this.size.y;
      }, screenWidth, screenHeight));
      this.adjacent.put(class_2350.field_11039, this.matchesAnyElement(es, (ePos, eSize) -> {
         return ePos.y == y && eSize.y == this.size.y && ePos.x + eSize.x == x;
      }, screenWidth, screenHeight));
      this.adjacent.put(class_2350.field_11034, this.matchesAnyElement(es, (ePos, eSize) -> {
         return ePos.y == y && eSize.y == this.size.y && ePos.x == x + this.size.x;
      }, screenWidth, screenHeight));
   }

   private boolean matchesAnyElement(List<HudElement<?>> elements, BiPredicate<Vector2i, Vector2i> vecValidator, int screenWidth, int screenHeight) {
      return elements.stream().filter((e) -> {
         return e.style.get() == this.style.get();
      }).anyMatch((element) -> {
         Vector2i eAbs = element.getAbsolutePosition(screenWidth, screenHeight);
         return vecValidator.test(eAbs, element.getSize());
      });
   }

   public boolean textShadows() {
      return this.style.get() == HudElement.Style.Transparent;
   }

   public HudElement.Style getDefaultStyle() {
      return HudElement.Style.Default;
   }

   public boolean canResize() {
      return true;
   }

   public Vector2i getMinSize() {
      return this.minSize;
   }

   public HudElement<T> fromJson(JsonObject object) {
      this.move(object.get("x").getAsInt(), object.get("y").getAsInt());
      if (this.canResize()) {
         this.resize(object.get("width").getAsInt(), object.get("height").getAsInt());
      }

      this.anchor = Anchor.valueOf(object.get("anchor").getAsString());
      this.settings.fromJson(object.getAsJsonObject("settings"));
      return this;
   }

   public JsonObject toJson(TreeSerializerFilter filter) {
      JsonObject object = new JsonObject();
      object.addProperty("id", this.getId());
      object.addProperty("x", this.position.x());
      object.addProperty("y", this.position.y());
      object.addProperty("width", this.size.x());
      object.addProperty("height", this.size.y());
      object.addProperty("anchor", this.anchor.name());
      object.add("settings", this.settings.toJson(filter.resolve("settings")));
      return object;
   }

   public String getNamespace() {
      return "hud.elements." + this.getId();
   }

   public T buildDefault() {
      return null;
   }

   public String getPresetName(PresetConfig<?> presetConfiguration) {
      class_2477 var10000 = class_2477.method_10517();
      String var10001 = this.getNamespace();
      return var10000.method_48307(var10001 + ".presets." + presetConfiguration.id());
   }

   public List<PresetConfig<?>> getPresets() {
      return HudManager.getInstance().getPresets(this.getClass());
   }

   public void configureDefault(boolean createdWithPreset) {
   }

   public void created() {
   }

   public void removed() {
   }

   public static enum Style {
      Default,
      Color,
      Tooltip,
      Transparent;

      // $FF: synthetic method
      private static HudElement.Style[] $values() {
         return new HudElement.Style[]{Default, Color, Tooltip, Transparent};
      }
   }
}
