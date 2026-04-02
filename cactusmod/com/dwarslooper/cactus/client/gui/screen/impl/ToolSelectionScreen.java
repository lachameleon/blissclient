package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.gui.hud.HudEditorScreen;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import com.dwarslooper.cactus.client.util.generic.WidgetIcons;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_10799;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_437;
import net.minecraft.class_6382;
import net.minecraft.class_7843;
import net.minecraft.class_7845;
import net.minecraft.class_7845.class_7939;
import org.jetbrains.annotations.NotNull;

public class ToolSelectionScreen extends CScreen {
   private final class_2960 BACKGROUND = class_2960.method_60654("textures/gui/container/gamemode_switcher.png");
   private int x;
   private int y;
   private int selectorHeight;
   private final List<ToolSelectionScreen.SelectionWidget> widgets;

   public ToolSelectionScreen() {
      super("tool_selection");
      this.widgets = new ArrayList(List.of((new ToolSelectionScreen.ToolSelectable(class_2561.method_43470("Modules"), () -> {
         CactusConstants.mc.method_1507(new ModuleListScreen());
      }, 8)).createWidget(), (new ToolSelectionScreen.ToolSelectable(class_2561.method_43470("Options"), () -> {
         CactusConstants.mc.method_1507(new CactusSettingsScreen((class_437)null));
      }, 6)).createWidget(), (new ToolSelectionScreen.ToolSelectable(class_2561.method_43470("HUD Editor"), () -> {
         CactusConstants.mc.method_1507(new HudEditorScreen());
      }, 14)).createWidget(), (new ToolSelectionScreen.ToolSelectable(class_2561.method_43470("Screenshots"), () -> {
         CactusConstants.mc.method_1507(new ScreenshotListScreen((class_437)null));
      }, 17)).createWidget(), (new ToolSelectionScreen.ToolSelectable(class_2561.method_43470("Cosmetics"), () -> {
         CactusConstants.mc.method_1507(new CosmeticsListScreen((class_437)null));
      }, 18)).createWidget(), (new ToolSelectionScreen.ToolSelectable(class_2561.method_43470("Emotes"), () -> {
         CactusConstants.mc.method_1507(new EmoteSelectScreen());
      }, WidgetIcons.EMOTES.ordinal())).createWidget()));
   }

   public void method_25426() {
      super.init(false);
      if ((Boolean)CactusSettings.get().experiments.get()) {
         this.widgets.add((new ToolSelectionScreen.ToolSelectable(class_2561.method_43470("Teams"), () -> {
            CactusConstants.mc.method_1507(new TeamsListScreen());
         }, 9)).createWidget());
      }

      this.x = (this.field_22789 - 125) / 2;
      this.selectorHeight = 30 + (this.widgets.size() + 4 - 1) / 4 * 30;
      this.y = (this.field_22790 - this.selectorHeight) / 2;
      class_7845 grid = new class_7845();
      grid.method_46458().method_46477(2).method_46475(4).method_46467().method_46472();
      class_7939 adder = grid.method_47610(4);
      List var10000 = this.widgets;
      Objects.requireNonNull(adder);
      var10000.forEach(adder::method_47612);
      grid.method_48222();
      class_7843.method_46443(grid, 0, (this.field_22790 - this.selectorHeight) / 2 + 22 + 4, this.field_22789, this.field_22790, 0.5F, 0.0F);
      this.widgets.forEach((x$0) -> {
         ToolSelectionScreen.SelectionWidget var10000 = (ToolSelectionScreen.SelectionWidget)this.method_37063(x$0);
      });
   }

   public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
      context.method_25290(class_10799.field_56883, this.BACKGROUND, this.x, this.y, 0.0F, 0.0F, 128, 26, 128, 128);

      for(int i = 0; (double)i < Math.ceil((double)this.widgets.size() / 4.0D); ++i) {
         context.method_25290(class_10799.field_56883, this.BACKGROUND, this.x, this.y + 22 + 4 + i * 30, 0.0F, 26.0F, 128, 30, 128, 128);
      }

      context.method_25290(class_10799.field_56883, this.BACKGROUND, this.x, this.y + this.selectorHeight - 4, 0.0F, 72.0F, 128, 4, 128, 128);
      super.method_25394(context, mouseX, mouseY, delta);
      this.method_25396().stream().filter((e) -> {
         boolean var10000;
         if (e instanceof ToolSelectionScreen.SelectionWidget) {
            ToolSelectionScreen.SelectionWidget w = (ToolSelectionScreen.SelectionWidget)e;
            if (w.method_25367()) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }).map((e) -> {
         return (ToolSelectionScreen.SelectionWidget)e;
      }).findFirst().ifPresentOrElse((e) -> {
         context.method_27534(CactusConstants.mc.field_1772, e.option.name, this.field_22789 / 2, this.y + 8, -1);
      }, () -> {
         context.method_25300(CactusConstants.mc.field_1772, "Cactus Mod", this.field_22789 / 2, this.y + 8, -1);
      });
   }

   public void method_25420(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
   }

   public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
      this.method_25419();
      super.method_25402(click, doubled);
      return true;
   }

   public boolean method_25421() {
      return false;
   }

   public static class ToolSelectable {
      private final class_2561 name;
      private final Runnable used;
      private final int icon;

      public ToolSelectable(class_2561 name, Runnable used, int icon) {
         this.name = name;
         this.used = used;
         this.icon = icon;
      }

      public class_2561 getName() {
         return this.name;
      }

      public void used() {
         this.used.run();
      }

      public void renderIcon(class_332 context, int x, int y) {
         RenderUtils.drawCactusTexture(context, x, y, this.icon);
      }

      public ToolSelectionScreen.SelectionWidget createWidget() {
         return new ToolSelectionScreen.SelectionWidget(this);
      }
   }

   public static class SelectionWidget extends class_339 {
      private static final class_2960 SLOT_TEXTURE = class_2960.method_60654("gamemode_switcher/slot");
      private static final class_2960 SELECTION_TEXTURE = class_2960.method_60654("gamemode_switcher/selection");
      private final ToolSelectionScreen.ToolSelectable option;

      public SelectionWidget(ToolSelectionScreen.ToolSelectable option) {
         super(0, 0, 26, 26, option.getName());
         this.option = option;
      }

      protected void method_48579(class_332 context, int mouseX, int mouseY, float delta) {
         context.method_52706(class_10799.field_56883, SLOT_TEXTURE, this.method_46426(), this.method_46427(), 26, 26);
         this.option.renderIcon(context, this.method_46426() + 3, this.method_46427() + 3);
         if (this.method_25367()) {
            context.method_52706(class_10799.field_56883, SELECTION_TEXTURE, this.method_46426(), this.method_46427(), 26, 26);
         }

      }

      public void method_25348(@NotNull class_11909 click, boolean doubled) {
         this.option.used();
      }

      protected void method_47399(@NotNull class_6382 builder) {
      }
   }
}
