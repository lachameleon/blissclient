package com.dwarslooper.cactus.client.gui.hud.element.impl;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.util.AntiAdvertise;
import com.dwarslooper.cactus.client.gui.hud.element.StaticHudElement;
import com.dwarslooper.cactus.client.mixins.accessor.BossHealthOverlayAccessor;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.class_10799;
import net.minecraft.class_1259;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_337;
import net.minecraft.class_345;
import org.joml.Vector2i;

public class BossbarElement extends StaticHudElement<BossbarElement> {
   public static final BossbarElement INSTANCE = new BossbarElement();
   private static final int BAR_WIDTH = 182;
   private static final int BAR_HEIGHT = 5;
   private static final int TITLE_PADDING = 4;
   private static final class_2960 BACKGROUND_TEXTURE = class_2960.method_60656("boss_bar/pink_background");
   private static final class_2960 PROGRESS_TEXTURE = class_2960.method_60656("boss_bar/pink_progress");

   public BossbarElement() {
      super("bossbar", new Vector2i(182, 18));
   }

   public boolean canResize() {
      return false;
   }

   public HudElement.Style getDefaultStyle() {
      return HudElement.Style.Transparent;
   }

   public BossbarElement getInstance() {
      return INSTANCE;
   }

   public void render(class_332 context, int x, int y, int screenWidth, int screenHeight, float delta, boolean inEditor) {
      if (inEditor) {
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         int base = 9 + 4 + 5;
         int dynamicHeight = base + Math.max(0, this.computeVisibleBarCount() - 1) * 19;
         Vector2i size = this.getSize();
         if (size.y() != dynamicHeight) {
            this.resize(size.x(), dynamicHeight);
         }

         super.render(context, x, y, screenWidth, screenHeight, delta, true);
      }

   }

   public void renderContent(class_332 context, int x, int y, int width, int height, int screenWidth, int screenHeight, float delta, boolean inEditor) {
      class_337 bossBarHud = CactusConstants.mc.field_1705.method_1740();
      boolean hasBars = false;
      if (bossBarHud instanceof BossHealthOverlayAccessor) {
         BossHealthOverlayAccessor accessor = (BossHealthOverlayAccessor)bossBarHud;
         Map<?, ?> map = accessor.cactus$getEvents();
         hasBars = map != null && !map.isEmpty();
      }

      if (hasBars) {
         bossBarHud.method_1796(context);
      } else {
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         int barY = y + 9 + 4;
         context.method_70846(class_10799.field_56883, BACKGROUND_TEXTURE, 182, 5, 0, 0, x, barY, 182, 5);
         int progress = 109;
         context.method_70846(class_10799.field_56883, PROGRESS_TEXTURE, 182, 5, 0, 0, x, barY, progress, 5);
         class_2561 title = class_2561.method_43470(this.getName());
         int titleWidth = CactusConstants.mc.field_1772.method_27525(title);
         int titleX = x + (182 - titleWidth) / 2;
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         int titleY = barY - (9 + 4);
         context.method_35720(CactusConstants.mc.field_1772, title.method_30937(), titleX, titleY, -1);
      }
   }

   public Vector2i getEffectiveSize() {
      int count = this.computeVisibleBarCount();
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      int base = 9 + 4 + 5;
      int height = base + Math.max(0, count - 1) * 19;
      return new Vector2i(182, height);
   }

   private int computeVisibleBarCount() {
      class_337 bossBarHud = CactusConstants.mc.field_1705.method_1740();
      int count = 0;
      if (bossBarHud instanceof BossHealthOverlayAccessor) {
         BossHealthOverlayAccessor accessor = (BossHealthOverlayAccessor)bossBarHud;
         Map<UUID, class_345> map = accessor.cactus$getEvents();
         if (map != null) {
            AntiAdvertise antiAds = (AntiAdvertise)ModuleManager.get().get(AntiAdvertise.class);
            if (antiAds != null && antiAds.active() && (Boolean)antiAds.blockBossBar.get()) {
               count = (int)map.values().stream().filter((v) -> {
                  return v instanceof class_1259 && !antiAds.isAd(v.method_5414());
               }).count();
            } else {
               count = map.size();
            }
         }
      }

      return Math.max(1, count);
   }
}
