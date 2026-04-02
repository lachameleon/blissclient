package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.toast.ToastSystem;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.list.ServerPlatformListWidget;
import com.dwarslooper.cactus.client.systems.localserver.LocalServerManager;
import com.dwarslooper.cactus.client.systems.localserver.PlatformManager;
import com.dwarslooper.cactus.client.systems.localserver.instance.LocalServer;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.render.RenderUtils;
import com.dwarslooper.cactus.client.util.generic.TextUtils;
import java.awt.Color;
import java.util.Objects;
import java.util.concurrent.Future;
import net.minecraft.class_124;
import net.minecraft.class_1802;
import net.minecraft.class_2477;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_3521;
import net.minecraft.class_437;
import net.minecraft.class_5244;
import net.minecraft.class_7919;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class CreateLocalServerScreen extends CScreen {
   private class_342 serverNameWidget;
   private class_342 portWidget;
   private ServerPlatformListWidget platformListWidget;
   private CButtonWidget createButton;
   private LocalServer serverToCreate;
   private Future<?> downloadFuture;
   private float downloadState = -1.0F;
   private String downloadError;
   private long downloadStartTime;

   public CreateLocalServerScreen(class_437 parent) {
      super("create_local_server");
      this.parent = parent;
   }

   public void method_25426() {
      super.method_25426();
      this.method_37063(this.platformListWidget = new ServerPlatformListWidget(this));
      this.method_37063(this.serverNameWidget = new class_342(CactusConstants.mc.field_1772, this.field_22789 / 2 - 120 - 2, 40, 120, 20, class_2561.method_43473()));
      this.serverNameWidget.method_1887(this.getTranslatableElement("widget.name", new Object[0]));
      this.serverNameWidget.method_1890(StringUtils::isAlphanumericSpace);
      this.serverNameWidget.method_1863((s) -> {
         this.serverNameWidget.method_1887(s.isEmpty() ? this.getTranslatableElement("widget.name", new Object[0]) : "");
         this.validateStates();
      });
      this.serverNameWidget.method_1880(20);
      this.method_37063(this.portWidget = new class_342(CactusConstants.mc.field_1772, this.field_22789 / 2 + 2, 40, 120, 20, class_2561.method_43473()));
      this.portWidget.method_1852("25565");
      this.portWidget.method_1890(TextUtils::isNumericOrEmpty);
      this.portWidget.method_1863((s) -> {
         this.portWidget.method_1887(s.isEmpty() ? this.getTranslatableElement("widget.port", new Object[0]) : "");
         this.validateStates();
      });
      this.portWidget.method_1880(20);
      this.method_37063(this.createButton = new CButtonWidget(this.field_22789 / 2 + 2, this.field_22790 - 32, 100, 20, class_2561.method_30163(this.getTranslatableElement("button.create", new Object[0])), (button) -> {
         int port = this.getPort();
         if (this.validateStates()) {
            PlatformManager.Platform platform = this.platformListWidget.selectedPlatform;
            if (platform != null && this.platformListWidget.selectedVersion != null) {
               button.field_22763 = false;
               this.serverNameWidget.field_22763 = false;
               this.portWidget.field_22763 = false;
               this.downloadStartTime = System.currentTimeMillis();
               this.serverToCreate = LocalServerManager.get().createServer(this.serverNameWidget.method_1882(), platform, port);
               if (platform != PlatformManager.Platform.CUSTOM) {
                  this.downloadFuture = LocalServerManager.get().downloadPlatformJar(platform, this.serverToCreate.getServerDir(), (progress) -> {
                     if (progress == -2.0F) {
                        this.downloadError = "gui.screen.local_servers.noProgress";
                        this.downloadState = -1.0F;
                     } else if (progress == -3.0F) {
                        this.downloadError = "gui.screen.local_servers.failed";
                        this.downloadState = -1.0F;
                     } else {
                        this.downloadError = null;
                        this.downloadState = progress;
                     }

                     if (progress == 1.0F) {
                        this.finishSetup(this.serverToCreate);
                     }

                  }, this.platformListWidget.selectedVersion);
                  if (this.downloadFuture == null) {
                     ToastSystem.displayMessage(class_2477.method_10517().method_48307("optimizeWorld.stage.failed"), this.getTranslatableElement("error.noDownload", new Object[0]), class_1802.field_8077);
                     button.field_22763 = true;
                     this.serverNameWidget.field_22763 = true;
                     this.portWidget.field_22763 = true;
                     this.platformListWidget.field_22763 = false;
                  }
               } else {
                  this.finishSetup(this.serverToCreate);
               }
            } else {
               ToastSystem.displayMessage(class_2477.method_10517().method_48307("optimizeWorld.stage.failed"), this.getTranslatableElement("error.platform", new Object[0]), class_1802.field_8077);
            }

         }
      }));
      this.createButton.field_22763 = false;
      this.method_37063(new CButtonWidget(this.field_22789 / 2 - 100 - 2, this.field_22790 - 32, 100, 20, class_5244.field_24335, (button) -> {
         if (this.downloadState != -1.0F && this.downloadFuture != null) {
            this.downloadFuture.cancel(true);
         }

         this.method_25419();
      }));
   }

   private void finishSetup(LocalServer server) {
      CactusConstants.mc.execute(() -> {
         LocalServerManager.get().addServer(server);
         ToastSystem.displayMessage(this.getTranslatableElement("status.finished", new Object[0]), server.getName(), class_1802.field_8270);
         this.method_25419();
      });
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 8, -1);
      class_327 var10001 = CactusConstants.mc.field_1772;
      String var10002 = this.getTranslatableElement("header.serverName", new Object[0]);
      int var10003 = this.field_22789 / 2 - 120 - 2;
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      context.method_25303(var10001, var10002, var10003, 40 - 9, Color.WHITE.getRGB());
      var10001 = CactusConstants.mc.field_1772;
      var10002 = this.getTranslatableElement("header.serverPort", new Object[0]);
      var10003 = this.field_22789 / 2 + 2;
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      context.method_25303(var10001, var10002, var10003, 40 - 9, Color.WHITE.getRGB());
      var10001 = CactusConstants.mc.field_1772;
      var10002 = this.platformListWidget.selectedPlatform == null ? this.getTranslatableElement("header.platform", new Object[0]) : this.getTranslatableElement("header.version", new Object[]{this.platformListWidget.selectedPlatform.getDisplayName()});
      var10003 = this.field_22789 / 2;
      Objects.requireNonNull(CactusConstants.mc.field_1772);
      context.method_25300(var10001, var10002, var10003, 80 - 9, Color.WHITE.getRGB());
      if (this.downloadError != null) {
         context.method_27534(CactusConstants.mc.field_1772, class_2561.method_43471(this.downloadError), this.field_22789 / 2, this.field_22790 - 74, -1);
      } else if (this.downloadState != -1.0F) {
         int w = this.field_22789 / 4;
         RenderUtils.renderLabeledProgressBar(context, this.field_22789 / 2 - w, this.field_22790 - 84, this.field_22789 / 2 + w, this.field_22790 - 74, this.downloadState, this.downloadStartTime, true);
      }

   }

   private int getPort() {
      String s = this.portWidget.method_1882();
      return TextUtils.isNumeric(s) ? Integer.parseInt(s) : -1;
   }

   private void validColor(class_342 widget, boolean v) {
      int color = v ? class_124.field_1068.method_532() : class_124.field_1061.method_532();
      widget.method_1868(color | -16777216);
   }

   public boolean validateStates() {
      int setPort = this.getPort();
      boolean portValid = setPort > 1024 && setPort < 65536 && class_3521.method_46872(setPort);
      this.validColor(this.portWidget, portValid);
      this.portWidget.method_47400(portValid ? (LocalServerManager.get().getServers().stream().noneMatch((server) -> {
         return server.getPort() == setPort;
      }) ? null : class_7919.method_47407(class_2561.method_43470(this.getTranslatableElement("error.portUsed", new Object[0])))) : class_7919.method_47407(class_2561.method_43470(this.getTranslatableElement("error.port", new Object[0]))));
      boolean nameValid = !this.serverNameWidget.method_1882().isEmpty();
      boolean nameFree = LocalServerManager.get().getServers().stream().noneMatch((server) -> {
         return server.getName().equalsIgnoreCase(this.serverNameWidget.method_1882());
      });
      this.validColor(this.serverNameWidget, nameValid && nameFree);
      this.serverNameWidget.method_47400(nameValid && nameFree ? null : class_7919.method_47407(class_2561.method_43470(this.getTranslatableElement(nameFree ? "error.name" : "error.nameUnavailable", new Object[0]))));
      boolean platformValid = this.platformListWidget.selectedVersion != null;
      return this.createButton.field_22763 = portValid && nameValid && nameFree && platformValid;
   }

   public boolean method_25422() {
      return this.downloadState == -1.0F;
   }
}
