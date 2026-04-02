package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.toast.ToastSystem;
import com.dwarslooper.cactus.client.gui.toast.internal.GenericToast;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_11735;
import net.minecraft.class_11909;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_3674;
import net.minecraft.class_4185;
import net.minecraft.class_442;
import net.minecraft.class_5244;
import net.minecraft.class_5489;
import net.minecraft.class_8577;
import org.jetbrains.annotations.NotNull;

public class SubmitLastCrashScreen extends CScreen {
   private final File crashReport;
   private final String content;
   private class_8577 textWidget;

   public SubmitLastCrashScreen(File crashReport, String content) {
      super("submit_crash");
      this.crashReport = crashReport;
      this.content = content.replace("\t", "  ");
   }

   public void method_25426() {
      super.init(false);
      this.method_37063(this.textWidget = new class_8577(20, 80, this.field_22789 - 40, this.field_22790 - 80 - 40, class_2561.method_43470(this.content), this.field_22793));
      this.method_37063(new CButtonWidget(this.field_22789 / 2 - 100 - 2, this.field_22790 - 24 - 4, 100, 20, class_5244.field_41873, (button) -> {
         this.method_25419();
      }));
      this.method_37063(new CButtonWidget(this.field_22789 / 2 + 2, this.field_22790 - 24 - 4, 100, 20, class_2561.method_43470("Submit"), (button) -> {
         this.method_25419();
         CompletableFuture.supplyAsync(() -> {
            return uploadLog(this.content);
         }).thenAccept((jsonObject) -> {
            if (jsonObject != null && jsonObject.has("url")) {
               String url = jsonObject.get("url").getAsString();
               (new class_3674()).method_15979(CactusConstants.mc.method_22683(), url);
               GenericToast toast = new GenericToast("Log link copied", url.replaceFirst("https://", ""));
               CactusConstants.mc.method_1566().method_1999(toast);
            } else {
               ToastSystem.displayMessage("Upload failed", "Can't upload crash-log", class_1802.field_8077);
            }

         });
      }));
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_25300(this.field_22793, "Last time you played the game seems to have crashed, and we figured out that it's probably our fault.", this.field_22789 / 2, 32, -1);
      class_327 var10001 = this.field_22793;
      int var10003 = this.field_22789 / 2;
      Objects.requireNonNull(this.field_22793);
      context.method_25300(var10001, "You can submit your crash log to allow us to maybe fix the issue and prevent such problems in future updates.", var10003, 32 + 9 + 2, -1);
      var10001 = this.field_22793;
      String var10002 = this.crashReport.getName();
      var10003 = this.field_22789 / 2;
      Objects.requireNonNull(this.field_22793);
      context.method_25300(var10001, var10002, var10003, 32 + (9 + 2) * 3, -1);
   }

   public void method_25410(int width, int height) {
      super.method_25410(width, height);
      if (this.parent != null) {
         this.parent.method_25410(width, height);
      }

   }

   public static JsonObject uploadLog(String logContent) {
      try {
         String url = "https://api.mclo.gs/1/log";
         String encodedContent = URLEncoder.encode(logContent, StandardCharsets.UTF_8);
         String formBody = "content=" + encodedContent;
         HttpClient client = HttpClient.newHttpClient();

         JsonObject var7;
         try {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).header("Content-Type", "application/x-www-form-urlencoded").POST(BodyPublishers.ofString(formBody)).build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            var7 = JsonParser.parseString((String)response.body()).getAsJsonObject();
         } catch (Throwable var9) {
            if (client != null) {
               try {
                  client.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (client != null) {
            client.close();
         }

         return var7;
      } catch (Exception var10) {
         return null;
      }
   }

   public static class CrashedScreen extends CScreen {
      private class_5489 message;

      public CrashedScreen() {
         super("none");
      }

      public void method_25426() {
         this.method_37063(class_4185.method_46430(class_5244.field_43109, (button) -> {
            this.field_22787.method_1507(new class_442());
         }).method_46434(this.field_22789 / 2 - 155, this.field_22790 / 4 + 120 + 12, 150, 20).method_46431());
         this.method_37063(class_4185.method_46430(class_2561.method_43471("menu.quit"), (button) -> {
            this.field_22787.method_1592();
         }).method_46434(this.field_22789 / 2 - 155 + 160, this.field_22790 / 4 + 120 + 12, 150, 20).method_46431());
         this.message = class_5489.method_30890(this.field_22793, class_2561.method_43470("Your game has ran into an error and normally would have crashed, but Cactus has prevented that from happening. To keep playing, you can simply return to the title screen. If this happens multiple times, we recommend to restart your game."), 295);
      }

      public boolean method_25422() {
         return false;
      }

      public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
         super.method_25394(context, mouseX, mouseY, delta);
         context.method_27534(this.field_22793, class_2561.method_43470("Crash prevented"), this.field_22789 / 2, this.field_22790 / 4 - 60 + 20, -1);
         this.message.method_75816(class_11735.field_62009, this.field_22789 / 2 - 145, this.field_22790 / 4, 9, context.method_75788());
      }

      public boolean method_25402(class_11909 click, boolean doubled) {
         if (click.comp_4798() < 8.0D && click.comp_4799() < 8.0D) {
            this.method_25419();
            return true;
         } else {
            return super.method_25402(click, doubled);
         }
      }
   }
}
