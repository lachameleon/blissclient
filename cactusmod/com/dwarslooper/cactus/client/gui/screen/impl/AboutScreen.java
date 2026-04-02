package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.WebImage;
import com.dwarslooper.cactus.client.util.networking.HttpUtils;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_11735;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_407;
import net.minecraft.class_4280;
import net.minecraft.class_437;
import net.minecraft.class_5489;
import net.minecraft.class_4280.class_4281;
import org.jetbrains.annotations.NotNull;

public class AboutScreen extends CScreen {
   private static final List<AboutScreen.Widget.Entry> entryCache = new ArrayList();
   private AboutScreen.Widget widget;

   public AboutScreen(class_437 parent) {
      super("about");
      this.parent = parent;
   }

   private CompletableFuture<List<AboutScreen.Group>> loadTeamInfo() {
      return CompletableFuture.supplyAsync(() -> {
         JsonObject json = HttpUtils.fetchJson("https://cactusmod.xyz/team/team.json").getAsJsonObject();
         List<AboutScreen.Group> groups = new ArrayList();
         Iterator var2 = json.keySet().iterator();

         while(var2.hasNext()) {
            String groupKey = (String)var2.next();
            JsonObject group = json.getAsJsonObject(groupKey);
            groups.add((AboutScreen.Group)CactusClient.GSON.fromJson(group, AboutScreen.Group.class));
         }

         return groups;
      });
   }

   public void method_25426() {
      super.method_25426();
      this.widget = new AboutScreen.Widget(this.field_22789, this.field_22790 - 72, 32);
      if (!entryCache.isEmpty()) {
         this.widget.updateEntries();
      } else {
         this.loadTeamInfo().thenAccept((groups) -> {
            Iterator var2 = groups.iterator();

            while(true) {
               AboutScreen.Group group;
               do {
                  if (!var2.hasNext()) {
                     this.widget.updateEntries();
                     return;
                  }

                  group = (AboutScreen.Group)var2.next();
               } while(group.compact());

               entryCache.add(new AboutScreen.Widget.GroupEntry(group));
               Iterator var4 = group.members().iterator();

               while(var4.hasNext()) {
                  AboutScreen.Contributor member = (AboutScreen.Contributor)var4.next();
                  entryCache.add(new AboutScreen.Widget.ContributorEntry(member));
               }
            }
         }).exceptionally((t) -> {
            t.printStackTrace();
            return null;
         });
      }

      this.method_37063(this.widget);
      this.method_37063(new CButtonWidget((this.field_22789 - 100) / 2, this.field_22790 - 32, 100, 20, class_2561.method_30163(this.getTranslatableElement("button.patchNotes", new Object[0])), (button) -> {
         try {
            PatchInfoScreen.openPatches();
         } catch (Exception var2) {
            CactusClient.getLogger().error("Failed to load patch notes", var2);
         }

      }));
      this.method_37063(new CButtonWidget((this.field_22789 - 100) / 2 - 100 - 4, this.field_22790 - 32, 100, 20, class_2561.method_43470(this.getTranslatableElement("button.website", new Object[0])), class_407.method_49625(this, "https://cactusmod.xyz")));
      this.method_37063(new CButtonWidget(this.field_22789 / 2 + 50 + 4, this.field_22790 - 32, 100, 20, class_2561.method_43470(this.getTranslatableElement("button.discord", new Object[0])), class_407.method_49625(this, "https://cactusmod.xyz/go/?discord")));
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 8, -1);
   }

   private static class Widget extends class_4280<AboutScreen.Widget.Entry> {
      public Widget(int width, int height, int y) {
         super(CactusConstants.mc, width, height, y, 36);
      }

      public int method_25322() {
         return 320;
      }

      protected int method_65507() {
         return this.method_55442() - 6;
      }

      public void updateEntries() {
         this.method_25314(AboutScreen.entryCache);
      }

      private static class ContributorEntry extends AboutScreen.Widget.Entry implements AutoCloseable {
         private final String name;
         private final class_5489 text;
         private final WebImage image;

         public ContributorEntry(AboutScreen.Contributor contributor) {
            this.name = contributor.name();
            this.text = class_5489.method_30890(CactusConstants.mc.field_1772, class_2561.method_43470(contributor.about()), 275);
            String var10004 = this.name.toLowerCase();
            this.image = new WebImage(class_2960.method_60655("cactus", "dynamic/about/contributors/" + var10004.replaceAll("[^a-z0-9_.]", "")));
            WebImage var10000 = this.image;
            String var10001 = contributor.img();
            var10000.load("https://cactusmod.xyz" + var10001.replace(".webp", ".png")).exceptionally((t) -> {
               t.printStackTrace();
               return null;
            });
         }

         public void method_25343(@NotNull class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int x = this.method_73380();
            int y = this.method_73382();
            this.image.draw(context, x, y, 32, 32);
            context.method_51433(CactusConstants.mc.field_1772, this.name, x + 32 + 5, y + 5, -1, false);
            int textX = x + 32 + 5;
            int var10000 = y + 5;
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            int textY = var10000 + 9 + 2;
            class_5489 var10 = this.text;
            class_11735 var10001 = class_11735.field_62009;
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            var10.method_75816(var10001, textX, textY, 9, context.method_75788());
         }

         public void close() {
            this.image.close();
         }
      }

      private static class GroupEntry extends AboutScreen.Widget.Entry {
         private final class_2561 name;

         public GroupEntry(AboutScreen.Group group) {
            this.name = class_2561.method_43470(group.title()).method_27694((style) -> {
               return style.method_10982(true).method_36139(Color.decode(group.color()).getRGB());
            });
         }

         public void method_25343(class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int x = this.method_73380();
            int y = this.method_73382();
            int entryHeight = this.method_73384();
            class_327 var10001 = CactusConstants.mc.field_1772;
            class_2561 var10002 = this.name;
            int var10003 = x + 5;
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            context.method_51439(var10001, var10002, var10003, y + (entryHeight - 9) / 2 + 1, -1, false);
         }
      }

      private abstract static class Entry extends class_4281<AboutScreen.Widget.Entry> {
         @NotNull
         public class_2561 method_37006() {
            return class_2561.method_43473();
         }
      }
   }

   private static record Group(String title, String description, String color, List<AboutScreen.Contributor> members, boolean compact) {
      private Group(String title, String description, String color, List<AboutScreen.Contributor> members, boolean compact) {
         this.title = title;
         this.description = description;
         this.color = color;
         this.members = members;
         this.compact = compact;
      }

      public String title() {
         return this.title;
      }

      public String description() {
         return this.description;
      }

      public String color() {
         return this.color;
      }

      public List<AboutScreen.Contributor> members() {
         return this.members;
      }

      public boolean compact() {
         return this.compact;
      }
   }

   private static record Contributor(String name, String about, String img) {
      private Contributor(String name, String about, String img) {
         this.name = name;
         this.about = about;
         this.img = img;
      }

      public String name() {
         return this.name;
      }

      public String about() {
         return this.about;
      }

      public String img() {
         return this.img;
      }
   }
}
