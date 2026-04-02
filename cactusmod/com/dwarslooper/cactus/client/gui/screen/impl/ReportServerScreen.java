package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.feature.commands.IRCCommand;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.irc.protocol.packets.security.ServerActionC2SPacket;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.dwarslooper.cactus.client.util.networking.HttpUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_4280;
import net.minecraft.class_437;
import net.minecraft.class_5244;
import net.minecraft.class_5250;
import net.minecraft.class_642;
import net.minecraft.class_4280.class_4281;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReportServerScreen extends CScreen {
   private ReportServerScreen.ReportReason reason;
   private ReportServerScreen.ReasonListWidget reasonList;
   private CButtonWidget sendButton;
   private final class_642 serverInfo;

   public ReportServerScreen(class_642 serverInfo) {
      super("report_server");
      this.serverInfo = serverInfo;
   }

   public void method_25426() {
      super.init(false);
      this.reasonList = new ReportServerScreen.ReasonListWidget(this);
      this.method_37063(this.reasonList);
      this.method_37063(this.sendButton = new CButtonWidget(this.field_22789 / 2 - 120 - 4, this.field_22790 - 20 - 4, 120, 20, class_2561.method_43471("gui.chatReport.send"), (button) -> {
         if (IRCCommand.checkIfConnectedElseError()) {
            if (this.serverInfo != null) {
               CactusClient.getInstance().getIrcClient().sendPacket(new ServerActionC2SPacket(this.serverInfo, this.reason));
            } else {
               ChatUtils.error("You are not on a server!");
            }
         }

         CactusConstants.mc.method_1507((class_437)null);
      }));
      this.method_37063(new CButtonWidget(this.field_22789 / 2 + 4, this.field_22790 - 20 - 4, 120, 20, class_5244.field_24335, (button) -> {
         this.method_25419();
      }));
      this.updateButtonState();
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 16, -1);
      class_327 var10001 = this.field_22793;
      String var10002 = this.serverInfo.field_3761;
      int var10003 = this.field_22789 / 2;
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      context.method_25300(var10001, var10002, var10003, 20 + 9, -1);
      int top = this.field_22790 - 95 + 4;
      int bottom = this.field_22790 - 20 - 8;
      int left = (this.field_22789 - 320) / 2;
      int right = (this.field_22789 + 320) / 2;
      context.method_25294(left, top, right, bottom, -16777216);
      context.method_73198(left, top, right - left, bottom - top, -1);
      context.method_25303(this.field_22793, "Description:", left + 4, top + 4, -1);
      if (this.reason != null) {
         int textX = left + 4 + 16;
         int rightPadding = right - 4;
         int var10000 = top + 4;
         Objects.requireNonNull(this.field_22793);
         int yBase = var10000 + 9 + 2;
         int bottomPadding = bottom - 4;
         int width = rightPadding - textX;
         int height = bottomPadding - yBase;
         class_2561 description = class_2561.method_43470(this.reason.description());
         int textHeight = this.field_22793.method_44378(description, width);
         context.method_51440(this.field_22793, description, textX, yBase + (height - textHeight) / 2, width, -1, true);
      }

   }

   public void updateButtonState() {
      this.sendButton.field_22763 = this.reason != null;
   }

   public static CompletableFuture<Collection<ReportServerScreen.ReportReason>> fetchReportReasons() {
      return CompletableFuture.supplyAsync(() -> {
         JsonObject json = HttpUtils.fetchJson("https://cdn.cactusmod.xyz/client/shared/server_report.json").getAsJsonObject();
         JsonObject reasonOptions = json.getAsJsonObject("reasonOptions");
         return reasonOptions.asMap().entrySet().stream().map((e) -> {
            JsonObject entryData = ((JsonElement)e.getValue()).getAsJsonObject();
            String name = entryData.get("name").getAsString();
            String description = entryData.get("description").getAsString();
            return new ReportServerScreen.ReportReason((String)e.getKey(), name, description);
         }).toList();
      });
   }

   public static class ReasonListWidget extends class_4280<ReportServerScreen.ReasonListWidget.ReasonEntry> {
      private final ReportServerScreen parent;

      public ReasonListWidget(ReportServerScreen parent) {
         super(CactusConstants.mc, parent.field_22789, parent.field_22790 - 95 - 40, 40, 18);
         this.parent = parent;
         ReportServerScreen.fetchReportReasons().thenAccept((reasons) -> {
            Iterator var2 = reasons.iterator();

            while(var2.hasNext()) {
               ReportServerScreen.ReportReason reportReason = (ReportServerScreen.ReportReason)var2.next();
               this.method_25321(new ReportServerScreen.ReasonListWidget.ReasonEntry(reportReason));
            }

         });
      }

      public int method_25322() {
         return 320;
      }

      protected int method_65507() {
         return this.method_31383() - 2;
      }

      public void setSelected(@Nullable ReportServerScreen.ReasonListWidget.ReasonEntry reasonEntry) {
         super.method_25313(reasonEntry);
         this.parent.reason = reasonEntry != null ? reasonEntry.getReason() : null;
         this.parent.updateButtonState();
      }

      public void method_48579(@NotNull class_332 context, int mouseX, int mouseY, float deltaTicks) {
         super.method_48579(context, mouseX, mouseY, deltaTicks);
         if (this.method_25396().isEmpty()) {
            class_327 var10001 = CactusConstants.mc.field_1772;
            int var10003 = this.method_46426() + this.method_25368() / 2;
            int var10004 = this.method_46427();
            int var10005 = this.method_25364();
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            context.method_25300(var10001, "Loading..", var10003, var10004 + (var10005 - 9) / 2 + 1, -5592406);
         }

      }

      @Environment(EnvType.CLIENT)
      public static class ReasonEntry extends class_4281<ReportServerScreen.ReasonListWidget.ReasonEntry> {
         final ReportServerScreen.ReportReason reason;

         public ReasonEntry(ReportServerScreen.ReportReason reason) {
            this.reason = reason;
         }

         public void method_25343(class_332 context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            class_327 var10001 = CactusConstants.mc.field_1772;
            class_5250 var10002 = class_2561.method_43470(this.reason.displayName());
            int var10003 = this.method_73380() + 1;
            int var10004 = this.method_73382();
            int var10005 = this.method_73384();
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            context.method_27535(var10001, var10002, var10003, var10004 + (var10005 - 9) / 2 + 1, -1);
         }

         @NotNull
         public class_2561 method_37006() {
            return class_2561.method_43473();
         }

         public boolean method_25402(class_11909 click, boolean doubled) {
            return click.method_74245() == 0;
         }

         public ReportServerScreen.ReportReason getReason() {
            return this.reason;
         }
      }
   }

   public static record ReportReason(String id, String displayName, String description) {
      public ReportReason(String id, String displayName, String description) {
         this.id = id;
         this.displayName = displayName;
         this.description = description;
      }

      public String id() {
         return this.id;
      }

      public String displayName() {
         return this.displayName;
      }

      public String description() {
         return this.description;
      }
   }
}
