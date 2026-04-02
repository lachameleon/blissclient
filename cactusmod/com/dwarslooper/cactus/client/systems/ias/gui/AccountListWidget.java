package com.dwarslooper.cactus.client.systems.ias.gui;

import com.dwarslooper.cactus.client.gui.screen.impl.AccountListScreen;
import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.systems.ias.AccountManager;
import com.dwarslooper.cactus.client.systems.ias.account.Account;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.ABValue;
import com.google.common.collect.ImmutableList;
import java.awt.Color;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_4265;
import net.minecraft.class_5250;
import net.minecraft.class_6379;
import net.minecraft.class_7532;
import net.minecraft.class_4265.class_4266;
import org.jetbrains.annotations.NotNull;

public class AccountListWidget extends class_4265<AccountListWidget.Entry> {
   private final AccountListScreen parent;

   public AccountListWidget(AccountListScreen parent, List<Account> accounts) {
      super(CactusConstants.mc, parent.field_22789, parent.field_22790 - 72, 32, 20);
      this.parent = parent;
      accounts.forEach(this::add);
      this.method_25321(new AccountListWidget.AddAccountEntry(this));
      this.method_25396().stream().filter((entry) -> {
         boolean var10000;
         if (entry instanceof AccountListWidget.AccountEntry) {
            AccountListWidget.AccountEntry ae = (AccountListWidget.AccountEntry)entry;
            if (ae.getAccount().uuid().equals(CactusConstants.mc.method_1548().method_44717())) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }).findFirst().ifPresentOrElse(this::select, () -> {
         this.select((AccountListWidget.Entry)this.method_25396().getFirst());
      });
   }

   private void add(Account account) {
      this.method_25321(new AccountListWidget.AccountEntry(this, account));
   }

   public void select(AccountListWidget.Entry entry) {
      this.method_25395(entry);
   }

   protected boolean method_73379() {
      return true;
   }

   public static class AddAccountEntry extends AccountListWidget.Entry {
      private final AccountListWidget owner;

      public AddAccountEntry(AccountListWidget owner) {
         this.owner = owner;
      }

      public void method_25343(class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
         int x = this.method_46426();
         int y = this.method_46427();
         int entryWidth = this.method_25368();
         int entryHeight = this.method_25364();
         class_327 var10001 = CactusConstants.mc.field_1772;
         class_5250 var10002 = class_2561.method_43470("+ ").method_10852(class_2561.method_43471("gui.screen.account_switcher.addAccount")).method_27695(new class_124[]{class_124.field_1080, class_124.field_1056});
         int var10003 = x + entryWidth / 2;
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         context.method_27534(var10001, var10002, var10003, y + (entryHeight - 9) / 2, Color.WHITE.getRGB());
      }

      public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
         this.add();
         return false;
      }

      @NotNull
      public List<? extends class_364> method_25396() {
         return ImmutableList.of();
      }

      @NotNull
      public List<? extends class_6379> method_37025() {
         return ImmutableList.of();
      }

      public boolean method_25404(class_11908 input) {
         if (input.comp_4795() != 257 && input.comp_4795() != 335) {
            return super.method_25404(input);
         } else {
            this.add();
            return true;
         }
      }

      private void add() {
         this.owner.parent.addAccount();
      }
   }

   public class AccountEntry extends AccountListWidget.Entry {
      public final Account account;
      public final AccountListWidget owner;
      private final class_5250 name;
      private final class_4185 loginButton;
      private final class_4185 removeButton;
      private static final class_2561 LOGIN_TEXT = class_2561.method_43471("gui.screen.account_switcher.login");
      private static final ABValue<List<class_2561>> ACCOUNT_DEFAULT_TOOLTIPS;

      public AccountEntry(AccountListWidget owner, Account account) {
         this.owner = owner;
         this.account = account;
         this.name = class_2561.method_43470(account.name());
         this.loginButton = class_4185.method_46430(LOGIN_TEXT, (button) -> {
            owner.parent.login(account);
         }).method_46437(CactusConstants.mc.field_1772.method_27525(LOGIN_TEXT) + 6, 18).method_46431();
         this.removeButton = (new CTextureButtonWidget.Builder((button) -> {
            owner.parent.removeAccount(account);
         })).uv(201, 1).dimensions(18, 18).build();
         this.removeButton.field_22763 = AccountManager.getAccountList().contains(account);
      }

      public void method_25343(class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
         int x = this.method_46426();
         int y = this.method_46427();
         int entryWidth = this.method_25368();
         int entryHeight = this.method_25364();
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         int fontHeight = 9;
         int mid = y + (entryHeight - fontHeight) / 2;
         class_124 color = CactusConstants.mc.method_1548().method_1676().equals(this.account.name()) ? class_124.field_1060 : class_124.field_1068;
         context.method_27535(CactusConstants.mc.field_1772, this.name.method_27661().method_27692(color), x + 16 + 5, mid + 1, -1);
         this.removeButton.method_48229(x + entryWidth - 19, y + 1);
         this.loginButton.method_48229(this.removeButton.method_46426() - this.loginButton.method_25368() - 2, y + 1);
         this.removeButton.method_25394(context, mouseX, mouseY, tickDelta);
         this.loginButton.method_25394(context, mouseX, mouseY, tickDelta);
         boolean isDefault = this.account == AccountManager.getDefaultAccount();
         if (isDefault || hovered) {
            int loginX = this.loginButton.method_46426();
            boolean iconHovered = mouseX >= loginX - 10 && mouseX < loginX - 2 && mouseY > y + 5 && mouseY < y + entryHeight - 5;
            context.method_25303(CactusConstants.mc.field_1772, "✔", loginX - 10, mid, iconHovered ? -171 : (isDefault ? -22016 : -5592406));
            if (iconHovered) {
               context.method_51434(CactusConstants.mc.field_1772, (List)ACCOUNT_DEFAULT_TOOLTIPS.fromBoolean(!isDefault), mouseX, mouseY);
            }
         }

         class_7532.method_52722(context, this.account.skin(), x + 2, y + 2, 16);
      }

      public boolean method_25402(class_11909 click, boolean doubled) {
         double y = click.comp_4799() - (double)this.owner.method_25337(this.owner.method_25396().indexOf(this));
         if (click.comp_4798() > (double)(this.loginButton.method_46426() - 10) && click.comp_4798() < (double)this.loginButton.method_46426() && y > 4.0D && y < 16.0D) {
            AccountListWidget.this.method_25354(CactusConstants.mc.method_1483());
            AccountManager.setDefaultAccount(AccountManager.getDefaultAccount() != this.account ? this.account : null);
            return true;
         } else {
            super.method_25402(click, doubled);
            return true;
         }
      }

      @NotNull
      public List<? extends class_364> method_25396() {
         return ImmutableList.of(this.loginButton, this.removeButton);
      }

      @NotNull
      public List<? extends class_6379> method_37025() {
         return ImmutableList.of(this.loginButton, this.removeButton);
      }

      public boolean method_25404(class_11908 input) {
         if (input.comp_4795() != 257 && input.comp_4795() != 335) {
            if (input.comp_4795() != 261 && input.comp_4795() != 259) {
               return super.method_25404(input);
            } else {
               this.owner.parent.removeAccount(this.account);
               return true;
            }
         } else {
            this.owner.parent.login(this.account);
            return true;
         }
      }

      public Account getAccount() {
         return this.account;
      }

      static {
         ACCOUNT_DEFAULT_TOOLTIPS = ABValue.of(List.of(class_2561.method_43471("gui.screen.account_switcher.setDefault")), List.of(class_2561.method_43471("gui.screen.account_switcher.default"), class_2561.method_43471("gui.screen.account_switcher.unsetDefault").method_27692(class_124.field_1073)));
      }
   }

   public abstract static class Entry extends class_4266<AccountListWidget.Entry> {
   }
}
