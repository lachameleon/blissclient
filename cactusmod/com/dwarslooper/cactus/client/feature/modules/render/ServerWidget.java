package com.dwarslooper.cactus.client.feature.modules.render;

import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.impl.EnumSetSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import java.util.Set;
import net.minecraft.class_420;
import net.minecraft.class_422;
import net.minecraft.class_433;
import net.minecraft.class_437;

public class ServerWidget extends Module {
   public Setting<Set<ServerWidget.ShowAt>> showAt;

   public ServerWidget() {
      super("serverWidget", ModuleManager.CATEGORY_RENDERING);
      this.showAt = this.mainGroup.add(new EnumSetSetting("showAt", ServerWidget.ShowAt.class, new ServerWidget.ShowAt[]{ServerWidget.ShowAt.DirectConnect, ServerWidget.ShowAt.AddOrEditServer, ServerWidget.ShowAt.GameMenu}));
   }

   public boolean shouldAddTo(class_437 screen) {
      return this.active() && ((Set)this.showAt.get()).stream().anyMatch((at) -> {
         return at.getScreen().isInstance(screen);
      });
   }

   public static enum ShowAt {
      DirectConnect(class_420.class),
      AddOrEditServer(class_422.class),
      GameMenu(class_433.class);

      private final Class<? extends class_437> screen;

      private ShowAt(Class<? extends class_437> screen) {
         this.screen = screen;
      }

      public Class<? extends class_437> getScreen() {
         return this.screen;
      }

      // $FF: synthetic method
      private static ServerWidget.ShowAt[] $values() {
         return new ServerWidget.ShowAt[]{DirectConnect, AddOrEditServer, GameMenu};
      }
   }
}
