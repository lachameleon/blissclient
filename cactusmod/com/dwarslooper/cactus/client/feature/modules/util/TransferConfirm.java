package com.dwarslooper.cactus.client.feature.modules.util;

import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.StringListSetting;
import java.util.Iterator;
import java.util.List;

public class TransferConfirm extends Module {
   public Setting<List<String>> alwaysAutoAccept;

   public TransferConfirm() {
      super("transferConfirm", ModuleManager.CATEGORY_UTILITY);
      this.alwaysAutoAccept = this.mainGroup.add(new StringListSetting("autoAccept", new String[0]));
   }

   public boolean isTrusted(String address) {
      String lowerAddress = address.toLowerCase();
      List<String> trusted = (List)this.alwaysAutoAccept.get();
      if (trusted.contains(lowerAddress)) {
         return true;
      } else {
         Iterator var4 = trusted.iterator();

         while(var4.hasNext()) {
            String entry = (String)var4.next();
            if (entry.startsWith("*.")) {
               String suffix = entry.substring(1);
               if (lowerAddress.endsWith(suffix)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public void trust(String address) {
      String lower = address.toLowerCase();
      List<String> list = (List)this.alwaysAutoAccept.get();
      if (!list.contains(lower)) {
         list.add(lower);
      }

   }
}
