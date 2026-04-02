package com.dwarslooper.cactus.client.feature.modules.util;

import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.util.Utils;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import java.util.UUID;
import net.minecraft.class_124;
import net.minecraft.class_2535;
import net.minecraft.class_2561;
import net.minecraft.class_2720;
import net.minecraft.class_2856;
import net.minecraft.class_2558.class_10608;
import net.minecraft.class_2568.class_10613;
import net.minecraft.class_2856.class_2857;

public class AntiForcePack extends Module {
   public AntiForcePack() {
      super("anti_force_pack", ModuleManager.CATEGORY_UTILITY, (new Module.Options()).set(Module.Flag.SERVER_UNSAFE, true).set(Module.Flag.HUD_LISTED, false));
   }

   public void sendPackInfo(class_2720 packet) {
      ChatUtils.info((class_2561)class_2561.method_43470(this.translate("message." + (packet.comp_2161() ? "required" : "optional"), new Object[0])));
      ChatUtils.info((class_2561)class_2561.method_43470("[" + this.translate("message.download", new Object[0]) + "]").method_27692(class_124.field_1075).method_27694((style) -> {
         return style.method_10958(new class_10608(Utils.asURI(packet.comp_2159()))).method_10949(new class_10613(class_2561.method_43470(this.translate("message.download.tooltip", new Object[0]))));
      }));
   }

   public void sendPackets(class_2535 connection, UUID id) {
      if (connection != null) {
         connection.method_10743(new class_2856(id, class_2857.field_13016));
         Utils.unsafeDelayed(() -> {
            connection.method_10743(new class_2856(id, class_2857.field_13017));
         }, (long)(Math.random() * 1000.0D));
      }

   }
}
