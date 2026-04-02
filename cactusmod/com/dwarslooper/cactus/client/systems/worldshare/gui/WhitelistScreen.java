package com.dwarslooper.cactus.client.systems.worldshare.gui;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.screen.window.TextInputWindow;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.systems.worldshare.OasisHostManager;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.List;
import net.minecraft.class_124;
import net.minecraft.class_2477;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_5244;
import net.minecraft.class_5676;
import net.minecraft.class_7940;
import org.jetbrains.annotations.NotNull;

public class WhitelistScreen extends CScreen {
   private final List<String> whitelist;
   private final OasisHostManager handler = (OasisHostManager)CactusClient.getConfig(OasisHostManager.class);
   private WhitelistListWidget whitelistListWidget;

   public WhitelistScreen(List<String> whitelist) {
      super("lan_whitelist");
      this.whitelist = whitelist;
   }

   public void method_25426() {
      super.method_25426();
      this.method_37063(class_5676.method_32614(this.handler.isWhitelistEnabled()).method_32617(this.field_22789 / 2 - 60, 58, 120, 20, this.field_22785, (button, value) -> {
         this.handler.setWhitelistEnabled(value);
      }));
      this.method_25429(this.whitelistListWidget = new WhitelistListWidget(this, (entries) -> {
         this.update();
      }));
      this.whitelistListWidget.addAll(this.whitelist);
      class_7940 whitelistInfo = (new class_7940(class_2561.method_43470(this.getTranslatableElement("whitelist", new Object[0])).method_27692(class_124.field_1061), this.field_22793)).method_48984(310).method_48981(true);
      whitelistInfo.method_48229(this.field_22789 / 2 - 155, 28);
      this.method_37063(whitelistInfo);
      this.method_37063(new CButtonWidget(this.field_22789 / 2 - 100 - 54, this.field_22790 - 30, 100, 20, class_2561.method_43470(this.getTranslatableElement("addPlayer", new Object[0])), (button) -> {
         CactusConstants.mc.method_1507((new TextInputWindow("none", this.getTranslatableElement("addPlayer", new Object[0]))).range(3, 16).allowEmptyText(false).setPlaceholder(class_2477.method_10517().method_48307("gui.screen.account_switcher.offlineSession.text")).onSubmit((s) -> {
            this.whitelistListWidget.add(s);
            this.update();
         }));
      }));
      this.method_37063(new CButtonWidget(this.field_22789 / 2 - 50, this.field_22790 - 30, 100, 20, class_2561.method_43470(this.getTranslatableElement("removePlayer", new Object[0])), (button) -> {
         WhitelistListWidget.WhitelistEntry entry = (WhitelistListWidget.WhitelistEntry)this.whitelistListWidget.method_25334();
         if (entry != null) {
            this.whitelistListWidget.remove(entry.getName());
            this.update();
         }

      }));
      this.method_37063(new CButtonWidget(this.field_22789 / 2 + 50 + 4, this.field_22790 - 30, 100, 20, class_5244.field_24334, (button) -> {
         this.method_25419();
      }));
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      this.whitelistListWidget.method_25394(context, mouseX, mouseY, delta);
      context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 16, -1);
   }

   public void method_25410(int width, int height) {
      super.method_25410(width, height);
      this.method_41843();
   }

   public void update() {
      this.whitelist.clear();
      this.whitelist.addAll(this.whitelistListWidget.method_25396().stream().map(WhitelistListWidget.WhitelistEntry::getName).toList());
   }
}
