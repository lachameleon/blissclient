package com.dwarslooper.cactus.client.mixins.client.screen;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.feature.content.ContentPackManager;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.render.ServerWidget;
import com.dwarslooper.cactus.client.feature.modules.util.QuitConfirm;
import com.dwarslooper.cactus.client.gui.screen.impl.ButtonOptions;
import com.dwarslooper.cactus.client.gui.screen.impl.CactusMainScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.CServerEntryWidget;
import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.mixins.accessor.ButtonAccessor;
import com.dwarslooper.cactus.client.systems.worldshare.OasisHostManager;
import com.dwarslooper.cactus.client.systems.worldshare.gui.HostWorldScreen;
import com.dwarslooper.cactus.client.systems.worldshare.gui.WhitelistScreen;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.ScreenUtils;
import java.util.Objects;
import net.minecraft.class_156;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_339;
import net.minecraft.class_410;
import net.minecraft.class_4185;
import net.minecraft.class_433;
import net.minecraft.class_437;
import net.minecraft.class_500;
import net.minecraft.class_5244;
import net.minecraft.class_4185.class_4241;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_433.class})
public abstract class PauseScreenMixin extends class_437 {
   @Shadow
   @Nullable
   private class_4185 field_40792;
   @Unique
   private long displayedAt;
   @Unique
   private class_2561 originalMessage;
   @Unique
   private boolean pressedQuitOnce = false;
   @Unique
   private CServerEntryWidget serverWidget;
   @Unique
   private final boolean serverWidgetEnabled = ((ServerWidget)ModuleManager.get().get(ServerWidget.class)).shouldAddTo(this);

   protected PauseScreenMixin(class_2561 title) {
      super(title);
   }

   @Inject(
      method = {"method_25426"},
      at = {@At("TAIL")}
   )
   private void onInit(CallbackInfo ci) {
      class_339 feedbackButton = ScreenUtils.getButton(this, "menu.sendFeedback");
      if (feedbackButton == null) {
         feedbackButton = ScreenUtils.getButton(this, "menu.feedback");
      }

      boolean feedbackToCactus = (Boolean)ButtonOptions.get().feedbackToCactus.get();
      if (!feedbackToCactus || feedbackButton == null) {
         this.method_37063(new CTextureButtonWidget(6, 6, 0, (button) -> {
            ((class_310)Objects.requireNonNull(this.field_22787)).method_1507(new CactusMainScreen(CactusConstants.mc.field_1755));
         }));
      }

      if (feedbackButton != null && feedbackToCactus) {
         ((ButtonAccessor)feedbackButton).setOnPress((btn) -> {
            ((class_310)Objects.requireNonNull(this.field_22787)).method_1507(new CactusMainScreen(this));
         });
         feedbackButton.method_25355(class_2561.method_43471("gui.screen.cactus.title").method_10852(class_5244.field_39678));
      }

      class_339 openToLanButton = ScreenUtils.getButton(this, "menu.shareToLan");
      if (openToLanButton == null && CactusConstants.mc.method_1576() != null && !CactusConstants.mc.method_1576().method_3860()) {
         openToLanButton = (class_339)this.method_37063(new CButtonWidget(4, 28, 100, 20, class_2561.method_43473(), (b) -> {
         }));
      }

      if (openToLanButton != null) {
         openToLanButton.method_25355(class_2561.method_43471("gui.screen.gameMenu.button.shareWorld"));
         ((ButtonAccessor)openToLanButton).setOnPress((button) -> {
            CactusConstants.mc.method_1507(new HostWorldScreen());
         });
      }

      if (CactusConstants.mc.method_1576() != null && CactusConstants.mc.method_1576().method_3806() && CactusConstants.mc.method_1576().method_3860() && ContentPackManager.get().isEnabled(ContentPackManager.get().ofId("world_hosting")) && CactusConstants.mc.method_1576().method_43824() != null && CactusConstants.mc.method_1576().method_43824().id().equals(CactusConstants.mc.method_1548().method_44717())) {
         class_339 reportPlayersButton = ScreenUtils.getButton(this, "menu.playerReporting");
         if (reportPlayersButton != null) {
            reportPlayersButton.method_25355(class_2561.method_43470("Whitelist"));
            ((ButtonAccessor)reportPlayersButton).setOnPress((button) -> {
               CactusConstants.mc.method_1507(new WhitelistScreen(((OasisHostManager)CactusClient.getConfig(OasisHostManager.class)).getWhitelist()));
            });
         }
      }

      QuitConfirm qc = (QuitConfirm)ModuleManager.get().get(QuitConfirm.class);
      this.pressedQuitOnce = false;
      this.displayedAt = class_156.method_658();
      if (this.field_40792 != null && qc.active()) {
         class_4241 action;
         switch((QuitConfirm.Mode)qc.mode.get()) {
         case Cooldown:
            this.originalMessage = this.field_40792.method_25369();
            this.field_40792.field_22763 = false;
            this.field_40792.method_25355(class_5244.field_39678);
            break;
         case ClickAgain:
            action = ((ButtonAccessor)this.field_40792).getOnPress();
            ((ButtonAccessor)this.field_40792).setOnPress((button) -> {
               if (!this.pressedQuitOnce) {
                  button.method_25355(class_2561.method_43471("modules.quitConfirm.promptClickAgain"));
                  this.pressedQuitOnce = true;
               } else {
                  action.onPress(button);
               }

            });
            break;
         case Confirm:
            action = ((ButtonAccessor)this.field_40792).getOnPress();
            ((ButtonAccessor)this.field_40792).setOnPress((button) -> {
               CactusConstants.mc.method_1507(new class_410((b) -> {
                  if (b) {
                     action.onPress(button);
                  } else {
                     CactusConstants.mc.method_1507(this);
                  }

               }, class_2561.method_43471("modules.quitConfirm.promptClickAgain"), class_2561.method_43471("modules.quitConfirm.promptConfirm")));
            });
         }
      }

      this.cactus$maybeInitServerWidget();
   }

   public void method_25393() {
      QuitConfirm qc = (QuitConfirm)ModuleManager.get().get(QuitConfirm.class);
      if (this.field_40792 != null && qc.active() && qc.mode.get() == QuitConfirm.Mode.Cooldown) {
         int remaining = Math.max(0, 5 - (int)((class_156.method_658() - this.displayedAt) / 1000L));
         if (remaining == 0) {
            this.field_40792.method_25355(this.originalMessage);
            this.field_40792.field_22763 = true;
         } else {
            this.field_40792.method_25355(class_2561.method_43469("modules.quitConfirm.cooldownText", new Object[]{remaining}));
         }
      }

      this.cactus$positionServerWidget(this.field_22789, this.field_22790);
   }

   @Unique
   private void cactus$maybeInitServerWidget() {
      if (this.serverWidgetEnabled) {
         if (CactusConstants.mc.method_1558() == null) {
            if (this.serverWidget != null) {
               this.serverWidget.field_22764 = false;
            }

         } else {
            if (this.serverWidget == null) {
               this.serverWidget = new CServerEntryWidget(0, 0, 300, 32, (class_500)null);
               this.method_37063(this.serverWidget);
            }

            this.serverWidget.field_22764 = true;
            this.serverWidget.update(CactusConstants.mc.method_1558().field_3752, CactusConstants.mc.method_1558().field_3761);
            this.cactus$positionServerWidget(this.field_22789, this.field_22790);
         }
      }
   }

   @Unique
   private void cactus$positionServerWidget(int width, int height) {
      if (this.serverWidget != null) {
         this.serverWidget.method_46421(width / 2 - this.serverWidget.method_25368() / 2);
         int centerY = height / 2;
         int offset = 72;
         int y = centerY + offset;
         y = Math.min(height - this.serverWidget.method_25364() - 8, Math.max(32, y));
         this.serverWidget.method_46419(y);
      }
   }
}
