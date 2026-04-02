package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.irc.IRCClient;
import com.dwarslooper.cactus.client.irc.protocol.packets.FeedbackBiDPacket;
import com.dwarslooper.cactus.client.render.RenderHelper;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.exception.FunnyException;
import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.class_1109;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_3417;
import net.minecraft.class_437;
import net.minecraft.class_7529;
import org.jetbrains.annotations.NotNull;

public class FeedbackScreen extends CScreen {
   private class_7529 feedbackTextField;
   private CButtonWidget submitButton;
   private String display = "";
   private boolean processing = false;

   public FeedbackScreen(class_437 parent) {
      super("feedback");
      this.parent = parent;
   }

   public void method_25426() {
      super.method_25426();
      this.feedbackTextField = class_7529.method_71507().method_71508(this.field_22789 / 2 - this.field_22789 / 4).method_71512(this.field_22790 / 4).method_71510(class_2561.method_43470(this.getTranslatableElement("widget.feedback", new Object[0]))).method_71509(this.field_22793, this.field_22789 / 2, this.field_22790 / 2, class_2561.method_43470("feedback"));
      this.feedbackTextField.method_44402(800);
      this.method_37063(this.feedbackTextField);
      this.method_37063(this.submitButton = new CButtonWidget(this.field_22789 / 2 - 100, this.field_22790 - 40, 200, 20, class_2561.method_43470(this.getTranslatableElement("button.submit", new Object[0])), (button) -> {
         this.submitFeedback();
      }));
      this.finishDrawables();
   }

   public boolean method_25422() {
      return !this.processing;
   }

   private void submitFeedback() {
      String userFeedback = this.feedbackTextField.method_44405();
      if (userFeedback.isEmpty()) {
         this.display = this.getTranslatableElement("state.empty", new Object[0]);
      } else if (userFeedback.equals("jokesterz")) {
         CactusConstants.APRILFOOLS = !CactusConstants.APRILFOOLS;
         CactusClient.getLogger().warn("Funny mode", new FunnyException("April fools activated"));
         CactusConstants.mc.method_1483().method_4873(class_1109.method_4758(class_3417.field_15195, 1.0F));
         CactusConstants.mc.method_1507((class_437)null);
      } else {
         this.handleFeedback(userFeedback);
      }
   }

   private void handleFeedback(String feedback) {
      this.processing = true;
      this.submitButton.field_22763 = false;
      this.display = "";
      (new Timer()).schedule(new TimerTask() {
         public void run() {
            if (!IRCClient.connected()) {
               FeedbackScreen.this.processing = false;
               FeedbackScreen.this.submitButton.field_22763 = true;
               FeedbackScreen.this.display = "§c" + (String)IRCClient.NOT_CONNECTED.get();
            } else {
               CactusClient.getInstance().getIrcClient().sendPacket(new FeedbackBiDPacket(FeedbackScreen.this.feedbackTextField.method_44405()));
               FeedbackScreen.this.display = FeedbackScreen.this.getTranslatableElement("state.processing", new Object[0]);
               (new Timer()).schedule(new TimerTask() {
                  // $FF: synthetic field
                  final <undefinedtype> this$1;

                  {
                     this.this$1 = this$1;
                  }

                  public void run() {
                     if (this.this$1.this$0.processing) {
                        this.this$1.this$0.handleFeedbackResponse(4);
                     }

                  }
               }, 8000L);
            }

         }
      }, 1000L);
   }

   public void handleFeedbackResponse(int status) {
      this.processing = false;
      this.submitButton.field_22763 = status > 2;
      if (status == 0) {
         this.display = this.getTranslatableElement("state.thankYou", new Object[0]);
      } else if (status == 1) {
         this.display = "§c" + (String)IRCClient.RATELIMIT.get();
      } else if (status == 2) {
         this.display = "§cYou have been blocked for spamming!";
      } else if (status == 3) {
         this.display = "§cBad request.";
      } else if (status == 4) {
         this.display = "§cThe IRC server did not respond.";
      }

   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 8, -1);
      context.method_27534(CactusConstants.mc.field_1772, class_2561.method_30163(this.display), this.field_22789 / 2, this.field_22790 - 60, Color.WHITE.getRGB());
      if (this.processing) {
         context.method_25300(CactusConstants.mc.field_1772, RenderHelper.getLoading(), this.field_22789 / 2, this.field_22790 - 80, -11141291);
      }

   }
}
