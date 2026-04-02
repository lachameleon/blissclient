package com.dwarslooper.cactus.client.gui.hud.element.impl;

import com.dwarslooper.cactus.client.gui.hud.element.StaticHudElement;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.ColorSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_266;
import net.minecraft.class_268;
import net.minecraft.class_269;
import net.minecraft.class_332;
import net.minecraft.class_5250;
import net.minecraft.class_8646;
import net.minecraft.class_9011;
import net.minecraft.class_9020;
import net.minecraft.class_9022;
import net.minecraft.class_9025;
import org.joml.Vector2i;

public class ScoreboardElement extends StaticHudElement<ScoreboardElement> {
   public static final ScoreboardElement INSTANCE = new ScoreboardElement();
   private static final Comparator<class_9011> SCOREBOARD_ENTRY_COMPARATOR;
   private static final class_9022 HIDDEN_FORMAT;
   public Setting<Boolean> hideNumbersSetting;
   public Setting<Boolean> customNumberColor;
   public Setting<ColorSetting.ColorValue> numbersColor;
   private boolean existsScoreboard;
   private final List<ScoreboardElement.SidebarEntry> scoreboardEntries;
   private class_2561 scoreboardTitle;
   private int maxWidth;

   public ScoreboardElement() {
      super("scoreboard", new Vector2i(84, 46));
      this.hideNumbersSetting = this.elementGroup.add(new BooleanSetting("hideNumbers", false));
      this.customNumberColor = this.elementGroup.add((new BooleanSetting("customNumberColor", false)).visibleIf(() -> {
         return !(Boolean)this.hideNumbersSetting.get();
      }));
      this.numbersColor = this.elementGroup.add(new ColorSetting("numbersColor", ColorSetting.ColorValue.of(new Color(16711680, false), false), false)).visibleIf(() -> {
         return !(Boolean)this.hideNumbersSetting.get() && (Boolean)this.customNumberColor.get();
      });
      this.existsScoreboard = false;
      this.scoreboardEntries = new ArrayList();
      this.maxWidth = 0;
      ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
         this.updateScoreboard(false);
      });
   }

   public boolean canResize() {
      return false;
   }

   public HudElement.Style getDefaultStyle() {
      return HudElement.Style.Transparent;
   }

   public ScoreboardElement getInstance() {
      return INSTANCE;
   }

   public void render(class_332 context, int x, int y, int screenWidth, int screenHeight, float delta, boolean inEditor) {
      int delimiterSize = CactusConstants.mc.field_1772.method_1727(": ");
      this.updateScoreboard(inEditor);
      if (this.existsScoreboard) {
         this.maxWidth = CactusConstants.mc.field_1772.method_27525(this.scoreboardTitle);

         ScoreboardElement.SidebarEntry sidebarEntry;
         for(Iterator var9 = this.scoreboardEntries.iterator(); var9.hasNext(); this.maxWidth = Math.max(this.maxWidth, CactusConstants.mc.field_1772.method_27525(sidebarEntry.name()) + 4 + (sidebarEntry.scoreWidth > 0 ? delimiterSize + sidebarEntry.scoreWidth : 0))) {
            sidebarEntry = (ScoreboardElement.SidebarEntry)var9.next();
         }

         super.render(context, x, y, screenWidth, screenHeight, delta, inEditor);
      }

   }

   public void renderContent(class_332 context, int x, int y, int width, int height, int screenWidth, int screenHeight, float delta, boolean inEditor) {
      List<ScoreboardElement.SidebarEntry> entries = this.scoreboardEntries;
      int titleWidth = CactusConstants.mc.field_1772.method_27525(this.scoreboardTitle);
      int length = entries.size();
      if (this.style.get() == HudElement.Style.Transparent) {
         context.method_25294(x, y, x + width, y + 9, CactusConstants.mc.field_1690.method_19345(0.4F));
         context.method_25294(x, y + 9, x + width, y + height, CactusConstants.mc.field_1690.method_19345(0.3F));
      }

      context.method_51439(CactusConstants.mc.field_1772, this.scoreboardTitle, x + (width - titleWidth) / 2, y + 1, -1, false);

      for(int t = 0; t < length; ++t) {
         ScoreboardElement.SidebarEntry entry = (ScoreboardElement.SidebarEntry)entries.get(t);
         int u = y + height - (length - t) * 9;
         context.method_51439(CactusConstants.mc.field_1772, entry.name(), x + 2, u, -1, false);
         context.method_51439(CactusConstants.mc.field_1772, entry.score(), x + width - entry.scoreWidth, u, -1, false);
      }

   }

   private ScoreboardElement.SidebarEntry createEntry(String name, int value) {
      class_9022 format = this.settingsOrDefault(class_9025.field_47567);
      class_5250 valueText = format.method_55457(value);
      return new ScoreboardElement.SidebarEntry(class_2561.method_43470(name), valueText, CactusConstants.mc.field_1772.method_27525(valueText));
   }

   private void updateScoreboard(boolean forceDefault) {
      this.existsScoreboard = forceDefault;
      this.scoreboardTitle = class_2561.method_43470("Scoreboard");
      this.scoreboardEntries.clear();
      if (Utils.isNetworkingAvailable() && !forceDefault) {
         class_269 scoreboard = CactusConstants.mc.field_1687.method_8428();
         class_266 objective = scoreboard.method_1189(class_8646.field_45157);
         class_268 playerTeam = scoreboard.method_1164(CactusConstants.mc.field_1724.method_5820());
         if (playerTeam != null) {
            class_8646 scoreboardDisplaySlot = class_8646.method_52622(playerTeam.method_1202());
            if (scoreboardDisplaySlot != null) {
               objective = (class_266)Utils.orElse(scoreboard.method_1189(scoreboardDisplaySlot), objective);
            }
         }

         if (objective != null) {
            class_9022 numberFormat = this.settingsOrDefault(objective.method_55380(class_9025.field_47567));
            ScoreboardElement.SidebarEntry[] sidebarEntries = (ScoreboardElement.SidebarEntry[])CactusConstants.mc.field_1687.method_8428().method_1184(objective).stream().filter((score) -> {
               return !score.method_55385();
            }).sorted(SCOREBOARD_ENTRY_COMPARATOR).limit(15L).map((scoreboardEntry) -> {
               class_268 team = scoreboard.method_1164(scoreboardEntry.comp_2127());
               class_2561 name = scoreboardEntry.method_55387();
               class_5250 styledName = class_268.method_1142(team, name);
               styledName.method_27694((style) -> {
                  if (style.method_10973() == null || style.method_10973().method_27716() == -1) {
                     style.method_36139(((ColorSetting.ColorValue)this.textColor.get()).color());
                  }

                  return style;
               });
               class_2561 styledScore = scoreboardEntry.method_55386(numberFormat);
               int scoreWidth = CactusConstants.mc.field_1772.method_27525(styledScore);
               return new ScoreboardElement.SidebarEntry(styledName, styledScore, scoreWidth);
            }).toArray((x$0) -> {
               return new ScoreboardElement.SidebarEntry[x$0];
            });
            this.scoreboardTitle = objective.method_1114();
            this.scoreboardEntries.addAll(Arrays.asList(sidebarEntries));
            this.existsScoreboard = true;
         }

      } else {
         this.scoreboardEntries.addAll(this.createDefaultEntries());
      }
   }

   private class_9022 settingsOrDefault(class_9022 format) {
      if ((Boolean)this.hideNumbersSetting.get()) {
         return HIDDEN_FORMAT;
      } else {
         return (class_9022)(!(format instanceof class_9020) && (Boolean)this.customNumberColor.get() ? new class_9025(class_2583.field_24360.method_36139(((ColorSetting.ColorValue)this.numbersColor.get()).color())) : format);
      }
   }

   private List<ScoreboardElement.SidebarEntry> createDefaultEntries() {
      return List.of(this.createEntry("§fEntry1", 3), this.createEntry("§fEntry2", 2), this.createEntry("§fEntry3", 1), this.createEntry("§acactusmod.xyz", 0));
   }

   public Vector2i getEffectiveSize() {
      int lineHeight = 9;
      int entriesHeight = this.scoreboardEntries.size() * lineHeight;
      return new Vector2i(this.maxWidth, entriesHeight + 10);
   }

   static {
      SCOREBOARD_ENTRY_COMPARATOR = Comparator.comparing(class_9011::comp_2128).reversed().thenComparing(class_9011::comp_2127, String.CASE_INSENSITIVE_ORDER);
      HIDDEN_FORMAT = class_9020.field_47557;
   }

   private static record SidebarEntry(class_2561 name, class_2561 score, int scoreWidth) {
      private SidebarEntry(class_2561 name, class_2561 score, int scoreWidth) {
         this.name = name;
         this.score = score;
         this.scoreWidth = scoreWidth;
      }

      public class_2561 name() {
         return this.name;
      }

      public class_2561 score() {
         return this.score;
      }

      public int scoreWidth() {
         return this.scoreWidth;
      }
   }
}
