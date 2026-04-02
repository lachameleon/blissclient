package com.dwarslooper.cactus.client.feature.modules.util;

import com.dwarslooper.cactus.client.event.EventHandler;
import com.dwarslooper.cactus.client.event.impl.ClientTickEvent;
import com.dwarslooper.cactus.client.feature.module.Module;
import com.dwarslooper.cactus.client.feature.module.ModuleManager;
import com.dwarslooper.cactus.client.gui.screen.ITranslatable;
import com.dwarslooper.cactus.client.systems.config.settings.impl.BooleanSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.EnumSetSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.EnumSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.IntegerSetting;
import com.dwarslooper.cactus.client.systems.config.settings.impl.Setting;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.game.ChatUtils;
import java.util.Set;
import net.minecraft.class_1109;
import net.minecraft.class_124;
import net.minecraft.class_1753;
import net.minecraft.class_1786;
import net.minecraft.class_1799;
import net.minecraft.class_1820;
import net.minecraft.class_1835;
import net.minecraft.class_2561;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3489;
import net.minecraft.class_408;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_7065;
import net.minecraft.class_8133;
import net.minecraft.class_8667;

public class ToolBreakWarning extends Module {
   private final Setting<ToolBreakWarning.Mode> mode;
   private final Setting<Boolean> warnSound;
   private final Setting<Boolean> lastHitSound;
   private final Setting<Boolean> lastHit;
   private final Setting<Integer> warnPercent;
   private final Setting<Set<ToolBreakWarning.ToolType>> tools;
   private class_1799 warnedStack;

   public ToolBreakWarning() {
      super("tool_break_warning", ModuleManager.CATEGORY_UTILITY);
      this.mode = this.mainGroup.add(new EnumSetting("mode", ToolBreakWarning.Mode.Screen));
      this.warnSound = this.mainGroup.add(new BooleanSetting("warnSound", true));
      this.lastHitSound = this.mainGroup.add(new BooleanSetting("lastHitSound", true));
      this.lastHit = this.mainGroup.add(new BooleanSetting("lastHit", true));
      this.warnPercent = this.mainGroup.add((new IntegerSetting("warnPercent", 5)).min(1).max(99));
      this.tools = this.mainGroup.add(new EnumSetSetting("tools", ToolBreakWarning.ToolType.class, ToolBreakWarning.ToolType.values()));
      this.warnedStack = null;
   }

   @EventHandler
   public void onTick(ClientTickEvent event) {
      if (CactusConstants.mc.field_1724 != null && CactusConstants.mc.field_1687 != null) {
         class_1799 stack = CactusConstants.mc.field_1724.method_6047();
         ToolBreakWarning.Stage stage = this.determineStage(stack);
         if (stage == ToolBreakWarning.Stage.NONE) {
            if (this.warnedStack != null && stack == this.warnedStack) {
               this.warnedStack = null;
            }

         } else if (this.warnedStack != stack) {
            this.sendNotification(stack, stage);
            this.warnedStack = stack;
         }
      } else {
         this.warnedStack = null;
      }
   }

   private ToolBreakWarning.Stage determineStage(class_1799 stack) {
      if (!stack.method_7960() && stack.method_7963()) {
         if (((Set)this.tools.get()).stream().noneMatch((t) -> {
            return t.matches(stack);
         })) {
            return ToolBreakWarning.Stage.NONE;
         } else {
            int max = stack.method_7936();
            if (max <= 0) {
               return ToolBreakWarning.Stage.NONE;
            } else if ((Boolean)this.lastHit.get() && stack.method_63692()) {
               return ToolBreakWarning.Stage.LAST_HIT;
            } else {
               int remaining = max - stack.method_7919();
               double remainingPct = (double)remaining * 100.0D / (double)max;
               return remainingPct <= (double)(Integer)this.warnPercent.get() ? ToolBreakWarning.Stage.WARN : ToolBreakWarning.Stage.NONE;
            }
         }
      } else {
         return ToolBreakWarning.Stage.NONE;
      }
   }

   private void sendNotification(class_1799 stack, ToolBreakWarning.Stage stage) {
      int remaining = stack.method_7936() - stack.method_7919();
      int percent = (int)Math.max(0L, Math.round((double)remaining * 100.0D / (double)stack.method_7936()));
      class_2561 shortMessage = stage == ToolBreakWarning.Stage.LAST_HIT ? class_2561.method_43469("modules.tool_break_warning.last_hit", new Object[]{stack.method_7964()}) : class_2561.method_43469("modules.tool_break_warning.warn", new Object[]{stack.method_7964(), percent});
      class_2561 detailedMessage = stage == ToolBreakWarning.Stage.LAST_HIT ? class_2561.method_43469("modules.tool_break_warning.screen.message_last_hit", new Object[]{stack.method_7964()}) : class_2561.method_43469("modules.tool_break_warning.screen.message_warn", new Object[]{stack.method_7964(), percent});
      switch(((ToolBreakWarning.Mode)this.mode.get()).ordinal()) {
      case 0:
         ChatUtils.warning((class_2561)shortMessage.method_27661().method_27692(stage == ToolBreakWarning.Stage.LAST_HIT ? class_124.field_1061 : class_124.field_1054));
         this.playSound(stage);
         CactusConstants.mc.method_1507(new class_408("", false));
         break;
      case 1:
         this.playSound(stage);
         CactusConstants.mc.method_1507(new ToolBreakWarning.ToolBreakWarningScreen(detailedMessage, stack));
      }

   }

   private void playSound(ToolBreakWarning.Stage stage) {
      if (CactusConstants.mc.method_1483() != null) {
         class_3414 event = stage == ToolBreakWarning.Stage.LAST_HIT ? (class_3414)class_3417.field_15075.comp_349() : (class_3414)class_3417.field_14793.comp_349();
         float volume = stage == ToolBreakWarning.Stage.LAST_HIT ? 1.05F : 1.1F;
         if (stage == ToolBreakWarning.Stage.LAST_HIT && (Boolean)this.lastHitSound.get() || stage == ToolBreakWarning.Stage.WARN && (Boolean)this.warnSound.get()) {
            CactusConstants.mc.method_1483().method_4873(class_1109.method_4758(event, volume));
         }

      }
   }

   public static enum Mode {
      Chat,
      Screen;

      // $FF: synthetic method
      private static ToolBreakWarning.Mode[] $values() {
         return new ToolBreakWarning.Mode[]{Chat, Screen};
      }
   }

   public static enum ToolType {
      Sword((stack) -> {
         return stack.method_31573(class_3489.field_42611);
      }),
      Pickaxe((stack) -> {
         return stack.method_31573(class_3489.field_42614);
      }),
      Axe((stack) -> {
         return stack.method_31573(class_3489.field_42612);
      }),
      Shovel((stack) -> {
         return stack.method_31573(class_3489.field_42615);
      }),
      Hoe((stack) -> {
         return stack.method_31573(class_3489.field_42613);
      }),
      Bow((stack) -> {
         return stack.method_7909() instanceof class_1753;
      }),
      Flint_and_steel((stack) -> {
         return stack.method_7909() instanceof class_1786;
      }),
      Shears((stack) -> {
         return stack.method_7909() instanceof class_1820;
      }),
      Trident((stack) -> {
         return stack.method_7909() instanceof class_1835;
      });

      private final ToolBreakWarning.ToolType.ToolPredicate predicate;

      private ToolType(ToolBreakWarning.ToolType.ToolPredicate predicate) {
         this.predicate = predicate;
      }

      public boolean matches(class_1799 stack) {
         return this.predicate.test(stack);
      }

      // $FF: synthetic method
      private static ToolBreakWarning.ToolType[] $values() {
         return new ToolBreakWarning.ToolType[]{Sword, Pickaxe, Axe, Shovel, Hoe, Bow, Flint_and_steel, Shears, Trident};
      }

      @FunctionalInterface
      private interface ToolPredicate {
         boolean test(class_1799 var1);
      }
   }

   public static enum Stage {
      NONE,
      WARN,
      LAST_HIT;

      // $FF: synthetic method
      private static ToolBreakWarning.Stage[] $values() {
         return new ToolBreakWarning.Stage[]{NONE, WARN, LAST_HIT};
      }
   }

   public static class ToolBreakWarningScreen extends class_7065 implements ITranslatable {
      private final class_437 parent;
      private final ToolBreakWarning.Stage stage;

      public ToolBreakWarningScreen(class_2561 message, class_1799 stack) {
         super(class_2561.method_43471("modules.tool_break_warning.screen.title").method_27694((style) -> {
            return style.method_10977(class_124.field_1061).method_10982(true);
         }), message, (class_2561)null, class_2561.method_43473());
         this.parent = CactusConstants.mc.field_1755;
         this.stage = stack.method_63692() ? ToolBreakWarning.Stage.LAST_HIT : ToolBreakWarning.Stage.WARN;
      }

      public void method_25419() {
         CactusConstants.mc.method_1507(this.parent);
      }

      protected class_8133 method_57750() {
         class_8667 layout = class_8667.method_52741().method_52735(8);
         layout.method_52740().method_46467();
         class_2561 buttonText = this.stage == ToolBreakWarning.Stage.LAST_HIT ? class_2561.method_43471("modules.tool_break_warning.screen.ack").method_27692(class_124.field_1067) : class_2561.method_43471("modules.tool_break_warning.screen.ack");
         layout.method_52736(class_4185.method_46430(buttonText, (button) -> {
            this.method_25419();
         }).method_46432(200).method_46431());
         return layout;
      }

      public String getKey() {
         return "tool_break_warning.screen";
      }
   }
}
