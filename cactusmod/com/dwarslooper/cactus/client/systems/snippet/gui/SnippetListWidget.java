package com.dwarslooper.cactus.client.systems.snippet.gui;

import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.SupplyingClickableTextWidget;
import com.dwarslooper.cactus.client.systems.snippet.Snippet;
import com.dwarslooper.cactus.client.systems.snippet.SnippetManager;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import com.dwarslooper.cactus.client.util.generic.ABValue;
import com.dwarslooper.cactus.client.util.generic.TextUtils;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_11909;
import net.minecraft.class_124;
import net.minecraft.class_156;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_4265;
import net.minecraft.class_5250;
import net.minecraft.class_6379;
import net.minecraft.class_4265.class_4266;
import org.jetbrains.annotations.NotNull;

public class SnippetListWidget extends class_4265<SnippetListWidget.SnippetEntry> {
   private final SnippetListScreen parent;
   private String filter = "";

   public SnippetListWidget(SnippetListScreen parent) {
      super(CactusConstants.mc, parent.field_22789, 200, (parent.field_22790 - 200) / 2, 28);
      this.parent = parent;
      this.reload();
   }

   public void updateFilter(String filter) {
      this.filter = filter;
      this.reload();
   }

   public void reload() {
      this.method_25339();
      SnippetManager.get().getSnippets().stream().filter((snippet) -> {
         return Utils.matchesSearch(snippet.getName(), this.filter);
      }).sorted((s1, s2) -> {
         if (s1.isFavorite() && !s2.isFavorite()) {
            return -1;
         } else {
            return !s1.isFavorite() && s2.isFavorite() ? 1 : 0;
         }
      }).forEach((snippet) -> {
         this.method_25321(new SnippetListWidget.SnippetEntry(snippet));
      });
   }

   public int method_25322() {
      return 305;
   }

   public boolean method_73379() {
      return true;
   }

   private void paste(Snippet snippet) {
      this.parent.pasteToChat(snippet.getContent());
   }

   public class SnippetEntry extends class_4266<SnippetListWidget.SnippetEntry> {
      private static final ABValue<class_5250> favoriteStars = ABValue.of("★", "☆").map(class_2561::method_43470).apply((text) -> {
         text.method_27692(class_124.field_1065);
      });
      private final Snippet snippet;
      private final class_339 selectButton;
      private final class_339 deleteButton;
      private final class_339 favoriteWidget;
      private final class_5250 displayName;
      private long last;

      public SnippetEntry(Snippet snippet) {
         this.snippet = snippet;
         this.displayName = class_2561.method_43470(snippet.getName().isEmpty() ? "Unnamed" : snippet.getName()).method_27692(class_124.field_1073);
         this.selectButton = class_4185.method_46430(class_2561.method_43470("✔"), (button) -> {
            SnippetListWidget.this.paste(snippet);
         }).method_46437(20, 20).method_46431();
         this.deleteButton = (new CTextureButtonWidget.Builder((button) -> {
            SnippetManager.get().getSnippets().remove(snippet);
            SnippetListWidget.this.reload();
         })).uv(200, 0).dimensions(20, 20).build();
         int var10005 = CactusConstants.mc.field_1772.method_1727("★");
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         this.favoriteWidget = new SupplyingClickableTextWidget(0, 0, var10005, 9, () -> {
            return (class_2561)favoriteStars.fromBoolean(snippet.isFavorite());
         }, (t) -> {
            return t;
         }, (button) -> {
            snippet.setFavorite(!snippet.isFavorite());
            SnippetListWidget.this.reload();
         });
      }

      public void method_25343(class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
         int x = this.method_46426();
         int y = this.method_46427();
         int entryWidth = this.method_25368();
         int entryHeight = this.method_25364();
         context.method_27535(CactusConstants.mc.field_1772, this.displayName, x + 2, y + 2, -1);
         String text = TextUtils.trimToWidth(this.snippet.getContent(), 253, "...");
         this.deleteButton.method_48229(x + entryWidth - 24, y - 1);
         this.selectButton.method_48229(x + entryWidth - 24 - 22, y - 1);
         this.favoriteWidget.method_48229(x + CactusConstants.mc.field_1772.method_27525(this.displayName) + 6, y + 2);
         this.method_25396().forEach((element) -> {
            if (element instanceof class_339) {
               class_339 w = (class_339)element;
               w.method_25394(context, mouseX, mouseY, tickDelta);
            }

         });
         class_327 var10001 = CactusConstants.mc.field_1772;
         int var10003 = x + 2;
         int var10004 = y + 2;
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         context.method_25303(var10001, text, var10003, var10004 + 9 + 4, -5592406);
      }

      public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
         if (super.method_25402(click, doubled)) {
            return true;
         } else {
            if (doubled || class_156.method_658() - this.last < 500L) {
               SnippetListWidget.this.paste(this.snippet);
            }

            SnippetListWidget.this.method_25313(this);
            this.last = class_156.method_658();
            return true;
         }
      }

      @NotNull
      public List<? extends class_364> method_25396() {
         return ImmutableList.of(this.selectButton, this.favoriteWidget, this.deleteButton);
      }

      @NotNull
      public List<? extends class_6379> method_37025() {
         return ImmutableList.of(this.selectButton, this.favoriteWidget, this.deleteButton);
      }
   }
}
