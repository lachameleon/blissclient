package com.dwarslooper.cactus.client.feature.content.impl;

import com.dwarslooper.cactus.client.feature.content.ContentPack;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_10799;
import net.minecraft.class_11909;
import net.minecraft.class_124;
import net.minecraft.class_1802;
import net.minecraft.class_2477;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_4280;
import net.minecraft.class_5348;
import net.minecraft.class_5481;
import net.minecraft.class_350.class_351;
import net.minecraft.class_4280.class_4281;
import org.jetbrains.annotations.NotNull;

public class ContentPackListWidget extends class_4280<ContentPackListWidget.Entry> {
   private static final class_2960 SELECT_HIGHLIGHTED_TEXTURE = class_2960.method_60656("transferable_list/select_highlighted");
   private static final class_2960 SELECT_TEXTURE = class_2960.method_60656("transferable_list/select");
   private static final class_2960 UNSELECT_HIGHLIGHTED_TEXTURE = class_2960.method_60656("transferable_list/unselect_highlighted");
   private static final class_2960 UNSELECT_TEXTURE = class_2960.method_60656("transferable_list/unselect");
   private static final class_2960 TAGS = class_2960.method_60655("cactus", "textures/gui/tags.png");
   private final class_2561 title;
   public final ContentPackScreen parent;

   public ContentPackListWidget(ContentPackScreen parent, class_2561 title) {
      super(CactusConstants.mc, 200, parent.field_22790, 33, 36);
      this.parent = parent;
      this.title = title;
      this.field_22744 = false;
      this.resetHeader();
   }

   private void resetHeader() {
      this.method_25339();
      class_2561 headerText = class_2561.method_43473().method_10852(this.title).method_27695(new class_124[]{class_124.field_1073, class_124.field_1067});
      this.method_73370(new ContentPackListWidget.HeaderEntry(this, CactusConstants.mc.field_1772, headerText), 13);
      this.method_25313((class_351)null);
   }

   public void add(ContentPack pack) {
      this.method_25321(new ContentPackListWidget.PackEntry(this, this, pack));
   }

   public void clear() {
      this.resetHeader();
   }

   public int method_25322() {
      return this.field_22758 - 4;
   }

   public int method_65507() {
      return this.method_55442() - 6;
   }

   public class HeaderEntry extends ContentPackListWidget.Entry {
      private final class_327 textRenderer;
      private final class_2561 text;

      public HeaderEntry(final ContentPackListWidget this$0, class_327 textRenderer, class_2561 text) {
         super();
         this.textRenderer = textRenderer;
         this.text = text;
      }

      public void method_25343(class_332 context, int mouseX, int mouseY, boolean hovered, float delta) {
         int cx = this.method_46426() + this.method_25368() / 2;
         int cy = this.method_73385();
         context.method_27534(this.textRenderer, this.text, cx, cy - 4, -1);
      }

      public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
         return false;
      }

      @NotNull
      public class_2561 method_37006() {
         return this.text;
      }

      public String getName() {
         return "";
      }
   }

   public class PackEntry extends ContentPackListWidget.Entry {
      private final ContentPack pack;
      private final ContentPackListWidget owner;
      private final class_2561 displayName;
      private final class_2561 description;

      public PackEntry(final ContentPackListWidget this$0, ContentPackListWidget owner, ContentPack pack) {
         super();
         this.pack = pack;
         this.owner = owner;
         this.displayName = class_2561.method_43470(pack.getName());
         this.description = class_2561.method_43470(pack.getDescription() != null ? pack.getDescription() : class_2477.method_10517().method_48307("generic.no_description"));
      }

      public void method_25343(@NotNull class_332 context, int mouseX, int mouseY, boolean hovered, float delta) {
         int contentX = this.method_73380();
         int contentY = this.method_73382();
         float iconScale = 2.0F;
         if (this.isSelectable() && hovered) {
            context.method_25294(contentX, contentY, contentX + 32, contentY + 32, -1601138544);
         }

         context.method_51448().pushMatrix();
         context.method_51448().scale(iconScale, iconScale);
         context.method_51448().translate((float)contentX / iconScale, (float)contentY / iconScale);
         context.method_51445(this.pack.getIcon() != null ? this.pack.getIcon().method_7854() : class_1802.field_8106.method_7854(), 0, 0);
         context.method_51448().popMatrix();
         if (this.isSelectable() && hovered) {
            int k = mouseX - contentX;
            int l = mouseY - contentY;
            if (!this.pack.isEnabled()) {
               context.method_52706(class_10799.field_56883, k < 32 ? ContentPackListWidget.SELECT_HIGHLIGHTED_TEXTURE : ContentPackListWidget.SELECT_TEXTURE, contentX, contentY, 32, 32);
            } else if (this.pack.getActivationPolicy() != ContentPack.ActivationPolicy.ALWAYS_ENABLED) {
               context.method_52706(class_10799.field_56883, k < 32 ? ContentPackListWidget.UNSELECT_HIGHLIGHTED_TEXTURE : ContentPackListWidget.UNSELECT_TEXTURE, contentX, contentY, 32, 32);
            }

            context.method_25290(class_10799.field_56883, ContentPackListWidget.TAGS, contentX + 32 - 9, contentY, 9.0F, 0.0F, 9, 8, 32, 32);
            if (k > 22 && k < 32 && l < 8) {
               List<class_2561> lines = new ArrayList(List.of(class_2561.method_43469("gui.screen.content_packs.addedBy", new Object[]{this.pack.ownerOrDefault()})));
               if (this.pack.getActivationPolicy() == ContentPack.ActivationPolicy.ALWAYS_ENABLED) {
                  lines.add(class_2561.method_43471("gui.screen.content_packs.alwaysEnabled").method_27692(class_124.field_1061));
               }

               context.method_51434(CactusConstants.mc.field_1772, lines, mouseX, mouseY);
            }
         }

         class_327 tr = CactusConstants.mc.field_1772;
         Object trimmedName;
         if (tr.method_27525(this.displayName) > 150) {
            trimmedName = class_5348.method_29433(new class_5348[]{tr.method_1714(this.displayName, 150 - tr.method_1727("...")), class_5348.method_29430("...")});
         } else {
            trimmedName = this.displayName;
         }

         context.method_35720(tr, class_2477.method_10517().method_30934((class_5348)trimmedName), contentX + 32 + 2, contentY + 1, -1);
         int descY = contentY + 12;
         Iterator var12 = tr.method_1728(this.description, 150).iterator();

         while(var12.hasNext()) {
            class_5481 line = (class_5481)var12.next();
            context.method_51430(tr, line, contentX + 32 + 2, descY, -8355712, true);
            Objects.requireNonNull(tr);
            descY += 9;
            int var10001 = contentY + 12;
            Objects.requireNonNull(tr);
            if (descY > var10001 + 9 * 2) {
               break;
            }
         }

      }

      private boolean isSelectable() {
         return this.pack.getActivationPolicy() != ContentPack.ActivationPolicy.ALWAYS_ENABLED;
      }

      public boolean method_25402(class_11909 click, boolean doubled) {
         double d = click.comp_4798() - (double)this.method_73380();
         double e = click.comp_4799() - (double)this.method_73382();
         if (this.isSelectable() && d <= 32.0D) {
            if (!this.pack.isEnabled()) {
               this.pack.setEnabled(true);
               this.owner.parent.update();
               return true;
            }

            if (this.pack.isEnabled()) {
               this.pack.setEnabled(false);
               this.owner.parent.update();
               return true;
            }
         }

         return super.method_25402(click, doubled);
      }

      public ContentPack getPack() {
         return this.pack;
      }

      @NotNull
      public class_2561 method_37006() {
         return class_2561.method_43469("narrator.select", new Object[]{class_2561.method_30163(this.pack.getName())});
      }

      public String getName() {
         return this.pack.getId();
      }
   }

   public abstract class Entry extends class_4281<ContentPackListWidget.Entry> {
      public int method_25368() {
         return super.method_25368() - (ContentPackListWidget.this.method_44392() ? 6 : 0);
      }

      public abstract String getName();
   }
}
