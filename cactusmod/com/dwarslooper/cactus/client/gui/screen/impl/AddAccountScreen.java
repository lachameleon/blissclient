package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.gui.screen.window.WindowScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.render.RenderHelper;
import com.dwarslooper.cactus.client.systems.ias.AccountManager;
import com.dwarslooper.cactus.client.systems.ias.MicrosoftAuthCallback;
import com.dwarslooper.cactus.client.systems.ias.account.Account;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.class_156;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_5244;
import org.jetbrains.annotations.NotNull;

public class AddAccountScreen extends WindowScreen {
   private final Consumer<Account> handler;
   private final MicrosoftAuthCallback callback = new MicrosoftAuthCallback();
   private String state = "";

   public AddAccountScreen(class_437 parent, Consumer<Account> handler) {
      super("addAccount", 200, 80);
      this.handler = handler;
      this.parent = parent;
   }

   public void method_25426() {
      super.method_25426();
      this.loginMicrosoft();
      this.method_37063(new CButtonWidget(this.x() + 4, this.y() + this.boxHeight() - 24, this.boxWidth() - 8, 20, class_5244.field_24335, (button) -> {
         this.method_25419();
      }));
   }

   public boolean method_25422() {
      return this.state.isEmpty();
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      if (this.state != null) {
         context.method_25300(this.field_22793, this.state, this.x() + this.boxWidth() / 2, this.y() + 4, -1);
         class_327 var10001 = this.field_22793;
         String var10002 = RenderHelper.getLoading();
         int var10003 = this.x() + this.boxWidth() / 2;
         int var10004 = this.y();
         int var10005 = this.boxHeight();
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         context.method_25300(var10001, var10002, var10003, var10004 + (var10005 - 9) / 2, -11141291);
      }

   }

   public void method_25419() {
      this.callback.close();
      super.method_25419();
   }

   private void loginMicrosoft() {
      this.state = "";
      AccountManager.EXECUTOR.execute(() -> {
         this.state = this.getTranslatableElement("checkBrowser", new Object[0]);
         class_156.method_668().method_670("https://login.live.com/oauth20_authorize.srf?client_id=54fd49e4-2103-4044-9603-2b028c814ec3&response_type=code&scope=XboxLive.signin%20XboxLive.offline_access&redirect_uri=http://localhost:59125&prompt=select_account");
         this.callback.start((s) -> {
            this.state = s;
         }, this.getTranslatableElement("complete", new Object[0])).whenComplete((acc, t) -> {
            if (CactusConstants.mc.field_1755 == this) {
               if (t != null) {
                  this.state = this.getTranslatableElement("error", new Object[0]);
               } else if (acc == null) {
                  CactusConstants.mc.execute(() -> {
                     CactusConstants.mc.method_1507(this.parent);
                  });
               } else {
                  CactusConstants.mc.execute(() -> {
                     this.handler.accept(acc);
                     CactusConstants.mc.method_1507(this.parent);
                  });
               }
            }
         });
      });
   }
}
