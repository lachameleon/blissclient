package com.dwarslooper.cactus.client.gui.widget.list;

import com.dwarslooper.cactus.client.feature.macro.Macro;
import com.dwarslooper.cactus.client.feature.macro.MacroManager;
import com.dwarslooper.cactus.client.gui.screen.impl.MacroEditScreen;
import com.dwarslooper.cactus.client.gui.screen.impl.MacroListScreen;
import com.dwarslooper.cactus.client.gui.widget.CCheckboxWidget;
import com.dwarslooper.cactus.client.gui.widget.CTextureButtonWidget;
import com.dwarslooper.cactus.client.systems.key.KeyBind;
import com.dwarslooper.cactus.client.util.CactusConstants;
import com.dwarslooper.cactus.client.util.generic.TextUtils;
import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_364;
import net.minecraft.class_410;
import net.minecraft.class_4185;
import net.minecraft.class_4265;
import net.minecraft.class_437;
import net.minecraft.class_6379;
import net.minecraft.class_4265.class_4266;
import org.jetbrains.annotations.NotNull;

public class MacroListWidget extends class_4265<MacroListWidget.MacroEntry> {
   public final MacroManager macroManager;
   public final MacroListScreen parent;

   public MacroListWidget(MacroListScreen parent) {
      super(CactusConstants.mc, parent.field_22789, parent.field_22790 - 80, 40, 22);
      this.parent = parent;
      this.macroManager = MacroManager.get();
      this.parent.method_25395(this);
      Iterator var2 = this.macroManager.getMacros().iterator();

      while(var2.hasNext()) {
         Macro macro = (Macro)var2.next();
         this.method_25321(new MacroListWidget.MacroEntry(macro));
      }

   }

   public int method_65507() {
      return super.method_65507() + 20;
   }

   protected boolean method_73379() {
      return true;
   }

   public int method_25322() {
      return 200;
   }

   public class MacroEntry extends class_4266<MacroListWidget.MacroEntry> {
      public final Macro macro;
      private final class_4185 editButton;
      private final class_4185 deleteButton;
      private final CCheckboxWidget checkboxWidget;

      public MacroEntry(Macro macro) {
         this.macro = macro;
         this.checkboxWidget = new CCheckboxWidget(0, 0, 20, 20, macro.enabled, (checkbox, checked) -> {
            macro.enabled = checked;
         });
         this.editButton = new CTextureButtonWidget(0, 0, 220, (button) -> {
            CactusConstants.mc.method_1507(new MacroEditScreen(CactusConstants.mc.field_1755, macro));
         });
         this.deleteButton = new CTextureButtonWidget(40, 0, 200, (button) -> {
            this.deleteIfConfirmed();
         });
      }

      public void method_25343(class_332 context, int mouseX, int mouseY, boolean hovered, float tickDelta) {
         String text = TextUtils.trimToWidth(this.macro.name, MacroListWidget.this.method_25322() - 2, "...");
         int x = this.method_73380();
         int y = this.method_73382();
         this.checkboxWidget.method_48229(x - 1, y - 1);
         this.editButton.method_48229(MacroListWidget.this.method_31383() - 21 - 22, y - 1);
         this.deleteButton.method_48229(MacroListWidget.this.method_31383() - 21, y - 1);
         this.method_25396().forEach((e) -> {
            if (e instanceof class_339) {
               class_339 w = (class_339)e;
               w.method_25394(context, mouseX, mouseY, tickDelta);
            }

         });
         class_327 var10001 = CactusConstants.mc.field_1772;
         int var10003 = x + 28;
         int var10005 = this.method_73384();
         Objects.requireNonNull(CactusConstants.mc.field_1772);
         context.method_25303(var10001, text, var10003, y + (var10005 - 9) / 2 + 1, this.macro.enabled ? -1 : -5592406);
      }

      public class_2561 getNarration() {
         return class_2561.method_43473();
      }

      public void delete() {
         MacroListWidget.this.method_25330(this);
         this.macro.setKeyBinding(KeyBind.none());
         MacroManager.get().removeMacro(this.macro);
      }

      public void deleteIfConfirmed() {
         class_437 parent = CactusConstants.mc.field_1755;
         CactusConstants.mc.method_1507(new class_410((confirmed) -> {
            if (confirmed) {
               this.delete();
            }

            CactusConstants.mc.method_1507(parent);
         }, class_2561.method_30163("Delete macro"), class_2561.method_30163("Are you sure you want to delete '%s'?".formatted(new Object[]{this.macro.name}))));
      }

      public boolean method_25402(@NotNull class_11909 click, boolean doubled) {
         super.method_25402(click, doubled);
         return true;
      }

      @NotNull
      public List<? extends class_6379> method_37025() {
         return ImmutableList.of(this.checkboxWidget, this.editButton, this.deleteButton);
      }

      @NotNull
      public List<? extends class_364> method_25396() {
         return ImmutableList.of(this.checkboxWidget, this.editButton, this.deleteButton);
      }
   }
}
