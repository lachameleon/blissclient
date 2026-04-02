package com.dwarslooper.cactus.client.gui.screen.impl;

import com.dwarslooper.cactus.client.feature.macro.Action;
import com.dwarslooper.cactus.client.feature.macro.Macro;
import com.dwarslooper.cactus.client.feature.macro.MacroManager;
import com.dwarslooper.cactus.client.gui.screen.CScreen;
import com.dwarslooper.cactus.client.gui.widget.CButtonWidget;
import com.dwarslooper.cactus.client.gui.widget.KeyBindWidget;
import com.dwarslooper.cactus.client.gui.widget.list.ExpandableStringListWidget;
import com.dwarslooper.cactus.client.systems.key.KeyBind;
import com.dwarslooper.cactus.client.util.CactusConstants;
import java.util.Iterator;
import net.minecraft.class_11908;
import net.minecraft.class_2477;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_364;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_5244;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MacroEditScreen extends CScreen {
   private final Macro macro;
   private ExpandableStringListWidget commandList;
   private class_342 nameField;
   private class_4185 doneButton;
   private KeyBindWidget keyBindWidget;
   private KeyBind bind;

   public MacroEditScreen(class_437 parent) {
      super("createMacro");
      this.parent = parent;
      this.macro = Macro.empty();
   }

   public MacroEditScreen(class_437 parent, Macro macro) {
      super("editMacro");
      this.parent = parent;
      this.macro = macro;
   }

   public void method_25426() {
      super.method_25426();
      if (this.nameField == null) {
         this.nameField = new class_342(this.field_22793, 0, 0, 158, 20, class_2561.method_30163(""));
         this.nameField.method_1852(this.macro.name.trim());
      }

      this.nameField.method_48229(this.field_22789 / 2 - 160, 56);
      this.method_37063(this.nameField);
      if (this.bind == null) {
         this.bind = this.macro.getKeyBinding();
      }

      this.keyBindWidget = (KeyBindWidget)this.method_37063(new KeyBindWidget(this.field_22789 / 2 + 2, 56, 158, 20, this.bind, (keyBind) -> {
         this.bind = keyBind;
      }));
      this.nameField.method_1863((s) -> {
         this.doneButton.field_22763 = this.canFinish() && (MacroManager.get().canAdd(s) || s.equalsIgnoreCase(this.macro.getName()));
         this.nameField.method_1887(s.isEmpty() ? "Name.." : "");
      });
      int widgetWidth = 320;
      if (this.commandList == null) {
         this.commandList = new ExpandableStringListWidget(widgetWidth, this.field_22790 / 3, 80);
         this.commandList.setPlaceholderText(class_2477.method_10517().method_48307("gui.screen.editMacro.addCommand"));
         Iterator var2 = this.macro.actions.iterator();

         while(var2.hasNext()) {
            Action<?> action = (Action)var2.next();
            if (action.type() == Action.Type.RUN_COMMAND) {
               this.commandList.add(action.data().toString());
            }
         }
      }

      this.commandList.method_53533(this.field_22790 / 3);
      this.commandList.method_46421((this.field_22789 - widgetWidth) / 2);
      this.commandList.method_65506();
      this.method_37063(this.commandList);
      int bottomX = this.commandList.method_55443() + 24;
      this.method_37063(new CButtonWidget(this.field_22789 / 2 - 100, bottomX, 98, 20, class_5244.field_24335, (button) -> {
         this.method_25419();
      }));
      this.doneButton = (class_4185)this.method_37063(new CButtonWidget(this.field_22789 / 2 + 2, bottomX, 98, 20, class_5244.field_24334, (button) -> {
         String name = this.nameField.method_1882().trim();
         if (!name.isEmpty()) {
            this.updateActions();
            this.macro.setName(this.nameField.method_1882());
            this.macro.setKeyBinding(this.bind);
            if (MacroManager.get().addIfAbsent(this.macro)) {
               this.method_48265(this.nameField);
            }

            CactusConstants.mc.method_1507(this.parent);
         }
      }, this.canFinish()));
      this.nameField.method_1852(this.nameField.method_1882());
   }

   private boolean canFinish() {
      return !this.nameField.method_1882().trim().isEmpty();
   }

   public void method_25394(@NotNull class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      context.method_27534(this.field_22793, this.field_22785, this.field_22789 / 2, 8, -1);
   }

   private void updateActions() {
      this.macro.actions.clear();
      Iterator var1 = this.commandList.getList().iterator();

      while(var1.hasNext()) {
         String command = (String)var1.next();
         this.macro.actions.add(new Action(command, Action.Type.RUN_COMMAND));
      }

   }

   public void method_25395(@Nullable class_364 focused) {
      if (focused != this.commandList) {
         this.commandList.method_25395((class_364)null);
      }

      super.method_25395(focused);
   }

   public boolean method_25404(@NotNull class_11908 input) {
      return this.keyBindWidget.method_25370() ? this.keyBindWidget.onKey(input.comp_4795()) : super.method_25404(input);
   }
}
