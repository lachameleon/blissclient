package com.dwarslooper.cactus.client.feature.modules.util;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.ClientTickEvent;
import com.dwarslooper.cactus.client.event.impl.MouseClickEvent;
import com.dwarslooper.cactus.client.event.impl.MouseScrollEvent;
import com.dwarslooper.cactus.client.event.impl.RawKeyEvent;
import com.dwarslooper.cactus.client.feature.content.ContentPack;
import com.dwarslooper.cactus.client.feature.content.ContentPackManager;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.gui.widget.RadialMenuComponent;
import com.dwarslooper.cactus.client.systems.config.settings.group.SettingGroup;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.IntegerSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.KeybindSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.StringSetting;
import com.dwarslooper.cactus.client.systems.key.KeyBind;
import com.dwarslooper.cactus.client.systems.profile.AbstractCosmetic;
import com.dwarslooper.cactus.client.systems.profile.EmoteExecutor;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_1109;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3532;
import net.minecraft.class_5250;
import org.jetbrains.annotations.Nullable;

public class EmoteQuickSelector extends Module {
   private static final int SLOT_COUNT = 8;
   private static final int RECENT_CAPACITY = 12;
   private static final String SMOOTH_ANIMATION_PACK = "smooth_animations";
   private final SettingGroup sgWheel;
   private final SettingGroup sgFeedback;
   private final SettingGroup sgQuickSlots;
   public Setting<KeyBind> wheelKey;
   public Setting<Integer> wheelSize;
   public Setting<Integer> animationSpeed;
   public Setting<Integer> centerDeadzone;
   public Setting<Integer> snappingBias;
   public Setting<Boolean> selectionSound;
   public Setting<String> slot1Emote;
   public Setting<KeyBind> slot1Key;
   public Setting<String> slot2Emote;
   public Setting<KeyBind> slot2Key;
   public Setting<String> slot3Emote;
   public Setting<KeyBind> slot3Key;
   public Setting<String> slot4Emote;
   public Setting<KeyBind> slot4Key;
   public Setting<String> slot5Emote;
   public Setting<KeyBind> slot5Key;
   public Setting<String> slot6Emote;
   public Setting<KeyBind> slot6Key;
   public Setting<String> slot7Emote;
   public Setting<KeyBind> slot7Key;
   public Setting<String> slot8Emote;
   public Setting<KeyBind> slot8Key;
   private final List<Setting<String>> quickSlotEmotes;
   private final List<Setting<KeyBind>> quickSlotKeys;
   private final RadialMenuComponent<EmoteQuickSelector.WheelEntry> radialMenu;
   private final List<RadialMenuComponent.Entry<EmoteQuickSelector.WheelEntry>> radialEntries;
   private final List<EmoteQuickSelector.WheelEntry> workingEntries;
   private final ArrayDeque<AbstractCosmetic.Emote> recentEmotes;
   private boolean wheelVisible;
   private boolean wasWheelPressed;
   private boolean shouldGrabMouseOnClose;
   private float openProgress;
   private int previousSelection;
   private long lastFrameNanos;
   private EmoteQuickSelector.WheelCategory activeCategory;

   public EmoteQuickSelector() {
      super("emoteQuickSelector", ModuleManager.CATEGORY_UTILITY, (new Module.Options()).set(Module.Flag.HUD_LISTED, false));
      this.sgWheel = this.settings.buildGroup("wheel");
      this.sgFeedback = this.settings.buildGroup("feedback");
      this.sgQuickSlots = this.settings.buildGroup("quickSlots");
      this.wheelKey = this.mainGroup.add(new KeybindSetting("wheelKey", KeyBind.of(71)));
      this.wheelSize = this.sgWheel.add((new IntegerSetting("wheelSize", 140)).min(50).max(220));
      this.animationSpeed = this.sgWheel.add((new IntegerSetting("animationSpeed", 14)).min(1).max(40));
      this.centerDeadzone = this.sgWheel.add((new IntegerSetting("centerDeadzone", 18)).min(6).max(70));
      this.snappingBias = this.sgWheel.add((new IntegerSetting("snappingBias", 14)).min(0).max(48));
      this.selectionSound = this.sgFeedback.add(new BooleanSetting("selectionSound", true));
      this.slot1Emote = this.sgQuickSlots.add((new StringSetting("slot1Emote", "")).setMaxLength(96));
      this.slot1Key = this.sgQuickSlots.add(new KeybindSetting("slot1Key", KeyBind.none()));
      this.slot2Emote = this.sgQuickSlots.add((new StringSetting("slot2Emote", "")).setMaxLength(96));
      this.slot2Key = this.sgQuickSlots.add(new KeybindSetting("slot2Key", KeyBind.none()));
      this.slot3Emote = this.sgQuickSlots.add((new StringSetting("slot3Emote", "")).setMaxLength(96));
      this.slot3Key = this.sgQuickSlots.add(new KeybindSetting("slot3Key", KeyBind.none()));
      this.slot4Emote = this.sgQuickSlots.add((new StringSetting("slot4Emote", "")).setMaxLength(96));
      this.slot4Key = this.sgQuickSlots.add(new KeybindSetting("slot4Key", KeyBind.none()));
      this.slot5Emote = this.sgQuickSlots.add((new StringSetting("slot5Emote", "")).setMaxLength(96));
      this.slot5Key = this.sgQuickSlots.add(new KeybindSetting("slot5Key", KeyBind.none()));
      this.slot6Emote = this.sgQuickSlots.add((new StringSetting("slot6Emote", "")).setMaxLength(96));
      this.slot6Key = this.sgQuickSlots.add(new KeybindSetting("slot6Key", KeyBind.none()));
      this.slot7Emote = this.sgQuickSlots.add((new StringSetting("slot7Emote", "")).setMaxLength(96));
      this.slot7Key = this.sgQuickSlots.add(new KeybindSetting("slot7Key", KeyBind.none()));
      this.slot8Emote = this.sgQuickSlots.add((new StringSetting("slot8Emote", "")).setMaxLength(96));
      this.slot8Key = this.sgQuickSlots.add(new KeybindSetting("slot8Key", KeyBind.none()));
      this.quickSlotEmotes = List.of(this.slot1Emote, this.slot2Emote, this.slot3Emote, this.slot4Emote, this.slot5Emote, this.slot6Emote, this.slot7Emote, this.slot8Emote);
      this.quickSlotKeys = List.of(this.slot1Key, this.slot2Key, this.slot3Key, this.slot4Key, this.slot5Key, this.slot6Key, this.slot7Key, this.slot8Key);
      this.radialMenu = new RadialMenuComponent();
      this.radialEntries = new ArrayList();
      this.workingEntries = new ArrayList();
      this.recentEmotes = new ArrayDeque();
      this.previousSelection = -1;
      this.lastFrameNanos = -1L;
      this.activeCategory = EmoteQuickSelector.WheelCategory.FAVORITES;
   }

   public void onDisable() {
      this.closeWheel(false, true);
      this.wasWheelPressed = false;
      this.openProgress = 0.0F;
      this.previousSelection = -1;
      this.lastFrameNanos = -1L;
   }

   @EventHandler
   public void onTick(ClientTickEvent event) {
      if (CactusConstants.mc.field_1724 != null && CactusConstants.mc.field_1687 != null && this.active()) {
         if (CactusConstants.mc.field_1755 != null) {
            this.closeWheel(false, true);
            this.wasWheelPressed = false;
         } else {
            boolean wheelPressed = ((KeyBind)this.wheelKey.get()).isBound() && ((KeyBind)this.wheelKey.get()).isPressed();
            if (wheelPressed && !this.wasWheelPressed) {
               this.openWheel();
            } else if (!wheelPressed && this.wasWheelPressed && this.wheelVisible) {
               this.closeWheel(true, false);
            }

            this.wasWheelPressed = wheelPressed;
            if (this.wheelVisible) {
               this.updateSelection();
            }

         }
      } else {
         this.closeWheel(false, true);
         this.wasWheelPressed = false;
      }
   }

   @EventHandler
   public void onRawKey(RawKeyEvent event) {
      if (this.active() && CactusConstants.mc.field_1755 == null && event.getAction() == 1) {
         if (this.wheelVisible) {
            if (event.getKey() == 256) {
               this.closeWheel(false, false);
            }

            event.cancel();
         } else {
            for(int slot = 0; slot < 8; ++slot) {
               KeyBind bind = (KeyBind)((Setting)this.quickSlotKeys.get(slot)).get();
               if (bind.isBound() && bind.key() == event.getKey()) {
                  if (this.triggerQuickSlot(slot)) {
                     event.cancel();
                  }

                  return;
               }
            }

         }
      }
   }

   @EventHandler
   public void onMouseClick(MouseClickEvent event) {
      if (this.wheelVisible) {
         event.cancel();
      }

   }

   @EventHandler
   public void onScroll(MouseScrollEvent event) {
      if (this.wheelVisible) {
         if (event.getVertical() != 0.0D) {
            this.cycleCategory(event.getVertical() > 0.0D ? 1 : -1);
            this.updateSelection();
         }

         event.cancel();
      }

   }

   public void renderWheel(class_332 context) {
      if (this.active() && (this.wheelVisible || !(this.openProgress <= 0.001F))) {
         this.updateAnimation();
         if (!this.wheelVisible && this.openProgress <= 0.001F) {
            this.finishClosing();
         } else {
            int guiWidth = context.method_51421();
            int guiHeight = context.method_51443();
            context.method_25294(0, 0, guiWidth, guiHeight, 1426063360);
            context.method_51448().pushMatrix();
            float eased = this.isSmoothAnimationEnabled() ? easeOutCubic(this.openProgress) : this.openProgress;
            float s = 0.82F + eased * 0.18F;
            context.method_51448().translate((float)guiWidth / 2.0F, (float)guiHeight / 2.0F);
            context.method_51448().scale(s, s);
            context.method_51448().translate((float)(-guiWidth) / 2.0F, (float)(-guiHeight) / 2.0F);
            int cx = guiWidth / 2;
            int cy = guiHeight / 2;
            int radius = (Integer)this.wheelSize.get();
            int slotWidth = 92;
            int slotHeight = 18;
            int accent = CactusClient.getInstance().getRGB() | -16777216;
            this.radialMenu.render(context, cx, cy, radius, slotWidth, slotHeight, accent & 1728053247, -1441722095, accent, -12566464);
            this.renderCenterPreview(context, cx, cy, accent);
            context.method_51448().popMatrix();
         }
      }
   }

   private void renderCenterPreview(class_332 context, int cx, int cy, int accent) {
      int width = 146;
      int height = 36;
      context.method_25294(cx - width / 2, cy - height / 2, cx + width / 2, cy + height / 2, -1341124592);
      context.method_73198(cx - width / 2, cy - height / 2, width, height, accent & -855638017);
      RadialMenuComponent.Entry<EmoteQuickSelector.WheelEntry> selected = this.radialMenu.getSelectedEntry();
      class_5250 topLine;
      class_5250 bottomLine;
      if (this.radialMenu.getEntries().isEmpty()) {
         topLine = class_2561.method_43471("modules.emoteQuickSelector.text.noEmotes");
         bottomLine = class_2561.method_43473();
      } else if (selected != null) {
         topLine = class_2561.method_43470(this.trimToWidth(((EmoteQuickSelector.WheelEntry)selected.value()).emote().getName(), width - 8));
         bottomLine = class_2561.method_43469("modules.emoteQuickSelector.text.category", new Object[]{this.categoryKey(this.activeCategory)});
      } else {
         topLine = class_2561.method_43471("modules.emoteQuickSelector.text.holdToSelect");
         bottomLine = class_2561.method_43469("modules.emoteQuickSelector.text.category", new Object[]{this.categoryKey(this.activeCategory)});
      }

      context.method_27534(CactusConstants.mc.field_1772, topLine, cx, cy - 9, -1);
      context.method_27534(CactusConstants.mc.field_1772, bottomLine, cx, cy + 4, -4671304);
   }

   private void openWheel() {
      if (!this.wheelVisible && CactusConstants.mc.field_1755 == null) {
         this.wheelVisible = true;
         this.openProgress = 0.0F;
         this.previousSelection = -1;
         this.activeCategory = EmoteQuickSelector.WheelCategory.FAVORITES;
         this.shouldGrabMouseOnClose = CactusConstants.mc.field_1729.method_1613();
         if (this.shouldGrabMouseOnClose) {
            CactusConstants.mc.field_1729.method_1610();
         }

         this.rebuildEntries(this.activeCategory);
         this.updateSelection();
      }
   }

   private void closeWheel(boolean executeSelection, boolean immediate) {
      if (this.wheelVisible || immediate) {
         if (executeSelection) {
            this.executeCurrentSelection();
         }

         this.wheelVisible = false;
         if (immediate || !this.isSmoothAnimationEnabled()) {
            this.openProgress = 0.0F;
            this.finishClosing();
         }

      }
   }

   private void finishClosing() {
      this.radialMenu.clearSelection();
      this.previousSelection = -1;
      if (this.shouldGrabMouseOnClose && CactusConstants.mc.field_1755 == null) {
         CactusConstants.mc.field_1729.method_1612();
      }

      this.shouldGrabMouseOnClose = false;
   }

   private void executeCurrentSelection() {
      RadialMenuComponent.Entry<EmoteQuickSelector.WheelEntry> selected = this.radialMenu.getSelectedEntry();
      if (selected != null) {
         EmoteExecutor.useEmote(((EmoteQuickSelector.WheelEntry)selected.value()).emote());
         this.registerRecent(((EmoteQuickSelector.WheelEntry)selected.value()).emote());
      }
   }

   private void updateSelection() {
      this.radialMenu.updateSelection(this.getGuiMouseX(), this.getGuiMouseY(), (double)CactusConstants.mc.method_22683().method_4486() / 2.0D, (double)CactusConstants.mc.method_22683().method_4502() / 2.0D, (float)(Integer)this.centerDeadzone.get(), (float)(Integer)this.snappingBias.get() / 100.0F);
      int selected = this.radialMenu.getSelectedIndex();
      if (selected != this.previousSelection) {
         this.previousSelection = selected;
         this.playSelectionSound();
      }

   }

   private void updateAnimation() {
      float deltaSeconds = this.frameDeltaSeconds();
      boolean smoothEnabled = this.isSmoothAnimationEnabled();
      float target = this.wheelVisible ? 1.0F : 0.0F;
      float speed;
      if (smoothEnabled) {
         speed = Math.max(1.0F, (float)(Integer)this.animationSpeed.get()) * 0.55F;
         float alpha = 1.0F - (float)Math.exp((double)(-speed * deltaSeconds));
         this.openProgress += (target - this.openProgress) * alpha;
      } else {
         speed = Math.max(1.0F, (float)(Integer)this.animationSpeed.get()) * 0.06F * Math.max(1.0F, deltaSeconds * 60.0F);
         this.openProgress = class_3532.method_15348(this.openProgress, target, speed);
      }

      this.openProgress = class_3532.method_15363(this.openProgress, 0.0F, 1.0F);
      this.radialMenu.updateAnimation(deltaSeconds, smoothEnabled ? 14.0F : 1000.0F);
   }

   private void cycleCategory(int direction) {
      EmoteQuickSelector.WheelCategory[] values = EmoteQuickSelector.WheelCategory.values();
      int index = this.activeCategory.ordinal();
      int next = Math.floorMod(index + direction, values.length);
      this.rebuildEntries(values[next]);
   }

   private void rebuildEntries(EmoteQuickSelector.WheelCategory preferredCategory) {
      this.workingEntries.clear();
      this.workingEntries.addAll(this.buildEntriesFor(preferredCategory));
      if (this.workingEntries.isEmpty() && preferredCategory != EmoteQuickSelector.WheelCategory.ALL) {
         preferredCategory = EmoteQuickSelector.WheelCategory.ALL;
         this.workingEntries.clear();
         this.workingEntries.addAll(this.buildEntriesFor(preferredCategory));
      }

      this.activeCategory = preferredCategory;
      this.radialEntries.clear();

      for(int i = 0; i < this.workingEntries.size(); ++i) {
         EmoteQuickSelector.WheelEntry entry = (EmoteQuickSelector.WheelEntry)this.workingEntries.get(i);
         this.radialEntries.add(new RadialMenuComponent.Entry(this.buildEntryLabel(entry), entry));
      }

      this.radialMenu.setEntries(this.radialEntries);
      this.radialMenu.clearSelection();
      this.previousSelection = -1;
   }

   private List<EmoteQuickSelector.WheelEntry> buildEntriesFor(EmoteQuickSelector.WheelCategory category) {
      List<EmoteQuickSelector.WheelEntry> entries = new ArrayList();
      int slot;
      switch(category.ordinal()) {
      case 0:
         for(slot = 0; slot < 8; ++slot) {
            AbstractCosmetic.Emote emote = this.resolveSlotEmote(slot);
            if (emote != null) {
               entries.add(new EmoteQuickSelector.WheelEntry(slot, emote));
            }
         }

         return entries;
      case 1:
         slot = 0;
         Iterator var7 = this.recentEmotes.iterator();

         while(var7.hasNext()) {
            AbstractCosmetic.Emote emote = (AbstractCosmetic.Emote)var7.next();
            entries.add(new EmoteQuickSelector.WheelEntry(slot++, emote));
         }

         return entries;
      case 2:
         List<AbstractCosmetic.Emote> all = EmoteExecutor.getAvailableEmotes();

         for(int i = 0; i < all.size(); ++i) {
            entries.add(new EmoteQuickSelector.WheelEntry(i, (AbstractCosmetic.Emote)all.get(i)));
         }
      }

      return entries;
   }

   private void registerRecent(AbstractCosmetic.Emote emote) {
      if (emote != null) {
         this.recentEmotes.removeIf((existing) -> {
            return existing.getId().equalsIgnoreCase(emote.getId());
         });
         this.recentEmotes.addFirst(emote);

         while(this.recentEmotes.size() > 12) {
            this.recentEmotes.removeLast();
         }

      }
   }

   private boolean triggerQuickSlot(int slot) {
      AbstractCosmetic.Emote emote = this.resolveSlotEmote(slot);
      if (emote == null) {
         return false;
      } else {
         EmoteExecutor.useEmote(emote);
         this.registerRecent(emote);
         return true;
      }
   }

   @Nullable
   private AbstractCosmetic.Emote resolveSlotEmote(int slot) {
      if (slot >= 0 && slot < 8) {
         String id = ((String)((Setting)this.quickSlotEmotes.get(slot)).get()).trim();
         if (id.isEmpty()) {
            return null;
         } else {
            Iterator var3 = EmoteExecutor.getAvailableEmotes().iterator();

            AbstractCosmetic.Emote emote;
            do {
               if (!var3.hasNext()) {
                  return null;
               }

               emote = (AbstractCosmetic.Emote)var3.next();
            } while(!emote.getId().equalsIgnoreCase(id));

            return emote;
         }
      } else {
         return null;
      }
   }

   private void playSelectionSound() {
      if (this.wheelVisible && (Boolean)this.selectionSound.get() && this.radialMenu.getSelectedIndex() >= 0 && CactusConstants.mc.method_1483() != null) {
         CactusConstants.mc.method_1483().method_4873(class_1109.method_4758((class_3414)class_3417.field_15015.comp_349(), 0.46F));
      }
   }

   private String buildEntryLabel(EmoteQuickSelector.WheelEntry entry) {
      String base = entry.slot() < 8 ? entry.slot() + 1 + ". " + entry.emote().getName() : entry.emote().getName();
      return this.trimToWidth(base, 86);
   }

   private String trimToWidth(String text, int maxWidth) {
      if (CactusConstants.mc.field_1772.method_1727(text) <= maxWidth) {
         return text;
      } else {
         String var10000 = CactusConstants.mc.field_1772.method_27523(text, Math.max(0, maxWidth - 6));
         return var10000 + "…";
      }
   }

   private boolean isSmoothAnimationEnabled() {
      ContentPack pack = ContentPackManager.get().ofId("smooth_animations");
      return pack != null && pack.isEnabled();
   }

   private float frameDeltaSeconds() {
      long now = System.nanoTime();
      if (this.lastFrameNanos < 0L) {
         this.lastFrameNanos = now;
         return 0.016666668F;
      } else {
         float delta = (float)(now - this.lastFrameNanos) / 1.0E9F;
         this.lastFrameNanos = now;
         return class_3532.method_15363(delta, 0.0033333334F, 0.083333336F);
      }
   }

   private class_2561 categoryKey(EmoteQuickSelector.WheelCategory category) {
      return class_2561.method_43471("modules.emoteQuickSelector.categories." + category.name().toLowerCase());
   }

   private static float easeOutCubic(float t) {
      float x = 1.0F - class_3532.method_15363(t, 0.0F, 1.0F);
      return 1.0F - x * x * x;
   }

   private double getGuiMouseX() {
      return CactusConstants.mc.field_1729.method_1603() * (double)CactusConstants.mc.method_22683().method_4486() / (double)CactusConstants.mc.method_22683().method_4480();
   }

   private double getGuiMouseY() {
      return CactusConstants.mc.field_1729.method_1604() * (double)CactusConstants.mc.method_22683().method_4502() / (double)CactusConstants.mc.method_22683().method_4507();
   }

   private static enum WheelCategory {
      FAVORITES,
      RECENT,
      ALL;

      // $FF: synthetic method
      private static EmoteQuickSelector.WheelCategory[] $values() {
         return new EmoteQuickSelector.WheelCategory[]{FAVORITES, RECENT, ALL};
      }
   }

   private static record WheelEntry(int slot, AbstractCosmetic.Emote emote) {
      private WheelEntry(int slot, AbstractCosmetic.Emote emote) {
         this.slot = slot;
         this.emote = emote;
      }

      public int slot() {
         return this.slot;
      }

      public AbstractCosmetic.Emote emote() {
         return this.emote;
      }
   }
}
