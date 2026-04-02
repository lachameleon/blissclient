package com.dwarslooper.cactus.client.mixins.render;

import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.util.ChatTweaks;
import com.dwarslooper.cactus.client.mixins.accessor.CommandSuggestionsAccessor;
import com.dwarslooper.cactus.client.systems.emoji.EmojiCode;
import com.dwarslooper.cactus.client.systems.emoji.EmojiManager;
import com.mojang.brigadier.suggestion.Suggestion;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_4717;
import net.minecraft.class_4717.class_464;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_464.class})
public abstract class SuggestionWindowMixin {
   @Shadow
   private int field_2766;
   @Shadow
   @Final
   private List<Suggestion> field_25709;
   @Shadow(
      aliases = {"field_21615"}
   )
   @Final
   class_4717 suggestionsRef;

   @Inject(
      method = {"method_2373"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_332;method_25303(Lnet/minecraft/class_327;Ljava/lang/String;III)V"
)}
   )
   public void render(class_332 context, int mouseX, int mouseY, CallbackInfo ci) {
      Suggestion suggestion = (Suggestion)this.field_25709.get(this.field_2766);
   }

   @Inject(
      method = {"method_2375"},
      at = {@At("TAIL")}
   )
   private void overwriteComplete(CallbackInfo ci) {
      ChatTweaks chatTweaks = (ChatTweaks)ModuleManager.get().get(ChatTweaks.class);
      if (chatTweaks.active() && (Boolean)chatTweaks.enableEmojis.get()) {
         CommandSuggestionsAccessor inputSuggestor = (CommandSuggestionsAccessor)this.suggestionsRef;
         Suggestion suggestion = (Suggestion)this.field_25709.get(this.field_2766);
         class_342 textFieldWidget = inputSuggestor.getInput();
         Iterator var6 = EmojiManager.getEmojis().iterator();

         while(var6.hasNext()) {
            EmojiCode ec = (EmojiCode)var6.next();
            if (ec.match(suggestion.getText())) {
               textFieldWidget.method_1878(-ec.getFormatted().length());
               textFieldWidget.method_1875(suggestion.getRange().getStart());
               textFieldWidget.method_1884(textFieldWidget.method_1881());
               textFieldWidget.method_1867(ec.emoji());
               break;
            }
         }
      }

   }

   @Inject(
      method = {"method_2373"},
      at = {@At(
   value = "INVOKE",
   target = "Lnet/minecraft/class_332;method_25303(Lnet/minecraft/class_327;Ljava/lang/String;III)V"
)}
   )
   public void renderHeads(class_332 context, int mouseX, int mouseY, CallbackInfo ci) {
   }
}
