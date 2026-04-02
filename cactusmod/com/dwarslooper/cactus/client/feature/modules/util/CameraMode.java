package com.dwarslooper.cactus.client.feature.modules.util;

import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.gui.toast.internal.GenericToast;
import com.dwarslooper.cactus.client.util.CactusConstants;
import net.minecraft.class_11909;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;
import org.jetbrains.annotations.NotNull;

public class CameraMode extends Module {
   public CameraMode() {
      super("camera_mode", ModuleManager.CATEGORY_UTILITY);
   }

   public void onEnable() {
      if (CactusConstants.mc.field_1687 != null) {
         CactusConstants.mc.method_1507(new CameraMode.CameraScreen(class_2561.method_30163("CamMode")));
      } else {
         GenericToast toast = new GenericToast(class_2561.method_43470(this.getDisplayName()).method_27692(class_124.field_1061), class_2561.method_43470(this.translate("notInGame", new Object[0])));
         CactusConstants.mc.method_1566().method_1999(toast);
      }

      this.toggle();
   }

   public boolean sendsToggleFeedback() {
      return false;
   }

   private static class CameraScreen extends class_437 {
      protected CameraScreen(class_2561 title) {
         super(title);
      }

      public boolean method_25421() {
         return false;
      }

      public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
         CactusConstants.mc.method_1507((class_437)null);
         return false;
      }

      public void method_25420(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      }
   }
}
