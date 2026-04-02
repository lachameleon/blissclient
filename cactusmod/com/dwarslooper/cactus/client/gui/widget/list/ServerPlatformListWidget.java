package com.dwarslooper.cactus.client.gui.widget.list;

import com.dwarslooper.cactus.client.gui.screen.impl.CreateLocalServerScreen;
import com.dwarslooper.cactus.client.systems.localserver.PlatformManager;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.function.BiConsumer;
import net.minecraft.class_11909;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_4280;
import net.minecraft.class_4280.class_4281;
import org.jetbrains.annotations.NotNull;

public class ServerPlatformListWidget extends class_4280<ServerPlatformListWidget.ServerPlatformListEntry> {
   private static final class_2561 BACK;
   public final CreateLocalServerScreen parent;
   public String selectedVersion;
   public PlatformManager.Platform selectedPlatform;

   public ServerPlatformListWidget(CreateLocalServerScreen parent) {
      super(CactusConstants.mc, parent.field_22789, parent.field_22790 - 120 - 60, 84, 20);
      this.parent = parent;
      this.platformSelection();
   }

   public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
      if (super.method_25402(click, doubled)) {
         this.parent.validateStates();
         return true;
      } else {
         return false;
      }
   }

   public int method_25322() {
      return 160;
   }

   public void add(ServerPlatformListWidget.ServerPlatformListEntry entry) {
      this.method_25321(entry);
   }

   public void platformSelection() {
      this.selectedPlatform = null;
      this.selectedVersion = null;
      this.method_25339();
      PlatformManager.Platform[] var1 = PlatformManager.Platform.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         PlatformManager.Platform value = var1[var3];
         this.method_25321(new ServerPlatformListWidget.Software(value));
      }

      this.method_44382(0.0D);
   }

   public void versionSelectionForPlatform(PlatformManager.Platform platform) {
      this.selectedPlatform = platform;
      if (platform == PlatformManager.Platform.CUSTOM) {
         this.selectedVersion = "custom";
      } else {
         this.method_25339();
         this.method_25321(new ServerPlatformListWidget.Navigation(BACK, (nav, click) -> {
            this.platformSelection();
         }));
         PlatformManager.fetchVersionFor(platform).forEach((s) -> {
            this.method_25321(new ServerPlatformListWidget.Version(s));
         });
         this.method_44382(0.0D);
      }
   }

   protected int method_65507() {
      return this.method_31383();
   }

   static {
      BACK = class_2561.method_43473().method_10852(class_2561.method_43470("<- ").method_10852(class_2561.method_43471("gui.screen.create_local_server.back"))).method_27695(new class_124[]{class_124.field_1067, class_124.field_1061});
   }

   public class Software extends ServerPlatformListWidget.ServerPlatformListEntry {
      private final PlatformManager.Platform platform;

      public Software(PlatformManager.Platform platform) {
         this.platform = platform;
      }

      public PlatformManager.Platform getPlatform() {
         return this.platform;
      }

      public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
         ServerPlatformListWidget.this.versionSelectionForPlatform(this.platform);
         return super.method_25402(click, doubled);
      }

      public void method_25343(class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
         context.method_27535(CactusConstants.mc.field_1772, class_2561.method_43470(this.platform.getDisplayName()).method_27692(hovered ? class_124.field_1060 : class_124.field_1068), this.method_73380() + 2, this.method_73382() + 4, -1);
      }
   }

   public static class Navigation extends ServerPlatformListWidget.ServerPlatformListEntry {
      private final BiConsumer<ServerPlatformListWidget.Navigation, class_11909> clickCallback;
      private final class_2561 text;

      public Navigation(class_2561 text, BiConsumer<ServerPlatformListWidget.Navigation, class_11909> clickCallback) {
         this.text = text;
         this.clickCallback = clickCallback;
      }

      public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
         this.clickCallback.accept(this, click);
         return super.method_25402(click, doubled);
      }

      public void method_25343(class_332 context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
         context.method_27535(CactusConstants.mc.field_1772, this.text, this.method_73380(), this.method_73382() + 4, -1);
      }
   }

   public class Version extends ServerPlatformListWidget.ServerPlatformListEntry {
      private final String version;

      public Version(String version) {
         this.version = version;
      }

      public String getVersion() {
         return this.version;
      }

      public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
         ServerPlatformListWidget.this.selectedVersion = this.version;
         return true;
      }

      public void method_25343(class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
         context.method_25303(CactusConstants.mc.field_1772, this.version, this.method_73380() + 2, this.method_73382() + 4, -1);
      }
   }

   public abstract static class ServerPlatformListEntry extends class_4281<ServerPlatformListWidget.ServerPlatformListEntry> {
      @NotNull
      public class_2561 method_37006() {
         return class_2561.method_43473();
      }
   }
}
