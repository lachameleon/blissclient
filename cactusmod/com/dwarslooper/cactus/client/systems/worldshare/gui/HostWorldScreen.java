package com.dwarslooper.cactus.client.systems.worldshare.gui;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.CIntSliderWidget;
import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.systems.worldshare.OasisHostManager;
import com.dwarslooper.cactus.client.util.CactusConstants;
import net.minecraft.class_10799;
import net.minecraft.class_1132;
import net.minecraft.class_124;
import net.minecraft.class_1934;
import net.minecraft.class_2477;
import net.minecraft.class_2561;
import net.minecraft.class_3093;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_342;
import net.minecraft.class_3521;
import net.minecraft.class_3532;
import net.minecraft.class_437;
import net.minecraft.class_5244;
import net.minecraft.class_5250;
import net.minecraft.class_5676;
import net.minecraft.class_7842;
import net.minecraft.class_7845;
import net.minecraft.class_7919;
import net.minecraft.class_8030;
import net.minecraft.class_8086;
import net.minecraft.class_8087;
import net.minecraft.class_8088;
import net.minecraft.class_8089;
import net.minecraft.class_7845.class_7939;

public class HostWorldScreen extends CScreen {
   private static final class_2561 ALLOW_COMMANDS_TEXT = class_2561.method_43471("selectWorld.allowCommands");
   private static final class_2561 GAME_MODE_TEXT = class_2561.method_43471("selectWorld.gameMode");
   private static final class_2561 PORT_TEXT = class_2561.method_43471("lanServer.port");
   private static final class_2561 UNAVAILABLE_PORT_TEXT = class_2561.method_43469("lanServer.port.unavailable", new Object[]{1024, 65535});
   private static final class_2561 INVALID_PORT_TEXT = class_2561.method_43469("lanServer.port.invalid", new Object[]{1024, 65535});
   private final class_8088 tabManager = new class_8088((x$0) -> {
      class_339 var10000 = (class_339)this.method_37063(x$0);
   }, (x$0) -> {
      this.method_37066(x$0);
   });
   private class_8089 tabNavigation;
   private class_7845 grid;
   private class_1132 integratedServer;
   private CButtonWidget startButton;
   private class_1934 gameMode;
   private boolean allowCommands;
   private int port;
   private int maxPlayers;
   private HostWorldScreen.ShareType shareType;

   public HostWorldScreen() {
      super("host_world");
      this.gameMode = class_1934.field_9215;
      this.port = class_3521.method_15302();
   }

   public void method_25426() {
      super.init(false);
      this.integratedServer = this.field_22787.method_1576();
      this.method_37063(this.startButton = new CButtonWidget(this.field_22789 / 2 - 120 - 4, this.field_22790 - 30, 120, 20, class_2561.method_43470(this.getTranslatableElement("start", new Object[0])), (button) -> {
         OasisHostManager.forThisSession = this.shareType == HostWorldScreen.ShareType.Public;
         this.field_22787.method_1507((class_437)null);
         class_5250 text = this.integratedServer.method_3763(this.gameMode, this.allowCommands, this.port) ? class_3093.method_46869(this.port) : class_2561.method_43471("commands.publish.failed");
         this.field_22787.field_1705.method_1743().method_1812(text);
         this.field_22787.method_24288();
      }));
      this.method_37063(new CButtonWidget(this.field_22789 / 2 + 4, this.field_22790 - 30, 120, 20, class_5244.field_24335, (button) -> {
         this.method_25419();
      }));
      this.grid = (new class_7845()).method_48635(10);
      this.tabNavigation = class_8089.method_48623(this.tabManager, this.field_22789).method_48631(new class_8087[]{new HostWorldScreen.GeneralTab(), new HostWorldScreen.HostTab()}).method_48627();
      this.method_37063(this.tabNavigation);
      this.tabNavigation.method_48987(0, false);
      this.method_48640();
   }

   protected void method_48640() {
      if (this.tabNavigation != null) {
         this.tabNavigation.method_48618(this.field_22789);
         this.tabNavigation.method_49613();
         int i = this.tabNavigation.method_48202().method_49619();
         class_8030 screenRect = new class_8030(0, i, this.field_22789, this.field_22790);
         this.tabManager.method_48616(screenRect);
         this.grid.method_48222();
      }

   }

   public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_25290(class_10799.field_56883, field_49896, 0, class_3532.method_28139(this.field_22790 - 36 - 2, 2), 0.0F, 0.0F, this.field_22789, 2, 32, 2);
   }

   // $FF: synthetic method
   static class_327 access$201(HostWorldScreen x0) {
      return x0.field_22793;
   }

   public class GeneralTab extends class_8086 {
      private final class_342 portField;

      public GeneralTab() {
         super(class_2561.method_43471(HostWorldScreen.this.getTranslatableElement("tab.general", new Object[0])));
         HostWorldScreen.this.gameMode = HostWorldScreen.this.integratedServer.method_3790();
         HostWorldScreen.this.allowCommands = HostWorldScreen.this.integratedServer.method_27728().method_194();
         class_7939 adder = this.field_42139.method_48635(10).method_48636(8).method_47610(2);
         adder.method_47612(class_5676.method_32606(class_1934::method_32763, HostWorldScreen.this.gameMode).method_32624(new class_1934[]{class_1934.field_9215, class_1934.field_9219, class_1934.field_9220, class_1934.field_9216}).method_32617(0, 0, 150, 20, HostWorldScreen.GAME_MODE_TEXT, (button, gameMode) -> {
            HostWorldScreen.this.gameMode = gameMode;
         }));
         adder.method_47612(class_5676.method_32614(HostWorldScreen.this.allowCommands).method_32617(0, 0, 150, 20, HostWorldScreen.ALLOW_COMMANDS_TEXT, (button, allowCommands) -> {
            HostWorldScreen.this.allowCommands = allowCommands;
         }));
         class_7939 portAdder = (new class_7845()).method_48636(4).method_47610(1);
         portAdder.method_47612(new class_7842(HostWorldScreen.PORT_TEXT, HostWorldScreen.this.field_22793));
         this.portField = new class_342(CactusConstants.mc.field_1772, 0, 0, 150, 20, class_2561.method_43471("lanServer.port"));
         this.portField.method_1863((portText) -> {
            class_2561 text = this.updatePort(portText);
            this.portField.method_47404(class_2561.method_43470(HostWorldScreen.this.port.makeConcatWithConstants<invokedynamic>(HostWorldScreen.this.port)).method_27692(class_124.field_1063));
            if (text == null) {
               this.portField.method_1868(-2039584);
               this.portField.method_47400((class_7919)null);
               HostWorldScreen.this.startButton.field_22763 = true;
            } else {
               this.portField.method_1868(-43691);
               this.portField.method_47400(class_7919.method_47407(text));
               HostWorldScreen.this.startButton.field_22763 = false;
            }

         });
         this.portField.method_47404(class_2561.method_43470(HostWorldScreen.this.port.makeConcatWithConstants<invokedynamic>(HostWorldScreen.this.port)).method_27692(class_124.field_1063));
         this.portField.method_1852("");
         portAdder.method_47615(this.portField, adder.method_47611().method_46464(1));
         adder.method_47612(portAdder.method_48638());
         class_7939 maxPlayersAdder = (new class_7845()).method_48636(4).method_47610(1);
         maxPlayersAdder.method_47612(new class_7842(class_2561.method_43470(HostWorldScreen.this.getTranslatableElement("maxPlayers", new Object[0])), HostWorldScreen.this.field_22793));
         CIntSliderWidget maxPlayersWidget = new CIntSliderWidget(0, 0, 150, 20, class_2561.method_43470(HostWorldScreen.this.getTranslatableElement("slots", new Object[0])), 1, 8, 8, (i) -> {
            HostWorldScreen.this.maxPlayers = i;
         }, (i) -> {
            return Integer.toString(i);
         });
         maxPlayersAdder.method_47612(maxPlayersWidget);
         adder.method_47612(maxPlayersAdder.method_48638());
      }

      private class_2561 updatePort(String portText) {
         if (portText.isBlank()) {
            HostWorldScreen.this.port = class_3521.method_15302();
            return null;
         } else {
            try {
               HostWorldScreen.this.port = Integer.parseInt(portText);
               if (HostWorldScreen.this.port >= 1024 && HostWorldScreen.this.port <= 65535) {
                  return !class_3521.method_46872(HostWorldScreen.this.port) ? HostWorldScreen.UNAVAILABLE_PORT_TEXT : null;
               } else {
                  return HostWorldScreen.INVALID_PORT_TEXT;
               }
            } catch (NumberFormatException var3) {
               HostWorldScreen.this.port = class_3521.method_15302();
               return HostWorldScreen.INVALID_PORT_TEXT;
            }
         }
      }
   }

   public class HostTab extends class_8086 {
      private class_342 customDomainWidget;
      private class_339 shareTypeWidget;

      public HostTab() {
         super(class_2561.method_43470(HostWorldScreen.this.getTranslatableElement("tab.host", new Object[0])));
         class_7939 adder = this.field_42139.method_48635(10).method_48636(8).method_47610(2);
         adder.method_47612(this.shareTypeWidget = class_5676.method_32606((t) -> {
            return class_2561.method_43470(t.name);
         }, HostWorldScreen.ShareType.Lan).method_32624(new HostWorldScreen.ShareType[]{HostWorldScreen.ShareType.Lan, HostWorldScreen.ShareType.Public}).method_32617(0, 0, 150, 20, class_2561.method_43470("Open to"), (m, v) -> {
            boolean sharePublic = v == HostWorldScreen.ShareType.Public;
            HostWorldScreen.this.shareType = v;
            this.customDomainWidget.field_22763 = sharePublic;
            this.customDomainWidget.method_1852(sharePublic ? this.customDomainWidget.method_1882() : "");
         }));
         this.shareTypeWidget.field_22763 = IRCClient.connected();
         adder.method_47612(new CButtonWidget(0, 0, 150, 20, class_2561.method_43471("gui.screen.lan_whitelist.title").method_10852(class_5244.field_39678), (button) -> {
            HostWorldScreen.this.field_22787.method_1507(new WhitelistScreen(((OasisHostManager)CactusClient.getConfig(OasisHostManager.class)).getWhitelist()));
         }));
         class_7939 adder2 = (new class_7845()).method_48636(4).method_47610(1);
         this.customDomainWidget = new class_342(HostWorldScreen.access$201(HostWorldScreen.this), 0, 0, 308, 20, class_2561.method_43470(""));
         this.customDomainWidget.method_1863((s) -> {
            this.customDomainWidget.method_1887(HostWorldScreen.this.shareType != HostWorldScreen.ShareType.Public ? "" : (s.isEmpty() ? CactusConstants.mc.method_1548().method_1676().toLowerCase() : "") + ".cactusmod.xyz");
         });
         this.customDomainWidget.method_1890((s) -> {
            return s.matches("^[a-zA-Z0-9_]*$") && s.length() <= 20;
         });
         this.customDomainWidget.method_1880(200);
         this.customDomainWidget.field_22763 = false;
         adder2.method_47612(new class_7842(class_2561.method_43470(HostWorldScreen.this.getTranslatableElement("customDomain", new Object[0])), HostWorldScreen.this.field_22793));
         adder2.method_47612(this.customDomainWidget);
         adder.method_47613(adder2.method_48638(), 2);
      }
   }

   public static enum ShareType {
      Public("gui.screen.host_world.type.public"),
      Lan("gui.screen.host_world.type.lan");

      public final String name;

      private ShareType(String key) {
         this.name = class_2477.method_10517().method_48307(key);
      }

      // $FF: synthetic method
      private static HostWorldScreen.ShareType[] $values() {
         return new HostWorldScreen.ShareType[]{Public, Lan};
      }
   }
}
