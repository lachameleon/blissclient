package com.dwarslooper.cactus.client.util.game;

import com.dwarslooper.cactus.client.util.CactusConstants;
import net.minecraft.class_2248;
import net.minecraft.class_2561;
import net.minecraft.class_640;
import net.minecraft.class_7923;

public class ServerUtils {
   public static int ping() {
      if (CactusConstants.mc.method_1562() != null && CactusConstants.mc.field_1724 != null) {
         class_640 playerListEntry = CactusConstants.mc.method_1562().method_2871(CactusConstants.mc.field_1724.method_5667());
         return playerListEntry != null ? playerListEntry.method_2959() : 0;
      } else {
         return 0;
      }
   }

   public static String getBlockIdStr(class_2248 block) {
      return String.valueOf(class_7923.field_41175.method_10221(block.method_9564().method_26204()));
   }

   public static String getBlockNameStr(class_2248 block) {
      return class_2561.method_43471(block.method_63499()).getString();
   }
}
