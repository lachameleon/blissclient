package com.dwarslooper.cactus.client.gui.hud;

import com.dwarslooper.cactus.client.gui.hud.element.StaticHudElement;
import com.dwarslooper.cactus.client.gui.hud.element.impl.HudElement;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.ABValue;
import com.dwarslooper.cactus.client.util.generic.WidgetIcons;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_364;
import net.minecraft.class_410;
import net.minecraft.class_4185;
import net.minecraft.class_4265;
import net.minecraft.class_5250;
import net.minecraft.class_6379;
import net.minecraft.class_4265.class_4266;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public class AddHudElementScreen extends CScreen {
   private final HudManager hudManager = HudManager.getInstance();

   public AddHudElementScreen() {
      super("addHudElement");
   }

   public void method_25426() {
      super.method_25426();
      AddHudElementScreen.Widget widget = (AddHudElementScreen.Widget)this.method_37063(new AddHudElementScreen.Widget(Math.max(200, this.field_22789 / 4), this.field_22790 - 26 - 4 - 28, 30, 24));
      widget.method_46421(6);
      widget.method_65506();
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
   }

   public class Widget extends class_4265<AddHudElementScreen.Widget.Entry> {
      public Widget(int width, int height, int y, int itemHeight) {
         super(CactusConstants.mc, width, height, y, itemHeight);
         AddHudElementScreen.this.hudManager.getElementRegistry().forEach((element) -> {
            this.method_25321(new AddHudElementScreen.Widget.ElementEntry(element));
            element.getPresets().forEach((presetConf) -> {
               this.method_25321(new AddHudElementScreen.Widget.PresetEntry(this, element, presetConf));
            });
         });
      }

      protected int method_65507() {
         return this.method_55442() - 6;
      }

      public int method_25322() {
         return this.field_22758 - 20;
      }

      public int method_25342() {
         return super.method_25342() - 2;
      }

      public class ElementEntry extends AddHudElementScreen.Widget.Entry {
         protected class_4185 deleteButton;

         public ElementEntry(HudElement<?> element) {
            super(element);
            List<HudElement<?>> ofType = this.getAllChildren();
            this.deleteButton = new CTextureButtonWidget(0, 0, WidgetIcons.DELETE.offsetX(), (button) -> {
               if (!this.canAdd()) {
                  AddHudElementScreen.this.hudManager.remove(this.registryElement);
                  this.update();
               } else {
                  CactusConstants.mc.method_1507(new class_410((b) -> {
                     if (b) {
                        HudManager var10001 = AddHudElementScreen.this.hudManager;
                        Objects.requireNonNull(var10001);
                        ofType.forEach(var10001::remove);
                        this.update();
                     }

                     CactusConstants.mc.method_1507(AddHudElementScreen.this);
                  }, class_2561.method_43470("Remove all '%s' elements?".formatted(new Object[]{this.registryElement.getName()})), class_2561.method_43470("This will permanently remove %s elements".formatted(new Object[]{ofType.size()}))));
               }

            });
            this.update();
         }

         protected void configure(HudElement<?> element) {
            super.configure(element);
            element.configureDefault(false);
         }

         public void method_25343(@NotNull class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            super.method_25343(context, mouseX, mouseY, hovered, tickDelta);
            int x = this.method_73380();
            int y = this.method_73382();
            int entryWidth = this.method_25368() - 4;
            int entryHeight = this.method_73384();
            class_327 var10001 = CactusConstants.mc.field_1772;
            String var10002 = this.registryElement.getName();
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            context.method_25303(var10001, var10002, x, y + (entryHeight - 9) / 2 + 1, -1);
            this.deleteButton.method_48229(x + entryWidth - 20 - 24, y);
            this.deleteButton.method_25394(context, mouseX, mouseY, tickDelta);
         }

         protected void update() {
            super.update();
            this.deleteButton.field_22763 = !this.getAllChildren().isEmpty();
            Widget.this.method_25396().forEach((entry) -> {
               if (entry instanceof AddHudElementScreen.Widget.PresetEntry) {
                  AddHudElementScreen.Widget.PresetEntry pe = (AddHudElementScreen.Widget.PresetEntry)entry;
                  if (pe.registryElement == this.registryElement) {
                     pe.update();
                  }
               }

            });
         }

         public List<? extends class_339> widgets() {
            return ImmutableList.of(this.createButton, this.deleteButton);
         }
      }

      public class PresetEntry extends AddHudElementScreen.Widget.Entry {
         private final PresetConfig<HudElement<?>> presetConfiguration;
         private final boolean isLast;

         public PresetEntry(final AddHudElementScreen.Widget this$1, HudElement element, PresetConfig presetConfiguration) {
            super(element);
            this.presetConfiguration = presetConfiguration;
            this.isLast = element.getPresets().getLast() == presetConfiguration;
            this.update();
         }

         protected void configure(HudElement<?> element) {
            super.configure(element);
            this.presetConfiguration.configurator().accept(element);
            element.configureDefault(true);
         }

         public List<? extends class_339> widgets() {
            return ImmutableList.of(this.createButton);
         }

         public void method_25343(@NotNull class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            super.method_25343(context, mouseX, mouseY, hovered, tickDelta);
            int x = this.method_73380();
            int y = this.method_73382();
            int entryHeight = this.method_73384();
            context.method_51742(x + 4, y - 3, y - 2 + (entryHeight + 4) / (this.isLast ? 2 : 1), -5592406);
            context.method_51738(x + 4, x + 12, y + entryHeight / 2, -5592406);
            class_327 var10001 = CactusConstants.mc.field_1772;
            String var10002 = this.registryElement.getPresetName(this.presetConfiguration);
            int var10003 = x + 16;
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            context.method_51433(var10001, var10002, var10003, y + (entryHeight - 9) / 2 + 1, -1, false);
         }
      }

      public abstract class Entry extends class_4266<AddHudElementScreen.Widget.Entry> {
         private static final ABValue<class_5250> CREATE_STATES;
         protected final HudElement<?> registryElement;
         protected class_4185 createButton;

         public Entry(HudElement<?> registryElement) {
            this.registryElement = registryElement;
            this.createButton = new CButtonWidget(0, 0, 20, 20, class_2561.method_43473(), (button) -> {
               this.add(registryElement);
            });
         }

         protected boolean canAdd() {
            HudElement var2 = this.registryElement;
            boolean var10000;
            if (var2 instanceof StaticHudElement) {
               StaticHudElement<?> eStatic = (StaticHudElement)var2;
               if (AddHudElementScreen.this.hudManager.getElements().contains(eStatic.getInstance())) {
                  var10000 = true;
                  return !var10000;
               }
            }

            var10000 = false;
            return !var10000;
         }

         protected void update() {
            boolean canAdd = this.canAdd();
            this.createButton.field_22763 = canAdd;
            this.createButton.method_25355((class_2561)CREATE_STATES.fromBoolean(canAdd));
         }

         protected void configure(HudElement<?> element) {
            element.setAnchor(Anchor.MIDDLE_MIDDLE);
            element.getRelativePosition().zero().sub((new Vector2i(element.getSize())).div(2));
         }

         private void add(HudElement<?> element) {
            AddHudElementScreen.this.hudManager.addFromRegistry(element, this::configure);
            AddHudElementScreen.this.method_25419();
         }

         protected List<HudElement<?>> getAllChildren() {
            return AddHudElementScreen.this.hudManager.getElements().stream().filter((e) -> {
               return e.getId().equals(this.registryElement.getId());
            }).toList();
         }

         public void method_25343(@NotNull class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int x = this.method_73380();
            int y = this.method_73382();
            int entryWidth = this.method_25368() - 4;
            this.createButton.method_48229(x + entryWidth - 20, y);
            this.createButton.method_25394(context, mouseX, mouseY, tickDelta);
            if (this.createButton.method_49606() & !this.createButton.field_22763) {
               context.method_51438(CactusConstants.mc.field_1772, class_2561.method_43470("Only one element of this type can exist at a time").method_27692(class_124.field_1061), mouseX, mouseY);
            }

         }

         @NotNull
         public List<? extends class_6379> method_37025() {
            return this.widgets();
         }

         @NotNull
         public List<? extends class_364> method_25396() {
            return this.widgets();
         }

         public abstract List<? extends class_339> widgets();

         static {
            CREATE_STATES = ABValue.of(class_124.field_1060, class_124.field_1063).map((f) -> {
               return class_2561.method_43470("+").method_27695(new class_124[]{f, class_124.field_1067});
            });
         }
      }
   }
}
