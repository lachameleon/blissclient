package com.dwarslooper.cactus.client.mixins.client.screen;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.gui.screen.ITranslatable;
import com.dwarslooper.cactus.client.gui.screen.impl.AccountListScreen;
import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.systems.config.CactusSettings;
import com.dwarslooper.cactus.client.systems.config.ConfigHandler;
import com.dwarslooper.cactus.client.systems.ias.AccountManager;
import com.dwarslooper.cactus.client.systems.ias.SessionUtils;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import com.dwarslooper.cactus.client.util.generic.ABValue;
import com.dwarslooper.cactus.client.util.generic.JsonCodec;
import com.dwarslooper.cactus.client.util.generic.ScreenUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2507;
import net.minecraft.class_2561;
import net.minecraft.class_339;
import net.minecraft.class_4267;
import net.minecraft.class_437;
import net.minecraft.class_500;
import net.minecraft.class_5244;
import net.minecraft.class_641;
import net.minecraft.class_642;
import net.minecraft.class_7842;
import net.minecraft.class_7919;
import net.minecraft.class_642.class_8678;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {class_500.class},
   priority = 1200
)
public abstract class JoinMultiplayerScreenMixin extends class_437 implements ITranslatable {
   @Shadow
   private class_641 field_3040;
   @Shadow
   protected class_4267 field_3043;
   @Unique
   private final JsonCodec<class_642> serverCodec = (new JsonCodec((map) -> {
      return new class_642((String)map.get("name"), (String)map.get("address"), class_8678.field_45611);
   }, (info) -> {
      return Map.of("name", info.field_3752, "address", info.field_3761);
   })).field("name", String.class).field("address", String.class);
   @Unique
   private List<class_339> expandableWidgets = new ArrayList();
   @Unique
   private static final ABValue<String> expandState = ABValue.of("▼", "▶");
   @Unique
   private static final int ACCOUNT_STATUS_ONLINE_COLOR = -11141291;
   @Unique
   private static final int ACCOUNT_STATUS_OFFLINE_COLOR = -3372920;
   @Unique
   private class_7842 accountStatusWidget;
   @Unique
   private String accountStatusLastName;
   @Unique
   private boolean accountStatusLastOffline;
   @Unique
   private boolean accountStatusStateInitialized;

   @Shadow
   protected abstract void method_20379(boolean var1);

   protected JoinMultiplayerScreenMixin(class_2561 title) {
      super(title);
   }

   @Inject(
      method = {"method_25426"},
      at = {@At("TAIL")}
   )
   private void injectCustomServerEntry(CallbackInfo ci) {
      CTextureButtonWidget serverListImportButton;
      if ((Boolean)CactusSettings.get().accountWidgetMultiplayer.get()) {
         serverListImportButton = new CTextureButtonWidget(6, 6, 140, (button) -> {
            CactusConstants.mc.method_1507(new AccountListScreen(this));
         });
         serverListImportButton.method_47400(class_7919.method_47407(class_2561.method_43471("gui.screen.cactus.button.accounts")));
         this.method_37063(serverListImportButton);
      }

      serverListImportButton = new CTextureButtonWidget(30, 6, 420, (button) -> {
         CompletableFuture.supplyAsync(() -> {
            return TinyFileDialogs.tinyfd_openFileDialog(this.getTranslatableElement("selectFile", new Object[0]), System.getProperty("user.home") + "\\server_list.json", Utils.createFileTypeFilter("*.json", "*.nbt"), "JSON / NBT Files", false);
         }).thenAccept((path) -> {
            if (path != null) {
               File file = new File(path);
               if (file.exists() && file.isFile()) {
                  try {
                     class_641 serverList = new class_641(CactusConstants.mc);
                     int i;
                     if (!path.endsWith(".json")) {
                        if (path.endsWith(".nbt")) {
                           class_2487 root = class_2507.method_10633(file.toPath());
                           class_2499 servers = (class_2499)root.method_10554("servers").get();

                           for(i = servers.size() - 1; i >= 0; --i) {
                              serverList.method_2988(class_642.method_2993((class_2487)servers.method_10602(i).get()), false);
                           }
                        }
                     } else {
                        String jsonContent = Files.readString(file.toPath());
                        JsonArray serverListArray = JsonParser.parseString(jsonContent).getAsJsonArray();

                        for(i = serverListArray.size() - 1; i >= 0; --i) {
                           JsonObject serverEntry = serverListArray.get(i).getAsJsonObject();
                           if (serverEntry.has("name") && serverEntry.has("address")) {
                              String name = serverEntry.get("name").getAsString();
                              String address = serverEntry.get("address").getAsString();
                              serverList.method_2988(new class_642(name, address, class_8678.field_45611), false);
                           } else {
                              CactusClient.getLogger().warn("Skipping invalid server entry for import ({})", serverEntry);
                           }
                        }
                     }

                     this.field_3040 = serverList;
                     this.field_3040.method_2987();
                     this.field_3043.method_20125(this.field_3040);
                  } catch (Exception var10) {
                     CactusClient.getLogger().error("Failed to import serverlist", var10);
                  }
               }

            }
         });
      });
      serverListImportButton.method_47400(class_7919.method_47407(class_2561.method_43470(this.getTranslatableElement("button.importServerList", new Object[0]))));
      this.method_37063(serverListImportButton);
      CTextureButtonWidget serverListExportButton = new CTextureButtonWidget(54, 6, 440, (button) -> {
         CompletableFuture.supplyAsync(() -> {
            return TinyFileDialogs.tinyfd_saveFileDialog(this.getTranslatableElement("saveAs", new Object[0]), System.getProperty("user.home") + "\\server_list.json", Utils.createFileTypeFilter("*.json", "*.nbt"), "JSON / NBT Files");
         }).thenAccept((path) -> {
            if (path != null) {
               File file = new File(path);
               if (!file.exists() || file.isFile()) {
                  try {
                     if (path.endsWith(".json")) {
                        JsonArray serverListArray = new JsonArray();

                        for(int i = 0; i < this.field_3040.method_2984(); ++i) {
                           JsonObject serverEntry = new JsonObject();
                           serverEntry.addProperty("name", this.field_3040.method_2982(i).field_3752);
                           serverEntry.addProperty("address", this.field_3040.method_2982(i).field_3761);
                           serverListArray.add(serverEntry);
                        }

                        String jsonContent = ConfigHandler.getGson().toJson(serverListArray);
                        Files.write(file.toPath(), jsonContent.getBytes(), new OpenOption[0]);
                     } else if (path.endsWith(".nbt")) {
                        class_2487 root = new class_2487();
                        class_2499 list = new class_2499();

                        for(int ix = 0; ix < this.field_3040.method_2984(); ++ix) {
                           list.add(this.field_3040.method_2982(ix).method_2992());
                        }

                        root.method_10566("servers", list);
                        class_2507.method_10630(root, file.toPath());
                     }
                  } catch (Exception var6) {
                     CactusClient.getLogger().error("Failed to export serverlist", var6);
                  }
               }

            }
         });
      });
      serverListExportButton.method_47400(class_7919.method_47407(class_2561.method_43471(this.getTranslatableElement("button.exportServerList", new Object[0]))));
      this.method_37063(serverListExportButton);
      class_339 viaButton = ScreenUtils.getButton(this, "ViaFabricPlus");
      if (viaButton != null) {
         viaButton.method_25355(class_2561.method_43470("Version").method_10852(class_5244.field_39678));
         if (viaButton.method_46426() < 20 && viaButton.method_46427() < 20) {
            viaButton.method_48229(30, 6);
            viaButton.method_25358(60);
         }
      }

      this.accountStatusWidget = new class_7842(class_2561.method_43473(), CactusConstants.mc.field_1772);
      this.method_37063(this.accountStatusWidget);
      this.cactus$updateAccountStatusWidget(true);
   }

   @Inject(
      method = {"method_25393"},
      at = {@At("TAIL")}
   )
   private void cactus$updateAccountStatusText(CallbackInfo ci) {
      this.cactus$updateAccountStatusWidget(false);
   }

   @Unique
   private void cactus$updateAccountStatusWidget(boolean forceUpdate) {
      if (this.accountStatusWidget != null && this.field_3043 != null && CactusConstants.mc.field_1772 != null) {
         boolean visible = (Boolean)CactusSettings.get().accountStatusTextMultiplayer.get();
         this.accountStatusWidget.field_22764 = visible;
         if (visible) {
            String accountName = (String)this.cactus$getAccountNameFromSwitcher().orElse(CactusConstants.mc.method_1548().method_1676());
            boolean offline = SessionUtils.isSessionOfflineMode();
            boolean statusChanged = forceUpdate || !this.accountStatusStateInitialized || offline != this.accountStatusLastOffline || !Objects.equals(accountName, this.accountStatusLastName);
            class_2561 message;
            if (statusChanged) {
               this.accountStatusLastName = accountName;
               this.accountStatusLastOffline = offline;
               this.accountStatusStateInitialized = true;
               message = this.cactus$getAccountStatusText(accountName, offline);
               this.accountStatusWidget.method_25355(message);
            }

            message = this.accountStatusWidget.method_25369();
            int var10001 = this.field_3043.method_46427();
            Objects.requireNonNull(CactusConstants.mc.field_1772);
            int statusY = Math.max(5, var10001 - 9 - 2);
            int statusX = (this.field_22789 - CactusConstants.mc.field_1772.method_27525(message)) / 2;
            this.accountStatusWidget.method_48229(statusX, statusY);
         }
      }
   }

   @Unique
   private class_2561 cactus$getAccountStatusText(String accountName, boolean offline) {
      String modeKey = offline ? "gui.screen.multiplayer.accountStatus.offline" : "gui.screen.multiplayer.accountStatus.online";
      int color = offline ? -3372920 : -11141291;
      return class_2561.method_43469("gui.screen.multiplayer.accountStatus", new Object[]{accountName, class_2561.method_43471(modeKey)}).method_54663(color);
   }

   @Unique
   private Optional<String> cactus$getAccountNameFromSwitcher() {
      return AccountManager.getAccountList().stream().filter((account) -> {
         return account.uuid().equals(CactusConstants.mc.method_1548().method_44717());
      }).map((account) -> {
         return account.name();
      }).findFirst();
   }

   @Unique
   public String getKey() {
      return "gui.screen.multiplayer";
   }
}
