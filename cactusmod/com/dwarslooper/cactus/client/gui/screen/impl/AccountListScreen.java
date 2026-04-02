package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.CactusClient;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.screen.window.TextInputWindow;
import com.dwarslooper.cactus.client.gui.toast.ToastSystem;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.render.RenderHelper;
import com.dwarslooper.cactus.client.systems.ias.AccountManager;
import com.dwarslooper.cactus.client.systems.ias.SessionUtils;
import com.dwarslooper.cactus.client.systems.ias.account.Account;
import com.dwarslooper.cactus.client.systems.ias.gui.AccountListWidget;
import com.dwarslooper.cactus.client.systems.ias.skins.SkinChangerScreen;
import com.dwarslooper.cactus.client.systems.ias.skins.SkinHelper;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_410;
import net.minecraft.class_437;
import net.minecraft.class_8765;
import net.minecraft.class_350.class_351;
import org.jetbrains.annotations.NotNull;

public class AccountListScreen extends CScreen {
   private String state = "";
   private AccountListWidget accountListWidget;
   private class_8765 playerSkinWidget;

   public AccountListScreen(class_437 parent) {
      super("account_switcher");
      this.parent = parent;
   }

   public void method_25426() {
      List<Account> accounts = new ArrayList(AccountManager.getAccountList());
      Account originalSession = AccountManager.getOriginalSession();
      if (originalSession != null && !accounts.contains(originalSession)) {
         accounts.addFirst(originalSession);
      }

      this.method_25429(this.accountListWidget = new AccountListWidget(this, accounts));
      super.method_25426();
      this.method_37063(new CButtonWidget(30, 6, 100, 20, class_2561.method_43470(this.getTranslatableElement("changeSkin", new Object[0])), (button) -> {
         CactusConstants.mc.method_1507(new SkinChangerScreen());
      }));
      this.method_37063(new CButtonWidget(this.field_22789 - 100 - 4, this.field_22790 - 24, 100, 20, class_2561.method_43470(this.getTranslatableElement("reloadSkins", new Object[0])), (button) -> {
         SkinHelper.getSkinCache().clear();
      }));
      this.method_37063(new CButtonWidget(this.field_22789 - 208, this.field_22790 - 24, 100, 20, class_2561.method_43470(this.getTranslatableElement("tokenLogin", new Object[0])), (button) -> {
         CactusConstants.mc.method_1507((new TextInputWindow("none", this.getTranslatableElement("tokenLogin.title", new Object[0]))).range(0, Integer.MAX_VALUE).allowEmptyText(false).setPlaceholder(this.getTranslatableElement("tokenLogin.text", new Object[0])).onSubmit((s) -> {
            try {
               SessionUtils.setSession(s);
               ToastSystem.displayMessage("§aLogged in", CactusConstants.mc.method_1548().method_1676());
            } catch (Exception var2) {
               CactusClient.getLogger().error("Failed to login with token", var2);
               ToastSystem.displayMessage("§cLogin failed", "Check if the token is correct");
            }

         }));
      }));
      String offlineLogin = this.getTranslatableElement("offlineSession", new Object[0]);
      this.method_37063(new CButtonWidget(this.field_22789 - 312, this.field_22790 - 24, 100, 20, class_2561.method_43470(offlineLogin), (button) -> {
         CactusConstants.mc.method_1507((new TextInputWindow("none", offlineLogin)).range(3, 16).allowEmptyText(false).setPlaceholder(this.getTranslatableElement("offlineSession.text", new Object[0])).onSubmit((s) -> {
            SessionUtils.setOfflineSession(s);
            ToastSystem.displayMessage("§aLogged in", CactusConstants.mc.method_1548().method_1676());
         }));
      }));
      this.method_25429(this.playerSkinWidget = new class_8765(85, 120, CactusConstants.mc.method_31974(), () -> {
         Account selected = this.getSelectedAccount();
         return selected != null ? selected.skin() : SkinHelper.getCachedSkinOrFetch(CactusConstants.mc.method_1548().method_44717());
      }));
      this.playerSkinWidget.method_48229(4, (this.field_22790 - 120) / 2);
      if (!AccountManager.getAccountList().isEmpty()) {
         this.method_48265(this.accountListWidget);
      }

   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      if (this.accountListWidget != null) {
         this.accountListWidget.method_25394(context, mouseX, mouseY, delta);
      }

      if (this.getSelectedAccount() != null) {
         this.playerSkinWidget.method_25394(context, mouseX, mouseY, delta);
      }

      context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 8, -1);
      if (!this.state.isEmpty()) {
         class_327 var10001 = this.field_22793;
         String var10002 = this.state;
         int var10004 = this.field_22790 - 4;
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         context.method_25303(var10001, var10002, 4, var10004 - 9, -1);
         var10001 = this.field_22793;
         var10002 = RenderHelper.getLoading();
         var10004 = this.field_22790 - 14;
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         context.method_51433(var10001, var10002, 4, var10004 - 9, -11141291, false);
      }

   }

   private Account getSelectedAccount() {
      if (this.accountListWidget != null) {
         class_351 var2 = this.accountListWidget.method_25334();
         if (var2 instanceof AccountListWidget.AccountEntry) {
            AccountListWidget.AccountEntry entry = (AccountListWidget.AccountEntry)var2;
            return entry.account;
         }
      }

      return null;
   }

   public void login(Account account) {
      this.state = "connect";
      account.login((s) -> {
         this.state = s;
      }).whenComplete((authData, throwable) -> {
         CactusConstants.mc.execute(() -> {
            try {
               if (throwable != null) {
                  throw throwable;
               }

               SessionUtils.setSession(authData.name(), authData.uuid(), authData.accessToken());
               this.state = "";
            } catch (Throwable var4) {
               this.state = "§cFailed to login";
               CactusClient.getLogger().error("Failed to login", var4);
            }

         });
      });
   }

   public void addAccount() {
      CactusConstants.mc.method_1507(new AddAccountScreen(this, (account) -> {
         if (CactusConstants.mc.field_1755 instanceof AddAccountScreen) {
            AccountManager.getAccountList().add(account);
            this.login(account);
         }

      }));
   }

   public void removeAccount(Account account) {
      if (account != null) {
         CactusConstants.mc.method_1507(new class_410((b) -> {
            if (b) {
               AccountManager.getAccountList().remove(account);
               this.method_41843();
            }

            CactusConstants.mc.method_1507(this);
         }, class_2561.method_43470(this.getTranslatableElement("remove.header", new Object[0])), class_2561.method_43470(this.getTranslatableElement("remove.warning", new Object[]{account.name()}))));
      }

   }
}
