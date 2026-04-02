package com.dwarslooper.cactus.client.feature.modules.render;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.ClientTickEvent;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.gui.screen.impl.ModuleOptionsScreen;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.EnumSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.StringSetting;
import com.dwarslooper.cactus.client.systems.ias.skins.SkinHelper;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.Utils;
import com.dwarslooper.cactus.client.util.mixinterface.ITextFieldWidget;
import java.io.BufferedInputStream;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_342;
import net.minecraft.class_5244;
import net.minecraft.class_7920;

public class StreamerMode extends Module {
   public static class_2960 SKIN_IDENTIFIER = class_2960.method_60655("cactus", "dynamic/nametags/skin");
   public static class_2561 WIDGET_TEXT_HIDDEN;
   public Setting<String> nick;
   public Setting<String> clientSkin;
   public Setting<StreamerMode.SkinModel> skinModel;
   public Setting<Boolean> hideServerAddresses;
   private boolean needsUpdate;
   private class_7920 determinedSkinModel;

   public void onEnable() {
      this.needsUpdate = true;
   }

   public StreamerMode() {
      super("streamerMode", ModuleManager.CATEGORY_RENDERING);
      this.nick = this.mainGroup.add(new StringSetting("nick", ""));
      this.clientSkin = this.mainGroup.add((new StringSetting("skin", "")).setMaxLength(200)).setCallback((s) -> {
         this.needsUpdate = !s.isEmpty();
      });
      this.skinModel = this.mainGroup.add(new EnumSetting("skinModel", StreamerMode.SkinModel.AutoDetermine));
      this.hideServerAddresses = this.mainGroup.add(new BooleanSetting("hideServers", false));
   }

   public boolean shouldChangeClientSkin() {
      return this.active() && !((String)this.clientSkin.get()).isEmpty() && CactusConstants.mc.method_1531().field_5286.containsKey(SKIN_IDENTIFIER);
   }

   public String hideName(String original) {
      if (!this.active()) {
         return original;
      } else {
         return !((String)this.nick.get()).isEmpty() ? Utils.replaceTypeableFormattingChars(original.replace(CactusConstants.mc.method_1548().method_1676(), (CharSequence)this.nick.get())) : original;
      }
   }

   @EventHandler
   public void onTick(ClientTickEvent event) {
      String url = (String)this.clientSkin.get();
      if (!url.isEmpty()) {
         if (this.needsUpdate && !(CactusConstants.mc.field_1755 instanceof ModuleOptionsScreen)) {
            this.needsUpdate = false;
            CompletableFuture.supplyAsync(() -> {
               try {
                  BufferedInputStream in = new BufferedInputStream((new URI(url)).toURL().openStream());

                  class_1011 var4;
                  try {
                     class_1011 nativeImage = class_1011.method_4309(in);
                     this.determinedSkinModel = SkinHelper.skinType(nativeImage);
                     var4 = nativeImage;
                  } catch (Throwable var6) {
                     try {
                        in.close();
                     } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                     }

                     throw var6;
                  }

                  in.close();
                  return var4;
               } catch (Exception var7) {
                  CactusClient.getLogger().error("Failed to fetch clientside skin", var7);
                  return null;
               }
            }).thenAccept((nativeImage) -> {
               if (nativeImage != null) {
                  CactusConstants.mc.execute(() -> {
                     CactusConstants.mc.method_1531().method_4616(SKIN_IDENTIFIER, new class_1043(() -> {
                        return "cactus:dynamic/nameTags/skin/" + url;
                     }, nativeImage));
                  });
                  CactusClient.getLogger().info("Client skin change success. (model={}, identifier={})", this.determinedSkinModel, SKIN_IDENTIFIER);
               }

            });
         }

      }
   }

   public class_7920 getSkinModel() {
      class_7920 var10000;
      switch(((StreamerMode.SkinModel)this.skinModel.get()).ordinal()) {
      case 0:
         var10000 = class_7920.field_41123;
         break;
      case 1:
         var10000 = class_7920.field_41122;
         break;
      case 2:
         var10000 = this.determinedSkinModel;
         break;
      default:
         throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public void hiderWidget(class_342 widget) {
      if (this.active() && (Boolean)this.hideServerAddresses.get()) {
         ((ITextFieldWidget)widget).cactus$setUnfocusedPlaceholder(WIDGET_TEXT_HIDDEN);
      }

   }

   static {
      WIDGET_TEXT_HIDDEN = class_2561.method_43471("modules.streamerMode.text.widgetTextHidden").method_10852(class_5244.field_39678).method_27692(class_124.field_1080);
   }

   public static enum SkinModel {
      Default,
      Slim,
      AutoDetermine;

      // $FF: synthetic method
      private static StreamerMode.SkinModel[] $values() {
         return new StreamerMode.SkinModel[]{Default, Slim, AutoDetermine};
      }
   }
}
