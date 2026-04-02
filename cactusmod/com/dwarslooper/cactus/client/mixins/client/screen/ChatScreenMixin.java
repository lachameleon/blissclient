package com.dwarslooper.cactus.client.mixins.client.screen;

import com.dwarslooper.cactus.client.feature.commands.ChatCommand;
import com.dwarslooper.cactus.client.feature.commands.IRCCommand;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.util.ChatTweaks;
import com.dwarslooper.cactus.client.gui.overlay.SelectionPopup;
import com.dwarslooper.cactus.client.gui.screen.window.TextInputWindow;
import com.dwarslooper.cactus.client.systems.snippet.SnippetManager;
import com.dwarslooper.cactus.client.systems.snippet.gui.SnippetListScreen;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import com.dwarslooper.cactus.client.util.mixinterface.IChatScreenSaveStateImpl;
import com.google.common.collect.ImmutableList;
import java.awt.Color;
import java.util.Objects;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_408;
import net.minecraft.class_437;
import net.minecraft.class_7077;
import net.minecraft.class_8016;
import net.minecraft.class_8023;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_408.class})
public abstract class ChatScreenMixin extends class_437 implements IChatScreenSaveStateImpl {
   @Shadow
   protected class_342 field_2382;
   @Unique
   private SelectionPopup popup;
   @Mutable
   @Unique
   private static String savedChatState = null;
   @Unique
   private boolean shouldSaveState;
   @Unique
   private final ChatTweaks chatTweaks = (ChatTweaks)ModuleManager.get().get(ChatTweaks.class);

   @Shadow
   protected abstract void method_25426();

   public ChatScreenMixin(class_2561 title) {
      super(title);
   }

   @Inject(
      method = {"method_25426"},
      at = {@At("TAIL")}
   )
   public void init(CallbackInfo ci) {
      this.popup = new SelectionPopup(0, 0, (option) -> {
         this.popup.setVisible(false);
      });
      this.popup.addOption(0, class_2561.method_43470("List snippets in raw format"), true, (option) -> {
         SnippetManager.get().getSnippets().forEach((snippet) -> {
            String var10000 = snippet.getName();
            ChatUtils.info(var10000 + " -> " + snippet.getContent());
         });
      }).addOption(0, class_2561.method_43470("Save command as snippet"), () -> {
         return !this.field_2382.method_1882().isEmpty();
      }, (option) -> {
         CactusConstants.mc.method_1507((new TextInputWindow("none", "Save snippet")).allowEmptyText(false).range(1, 32).setPlaceholder("Snippet name").onSubmit((s) -> {
            SnippetManager m = SnippetManager.get();
            m.add(s, this.field_2382.method_1882());
         }));
      }).addOption(0, class_2561.method_43470("Paste Snippet.."), true, (option) -> {
         CactusConstants.mc.method_1507(new SnippetListScreen(this));
      });
      this.popup.setVisible(false);
      int var10006 = this.field_22793.method_1727(this.getChatModeDisplay() + "80");
      Objects.requireNonNull(this.field_22793);
      this.method_25429(new class_7077(this, 4, 4, var10006, 9, class_2561.method_43470(this.getChatModeDisplay()), (button) -> {
         if (ChatCommand.MODE == ChatCommand.ChatMode.IRC) {
            ChatCommand.MODE = ChatCommand.ChatMode.DEFAULT;
            ChatUtils.infoPrefix("CNet", "Chat changed to §aDefault§7.");
         } else if (IRCCommand.checkIfConnectedElseError()) {
            ChatCommand.MODE = ChatCommand.ChatMode.IRC;
            ChatUtils.infoPrefix("CNet", "Chat changed to §aIRC§7.");
         }

         button.method_25355(class_2561.method_43470(this.getChatModeDisplay()));
         button.method_25358(this.field_22793.method_1727(this.getChatModeDisplay()));
      }, this.field_22793) {
         @Nullable
         public class_8016 method_48205(@NotNull class_8023 navigation) {
            return null;
         }
      });
      ChatTweaks chatTweaks = (ChatTweaks)ModuleManager.get().get(ChatTweaks.class);
      if ((Boolean)chatTweaks.showMessagedInChatScreen.get()) {
         this.method_37063(new ChatTweaks.DirectMessagesWidget(chatTweaks, this.field_22789 - 120 - 10, 10, 120, 100));
      }

      if (savedChatState != null) {
         this.field_2382.method_1852(savedChatState);
      }

   }

   @Inject(
      method = {"method_25404"},
      at = {@At("HEAD")}
   )
   public void keyPressed(class_11908 input, CallbackInfoReturnable<Boolean> cir) {
      this.method_25395(this.field_2382);
      ImmutableList<Integer> sendKeys = ImmutableList.of(257, 335);
      this.shouldSaveState = !sendKeys.contains(input.method_74228()) && ((Boolean)this.chatTweaks.saveChatWhenClosed.get() || input.method_74228() != 256);
   }

   @Inject(
      method = {"method_25402"},
      at = {@At("TAIL")}
   )
   public void onMouse(class_11909 click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
      if (click.method_74245() == 1) {
         this.popup.setPosition((int)click.comp_4798(), (int)click.comp_4799(), this.field_22789, this.field_22790);
         this.popup.setVisible(true);
      } else if (click.method_74245() == 0 && !this.popup.mouseClicked(0, click.comp_4798(), click.comp_4799())) {
         this.popup.setVisible(false);
      }

   }

   @Inject(
      method = {"method_25394"},
      at = {@At("TAIL")}
   )
   public void render(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      this.popup.render(context, (double)mouseX, (double)mouseY);
      context.method_25303(this.field_22793, "§fChat: " + (ChatCommand.MODE == ChatCommand.ChatMode.DEFAULT ? "§aGlobal" : "§aIRC"), 4, 4, Color.WHITE.getRGB());
   }

   @Inject(
      method = {"method_25432"},
      at = {@At("HEAD")}
   )
   public void saveState(CallbackInfo ci) {
      savedChatState = this.shouldSaveState ? this.field_2382.method_1882() : null;
   }

   @Unique
   private String getChatModeDisplay() {
      return "§fChat: " + (ChatCommand.MODE == ChatCommand.ChatMode.DEFAULT ? "§aGlobal" : "§aIRC");
   }

   @Unique
   public String cactus$getSaveStats() {
      return savedChatState;
   }

   @Unique
   public void cactus$setSaveState(String text) {
      savedChatState = text;
   }
}
