package com.dwarslooper.cactus.client.systems.config.settings.gui;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.toast.ToastSystem;
import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.irc.protocol.packets.SynchronizeSettingsC2SPacket;
import com.dwarslooper.cactus.client.systems.config.TreeSerializerFilter;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.class_1802;
import net.minecraft.class_2477;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_3674;
import net.minecraft.class_4185;
import net.minecraft.class_7919;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractSettingScreen extends CScreen {
   private SettingListWidget widget;
   private final SettingContainer container;
   private final Map<Setting<?>, Object> states = new HashMap();

   public AbstractSettingScreen(String key, SettingContainer container) {
      super(key);
      this.container = container;
   }

   public void method_25426() {
      super.method_25426();
      if (this.container != null) {
         this.method_25429(this.widget = new SettingListWidget(this.container, 0, 0, 0, 24));
         this.container.forEach((settings) -> {
            settings.forEach((setting) -> {
               if (setting.isSynced()) {
                  this.states.put(setting, setting.get());
               }

            });
         });
         this.method_48640();
      }
   }

   protected void method_48640() {
      this.widget.method_57714(this.field_22789, this.field_22790 - 40 - 20 - 12, 40);
   }

   public void method_25432() {
      List<Setting<?>> sync = new ArrayList();
      this.container.forEach((settings) -> {
         settings.forEach((setting) -> {
            if (setting.isSynced() && this.states.get(setting) != setting.get()) {
               sync.add(setting);
            }

         });
      });
      if (!sync.isEmpty()) {
         CactusClient.getInstance().getIrcClient().sendPacket(new SynchronizeSettingsC2SPacket(sync));
      }

   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      if (this.widget.method_25396().isEmpty()) {
         context.method_25300(CactusConstants.mc.field_1772, this.getTranslatableElement("text.no_settings", new Object[0]), this.field_22789 / 2, this.field_22790 / 2, Color.GRAY.getRGB());
      }

      this.widget.method_25394(context, mouseX, mouseY, delta);
   }

   protected void addCopyPaste() {
      addCopyAndPasteButtons((x$0) -> {
         class_339 var10000 = (class_339)this.method_37063(x$0);
      }, this.field_22789 - 6 - 40 - 4, this.field_22790 - 6 - 20, () -> {
         return this.container.toJson(TreeSerializerFilter.ALL);
      }, (o) -> {
         this.container.fromJson(o);
         this.method_41843();
      });
   }

   private static void addCopyAndPasteButtons(Consumer<class_339> adder, int x, int y, Supplier<JsonObject> copy, Consumer<JsonObject> paste) {
      class_4185 copyWidget = new CTextureButtonWidget(x, y, 380, (button) -> {
         class_3674 clipboard = new class_3674();
         clipboard.method_15979(CactusConstants.mc.method_22683(), Base64.getEncoder().encodeToString(((JsonObject)copy.get()).toString().getBytes()));
      });
      copyWidget.method_47400(class_7919.method_47407(class_2561.method_43471("gui.screen.settings_screen.copy")));
      class_4185 pasteWidget = new CTextureButtonWidget(x + 24, y, 400, (button) -> {
         try {
            JsonObject o = JsonParser.parseString(new String(Base64.getDecoder().decode(class_310.method_1551().field_1774.method_1460()))).getAsJsonObject();
            paste.accept(o);
         } catch (Exception var4) {
            String s = "gui.screen.settings_screen.pasteFailed.";
            ToastSystem.displayMessage(class_2477.method_10517().method_48307(s + "title"), class_2477.method_10517().method_48307(s + "text"), class_1802.field_8077);
         }

      });
      pasteWidget.method_47400(class_7919.method_47407(class_2561.method_43471("gui.screen.settings_screen.paste")));
      adder.accept(copyWidget);
      adder.accept(pasteWidget);
   }
}
