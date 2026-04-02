package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.BackendPacketReceiveEvent;
import com.dwarslooper.cactus.client.gui.screen.EventHandlingCScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.GridScrollableWidget;
import com.dwarslooper.cactus.client.irc.protocol.PacketIn;
import com.dwarslooper.cactus.client.irc.protocol.packets.UserInfoS2CPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.cape.UpdateCapeBiDPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.cosmetic.ClientCosmeticListS2CPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.cosmetic.GetAvailableCosmeticsC2SPacket;
import com.dwarslooper.cactus.client.irc.protocol.packets.cosmetic.UpdateCosmeticsC2SPacket;
import com.dwarslooper.cactus.client.systems.profile.AbstractCosmetic;
import com.dwarslooper.cactus.client.systems.profile.CosmeticList;
import com.dwarslooper.cactus.client.systems.profile.ProfileHandler;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import com.mojang.authlib.GameProfile;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.class_10042;
import net.minecraft.class_10799;
import net.minecraft.class_1109;
import net.minecraft.class_11909;
import net.minecraft.class_156;
import net.minecraft.class_1664;
import net.minecraft.class_2477;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_3417;
import net.minecraft.class_342;
import net.minecraft.class_3532;
import net.minecraft.class_4264;
import net.minecraft.class_437;
import net.minecraft.class_5244;
import net.minecraft.class_638;
import net.minecraft.class_6382;
import net.minecraft.class_745;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class CosmeticsListScreen extends EventHandlingCScreen {
   private static final class_2561 PREVIEW_TITLE = class_2561.method_43471("gui.screen.cosmetics.title.preview");
   private static final class_2561 PREVIEW_HINT = class_2561.method_43471("gui.screen.cosmetics.text.dragToRotate");
   private final Map<AbstractCosmetic<?>, Boolean> selections = new LinkedHashMap();
   private class_342 searchWidget;
   private CTextureButtonWidget reloadButton;
   private CosmeticsListScreen.Widget widget;
   private CosmeticsListScreen.CosmeticPreviewPanel previewPanel;
   private boolean modified = false;
   private boolean loaded = false;

   public CosmeticsListScreen(class_437 parent) {
      super("cosmetics");
      this.parent = parent;
      this.update();
   }

   public void update() {
      this.loaded = false;
      this.selections.clear();
      CosmeticList list = ProfileHandler.me().getCosmetics();
      ProfileHandler.getClientAvailableCosmetics(true).forEach((c) -> {
         this.selections.putIfAbsent(c, list != null && list.all().stream().anyMatch((ic) -> {
            return ic.getId().equals(c.getId());
         }));
      });
      this.loaded = true;
   }

   public void method_25426() {
      super.method_25426();
      int topBarY = 30;
      int contentTop = topBarY + 26;
      int contentHeight = this.field_22790 - contentTop - 44;
      int listX = 6;
      int listWidth = this.field_22789 - 12;
      if (CactusConstants.mc.field_1687 != null) {
         int previewX = 6;
         int previewWidth = (int)class_3532.method_15350((double)this.field_22789 / 3.2D, 80.0D, 330.0D);
         listX = previewX + previewWidth + 12;
         listWidth = this.field_22789 - listX - 6;
         this.previewPanel = new CosmeticsListScreen.CosmeticPreviewPanel(previewX, contentTop, previewWidth, contentHeight);
      }

      this.reloadButton = new CTextureButtonWidget(listX + listWidth - 20, topBarY, 300, (button) -> {
         button.field_22763 = false;
         CactusClient.getInstance().getIrcClient().sendPacket(new GetAvailableCosmeticsC2SPacket());
      });
      if (this.widget == null) {
         this.widget = new CosmeticsListScreen.Widget(listWidth, contentHeight, contentTop);
      }

      this.widget.method_46421(listX);
      this.widget.method_25358(listWidth);
      this.widget.method_53533(contentHeight);
      this.widget.reload();
      int searchWidth = Math.max(160, listWidth - 26);
      if (this.searchWidget == null) {
         this.searchWidget = new class_342(CactusConstants.mc.field_1772, listX, topBarY, searchWidth, 20, class_2561.method_43473());
         this.searchWidget.method_1863((s) -> {
            this.searchWidget.method_1887(s.isEmpty() ? class_2477.method_10517().method_48307("gui.screen.modules.text.search") : "");
            this.widget.updateFilter(s.isEmpty() ? null : s);
         });
         this.searchWidget.method_1852("");
      } else {
         this.searchWidget.method_46421(listX);
         this.searchWidget.method_46419(topBarY);
         this.searchWidget.method_25358(searchWidth);
      }

      this.widget.method_65506();
      this.method_37063(this.searchWidget);
      this.method_37063(this.reloadButton);
      if (this.previewPanel != null) {
         this.method_37063(this.previewPanel);
      }

      this.method_37063(this.widget);
      this.method_37063(new CButtonWidget(this.field_22789 / 2 - 150 - 2, this.field_22790 - 32, 150, 20, class_5244.field_24335, (button) -> {
         this.method_25419();
      }));
      this.method_37063(new CButtonWidget(this.field_22789 / 2 + 2, this.field_22790 - 32, 150, 20, class_5244.field_24334, (button) -> {
         if (this.modified) {
            if (this.selections.keySet().stream().anyMatch((c) -> {
               return c instanceof AbstractCosmetic.Cape;
            })) {
               UpdateCapeBiDPacket.checkCapeOptionsAndWarn();
            }

            CactusClient.getInstance().getIrcClient().sendPacket(new UpdateCosmeticsC2SPacket(this.selections.entrySet().stream().filter(Entry::getValue).map(Entry::getKey).map(AbstractCosmetic::getId).toList()));
         }

         this.method_25419();
      }));
      this.method_48265(this.searchWidget);
      this.syncToPreview();
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 8, -1);
   }

   private void toggleSelection(AbstractCosmetic<?> cosmetic) {
      this.modified = true;
      this.selections.computeIfPresent(cosmetic, (ic, enabled) -> {
         boolean state = !enabled;
         if (state) {
            this.selections.replaceAll((otherCosmetic, currentState) -> {
               return otherCosmetic != ic && !ic.canEquipWith(otherCosmetic) ? false : currentState;
            });
         }

         return state;
      });
      this.syncToPreview();
      CactusConstants.mc.method_1483().method_4873(class_1109.method_47978(class_3417.field_15015, 1.0F));
   }

   private void syncToPreview() {
      if (this.previewPanel != null) {
         Set<AbstractCosmetic<?>> selected = this.getSelected();
         this.previewPanel.previewEntity.updateCosmetics(new CosmeticList(selected));
      }

   }

   private Set<AbstractCosmetic<?>> getSelected() {
      return (Set)this.selections.entrySet().stream().filter(Entry::getValue).map(Entry::getKey).collect(Collectors.toSet());
   }

   @EventHandler
   public void updatePacketReceived(BackendPacketReceiveEvent event) {
      if (!(event.packet() instanceof ClientCosmeticListS2CPacket)) {
         PacketIn var3 = event.packet();
         if (!(var3 instanceof UserInfoS2CPacket)) {
            return;
         }

         UserInfoS2CPacket userInfo = (UserInfoS2CPacket)var3;
         if (!userInfo.getUuid().equals(CactusConstants.mc.method_1548().method_44717())) {
            return;
         }
      }

      CactusConstants.mc.execute(() -> {
         this.update();
         this.method_41843();
         this.reloadButton.field_22763 = true;
      });
   }

   private static class CosmeticPreviewPanel extends class_339 {
      private static final float AUTO_ROTATION_SPEED = 18.0F;
      private static final float ENTITY_SCALE_FACTOR = 0.58F;
      private float yaw = 180.0F;
      private float pitch = -6.0F;
      private boolean dragging = false;
      private long lastUserRotation = class_156.method_658();
      private long lastFrameMillis = class_156.method_658();
      private CosmeticsListScreen.PreviewPlayer previewEntity;

      private CosmeticPreviewPanel(int x, int y, int width, int height) {
         super(x, y, width, height, class_2561.method_43473());
         if (CactusConstants.mc.field_1687 != null && CactusConstants.mc.field_1724 != null) {
            this.previewEntity = new CosmeticsListScreen.PreviewPlayer(CactusConstants.mc.field_1687, CactusConstants.mc.method_53462());
         }

      }

      public void method_48579(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
         context.method_25294(this.method_46426(), this.method_46427() - 2, this.method_46426() + this.field_22758, this.method_46427() + this.field_22759 + 2, 1711276032);
         context.method_73198(this.method_46426(), this.method_46427() - 2, this.field_22758, this.field_22759 + 4, -9803158);
         context.method_51439(CactusConstants.mc.field_1772, CosmeticsListScreen.PREVIEW_TITLE, this.method_46426() + 8, this.method_46427() + 6, -2039584, false);
         class_327 var10001 = CactusConstants.mc.field_1772;
         class_2561 var10002 = CosmeticsListScreen.PREVIEW_HINT;
         int var10003 = this.method_46426() + 8;
         int var10004 = this.method_46427() + 6;
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         context.method_51439(var10001, var10002, var10003, var10004 + 9, -5987164, false);
         if (this.previewEntity != null) {
            long now = class_156.method_658();
            float frameSeconds = Math.min(0.05F, (float)(now - this.lastFrameMillis) / 1000.0F);
            this.lastFrameMillis = now;
            if (!this.dragging && now - this.lastUserRotation > 1100L) {
               this.yaw += 18.0F * frameSeconds;
            }

            this.yaw = class_3532.method_15393(this.yaw);
            this.pitch = class_3532.method_15363(this.pitch, -24.0F, 24.0F);
            int yOffset = 40;
            int previewTop = this.method_46427() + yOffset;
            int previewBottom = this.method_46427() + this.field_22759 - 12;
            int renderHeight = previewBottom - previewTop;
            if (renderHeight > 24) {
               int scale = Math.max(28, Math.round((float)renderHeight * 0.58F * 0.7F));
               Quaternionf rotation = (new Quaternionf()).rotateXYZ(0.017453292F * this.pitch, 0.017453292F * this.yaw, 3.1415927F);
               RenderUtils.drawEntityAligned(context, this.method_46426() + 8, this.method_46427() + yOffset, this.field_22758 - 8, this.field_22759 - yOffset - 12, scale, this.previewEntity, rotation, (Quaternionf)null, (state) -> {
                  class_10042 living = (class_10042)state;
                  living.field_53448 = 0.0F;
                  living.field_53447 = 0.0F;
                  living.field_53446 = 0.0F;
                  living.field_53337 = null;
               });
            }
         }

      }

      public boolean method_25402(class_11909 click, boolean doubled) {
         if (click.method_74245() != 0) {
            return false;
         } else {
            this.dragging = true;
            this.lastUserRotation = class_156.method_658();
            return true;
         }
      }

      public boolean method_25406(@NotNull class_11909 click) {
         if (this.dragging && click.method_74245() == 0) {
            this.dragging = false;
            return true;
         } else {
            return false;
         }
      }

      public boolean method_25403(@NotNull class_11909 click, double deltaX, double deltaY) {
         if (this.dragging && click.method_74245() == 0) {
            this.yaw += (float)(-deltaX * 1.4500000476837158D);
            this.pitch += (float)(deltaY * 0.8500000238418579D);
            this.lastUserRotation = class_156.method_658();
            return true;
         } else {
            return false;
         }
      }

      public void method_47399(@NotNull class_6382 narrationElementOutput) {
      }
   }

   public class Widget extends GridScrollableWidget<CosmeticsListScreen.Widget.CosmeticEntry> {
      private static final class_2561 noneText = class_2561.method_43471("gui.screen.cosmetics.none");
      private static final class_2561 noneSearchText = class_2561.method_43471("gui.screen.cosmetics.noneSearch");
      private static final class_2561 loadingText = class_2561.method_43471("gui.screen.cosmetics.loading");
      private String filter;

      public Widget(int width, int height, int y) {
         super(CactusConstants.mc, width, height, y, 26, 126);
      }

      public void updateFilter(String filter) {
         this.filter = filter;
         this.reload();
      }

      public void reload() {
         this.method_25339();
         CosmeticsListScreen.this.selections.keySet().forEach((ic) -> {
            if (Utils.matchesSearch(ic.getName(), this.filter) || Utils.matchesSearch(ic.getId(), this.filter)) {
               this.addEntry(new CosmeticsListScreen.Widget.CosmeticEntry(ic));
            }

         });
         this.method_65506();
         CosmeticsListScreen.this.loaded = true;
      }

      public void method_48579(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
         super.method_48579(context, mouseX, mouseY, delta);
         if (this.method_25396().isEmpty()) {
            context.method_27534(CactusConstants.mc.field_1772, CosmeticsListScreen.this.loaded ? (this.filter == null ? noneText : noneSearchText) : loadingText, this.method_46426() + this.method_25368() / 2, this.method_46427() + this.method_25364() / 2, -5592406);
         }

      }

      public int method_25322() {
         return this.field_22758 - 28;
      }

      protected int method_65507() {
         return this.method_55442() - 6;
      }

      public class CosmeticEntry extends GridScrollableWidget.GridEntry<CosmeticsListScreen.Widget.CosmeticEntry> {
         private final AbstractCosmetic<?> cosmetic;

         public CosmeticEntry(AbstractCosmetic<?> cosmetic) {
            this.cosmetic = cosmetic;
         }

         public void renderGrid(class_332 context, int index, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            boolean selected = (Boolean)CosmeticsListScreen.this.selections.get(this.cosmetic);
            boolean actuallyHovered = hovered && mouseX >= x + 4 && mouseX < x + entryWidth - 4 && mouseY >= y + 4 && mouseY < y + entryHeight - 4;
            context.method_52706(class_10799.field_56883, selected ? class_4264.field_45339.method_52729(true, actuallyHovered) : RenderUtils.SLIDER_TEXTURES.method_52729(false, actuallyHovered), x + 4, y + 4, entryWidth - 8, entryHeight - 8);
            context.method_44379(x, y, x + entryWidth, y + entryHeight);
            int center = x + (entryWidth + 16) / 2;
            context.method_51445(this.cosmetic.getDisplayIcon().method_7854(), x + 5, y + 5);
            context.method_51742(x + 4 + 16 + 1, y + 4, y + entryHeight - 1 - 4, 1140850688);
            class_327 var10001 = CactusConstants.mc.field_1772;
            String var10002 = this.cosmetic.getName();
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            context.method_25300(var10001, var10002, center, y + (entryHeight - 9) / 2 + 1, selected ? -1 : -5592406);
            context.method_44380();
         }

         public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
            if (click.method_74245() != 0) {
               return false;
            } else {
               CosmeticsListScreen.this.toggleSelection(this.cosmetic);
               return true;
            }
         }
      }
   }

   public static class PreviewPlayer extends class_745 {
      private final ProfileHandler profileOverride = new ProfileHandler(new UUID(0L, 0L));

      public PreviewPlayer(class_638 clientLevel, GameProfile gameProfile) {
         super(clientLevel, gameProfile);
      }

      public boolean method_74091(@NotNull class_1664 playerModelPart) {
         return true;
      }

      public void updateCosmetics(CosmeticList list) {
         this.profileOverride.updateCosmetics(list);
      }

      public ProfileHandler getProfileOverride() {
         return this.profileOverride;
      }
   }
}
