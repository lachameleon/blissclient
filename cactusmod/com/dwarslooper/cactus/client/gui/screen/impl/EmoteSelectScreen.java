package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.BackendPacketReceiveEvent;
import com.dwarslooper.cactus.client.gui.screen.EventHandlingCScreen;
import com.dwarslooper.cactus.client.gui.widget.GridScrollableWidget;
import com.dwarslooper.cactus.client.irc.protocol.packets.cosmetic.ClientCosmeticListS2CPacket;
import com.dwarslooper.cactus.client.systems.profile.AbstractCosmetic;
import com.dwarslooper.cactus.client.systems.profile.EmoteExecutor;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_10799;
import net.minecraft.class_1109;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_3417;
import net.minecraft.class_4185;
import org.jetbrains.annotations.NotNull;

public class EmoteSelectScreen extends EventHandlingCScreen {
   private EmoteSelectScreen.Widget widget;
   private boolean loaded = false;

   public EmoteSelectScreen() {
      super("emotes");
   }

   public void method_25426() {
      super.method_25426();
      int h2 = this.field_22790 / 2;
      this.widget = new EmoteSelectScreen.Widget(400, h2, h2 / 2);
      this.widget.method_46421((this.field_22789 - 400) / 2);
      this.update();
      this.method_37063(this.widget);
   }

   private void update() {
      this.loaded = false;
      this.widget.clear();
      List var10000 = EmoteExecutor.getAvailableEmotes();
      EmoteSelectScreen.Widget var10001 = this.widget;
      Objects.requireNonNull(var10001);
      var10000.forEach(var10001::add);
      this.loaded = true;
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 8, -1);
   }

   @EventHandler
   public void updatePacketReceived(BackendPacketReceiveEvent event) {
      if (event.packet() instanceof ClientCosmeticListS2CPacket) {
         CactusConstants.mc.execute(() -> {
            this.update();
            this.method_41843();
         });
      }

   }

   public class Widget extends GridScrollableWidget<EmoteSelectScreen.Widget.Entry> {
      private static final class_2561 noneText = class_2561.method_43471("gui.screen.emotes.none");
      private static final class_2561 loadingText = class_2561.method_43471("gui.screen.emotes.loading");

      public Widget(int width, int height, int y) {
         super(CactusConstants.mc, width, height, y, 24, 84);
      }

      public int method_25322() {
         return this.field_22758 - 20;
      }

      protected int method_65507() {
         return this.method_55442() - 6;
      }

      public void add(AbstractCosmetic.Emote emote) {
         this.addEntry(new EmoteSelectScreen.Widget.Entry(emote));
      }

      public void method_48579(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
         super.method_48579(context, mouseX, mouseY, delta);
         if (this.method_25396().isEmpty()) {
            context.method_27534(CactusConstants.mc.field_1772, EmoteSelectScreen.this.loaded ? noneText : loadingText, this.method_46426() + this.method_25368() / 2, this.method_46427() + this.method_25364() / 2, -5592406);
         }

      }

      public static class Entry extends GridScrollableWidget.GridEntry<EmoteSelectScreen.Widget.Entry> {
         private final AbstractCosmetic.Emote emote;

         public Entry(AbstractCosmetic.Emote emote) {
            this.emote = emote;
         }

         public void renderGrid(class_332 context, int index, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            boolean hoveredButton = hovered && mouseX > x + 2 && mouseX <= x + entryWidth - 2 && mouseY > y + 2 && mouseY <= y + entryHeight - 2;
            context.method_52706(class_10799.field_56883, class_4185.field_45339.method_52729(true, hoveredButton), x + 2, y + 2, entryWidth - 4, entryHeight - 4);
            class_327 var10001 = CactusConstants.mc.field_1772;
            String var10002 = this.emote.getName();
            int var10003 = x + entryWidth / 2;
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            context.method_25300(var10001, var10002, var10003, y + (entryHeight - 9) / 2 + 1, -1);
         }

         public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
            CactusConstants.mc.method_1483().method_4873(class_1109.method_47978(class_3417.field_15015, 1.0F));
            if (CactusConstants.mc.field_1755 != null) {
               CactusConstants.mc.field_1755.method_25419();
            }

            EmoteExecutor.useEmote(this.emote);
            return true;
         }
      }
   }
}
