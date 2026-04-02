package com.dwarslooper.cactus.client.mixins.client;

import com.dwarslooper.cactus.client.feature.command.CommandManager;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.feature.modules.util.ChatTweaks;
import com.dwarslooper.cactus.client.systems.emoji.EmojiCode;
import com.dwarslooper.cactus.client.systems.emoji.EmojiManager;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.google.common.base.Strings;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.class_2172;
import net.minecraft.class_310;
import net.minecraft.class_342;
import net.minecraft.class_4717;
import net.minecraft.class_4717.class_464;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({class_4717.class})
public abstract class CommandSuggestorMixin {
   @Unique
   private static final Pattern COLON_PATTERN = Pattern.compile("(:)");
   @Unique
   private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");
   @Unique
   private static final Pattern TAG_PATTERN = Pattern.compile("(@)");
   @Shadow
   private ParseResults<class_2172> field_21610;
   @Shadow
   @Final
   class_342 field_21599;
   @Shadow
   @Final
   class_310 field_21597;
   @Shadow
   boolean field_21614;
   @Shadow
   private CompletableFuture<Suggestions> field_21611;
   @Shadow
   private class_464 field_21612;
   @Shadow
   @Final
   private boolean field_21601;

   @Shadow
   public abstract void method_23920(boolean var1);

   @Shadow
   protected abstract void method_23937();

   @Inject(
      method = {"method_23934"},
      at = {@At(
   value = "INVOKE",
   target = "Lcom/mojang/brigadier/StringReader;canRead()Z",
   remap = false
)},
      cancellable = true,
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   public void onRefresh(CallbackInfo ci, String string, StringReader reader) {
      if (reader.canRead(1) && reader.getString().startsWith(CommandManager.getIRCPrefix()) && !CommandManager.getIRCPrefix().isEmpty()) {
         ci.cancel();
      } else if (reader.canRead(1) && reader.getString().startsWith(String.valueOf(CommandManager.getCommandPrefix()), reader.getCursor())) {
         reader.setCursor(reader.getCursor() + 1);

         assert this.field_21597.field_1724 != null;

         CommandDispatcher<class_2172> commandDispatcher = CommandManager.get().getDispatcher();
         if (this.field_21610 == null) {
            this.field_21610 = commandDispatcher.parse(reader, CommandManager.get().getCommandSource());
         }

         int cursor = this.field_21599.method_1881();
         if (cursor >= 1 && (this.field_21612 == null || !this.field_21614)) {
            this.field_21611 = commandDispatcher.getCompletionSuggestions(this.field_21610, cursor);
            this.field_21611.thenRun(() -> {
               if (this.field_21611.isDone()) {
                  this.method_23937();
               }

            });
         }

         ci.cancel();
      } else if (reader.canRead() && reader.peek() != '/' && !this.field_21601) {
         ChatTweaks chatTweaks = (ChatTweaks)ModuleManager.get().get(ChatTweaks.class);
         if (chatTweaks.active()) {
            String cursorText = this.field_21599.method_1882().substring(0, this.field_21599.method_1881());
            int whitespace = this.getLastPattern(cursorText, WHITESPACE_PATTERN);
            int start;
            if ((Boolean)chatTweaks.enableEmojis.get()) {
               start = this.getLastPattern(cursorText, COLON_PATTERN) - 1;
               if (start >= 0 && start < cursorText.length() && start >= whitespace) {
                  this.field_21611 = class_2172.method_9265(EmojiManager.getEmojis().stream().map(EmojiCode::getFormatted).toList(), new SuggestionsBuilder(cursorText, start));
                  if (this.field_21611.isDone()) {
                     this.method_23920(false);
                  }
               }
            }

            if ((Boolean)chatTweaks.enableTagging.get()) {
               start = this.getLastPattern(cursorText, TAG_PATTERN) - 1;
               if (CactusConstants.mc.method_1562() != null && start >= 0 && start < cursorText.length() && start >= whitespace) {
                  this.field_21611 = class_2172.method_9265(CactusConstants.mc.method_1562().method_2880().stream().map((ple) -> {
                     return ple.method_2966().name();
                  }).toList(), new SuggestionsBuilder(cursorText, start + 1));
                  if (this.field_21611.isDone()) {
                     this.method_23920(false);
                  }
               }
            }
         }
      }

   }

   @Unique
   private int getLastPattern(String input, Pattern pattern) {
      if (Strings.isNullOrEmpty(input)) {
         return 0;
      } else {
         int i = 0;

         for(Matcher matcher = pattern.matcher(input); matcher.find(); i = matcher.end()) {
         }

         return i;
      }
   }
}
