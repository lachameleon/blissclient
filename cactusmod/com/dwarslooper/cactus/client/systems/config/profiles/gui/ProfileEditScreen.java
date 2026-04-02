package com.dwarslooper.cactus.client.systems.config.profiles.gui;

import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.list.CheckboxListWidget;
import com.dwarslooper.cactus.client.systems.config.FileConfiguration;
import com.dwarslooper.cactus.client.systems.config.profiles.ConfigurationProfile;
import com.dwarslooper.cactus.client.systems.config.profiles.ProfileManager;
import com.dwarslooper.cactus.client.systems.config.settings.impl.StringListSetting;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.WidgetIcons;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_342;
import net.minecraft.class_410;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_5244;
import net.minecraft.class_7919;
import org.jetbrains.annotations.NotNull;

public class ProfileEditScreen extends CScreen {
   private final ProfileManager manager;
   private final ConfigurationProfile profile;
   private class_342 nameWidget;
   private CheckboxListWidget<String> selections;
   private List<String> loadOnJoin;
   private class_339 doneButton;

   public ProfileEditScreen(ProfileManager manager, ConfigurationProfile profile) {
      super("editProfile");
      this.manager = manager;
      this.profile = profile;
   }

   public ProfileEditScreen(ProfileManager manager) {
      super("createProfile");
      this.manager = manager;
      this.profile = new ConfigurationProfile("");
   }

   public void method_25426() {
      super.method_25426();
      int x = this.field_22789 / 2 - 100 - 2;
      int y = this.field_22790 / 5;
      int right = x + 200 + 4;
      int bottomY = y + 24 + 100 + 24 + 4;
      if (this.loadOnJoin == null) {
         this.loadOnJoin = new ArrayList(this.profile.getLoadOnJoin());
      }

      this.doneButton = class_4185.method_46430(class_5244.field_24334, (b) -> {
         this.profile.getConfigurations().clear();
         this.profile.getConfigurations().addAll(this.getConfigurations());
         this.profile.getLoadOnJoin().clear();
         this.profile.getLoadOnJoin().addAll(this.loadOnJoin);
         this.profile.setName(this.nameWidget.method_1882());
         this.manager.addIfAbsent(this.profile);
         this.method_25419();
      }).method_46434(right - 100, bottomY, 100, 20).method_46431();
      if (this.nameWidget == null) {
         this.nameWidget = new class_342(CactusConstants.mc.field_1772, x, y, 204, 20, class_2561.method_43473());
         this.nameWidget.method_1880(20);
         this.nameWidget.method_1863((s) -> {
            this.nameWidget.method_1887(s.isEmpty() ? "Profile Name.." : "");
            this.nameWidget.method_1868(s.isEmpty() ? -43691 : -2039584);
            this.update();
         });
         this.nameWidget.method_1852(this.profile.getName());
      }

      this.nameWidget.method_48229(x, y);
      class_339 importWidget = new CTextureButtonWidget(right - 20 - 44, y, WidgetIcons.IMPORT.offsetX(), (b) -> {
         this.manager.importProfile(this.profile);
      });
      importWidget.method_47400(class_7919.method_47407(class_2561.method_43470("Import")));
      class_339 exportWidget = new CTextureButtonWidget(right - 20 - 22, y, WidgetIcons.EXPORT.offsetX(), (b) -> {
         this.manager.exportProfile(this.profile);
      });
      exportWidget.method_47400(class_7919.method_47407(class_2561.method_43470("Export")));
      class_339 deleteWidget = new CTextureButtonWidget(right - 20, y, WidgetIcons.DELETE.offsetX(), (b) -> {
         class_437 parent = CactusConstants.mc.field_1755;
         CactusConstants.mc.method_1507(new class_410((confirmed) -> {
            if (confirmed) {
               this.manager.remove(this.profile);
               if (parent instanceof ProfileEditScreen) {
                  ProfileEditScreen s = (ProfileEditScreen)parent;
                  CactusConstants.mc.method_1507(s.parent);
               }
            } else {
               CactusConstants.mc.method_1507(parent);
            }

         }, class_2561.method_30163("Delete profile"), class_2561.method_30163("Are you sure you want to delete '%s'?".formatted(new Object[]{this.profile.getName()}))));
      });
      deleteWidget.method_47400(class_7919.method_47407(class_2561.method_43471("key.keyboard.delete")));
      this.method_37063(this.nameWidget);
      if (!this.profile.getName().isEmpty()) {
         this.nameWidget.method_25358(138);
         this.method_37063(importWidget);
         this.method_37063(exportWidget);
         this.method_37063(deleteWidget);
      }

      if (this.selections == null) {
         this.selections = new CheckboxListWidget(204, 100, 0);
         Iterator var8 = ProfileManager.getConfigurations().iterator();

         while(var8.hasNext()) {
            FileConfiguration<?> config = (FileConfiguration)var8.next();
            boolean enabled = this.profile.containsConfiguration(config);
            String name = config.getName();
            this.selections.add(name, class_2561.method_43470(config.getDisplayName()), enabled);
         }
      }

      this.selections.method_73369(204, 100, x, y + 24);
      this.method_37063(this.selections);
      this.method_37063(class_4185.method_46430(class_2561.method_43471("gui.screen.editProfile.loadOnJoin").method_27693(" (%s)".formatted(new Object[]{this.loadOnJoin.size()})), (b) -> {
         CactusConstants.mc.method_1507(new StringListSetting.SelectionScreen(this.loadOnJoin, class_2561.method_43471("gui.screen.editProfile.loadOnJoin")));
      }).method_46434(x, bottomY - 24, 200, 20).method_46431());
      this.method_37063(this.doneButton);
      this.method_37063(class_4185.method_46430(class_5244.field_24335, (b) -> {
         this.method_25419();
      }).method_46434(x, bottomY, 100, 20).method_46431());
      this.method_48265(this.nameWidget);
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 8, -1);
   }

   private Collection<String> getConfigurations() {
      return this.selections.getAsMap().entrySet().stream().filter(Entry::getValue).map(Entry::getKey).toList();
   }

   private void update() {
      String text = this.nameWidget.method_1882();
      this.doneButton.field_22763 = !text.trim().isEmpty() && (text.equalsIgnoreCase(this.profile.getName()) || this.manager.canAdd(text));
   }
}
