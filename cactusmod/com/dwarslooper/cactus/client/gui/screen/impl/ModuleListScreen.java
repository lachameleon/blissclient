package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.feature.module.Category;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.GridScrollableWidget;
import com.dwarslooper.cactus.client.mixins.accessor.KeyMappingAccessor;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.systems.key.KeybindManager;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import com.dwarslooper.cactus.client.util.exception.FunnyException;
import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import com.dwarslooper.cactus.client.util.generic.ABValue;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_10799;
import net.minecraft.class_1109;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_342;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_4264;
import net.minecraft.class_4265;
import net.minecraft.class_437;
import net.minecraft.class_5250;
import net.minecraft.class_6379;
import net.minecraft.class_4265.class_4266;
import org.jetbrains.annotations.NotNull;

public class ModuleListScreen extends CScreen {
   private static final class_2960 SETTINGS_ICON = class_2960.method_60655("cactus", "module_settings");
   private static final class_2960 SEARCH_ICON = class_2960.method_60654("icon/search");
   private static final int widgetHeight = 26;
   private static final int widgetWidth = 128;
   private static final int topListY = 32;
   private static Category category;
   private final List<class_2561> experimentalWarning = List.of(class_2561.method_43470("§cThis module is marked as unfinished."), class_2561.method_43470("§cIt might not work or cause bugs."), class_2561.method_43470("§c§lDo not report issues related to this module!"));
   private final CactusSettings.ModuleButtonStyle style;
   private final boolean experimentsEnabled;
   public class_2561 moduleDescriptionTooltip;
   private String searchTerm;
   private ModuleListScreen.ModuleListWidget moduleList;
   private ModuleListScreen.CategoryWidget categoryList;
   private class_4185 toggleSearchButton;
   private class_342 searchWidget;

   public ModuleListScreen() {
      super("modules");
      this.style = (CactusSettings.ModuleButtonStyle)CactusSettings.get().moduleWidgetStateStyle.get();
      this.experimentsEnabled = (Boolean)CactusSettings.get().experiments.get();
   }

   public void method_25426() {
      super.method_25426();
      this.method_37063(new CTextureButtonWidget(30, 6, 0, (button) -> {
         CactusConstants.mc.method_1507(new CactusMainScreen(this));
      }));
      if (this.moduleList == null) {
         this.moduleList = new ModuleListScreen.ModuleListWidget();
      }

      if (this.categoryList == null) {
         this.categoryList = new ModuleListScreen.CategoryWidget(this);
      }

      ((ModuleListScreen.ModuleListWidget)this.method_37063(this.moduleList)).update();
      ((ModuleListScreen.CategoryWidget)this.method_37063(this.categoryList)).update();
      this.method_37063(this.toggleSearchButton = new CButtonWidget(54, 6, 20, 20, class_2561.method_43473(), (button) -> {
         this.searchWidget.field_22764 = !this.searchWidget.field_22764;
         this.moduleList.displayMatches(this.searchTerm = this.searchWidget.field_22764 ? this.searchWidget.method_1882() : null, category);
      }));
      if (this.searchWidget == null) {
         this.searchWidget = new class_342(CactusConstants.mc.field_1772, 78, 6, 200, 20, class_2561.method_43473());
         this.searchWidget.field_22764 = false;
         this.searchWidget.method_1863((s) -> {
            this.searchWidget.method_1887(s.isEmpty() ? this.getTranslatableElement("text.search", new Object[0]) : "");
            this.moduleList.displayMatches(this.searchTerm = s.isEmpty() ? null : s, category);
            if (s.equals("jokesterz")) {
               CactusConstants.APRILFOOLS = !CactusConstants.APRILFOOLS;
               CactusClient.getLogger().warn("Funny mode", new FunnyException("April fools activated"));
               CactusConstants.mc.method_1483().method_4873(class_1109.method_4758(class_3417.field_15195, 1.0F));
               CactusConstants.mc.method_1507((class_437)null);
            }

         });
         this.searchWidget.method_1852("");
      }

      this.method_37063(this.searchWidget);
      this.displayMatches();
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_52706(class_10799.field_56883, SEARCH_ICON, 58, 10, 12, 12);
      if ((Boolean)CactusSettings.get().showModuleDescriptionOnHover.get() && this.moduleDescriptionTooltip != null) {
         context.method_51438(CactusConstants.mc.field_1772, this.moduleDescriptionTooltip, mouseX, mouseY);
      }

      class_327 var10001 = this.field_22793;
      String var10002 = this.getTranslatableElement("text.buttonToggle", new Object[0]);
      int var10004 = this.field_22790;
      Objects.requireNonNull(this.field_22793);
      context.method_25303(var10001, var10002, 4, var10004 - 9 * 2 - 4 - 2, -1);
      var10001 = this.field_22793;
      var10002 = this.getTranslatableElement("text.buttonSettings", new Object[0]);
      var10004 = this.field_22790;
      Objects.requireNonNull(this.field_22793);
      context.method_25303(var10001, var10002, 4, var10004 - 9 - 4, -1);
      String var5 = this.getTranslatableElement("text.listInfo", new Object[]{this.moduleList.method_25396().size(), ModuleManager.get().getModules().size()});
      int var6 = this.field_22789 - 2;
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      RenderUtils.drawTextAlignedRight(context, (String)var5, var6, 32 - 9 - 2, -1, true);
   }

   public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
      if (this.toggleSearchButton.method_25402(click, doubled)) {
         this.method_25395(this.searchWidget);
         return true;
      } else {
         return super.method_25402(click, doubled);
      }
   }

   public boolean method_25404(class_11908 input) {
      if (input.comp_4795() >= 65 && input.comp_4795() <= 90) {
         this.searchWidget.field_22764 = true;
         this.method_25395(this.searchWidget);
      } else if (KeybindManager.menuBind.method_1417(input)) {
         ((KeyMappingAccessor)KeybindManager.menuBind).resetPressState();
         this.method_25419();
         return true;
      }

      return super.method_25404(input);
   }

   public void setCategory(Category searchCategory) {
      category = searchCategory;
      this.displayMatches();
      this.moduleList.method_65506();
   }

   private void displayMatches() {
      this.moduleList.displayMatches(this.searchTerm, category);
   }

   public boolean method_25421() {
      return false;
   }

   static {
      category = Category.ALL;
   }

   public class ModuleListWidget extends GridScrollableWidget<ModuleListScreen.ModuleListWidget.Entry> {
      public ModuleListWidget() {
         super(CactusConstants.mc, 0, 0, 32, 26, 128);
         this.update();
         this.method_46421(118);
      }

      public void update() {
         this.method_25358(ModuleListScreen.this.field_22789 - 116 - 2);
         this.method_53533(ModuleListScreen.this.field_22790 - 72);
      }

      public void method_48579(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
         ModuleListScreen.this.moduleDescriptionTooltip = null;
         super.method_48579(context, mouseX, mouseY, delta);
         if (this.method_25396().isEmpty()) {
            context.method_51439(CactusConstants.mc.field_1772, class_2561.method_43470(ModuleListScreen.this.getTranslatableElement("text.noMatches", new Object[0])).method_27692(class_124.field_1080), this.method_46426() + 8, this.method_46427() + 8, -1, false);
         }

      }

      private void displayMatches(String searchTerm, Category category) {
         this.method_25339();
         ModuleManager.get().getModules().values().forEach((module) -> {
            if ((!module.getOption(Module.Flag.EXPERIMENTAL) || ModuleListScreen.this.experimentsEnabled) && category.contains(module) && (searchTerm == null || Utils.matchesSearch(module.getDisplayName(), searchTerm) || Utils.matchesSearch(module.getDescription() != null ? module.getDescription() : "", searchTerm))) {
               ModuleListScreen.ModuleListWidget.Entry entry = new ModuleListScreen.ModuleListWidget.Entry(module);
               if (module.isFavorite() && (Boolean)CactusSettings.get().favoritesOnTop.get()) {
                  this.method_44399(entry);
               } else {
                  this.addEntry(entry);
               }

            }
         });
      }

      public int method_25342() {
         return this.method_46426();
      }

      public int method_25322() {
         return this.method_25368() - 8;
      }

      protected int method_65507() {
         return this.method_55442() - 6;
      }

      public class Entry extends GridScrollableWidget.GridEntry<ModuleListScreen.ModuleListWidget.Entry> {
         private static final ABValue<class_5250> favoriteStars = ABValue.of("★", "☆").map(class_2561::method_43470).apply((text) -> {
            text.method_27692(class_124.field_1065);
         });
         private final Module module;
         private final String description;

         public Entry(Module module) {
            this.module = module;
            this.description = module.getDescription();
         }

         public void renderGrid(class_332 context, int index, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            boolean active = this.module.active();
            boolean actuallyHovered = hovered && mouseX >= x + 4 && mouseX < x + entryWidth - 4 && mouseY >= y + 4 && mouseY < y + entryHeight - 4;
            boolean renderAsDisabled = ModuleListScreen.this.style == CactusSettings.ModuleButtonStyle.Vanilla && !active;
            boolean experiment = this.module.getOption(Module.Flag.EXPERIMENTAL);
            context.method_51448().pushMatrix();
            this.renderBackground(context, x, y, entryWidth, entryHeight, !renderAsDisabled, actuallyHovered);
            if (RenderUtils.darkMode) {
               context.method_25294(x + 4, y + 4, x + entryWidth - 4, y + entryHeight - 4, -2013265920);
            }

            context.method_51448().popMatrix();
            boolean modText = ModuleListScreen.this.style == CactusSettings.ModuleButtonStyle.TextColor;
            int textColor = -1;
            if (active && modText) {
               textColor = -11141291;
            } else if (renderAsDisabled || modText) {
               textColor = -5592406;
            }

            class_327 var10001 = CactusConstants.mc.field_1772;
            String var10002 = this.module.getDisplayName();
            int var10003 = x + (entryWidth - 18) / 2;
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            context.method_25300(var10001, var10002, var10003, y + (entryHeight - 9) / 2 + 1, textColor);
            context.method_52706(class_10799.field_56883, ModuleListScreen.SETTINGS_ICON, x + entryWidth - 4 - 17, y + 4 + 1, 16, 16);
            if (actuallyHovered || hovered && mouseX < x + 8 && mouseY < y + 8 || this.module.isFavorite()) {
               context.method_51439(CactusConstants.mc.field_1772, (class_2561)favoriteStars.fromBoolean(this.module.isFavorite()), x + 2, y + 2, -1, true);
            }

            if (experiment) {
               context.method_25290(class_10799.field_56883, RenderUtils.TAGS, x + entryWidth - 9, y, 0.0F, 0.0F, 9, 8, 32, 32);
               int mX = mouseX - x;
               int mY = mouseY - y;
               if (mX >= entryWidth - 8 && mX < entryWidth && mY >= 0 && mY < 8) {
                  context.method_51434(CactusConstants.mc.field_1772, ModuleListScreen.this.experimentalWarning, mouseX, mouseY);
               }
            }

            if (actuallyHovered && this.description != null) {
               ModuleListScreen.this.moduleDescriptionTooltip = class_2561.method_43470(this.description);
            }

         }

         private void renderBackground(class_332 context, int x, int y, int entryWidth, int entryHeight, boolean enabled, boolean hovered) {
            boolean active = this.module.active();
            int color = active ? -1442775296 : -1426128896;
            class_2960 texture = enabled ? class_4264.field_45339.method_52729(true, hovered) : RenderUtils.SLIDER_TEXTURES.method_52729(true, hovered);
            context.method_52706(class_10799.field_56883, texture, x + 4, y + 4, entryWidth - 8, entryHeight - 8);
            context.method_52706(class_10799.field_56883, texture, x + entryWidth - 24 + 2, y + 4, entryHeight - 8, entryHeight - 8);
            if (ModuleListScreen.this.style == CactusSettings.ModuleButtonStyle.Border || ModuleListScreen.this.style == CactusSettings.ModuleButtonStyle.BorderActive && active) {
               context.method_73198(x + 3, y + 3, entryWidth - 6, entryHeight - 6, color);
            } else if (ModuleListScreen.this.style == CactusSettings.ModuleButtonStyle.Fill || ModuleListScreen.this.style == CactusSettings.ModuleButtonStyle.FillActive && active) {
               context.method_25294(x + 4, y + 4, x + entryWidth - 4, y + entryHeight - 4, active ? 1146486613 : 1157584213);
            }

         }

         public boolean method_25402(class_11909 click, boolean doubled) {
            int index = ModuleListWidget.this.method_25396().indexOf(this);
            double x = click.comp_4798() - (double)ModuleListWidget.this.getRowLeft(index);
            double y = click.comp_4799() - (double)ModuleListWidget.this.method_25337(index);
            int button = click.comp_4800().comp_4801();
            if ((!(x >= 2.0D) || !(x < 8.0D) || !(y >= 2.0D) || !(y < 8.0D)) && button != 2) {
               if (x >= 4.0D && x < 124.0D && y >= 4.0D && y < 22.0D) {
                  ModuleListWidget.this.method_25354(CactusConstants.mc.method_1483());
                  if (button != 1 && !(x > 108.0D)) {
                     if (button == 0) {
                        this.module.toggle();
                     }
                  } else {
                     CactusConstants.mc.method_1507(new ModuleOptionsScreen(this.module, ModuleListScreen.this));
                  }

                  return true;
               } else {
                  return false;
               }
            } else {
               ModuleListWidget.this.method_25354(CactusConstants.mc.method_1483());
               this.module.setFavorite(!this.module.isFavorite());
               ModuleListScreen.this.displayMatches();
               return true;
            }
         }
      }
   }

   public static class CategoryWidget extends class_4265<ModuleListScreen.CategoryWidget.CategoryEntry> {
      private final ModuleListScreen screen;

      public CategoryWidget(ModuleListScreen screen) {
         super(CactusConstants.mc, 0, 0, 32, 24);
         this.screen = screen;
         this.update();
         ModuleManager.get().getCategories().forEach((c) -> {
            this.method_25321(new ModuleListScreen.CategoryWidget.CategoryEntry(c, this));
         });
      }

      public void update() {
         this.method_53533(this.screen.field_22790 - 72);
         this.method_25358(114);
      }

      public int method_25322() {
         return 100;
      }

      public int method_25342() {
         return this.method_46426() + 4;
      }

      protected int method_65507() {
         return this.method_55442() - 6;
      }

      public static class CategoryEntry extends class_4266<ModuleListScreen.CategoryWidget.CategoryEntry> {
         private final Category category;
         private final ModuleListScreen.CategoryWidget widget;

         public CategoryEntry(Category category, ModuleListScreen.CategoryWidget widget) {
            this.category = category;
            this.widget = widget;
         }

         private boolean hoversButton(int y, double mouseY) {
            return mouseY >= (double)(y + 2) && mouseY < (double)(y + 22);
         }

         public void method_25343(class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int x = this.method_46426() + 2;
            int y = this.method_46427() + 2;
            int entryWidth = this.method_25368() - 4;
            int entryHeight = this.method_25364() - 4;
            boolean canSelect = ModuleListScreen.category != this.category;
            boolean buttonHovered = canSelect && hovered && this.hoversButton(y, (double)mouseY);
            context.method_52706(class_10799.field_56883, class_4185.field_45339.method_52729(canSelect, buttonHovered), x, y, entryWidth, 20);
            if (RenderUtils.darkMode) {
               context.method_25294(x, y, x + entryWidth, y + 20, -2013265920);
            }

            if (this.category.getIcon() != null) {
               context.method_51445(this.category.getIcon(), x + 2, y + 2);
            }

            class_327 var10001 = this.widget.field_22740.field_1772;
            String var10002 = this.category.getName();
            int var10003 = x + 16 + 4;
            Objects.requireNonNull(this.widget.field_22740.field_1772);
            context.method_25303(var10001, var10002, var10003, y + (entryHeight - 9) / 2 + 1, -1);
         }

         public boolean method_25402(class_11909 click, boolean doubled) {
            int index = this.widget.method_25396().indexOf(this);
            int rowTop = this.widget.method_25337(index);
            if (this.hoversButton(rowTop, click.comp_4799()) && ModuleListScreen.category != this.category) {
               this.widget.screen.setCategory(this.category);
               this.widget.field_22740.method_1483().method_4873(class_1109.method_4758((class_3414)class_3417.field_15015.comp_349(), 1.0F));
            }

            return false;
         }

         @NotNull
         public List<? extends class_364> method_25396() {
            return List.of();
         }

         @NotNull
         public List<? extends class_6379> method_37025() {
            return List.of();
         }
      }
   }
}
