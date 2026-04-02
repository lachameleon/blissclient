package com.dwarslooper.cactus.client.feature.modules.util;

import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.MessageReceiveEvent;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.systems.config.settings.group.SettingGroup;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.StringListSetting;
import com.dwarslooper.cactus.client.util.generic.TextUtils;
import java.util.List;
import net.minecraft.class_2561;
import org.apache.commons.lang3.StringUtils;

public class AntiAdvertise extends Module {
   public SettingGroup sg;
   public Setting<Boolean> blockBossBar;
   public Setting<Boolean> blockTitles;
   public Setting<Boolean> blockChat;
   public Setting<List<String>> keywords;

   public AntiAdvertise() {
      super("antiAds", ModuleManager.CATEGORY_UTILITY, (new Module.Options()).set(Module.Flag.HUD_LISTED, false));
      this.sg = this.settings.getDefault();
      this.blockBossBar = this.sg.add(new BooleanSetting("bossbar", true));
      this.blockTitles = this.sg.add(new BooleanSetting("titles", true));
      this.blockChat = this.sg.add(new BooleanSetting("chat", true));
      this.keywords = this.sg.add(new StringListSetting("keywords", new String[]{"% off", "percent off", "visit our shop", "sale", "vote for", "forget to vote"}));
   }

   public boolean isAd(class_2561 text) {
      return this.isAd(text.getString());
   }

   public boolean isAd(String text) {
      return ((List)this.keywords.get()).stream().anyMatch((term) -> {
         return StringUtils.containsIgnoreCase(text, term) || StringUtils.containsIgnoreCase(text, TextUtils.toSmallCaps(term));
      });
   }

   @EventHandler
   public void onMessageReceive(MessageReceiveEvent event) {
      if ((Boolean)this.blockChat.get() && this.isAd(event.message) && event.signature == null) {
         event.cancel();
      }

   }
}
